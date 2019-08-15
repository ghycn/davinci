

package com.huilan.zhihui.dto.displayDto;

import com.huilan.zhihui.model.Display;
import com.huilan.zhihui.model.Project;
import lombok.Data;

@Data
public class DisplayWithProject extends Display {

    private Project project;
}
