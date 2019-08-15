

package com.huilan.zhihui.dto.organizationDto;

import lombok.Data;

@Data
public class OrganizationInfo extends OrganizationBaseInfo {

    private Integer projectNum;

    private Integer memberNum;

    private Integer roleNum;

    private Boolean allowCreateProject;

    private Short memberPermission;
}
