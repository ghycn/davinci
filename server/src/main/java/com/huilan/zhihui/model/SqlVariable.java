

package com.huilan.zhihui.model;

import lombok.Data;

import java.util.List;

@Data
public class SqlVariable {
    private String name;
    private String type;           //变量类型 query/auth
    private String valueType;      //变量值类型 string/number/boolean/date
    private boolean udf;
    private List<Object> defaultValues;   //默认值

    private SqlVariableChannel channel;
}


