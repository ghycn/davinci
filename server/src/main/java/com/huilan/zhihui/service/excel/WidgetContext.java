

package com.huilan.zhihui.service.excel;

import com.huilan.zhihui.dto.viewDto.ViewExecuteParam;
import com.huilan.zhihui.model.Dashboard;
import com.huilan.zhihui.model.MemDashboardWidget;
import com.huilan.zhihui.model.Widget;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author daemon
 * @Date 19/5/29 11:46
 * To change this template use File | Settings | File Templates.
 */
@Data
public class WidgetContext implements Serializable {

    private Widget widget;

    private Dashboard dashboard;

    private MemDashboardWidget memDashboardWidget;

    private Boolean isMaintainer;

    private ViewExecuteParam executeParam;

    private boolean hasExecuteParam = false;


    public WidgetContext(Widget widget, Dashboard dashboard, MemDashboardWidget memDashboardWidget, ViewExecuteParam executeParam) {
        this.widget = widget;
        this.dashboard = dashboard;
        this.memDashboardWidget = memDashboardWidget;
        if (null != executeParam) {
            this.executeParam = executeParam;
            this.hasExecuteParam = true;
        }
    }

    public WidgetContext() {
    }
}
