

package com.huilan.zhihui.dto.widgetDto;

import com.huilan.zhihui.model.Widget;
import lombok.Data;

@Data
public class WidgetWithRelationDashboardId extends Widget {
    private Long relationId;
}
