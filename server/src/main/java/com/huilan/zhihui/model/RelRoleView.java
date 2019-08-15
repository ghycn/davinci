

package com.huilan.zhihui.model;

import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

@Data
public class RelRoleView extends RecordInfo<RelRoleView> {
    private Long viewId;
    private Long roleId;
    private String rowAuth;
    private String columnAuth;

    public RelRoleView(Long viewId, Long roleId, String rowAuth, String columnAuth) {
        this.viewId = viewId;
        this.roleId = roleId;
        this.rowAuth = rowAuth;
        this.columnAuth = columnAuth;
    }

    public RelRoleView() {
    }
}
