

package com.huilan.zhihui.dto.sourceDto;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SourceTest {

    private String username;
    private String password;

    @NotBlank(message = "connection url cannot be EMPTY")
    private String url;
}
