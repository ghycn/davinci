

package com.huilan.zhihui.dto.dashboardDto;

import com.huilan.zhihui.model.Dashboard;
import com.huilan.zhihui.model.DashboardPortal;
import com.huilan.zhihui.model.Project;
import lombok.Data;

@Data
public class DashboardWithPortal extends Dashboard {
    private DashboardPortal portal;
    private Project project;
}
