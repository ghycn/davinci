

package com.huilan.zhihui.dto.roleDto;

import lombok.Data;

@Data
public class RoleWithProjectPermission extends RoleBaseInfo {

    private RelRoleProjectDto permission;
}
