

package com.huilan.zhihui.dto.roleDto;

import com.alibaba.fastjson.annotation.JSONType;
import com.huilan.zhihui.model.Project;
import com.huilan.zhihui.model.RelRoleProject;
import lombok.Data;

@Data
@JSONType(ignores = {"projectId", "roleId", "id"})
public class RoleProject extends RelRoleProject {
    private Project project;

    public RoleProject(Project project) {
        this.project = project;
    }

    public RoleProject() {
    }
}
