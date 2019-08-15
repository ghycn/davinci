

package com.huilan.zhihui.core.enums;

public enum CheckEntityEnum {
    USER("user", "userService", "User"),
    PROJECT("project", "projectService", "Project"),
    ORGANIZATION("organization", "organizationService", "Organization"),
    SOURCE("source", "sourceService", "Source"),
    VIEW("view", "viewService", "View"),
    WIDGET("widget", "widgetService", "Widget"),
    DISPLAY("display", "displayService", "Display"),
    DASHBOARD("dashboard", "dashboardService", "Dashboard"),
    DASHBOARDPORTAL("dashboardPortal", "dashboardPortalService", "DashboardPortal"),
    CRONJOB("cronJob", "cronJobService", "CronJob");

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
