

package com.huilan.zhihui.dto.cronJobDto;

import lombok.Data;

@Data
public class CronJobInfo extends CronJobBaseInfo {
    private Long id;

    private String jobStatus;
}
