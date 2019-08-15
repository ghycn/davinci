

package com.huilan.zhihui.model;

import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

@Data
public class RelRolePortal extends RecordInfo<RelRolePortal> {
    private Long roleId;

    private Long portalId;

    private Boolean visible = false; // 可见/不可见  true/false

    public RelRolePortal(Long portalId, Long roleId) {
        this.roleId = roleId;
        this.portalId = portalId;
    }

    public RelRolePortal() {
    }

}