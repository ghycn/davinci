

package com.huilan.zhihui.dto.projectDto;

import com.alibaba.fastjson.annotation.JSONField;
import com.huilan.zhihui.core.enums.UserPermissionEnum;
import lombok.Data;

@Data
public class ProjectPermission {

    private Short sourcePermission = 1;

    private Short viewPermission = 1;

    private Short widgetPermission = 1;

    private Short vizPermission = 1;

    private Short schedulePermission = 1;

    private Boolean sharePermission = false;

    private Boolean downloadPermission = false;

    @JSONField(serialize = false)
    private boolean isProjectMaintainer = false;


    public ProjectPermission() {
    }

    public ProjectPermission(Short permission) {
        this.sourcePermission = permission;
        this.viewPermission = permission;
        this.widgetPermission = permission;
        this.vizPermission = permission;
        this.schedulePermission = permission;
    }

    public static ProjectPermission previewPermission() {
        ProjectPermission permission = new ProjectPermission();
        permission.setVizPermission((short) 1);
        permission.setWidgetPermission((short) 0);
        permission.setViewPermission((short) 0);
        permission.setSourcePermission((short) 0);
        permission.setSchedulePermission((short) 0);
        permission.setDownloadPermission(false);
        permission.setSharePermission(false);
        return permission;
    }

    public static ProjectPermission adminPermission() {
        ProjectPermission permission = new ProjectPermission(UserPermissionEnum.DELETE.getPermission());
        permission.setDownloadPermission(true);
        permission.setSharePermission(true);
        permission.isProjectMaintainer = true;
        return permission;
    }
}
