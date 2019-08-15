

package com.huilan.zhihui.model;

import lombok.Data;

@Data
public class LdapPerson {

    /**
     * 姓名
     */
    private String name;

    /**
     * 用户名
     */
    private String sAMAccountName;

    /**
     * 邮箱
     */
    private String email;
}
