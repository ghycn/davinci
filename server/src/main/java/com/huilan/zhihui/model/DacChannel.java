

package com.huilan.zhihui.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class DacChannel {

    private String name;

    @JSONField(name = "base-url")
    private String baseUrl;

    @JSONField(name = "auth-code")
    private String authCode;
}
