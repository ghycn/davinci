

package com.huilan.zhihui.model;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.huilan.zhihui.common.model.RecordInfo;
import lombok.Data;

import java.util.List;

@Data
public class View extends RecordInfo<View> {
    private Long id;

    private String name;

    private String description;

    private Long projectId;

    private Long sourceId;

    private String sql;

    private String model;

    private String variable;

    private String config;

    @Override
    public String toString() {
        return "View{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", projectId=" + projectId +
                ", sourceId=" + sourceId +
                ", sql='" + sql + '\'' +
                ", model='" + model + '\'' +
                ", variable='" + variable + '\'' +
                ", config='" + config + '\'' +
                '}';
    }


    @JSONField(serialize = false)
    public List<SqlVariable> getVariables() {
        if (StringUtils.isEmpty(variable) || StringUtils.isEmpty(sql)) {
            return null;
        }

        try {
            return JSONObject.parseArray(variable, SqlVariable.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}