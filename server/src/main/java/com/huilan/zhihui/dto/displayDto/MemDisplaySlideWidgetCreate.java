

package com.huilan.zhihui.dto.displayDto;


import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NotNull(message = "display slide widget info cannot be null")
public class MemDisplaySlideWidgetCreate {

    private Long id;

    @NotBlank(message = "name cannot be EMPTY")
    private String name;

    @Min(value = 1L, message = "Invalid display slide id")
    private Long displaySlideId;

    private Long widgetId;

    @Min(value = 0, message = "Invalid type")
    private Short type;

    private Short subType;

    private Integer index = 0;

    @NotBlank(message = "type cannot be EMPTY")
    private String params;

    private List<Long> roleIds;
}
