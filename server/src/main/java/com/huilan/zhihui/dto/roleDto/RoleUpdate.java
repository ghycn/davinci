

package com.huilan.zhihui.dto.roleDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "role info cannot be EMPTY")
public class RoleUpdate {

    @NotBlank(message = "role name cannot be EMPTY")
    private String name;

    private String description;

}
