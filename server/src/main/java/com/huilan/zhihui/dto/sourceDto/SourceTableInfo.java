

package com.huilan.zhihui.dto.sourceDto;

import com.huilan.core.model.TableInfo;
import lombok.Data;

@Data
public class SourceTableInfo extends TableInfo {
    private Long sourceId;
    private String tableName;
}
