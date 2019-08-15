

package com.huilan.zhihui.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huilan.zhihui.core.enums.NumericUnitEnum;
import lombok.Data;

@Data
public class FieldNumeric {
    private int decimalPlaces;

    @JsonIgnore
    private NumericUnitEnum unit;
    private boolean useThousandSeparator;
}
