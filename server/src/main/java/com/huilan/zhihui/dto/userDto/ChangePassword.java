

package com.huilan.zhihui.dto.userDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "Invalid password")
public class ChangePassword {

    @NotBlank(message = "password cannot be EMPTY")
    private String oldPassword;

    @NotBlank(message = "new password cannot be EMPTY")
    private String password;
}
