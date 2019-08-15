

package com.huilan.zhihui.dto.organizationDto;

import lombok.Data;

@Data
public class OrganizationBaseInfo {
    protected Long id;
    protected String name;
    protected String description;
    protected String avatar;
    protected Short role = 0;
}
