

package com.huilan.zhihui.dto.dashboardDto;

import com.huilan.zhihui.model.DashboardPortal;
import lombok.Data;

@Data
public class PortalInfo extends DashboardPortal {
    private Short permission;
}
