

package com.huilan.zhihui.model;

import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "display slide widget cannot be null")
public class MemDisplaySlideWidget extends RecordInfo<MemDisplaySlideWidget> {

    @Min(value = 1L, message = "Invalid id")
    private Long id;

    @Min(value = 1L, message = "Invalid display slide id")
    private Long displaySlideId;

    private Long widgetId;

    @NotBlank(message = "name cannot be EMPTY")
    private String name;

    @Min(value = 0, message = "Invalid slide widget type")
    private Short type;

    private Short subType;

    private Integer index = 0;

    @NotBlank(message = "params cannot be EMPTY")
    private String params;

}