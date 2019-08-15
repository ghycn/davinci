

package com.huilan.zhihui.dto.sourceDto;

import com.huilan.zhihui.model.Project;
import com.huilan.zhihui.model.Source;
import lombok.Data;

@Data
public class SourceWithProject extends Source {
    private Project project;
}
