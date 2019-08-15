

package com.huilan.zhihui.model;

import com.huilan.core.model.TokenDetail;
import lombok.Data;

import java.util.Date;

@Data
public class User extends TokenDetail {
    private Long id;

    private String email;

    private Boolean admin = true;

    private Boolean active = false;

    private String name;

    private String description;

    private String department;

    private String avatar;

    private Date createTime = new Date();

    private Long createBy = 0L;

    private Date updateTime;

    private Long updateBy;

    public User() {
    }

    public User(LdapPerson ldapPerson) {
        this.username = ldapPerson.getSAMAccountName();
        this.email = ldapPerson.getEmail();
        this.name = ldapPerson.getName();
    }
}