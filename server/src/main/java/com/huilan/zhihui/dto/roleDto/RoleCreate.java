

package com.huilan.zhihui.dto.roleDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "role info cannot be null")
public class RoleCreate {

    @NotBlank(message = "role name cannot be EMPTY")
    private String name;

    private String description;

    @Min(value = 1L, message = "Invalid orgId")
    private Long orgId;
}
