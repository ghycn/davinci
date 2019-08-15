

package com.huilan.zhihui.dto.viewDto;

import com.huilan.zhihui.dto.sourceDto.SourceBaseInfo;
import com.huilan.zhihui.model.RelRoleView;
import com.huilan.zhihui.model.View;
import lombok.Data;

import java.util.List;

@Data
public class ViewWithSourceBaseInfo extends View {
    private SourceBaseInfo source;

    private List<RelRoleView> roles;
}
