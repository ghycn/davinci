

package com.huilan.zhihui.model;

import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

@Data
public class DashboardPortal extends RecordInfo<DashboardPortal> {
    private Long id;

    private String name;

    private String description;

    private Long projectId;

    private String avatar;

    private Boolean publish;

}