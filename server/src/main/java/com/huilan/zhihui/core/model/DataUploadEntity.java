

package com.huilan.zhihui.core.model;

import com.huilan.core.model.QueryColumn;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class DataUploadEntity {

    private Set<QueryColumn> headers;

    private List<Map<String, Object>> values;
}
