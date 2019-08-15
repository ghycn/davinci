

package com.huilan.zhihui.dto.viewDto;

import lombok.Data;

import java.util.List;

@Data
public class WhereParam {
    private String column;

    private List<String> value;
}
