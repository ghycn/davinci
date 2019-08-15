

package com.huilan.zhihui.dto.organizationDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "organzation info cannot be null")
public class OrganizationCreate {
    @NotBlank(message = "organzation name cannot be EMPTY")
    private String name;

    private String description;

    @Override
    public String toString() {
        return "OrganizationCreate{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
