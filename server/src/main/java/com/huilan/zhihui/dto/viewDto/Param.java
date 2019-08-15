

package com.huilan.zhihui.dto.viewDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Param {

    @NotBlank(message = "Invald parameter name")
    private String name;

    @NotBlank(message = "Invalid parameter value")
    private String value;

    public Param() {
    }

    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
