

package com.huilan.zhihui.model;

import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

@Data
public class Widget extends RecordInfo<Widget> {
    private Long id;

    private String name;

    private String description;

    private Long viewId;

    private Long projectId;

    private Long type;

    private Boolean publish = false;

    private String config;

}