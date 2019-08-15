

package com.huilan.zhihui.dto.cronJobDto;

import com.huilan.core.utils.DateUtils;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NotNull(message = "cron job info cannot be null")
public class CronJobBaseInfo {

    @NotBlank(message = "cron job name cannot be EMPTY")
    private String name;

    @Min(value = 1L, message = "Invalid project Id")
    private Long projectId;

    @NotBlank(message = "cron job type cannot be EMPTY")
    private String jobType;

    @NotBlank(message = "cron job config cannot be EMPTY")
    private String config;

    @NotBlank(message = "Invalid cron pattern")
    private String cronExpression;

    @NotBlank(message = "start time cannot be EMPTY")
    @Pattern(regexp = DateUtils.DATE_HMS_REGEX, message = "Unparseable start date format")
    private String startDate;

    @NotBlank(message = "end time cannot be EMPTY")
    @Pattern(regexp = DateUtils.DATE_HMS_REGEX, message = "Unparseable end date format")
    private String endDate;

    private String description;
}
