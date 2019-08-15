

package com.huilan.zhihui.dto.roleDto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NotNull
public class RelRoleProjectDto {

    @Min(value = (short) 0, message = "Invalid source permission")
    @Max(value = (short) 3, message = "Invalid source permission")
    private Short sourcePermission = 1;

    @Min(value = (short) 0, message = "Invalid view permission")
    @Max(value = (short) 3, message = "Invalid view permission")
    private Short viewPermission = 1;

    @Min(value = (short) 0, message = "Invalid widget permission")
    @Max(value = (short) 3, message = "Invalid widget permission")
    private Short widgetPermission = 1;

    @Min(value = (short) 0, message = "Invalid viz permission")
    @Max(value = (short) 3, message = "Invalid viz permission")
    private Short vizPermission = 1;

    @Min(value = (short) 0, message = "Invalid schedule permission")
    @Max(value = (short) 3, message = "Invalid schedule permission")
    private Short schedulePermission = 1;

    private Boolean sharePermission = false;

    private Boolean downloadPermission = false;
}
