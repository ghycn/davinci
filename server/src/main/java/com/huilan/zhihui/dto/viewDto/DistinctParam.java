

package com.huilan.zhihui.dto.viewDto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NotNull(message = "request parameter cannot be null")
public class DistinctParam {
    @NotEmpty(message = "distinct column cannot be EMPTY")
    private List<String> columns;

    private List<String> filters;

    private List<Param> params;

    private Boolean cache;

    private Long expired;
}
