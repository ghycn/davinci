

package com.huilan.zhihui.dto.projectDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "project cannot be null")
public class ProjectUpdate {

    @NotBlank(message = "project name cannot be EMPTY")
    private String name;

    private String description;

    private Boolean visibility = true;
}
