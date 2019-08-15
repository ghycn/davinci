

package com.huilan.zhihui.dto.userDto;

import com.huilan.zhihui.core.common.Constants;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserLogin {

    @NotBlank(message = "username cannot be EMPTY")
    private String username;

    @NotBlank(message = "password cannot be EMPTY")
    @Pattern(regexp = Constants.REG_USER_PASSWORD, message = "密码长度为6-20位")
    private String password;
}
