

package com.huilan.zhihui.core.model;

import lombok.Data;

@Data
public class FieldCurrency extends FieldNumeric {
    private String prefix;
    private String suffix;
}
