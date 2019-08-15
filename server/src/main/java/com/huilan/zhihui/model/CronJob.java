

package com.huilan.zhihui.model;

import com.huilan.core.model.ScheduleJob;
import com.huilan.core.utils.DateUtils;
import com.huilan.zhihui.core.enums.CronJobStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class CronJob extends ScheduleJob {

    private Long id;

    private String name;

    private Long projectId;

    private String jobType;

    private String config;

    private String jobStatus = CronJobStatusEnum.NEW.getStatus();

    private String execLog;

    private String cronExpression;

    private Date startDate;

    private Date endDate;

    private String description;

    @Override
    public String toString() {
        return "CronJob{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", projectId=" + projectId +
                ", jobType='" + jobType + '\'' +
                ", config='" + config + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", execLog='" + execLog + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", startDate=" + DateUtils.toyyyyMMddHHmmss(startDate) +
                ", endDate=" + DateUtils.toyyyyMMddHHmmss(endDate) +
                ", description='" + description + '\'' +
                '}';
    }
}