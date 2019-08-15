

package com.huilan.zhihui.dto.shareDto;

import com.huilan.zhihui.model.MemDashboardWidget;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ShareDashboard {
    private Long id;

    private String name;

    private String config;

    private Set<ShareWidget> widgets;

    private List<MemDashboardWidget> relations;
}
