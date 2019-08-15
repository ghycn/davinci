

package com.huilan.zhihui.dto.dashboardDto;

import com.huilan.zhihui.model.Dashboard;
import lombok.Data;

import java.util.List;

@Data
public class DashboardDto extends Dashboard {
    private List<Long> roleIds;
}
