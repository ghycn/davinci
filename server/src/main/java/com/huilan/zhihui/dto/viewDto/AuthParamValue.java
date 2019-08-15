

package com.huilan.zhihui.dto.viewDto;

import lombok.Data;

import java.util.List;

@Data
public class AuthParamValue {
    private String name;
    private List<Object> values;
    private boolean enable;
}
