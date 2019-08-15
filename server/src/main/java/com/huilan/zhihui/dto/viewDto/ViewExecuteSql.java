

package com.huilan.zhihui.dto.viewDto;

import com.huilan.zhihui.model.SqlVariable;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ViewExecuteSql {
    @Min(value = 1L, message = "Invalid Source Id")
    private Long sourceId;

    @NotBlank(message = "sql cannot be EMPTY")
    private String sql;

    private List<SqlVariable> variables;

    private int limit = 0;
    private int pageNo = -1;
    private int pageSize = -1;
}
