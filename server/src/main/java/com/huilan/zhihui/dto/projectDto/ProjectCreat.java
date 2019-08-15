

package com.huilan.zhihui.dto.projectDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "project info cannot be null")
public class ProjectCreat {

    @NotBlank(message = "project name cannot be EMPTY")
    private String name;

    private String description;

    @Min(value = 1L, message = "orgId cannot be EMPTY")
    private Long orgId;

    private String pic;

    private boolean visibility;
}
