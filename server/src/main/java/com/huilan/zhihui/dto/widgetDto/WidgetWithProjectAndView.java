

package com.huilan.zhihui.dto.widgetDto;

import com.huilan.zhihui.model.Project;
import com.huilan.zhihui.model.View;
import com.huilan.zhihui.model.Widget;
import lombok.Data;

@Data
public class WidgetWithProjectAndView extends Widget {
    private Project project;
    private View view;
}
