

package com.huilan.zhihui.dto.displayDto;


import com.huilan.zhihui.model.DisplaySlide;
import com.huilan.zhihui.model.MemDisplaySlideWidget;
import lombok.Data;

@Data
public class MemDisplaySlideWidgetWithSlide extends MemDisplaySlideWidget {
    private DisplaySlide displaySlide;
}
