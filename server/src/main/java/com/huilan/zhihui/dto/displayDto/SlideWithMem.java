

package com.huilan.zhihui.dto.displayDto;

import com.huilan.zhihui.model.DisplaySlide;
import com.huilan.zhihui.model.MemDisplaySlideWidget;
import com.huilan.zhihui.model.View;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SlideWithMem extends DisplaySlide {
    private List<MemDisplaySlideWidget> widgets;
    private Set<View> views;
}
