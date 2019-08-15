

package com.huilan.zhihui.service;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.dto.cronJobDto.CronJobBaseInfo;
import com.huilan.zhihui.dto.cronJobDto.CronJobInfo;
import com.huilan.zhihui.dto.cronJobDto.CronJobUpdate;
import com.huilan.zhihui.model.CronJob;
import com.huilan.zhihui.model.User;

import java.util.List;

public interface CronJobService extends CheckEntityService {
    List<CronJob> getCronJobs(Long projectId, User user);

    CronJobInfo createCronJob(CronJobBaseInfo cronJobBaseInfo, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean updateCronJob(CronJobUpdate cronJobUpdate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean deleteCronJob(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    CronJob startCronJob(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    CronJob stopCronJob(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    void startAllJobs();
}
