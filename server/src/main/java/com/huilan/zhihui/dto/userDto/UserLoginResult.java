

package com.huilan.zhihui.dto.userDto;

import com.huilan.zhihui.model.User;
import lombok.Data;

@Data
public class UserLoginResult extends UserBaseInfo {

    private String email;

    private Boolean admin;

    private String name;

    private String description;

    private String department;


    public UserLoginResult(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.admin = user.getAdmin();
        this.name = user.getName();
        this.description = user.getDescription();
        this.department = user.getDepartment();
        this.avatar = user.getAvatar();
    }

    public UserLoginResult() {
    }
}