

package com.huilan.zhihui.dto.organizationDto;

import com.huilan.zhihui.dto.userDto.UserWithOrgRole;
import lombok.Data;

@Data
public class OrganizationMember {

    /**
     * 关联id
     */
    private Long id;

    /**
     * 复合属性
     */
    private UserWithOrgRole user;
}
