

package com.huilan.zhihui.dto.userDto;

import com.huilan.zhihui.core.common.Constants;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NotNull(message = "user info cannot be null")
public class UserRegist {
    @NotBlank(message = "username cannot be EMPTY")
    private String username;

    @NotBlank(message = "email cannot be EMPTY")
    @Pattern(regexp = Constants.REG_EMAIL_FORMAT, message = "invalid email format")
    private String email;

    @NotBlank(message = "password cannot be EMPTY")
    @Pattern(regexp = Constants.REG_USER_PASSWORD, message = "密码长度为6-20位")
    private String password;

    @Override
    public String toString() {
        return "UserRegist{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
