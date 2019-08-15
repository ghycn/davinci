

package com.huilan.zhihui.dto.displayDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NotNull(message = "display slide cannot be null")
public class DisplaySlideCreate {

    @Min(value = 1L, message = "Invalid display id")
    private Long displayId;

    private Integer index = 0;

    private String config;

    private List<Long> roleIds;
}
