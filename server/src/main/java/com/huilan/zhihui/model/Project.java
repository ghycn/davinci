

package com.huilan.zhihui.model;

import lombok.Data;

@Data
public class Project {
    private Long id;

    private String name;

    private String description;

    private String pic;

    private Long orgId;

    private Long userId;

    private Integer starNum = 0;

    private Boolean visibility = true;

    private Boolean isTransfer = false;

    private Long initialOrgId;

    public Project() {
    }

    public Project(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pic='" + pic + '\'' +
                ", orgId=" + orgId +
                ", userId=" + userId +
                ", starNum=" + starNum +
                ", visibility=" + visibility +
                ", isTransfer=" + isTransfer +
                ", initialOrgId=" + initialOrgId +
                '}';
    }

    public String baseInfoToString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                '}';
    }
}