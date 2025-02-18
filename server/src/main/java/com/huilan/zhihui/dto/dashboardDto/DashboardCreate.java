

package com.huilan.zhihui.dto.dashboardDto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NotNull(message = "dashboard cannot be null")
public class DashboardCreate {

    @NotBlank(message = "dashboard name cannot be EMPTY")
    private String name;

    @Min(value = 1L, message = "Invalid dashboard portal id")
    private Long dashboardPortalId;

    @Min(value = (short) 0, message = "Invalid dashboard type")
    @Max(value = (short) 2, message = "Invalid dashboard type")
    private Short type = 0;

    private Integer index = 0;

    private Long parentId;

    private String config;

    private List<Long> roleIds;
}
