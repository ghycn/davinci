

package com.huilan.zhihui.dto.viewDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NotNull(message = "view cannot be null")
public class ViewCreate {

    @NotBlank(message = "view name cannot be EMPTY")
    private String name;

    private String description;

    @Min(value = 1, message = "Invalid project Id")
    private Long projectId;

    @Min(value = 1, message = "Invalid source Id")
    private Long sourceId;

    private String sql;

    private String model;

    private String variable;

    private String config;

    private List<RelRoleViewDto> roles;
}
