

package com.huilan.zhihui.service.impl;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.core.utils.CollectionUtils;
import com.huilan.zhihui.core.enums.LogNameEnum;
import com.huilan.zhihui.core.enums.UserPermissionEnum;
import com.huilan.zhihui.dto.dashboardDto.DashboardPortalCreate;
import com.huilan.zhihui.dto.dashboardDto.DashboardPortalUpdate;
import com.huilan.zhihui.dto.projectDto.ProjectDetail;
import com.huilan.zhihui.dto.projectDto.ProjectPermission;
import com.huilan.zhihui.dto.roleDto.VizVisibility;
import com.huilan.zhihui.model.DashboardPortal;
import com.huilan.zhihui.model.RelRolePortal;
import com.huilan.zhihui.model.Role;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.service.DashboardPortalService;
import com.huilan.zhihui.service.ProjectService;
import com.huilan.zhihui.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service("dashboardPortalService")
@Slf4j
public class DashboardPortalServiceImpl implements DashboardPortalService {
    private static final Logger optLogger = LoggerFactory.getLogger(LogNameEnum.BUSINESS_OPERATION.getName());

    @Autowired
    private DashboardPortalMapper dashboardPortalMapper;

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RelRolePortalMapper relRolePortalMapper;

    @Autowired
    private RelRoleDashboardWidgetMapper relRoleDashboardWidgetMapper;

    @Autowired
    private MemDashboardWidgetMapper memDashboardWidgetMapper;

    @Autowired
    private RelRoleDashboardMapper relRoleDashboardMapper;

    @Override
    public synchronized boolean isExist(String name, Long id, Long projectId) {
        Long portalId = dashboardPortalMapper.getByNameWithProjectId(name, projectId);
        if (null != id && null != portalId) {
            return id.longValue() != portalId.longValue();
        }
        return null != portalId && portalId.longValue() > 0L;
    }

    /**
     * 获取DashboardPortal列表
     *
     * @param projectId
     * @param user
     * @return
     */
    @Override
    public List<DashboardPortal> getDashboardPortals(Long projectId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {

        ProjectDetail projectDetail = null;
        try {
            projectDetail = projectService.getProjectDetail(projectId, user, false);
        } catch (NotFoundException e) {
            throw e;
        } catch (UnAuthorizedExecption e) {
            return null;
        }

        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
        if (projectPermission.getVizPermission() < UserPermissionEnum.READ.getPermission()) {
            return null;
        }

        List<DashboardPortal> dashboardPortals = dashboardPortalMapper.getByProject(projectId);


        List<Long> disbalePortals = relRolePortalMapper.getDisablePortalByUser(user.getId(), projectId);

        if (CollectionUtils.isEmpty(disbalePortals)) {
            return dashboardPortals;
        }

        Iterator<DashboardPortal> iterator = dashboardPortals.iterator();
        while (iterator.hasNext()) {
            DashboardPortal portal = iterator.next();
            if (!projectPermission.isProjectMaintainer() && (disbalePortals.contains(portal.getId()) || !portal.getPublish())) {
                iterator.remove();
            }
        }

        return dashboardPortals;
    }


    /**
     * 新建DashboardPortal
     *
     * @param dashboardPortalCreate
     * @param user
     * @return
     */
    @Override
    @Transactional
    public DashboardPortal createDashboardPortal(DashboardPortalCreate dashboardPortalCreate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {

        ProjectDetail projectDetail = projectService.getProjectDetail(dashboardPortalCreate.getProjectId(), user, false);
        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);

        //校验权限
        if (projectPermission.getVizPermission() < UserPermissionEnum.WRITE.getPermission()) {
            log.info("user {} have not permisson to create dashboard portal", user.getUsername());
            throw new UnAuthorizedExecption("you have not permission to create portal");
        }

        if (isExist(dashboardPortalCreate.getName(), null, dashboardPortalCreate.getProjectId())) {
            log.info("the dashboardPortal \"{}\" name is already taken", dashboardPortalCreate.getName());
            throw new ServerException("the dashboard portal name is already taken");
        }

        DashboardPortal dashboardPortal = new DashboardPortal().createdBy(user.getId());
        BeanUtils.copyProperties(dashboardPortalCreate, dashboardPortal);

        int insert = dashboardPortalMapper.insert(dashboardPortal);
        if (insert > 0) {
            optLogger.info("portal ({}) is created by user(:{})", dashboardPortal.toString(), user.getId());

            if (!CollectionUtils.isEmpty(dashboardPortalCreate.getRoleIds())) {
                List<Role> roles = roleMapper.getRolesByIds(dashboardPortalCreate.getRoleIds());

                List<RelRolePortal> list = roles.stream()
                        .map(r -> new RelRolePortal(dashboardPortal.getId(), r.getId()).createdBy(user.getId()))
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(list)) {
                    relRolePortalMapper.insertBatch(list);

                    optLogger.info("portal ({}) limit role ({}) access", dashboardPortal.getId(), roles.stream().map(r -> r.getId()).collect(Collectors.toList()));
                }

            }

            return dashboardPortal;
        } else {
            throw new ServerException("create dashboardPortal fail");
        }
    }


    /**
     * 更新DashboardPortal
     *
     * @param dashboardPortalUpdate
     * @param user
     * @return
     */
    @Override
    @Transactional
    public DashboardPortal updateDashboardPortal(DashboardPortalUpdate dashboardPortalUpdate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {

        DashboardPortal dashboardPortal = dashboardPortalMapper.getById(dashboardPortalUpdate.getId());
        if (null == dashboardPortal) {
            throw new NotFoundException("dashboard portal is not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(dashboardPortal.getProjectId(), user, false);
        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);

        List<Long> disbalePortals = relRolePortalMapper.getDisablePortalByUser(user.getId(), dashboardPortal.getProjectId());


        //校验权限
        if (projectPermission.getVizPermission() < UserPermissionEnum.WRITE.getPermission() ||
                (!projectPermission.isProjectMaintainer() && disbalePortals.contains(dashboardPortal.getId()))) {
            log.info("user {} have not permisson to update widget", user.getUsername());
            throw new UnAuthorizedExecption("you have not permission to update portal");
        }

        if (isExist(dashboardPortalUpdate.getName(), dashboardPortal.getId(), dashboardPortal.getProjectId())) {
            log.info("the dashboardPortal \"{}\" name is already taken", dashboardPortalUpdate.getName());
            throw new ServerException("the dashboard portal name is already taken");
        }

        String origin = dashboardPortal.toString();
        BeanUtils.copyProperties(dashboardPortalUpdate, dashboardPortal);
        dashboardPortal.updatedBy(user.getId());

        int update = dashboardPortalMapper.update(dashboardPortal);
        if (update > 0) {
            optLogger.info("portal ({}) is update by (:{}), origin: ({})", dashboardPortal.toString(), user.getId(), origin);
            relRolePortalMapper.deleteByProtalId(dashboardPortal.getId());
            if (!CollectionUtils.isEmpty(dashboardPortalUpdate.getRoleIds())) {

                List<Role> roles = roleMapper.getRolesByIds(dashboardPortalUpdate.getRoleIds());

                List<RelRolePortal> list = roles.stream()
                        .map(r -> new RelRolePortal(dashboardPortal.getId(), r.getId()).createdBy(user.getId()))
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(list)) {
                    relRolePortalMapper.insertBatch(list);

                    optLogger.info("update portal ({}) limit role ({}) access", dashboardPortal.getId(), roles.stream().map(r -> r.getId()).collect(Collectors.toList()));
                }
            }

            return dashboardPortal;
        } else {
            throw new ServerException("update dashboard fail");
        }
    }


    @Override
    public List<Long> getExcludeRoles(Long id) {
        return relRolePortalMapper.getExecludeRoles(id);
    }

    @Override
    @Transactional
    public boolean postPortalVisibility(Role role, VizVisibility vizVisibility, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        DashboardPortal portal = dashboardPortalMapper.getById(vizVisibility.getId());
        if (null == portal) {
            throw new NotFoundException("dashboard portal is not found");
        }

        projectService.getProjectDetail(portal.getProjectId(), user, true);

        if (vizVisibility.isVisible()) {
            int delete = relRolePortalMapper.delete(portal.getId(), role.getId());
            if (delete > 0) {
                optLogger.info("portal ({}) can be accessed by role ({}), update by (:{})", portal, role, user.getId());
            }
        } else {
            RelRolePortal relRolePortal = new RelRolePortal(portal.getId(), role.getId()).createdBy(user.getId());
            relRolePortalMapper.insert(relRolePortal);
            optLogger.info("portal ({}) limit role ({}) access, create by (:{})", portal, role, user.getId());
        }

        return true;
    }

    /**
     * 删除DashboardPortal
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    @Transactional
    public boolean deleteDashboardPortal(Long id, User user) throws NotFoundException, UnAuthorizedExecption {
        DashboardPortal dashboardPortal = dashboardPortalMapper.getById(id);
        if (null == dashboardPortal) {
            log.info("dashboard portal (:{}) not found", id);
            return true;
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(dashboardPortal.getProjectId(), user, false);
        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);

        List<Long> disbalePortals = relRolePortalMapper.getDisablePortalByUser(user.getId(), dashboardPortal.getProjectId());


        //校验权限
        if (projectPermission.getVizPermission() < UserPermissionEnum.DELETE.getPermission() ||
                (!projectPermission.isProjectMaintainer() && disbalePortals.contains(dashboardPortal.getId()))) {
            log.info("user {} have not permisson to delete widget", user.getUsername());
            throw new UnAuthorizedExecption("you have not permission to delete portal");
        }

        //delete rel_role_dashboard_widget
        relRoleDashboardWidgetMapper.deleteByPortalId(id);

        //delete mem_dashboard_widget
        memDashboardWidgetMapper.deleteByPortalId(id);

        //delete rel_role_dashboard
        relRoleDashboardMapper.deleteByPortalId(id);

        //delete dashboard
        dashboardMapper.deleteByPortalId(id);

        //delete dashboard_portal
        int i = dashboardPortalMapper.deleteById(id);
        if (i > 0) {
            relRolePortalMapper.deleteByProtalId(dashboardPortal.getId());

            optLogger.info("dashboaard portal( {} ) delete by user( :{}) ", dashboardPortal.toString(), user.getId());
            return true;
        }
        return false;
    }
}
