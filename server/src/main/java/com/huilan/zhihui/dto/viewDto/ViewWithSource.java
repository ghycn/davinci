

package com.huilan.zhihui.dto.viewDto;

import com.huilan.zhihui.model.Source;
import com.huilan.zhihui.model.View;
import lombok.Data;

@Data
public class ViewWithSource extends View {
    private Source source;

    @Override
    public String toString() {
        return super.toString();
    }
}
