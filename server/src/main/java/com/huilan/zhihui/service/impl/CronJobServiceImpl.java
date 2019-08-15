

package com.huilan.zhihui.service.impl;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.core.utils.CollectionUtils;
import com.huilan.core.utils.DateUtils;
import com.huilan.core.utils.QuartzUtils;
import com.huilan.zhihui.core.enums.CronJobStatusEnum;
import com.huilan.zhihui.core.enums.LogNameEnum;
import com.huilan.zhihui.core.enums.UserPermissionEnum;
import com.huilan.zhihui.dao.CronJobMapper;
import com.huilan.zhihui.dto.cronJobDto.CronJobBaseInfo;
import com.huilan.zhihui.dto.cronJobDto.CronJobInfo;
import com.huilan.zhihui.dto.cronJobDto.CronJobUpdate;
import com.huilan.zhihui.dto.projectDto.ProjectDetail;
import com.huilan.zhihui.dto.projectDto.ProjectPermission;
import com.huilan.zhihui.model.CronJob;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.service.CronJobService;
import com.huilan.zhihui.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service("cronJobService")
public class CronJobServiceImpl implements CronJobService {
    private static final Logger optLogger = LoggerFactory.getLogger(LogNameEnum.BUSINESS_OPERATION.getName());

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CronJobMapper cronJobMapper;

    @Autowired
    private QuartzUtils quartzUtils;

    @Override
    public synchronized boolean isExist(String name, Long id, Long projectId) {
        Long cronJobId = cronJobMapper.getByNameWithProjectId(name, projectId);
        if (null != id && null != cronJobId) {
            return !id.equals(cronJobId);
        }
        return null != cronJobId && cronJobId.longValue() > 0L;
    }

    /**
     * 获取所在project对用户可见的jobs
     *
     * @param projectId
     * @param user
     * @return
     */
    @Override
    public List<CronJob> getCronJobs(Long projectId, User user) {
        ProjectDetail projectDetail = null;
        try {
            projectDetail = projectService.getProjectDetail(projectId, user, false);
        } catch (NotFoundException e) {
            return null;
        } catch (UnAuthorizedExecption e) {
            return null;
        }
        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
        if (projectPermission.getSchedulePermission() < UserPermissionEnum.READ.getPermission()) {
            return null;
        }
        return cronJobMapper.getByProject(projectId);
    }


    /**
     * 创建job
     *
     * @param cronJobBaseInfo
     * @param user
     * @return
     */
    @Override
    @Transactional
    public CronJobInfo createCronJob(CronJobBaseInfo cronJobBaseInfo, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {

        ProjectPermission projectPermission = projectService.getProjectPermission(projectService.getProjectDetail(cronJobBaseInfo.getProjectId(), user, false), user);

        if (projectPermission.getSchedulePermission() < UserPermissionEnum.WRITE.getPermission()) {
            log.info("user {} have not permisson to create job", user.getUsername());
            throw new UnAuthorizedExecption("you have not permission to create job");
        }


        if (isExist(cronJobBaseInfo.getName(), null, cronJobBaseInfo.getProjectId())) {
            log.info("the job {} name is already taken", cronJobBaseInfo.getName());
            throw new ServerException("this job name is already taken");
        }

        CronJob cronJob = new CronJob().createdBy(user.getId());
        BeanUtils.copyProperties(cronJobBaseInfo, cronJob);
        cronJob.setCreateBy(user.getId());
        try {
            cronJob.setStartDate(DateUtils.toDate(cronJobBaseInfo.getStartDate()));
            cronJob.setEndDate(DateUtils.toDate(cronJobBaseInfo.getEndDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int insert = cronJobMapper.insert(cronJob);
        if (insert > 0) {
            optLogger.info("cronJob ({}) is create by (:{})", cronJob.toString(), user.getId());
            CronJobInfo cronJobInfo = new CronJobInfo();
            BeanUtils.copyProperties(cronJobBaseInfo, cronJobInfo);
            cronJobInfo.setId(cronJob.getId());
            cronJobInfo.setJobStatus(CronJobStatusEnum.NEW.getStatus());
            return cronJobInfo;
        } else {
            throw new ServerException("create cronJob fail");
        }
    }

    /**
     * 修改job
     *
     * @param cronJobUpdate
     * @param user
     * @return
     */
    @Override
    @Transactional
    public boolean updateCronJob(CronJobUpdate cronJobUpdate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        CronJob cronJob = cronJobMapper.getById(cronJobUpdate.getId());
        if (null == cronJob) {
            throw new NotFoundException("cronjob is not found");
        }

        if (null != cronJob && !cronJob.getProjectId().equals(cronJobUpdate.getProjectId())) {
            throw new ServerException("Invalid project id");
        }

        ProjectPermission projectPermission = projectService.getProjectPermission(projectService.getProjectDetail(cronJob.getProjectId(), user, false), user);

        //校验权限
        if (projectPermission.getSchedulePermission() < UserPermissionEnum.WRITE.getPermission()) {
            log.info("user {} have not permisson to update this job", user.getUsername());
            throw new UnAuthorizedExecption("you have not permission to update this job");
        }

        if (isExist(cronJobUpdate.getName(), cronJobUpdate.getId(), cronJob.getProjectId())) {
            log.info("the cronjob {} name is already taken", cronJobUpdate.getName());
            throw new ServerException("the name is already taken");
        }

        if (CronJobStatusEnum.START.getStatus().equals(cronJob.getJobStatus())) {
            throw new ServerException("Please stop the job before updating");
        }

        String origin = cronJob.toString();
        BeanUtils.copyProperties(cronJobUpdate, cronJob);
        cronJob.updatedBy(user.getId());
        try {
            cronJob.setStartDate(DateUtils.toDate(cronJobUpdate.getStartDate()));
            cronJob.setEndDate(DateUtils.toDate(cronJobUpdate.getEndDate()));

            cronJob.setUpdateTime(new Date());
            int update = cronJobMapper.update(cronJob);
            if (update > 0) {
                optLogger.info("cronJob ({}) is update by (:{}), origin: ({})", cronJob.toString(), user.getId(), origin);
                quartzUtils.modifyJob(cronJob);
            }
        } catch (Exception e) {
            quartzUtils.removeJob(cronJob);
            cronJob.setJobStatus(CronJobStatusEnum.FAILED.getStatus());
            cronJobMapper.update(cronJob);

            e.printStackTrace();
        }

        return true;
    }

    /**
     * 删除job
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    @Transactional
    public boolean deleteCronJob(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {

        CronJob cronJob = cronJobMapper.getById(id);
        if (null == cronJob) {
            log.info("cronjob (:{}) is not found", id);
            throw new NotFoundException("cronjob is not found");
        }

        ProjectPermission projectPermission = projectService.getProjectPermission(projectService.getProjectDetail(cronJob.getProjectId(), user, false), user);

        //校验权限
        if (projectPermission.getSchedulePermission() < UserPermissionEnum.DELETE.getPermission()) {
            log.info("user {} have not permisson to delete the cronjob {}", user.getUsername(), id);
            throw new UnAuthorizedExecption("you have not permission to delete this job");
        }

        int i = cronJobMapper.deleteById(id);
        if (i > 0) {
            optLogger.info("cronjob ({}) is delete by (:{})", cronJob.toString(), user.getId());
            quartzUtils.removeJob(cronJob);
        }

        return true;
    }

    @Override
    @Transactional
    public CronJob startCronJob(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        CronJob cronJob = cronJobMapper.getById(id);
        if (null == cronJob) {
            log.info("cronjob (:{}) is not found", id);
            throw new NotFoundException("cronjob is not found");
        }

        ProjectPermission projectPermission = projectService.getProjectPermission(projectService.getProjectDetail(cronJob.getProjectId(), user, false), user);

        //校验权限
        if (projectPermission.getSchedulePermission() < UserPermissionEnum.WRITE.getPermission()) {
            throw new UnAuthorizedExecption("Insufficient permissions");
        }

        try {
            quartzUtils.addJob(cronJob);
            cronJob.setJobStatus(CronJobStatusEnum.START.getStatus());
            cronJob.setUpdateTime(new Date());
            cronJobMapper.update(cronJob);
            return cronJob;
        } catch (ServerException e) {
            cronJob.setJobStatus(CronJobStatusEnum.FAILED.getStatus());
            cronJob.setUpdateTime(new Date());
            cronJobMapper.update(cronJob);

            e.printStackTrace();
            return cronJob;
        }
    }

    @Override
    @Transactional
    public CronJob stopCronJob(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        CronJob cronJob = cronJobMapper.getById(id);
        if (null == cronJob) {
            log.info("cronjob (:{}) is not found", id);
            throw new NotFoundException("cronjob is not found");
        }

        ProjectPermission projectPermission = projectService.getProjectPermission(projectService.getProjectDetail(cronJob.getProjectId(), user, false), user);

        //校验权限
        if (projectPermission.getSchedulePermission() < UserPermissionEnum.WRITE.getPermission()) {
            throw new UnAuthorizedExecption("Insufficient permissions");
        }

        try {
            quartzUtils.removeJob(cronJob);
            cronJob.setJobStatus(CronJobStatusEnum.STOP.getStatus());
            cronJob.setUpdateTime(new Date());
            cronJobMapper.update(cronJob);
            return cronJob;
        } catch (ServerException e) {
            cronJob.setJobStatus(CronJobStatusEnum.FAILED.getStatus());
            cronJobMapper.update(cronJob);

            e.printStackTrace();
            return cronJob;
        }
    }


    @Override
    public void startAllJobs() {
        List<CronJob> jobList = cronJobMapper.getStartedJobs();
        if (!CollectionUtils.isEmpty(jobList)) {
            for (CronJob cronJob : jobList) {
                if (CronJobStatusEnum.START.getStatus().equals(cronJob.getJobStatus())) {
                    try {
                        quartzUtils.addJob(cronJob);
                    } catch (ServerException e) {
                        log.info(e.getMessage());
                    }
                }
            }
            quartzUtils.startJobs();
        }
    }
}
