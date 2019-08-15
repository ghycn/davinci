

package com.huilan.zhihui.dto.sourceDto;

import lombok.Data;

import java.util.List;

@Data
public class SourceCatalogInfo {
    private Long sourceId;
    private List<String> databases;

    public SourceCatalogInfo(Long sourceId, List<String> databases) {
        this.sourceId = sourceId;
        this.databases = databases;
    }
}
