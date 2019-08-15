

package com.huilan.zhihui.dto.dashboardDto;

import com.huilan.zhihui.model.Dashboard;
import com.huilan.zhihui.model.MemDashboardWidget;
import com.huilan.zhihui.model.View;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DashboardWithMem extends Dashboard {
    List<MemDashboardWidget> widgets;
    Set<View> views;
}
