

package com.huilan.zhihui.model;


import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "DisplaySlide info cannot be null")
public class DisplaySlide extends RecordInfo<DisplaySlide> {
    @Min(value = 1L, message = "Invalid slide id")
    private Long id;

    @Min(value = 1L, message = "Invalid display id")
    private Long displayId;

    private Integer index;

    private String config;

    @Override
    public String toString() {
        return "DisplaySlide{" +
                "id=" + id +
                ", displayId=" + displayId +
                ", index=" + index +
                ", config='" + config + '\'' +
                '}';
    }
}