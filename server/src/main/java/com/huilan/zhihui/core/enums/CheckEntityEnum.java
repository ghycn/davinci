

package com.huilan.zhihui.core.enums;

public enum CheckEntityEnum {
    USER("user", "userService", "com.huilan.zhihui.model.User"),
    PROJECT("project", "projectService", "com.huilan.zhihui.model.Project"),
    ORGANIZATION("organization", "organizationService", "com.huilan.zhihui.model.Organization"),
    SOURCE("source", "sourceService", "com.huilan.zhihui.model.Source"),
    VIEW("view", "viewService", "com.huilan.zhihui.model.View"),
    WIDGET("widget", "widgetService", "com.huilan.zhihui.model.Widget"),
    DISPLAY("display", "displayService", "com.huilan.zhihui.model.Display"),
    DASHBOARD("dashboard", "dashboardService", "com.huilan.zhihui.model.Dashboard"),
    DASHBOARDPORTAL("dashboardPortal", "dashboardPortalService", "com.huilan.zhihui.model.DashboardPortal"),
    CRONJOB("cronJob", "cronJobService", "com.huilan.zhihui.model.CronJob");

    private String source;
    private String service;
    private String clazz;


    CheckEntityEnum(String source, String service, String clazz) {
        this.source = source;
        this.service = service;
        this.clazz = clazz;
    }

    public static CheckEntityEnum sourceOf(String source) {
        for (CheckEntityEnum sourceEnum : values()) {
            if (sourceEnum.source.equals(source)) {
                return sourceEnum;
            }
        }
        return null;
    }

    public String getService() {
        return service;
    }

    public String getClazz() {
        return clazz;
    }

    public String getSource() {
        return source;
    }
}
