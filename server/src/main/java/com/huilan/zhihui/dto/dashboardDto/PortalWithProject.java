

package com.huilan.zhihui.dto.dashboardDto;

import com.huilan.zhihui.model.DashboardPortal;
import com.huilan.zhihui.model.Project;
import lombok.Data;

@Data
public class PortalWithProject extends DashboardPortal {

    private Project project;
}
