

package com.huilan.zhihui.dto.roleDto;

import com.huilan.zhihui.model.Organization;
import com.huilan.zhihui.model.Role;
import lombok.Data;

@Data
public class RoleWithOrganization extends Role {
    private Organization organization;
}
