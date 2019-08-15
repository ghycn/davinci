

package com.huilan.zhihui.dto.displayDto;

import com.huilan.zhihui.model.Display;
import lombok.Data;

import java.util.List;

@Data
public class DisplayWithSlides extends Display {

    private List<DisplaySlideInfo> slides;
}
