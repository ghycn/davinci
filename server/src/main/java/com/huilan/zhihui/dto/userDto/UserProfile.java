

package com.huilan.zhihui.dto.userDto;

import com.huilan.zhihui.dto.organizationDto.OrganizationInfo;
import lombok.Data;

import java.util.List;

@Data
public class UserProfile extends UserBaseInfo {
    private String name;
    private String description;
    private String department;

    private List<OrganizationInfo> organizations;
}
