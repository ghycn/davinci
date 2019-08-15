

package com.huilan.zhihui.dto.projectDto;

import com.huilan.zhihui.dto.userDto.UserBaseInfo;
import com.huilan.zhihui.model.Project;
import lombok.Data;

@Data
public class ProjectWithCreateBy extends Project {

    private Boolean isStar = false;

    private UserBaseInfo createBy;
}
