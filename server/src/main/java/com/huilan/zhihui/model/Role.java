

package com.huilan.zhihui.model;

import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

@Data
public class Role extends RecordInfo<Role> {
    private Long id;

    private Long orgId;

    private String name;

    private String description;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", orgId=" + orgId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}