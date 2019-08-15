

package com.huilan.zhihui.dto.displayDto;

import com.huilan.zhihui.model.Display;
import lombok.Data;

import java.util.List;

@Data
public class DisplayUpdate extends Display {
    private List<Long> roleIds;
}
