

package com.huilan.zhihui.dto.projectDto;

import lombok.Data;

@Data
public class ProjectInfo extends ProjectWithCreateBy {
    private ProjectPermission permission = new ProjectPermission();
}
