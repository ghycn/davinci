

package com.huilan.zhihui.dto.roleDto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VizPermission {
    List<Long> portals = new ArrayList<>();
    List<Long> dashboards = new ArrayList<>();
    List<Long> displays = new ArrayList<>();
    List<Long> slides = new ArrayList<>();
}
