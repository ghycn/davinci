

package com.huilan.zhihui.dto.projectDto;

import com.huilan.zhihui.dto.userDto.UserBaseInfo;
import com.huilan.zhihui.model.Organization;
import com.huilan.zhihui.model.Project;
import lombok.Data;

@Data
public class ProjectDetail extends Project {
    private Organization organization;

    private UserBaseInfo createBy;


    @Override
    public String toString() {
        return "Project{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", pic='" + getPic() + '\'' +
                ", orgId=" + getOrgId() +
                ", userId=" + getUserId() +
                ", starNum=" + getStarNum() +
                ", visibility=" + getVisibility() +
                ", isTransfer=" + getIsTransfer() +
                ", initialOrgId=" + getInitialOrgId() +
                '}';
    }
}
