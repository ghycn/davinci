

package com.huilan.zhihui.dto.displayDto;

import com.huilan.zhihui.model.Display;
import com.huilan.zhihui.model.DisplaySlide;
import com.huilan.zhihui.model.Project;
import lombok.Data;

@Data
public class SlideWithDisplayAndProject extends DisplaySlide {

    private Display display;

    private Project project;

    @Override
    public String toString() {
        return super.toString();
    }
}
