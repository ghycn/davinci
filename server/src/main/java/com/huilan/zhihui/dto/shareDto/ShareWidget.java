

package com.huilan.zhihui.dto.shareDto;

import lombok.Data;

@Data
public class ShareWidget {
    private Long id;
    private String name;
    private String description;
    private Long type;
    private String config;
    private String dataToken;
    private String model;
    private Long viewId;
}
