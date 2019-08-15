

package com.huilan.zhihui.dto.viewDto;

import com.huilan.zhihui.model.Project;
import com.huilan.zhihui.model.Source;
import lombok.Data;

@Data
public class ViewWithProjectAndSource extends ViewWithSource {

    private Project project;
    private Source source;
}
