

package com.huilan.core.model;

import com.huilan.zhihui.common.model.RecordInfo;
import com.huilan.zhihui.model.CronJob;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleJob extends RecordInfo<CronJob> {

    private Long id;

    private String jobType;

    private String description;

    private String cronExpression;

    private Date startDate;

    private Date endDate;
}
