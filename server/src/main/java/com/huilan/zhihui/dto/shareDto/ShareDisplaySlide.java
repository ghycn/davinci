

package com.huilan.zhihui.dto.shareDto;

import com.huilan.zhihui.model.MemDisplaySlideWidget;
import lombok.Data;

import java.util.Set;

@Data
public class ShareDisplaySlide {
    private Long displayId;

    private Integer index;

    private String config;

    private Set<MemDisplaySlideWidget> relations;
}
