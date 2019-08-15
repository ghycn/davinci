

package com.huilan.zhihui.dto.sourceDto;

import com.huilan.zhihui.core.enums.UploadModeEnum;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "csv meta cannot be null")
public class UploadMeta {

    @NotBlank(message = "table name cannot be EMPTY")
    private String tableName;

    @Min(value = (short) 0, message = "Invalid mode")
    @Max(value = (short) 2, message = "Invalid mode")
    private short mode = UploadModeEnum.NEW.getMode();
}
