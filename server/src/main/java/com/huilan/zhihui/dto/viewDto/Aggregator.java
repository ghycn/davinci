

package com.huilan.zhihui.dto.viewDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Aggregator {

    @NotBlank(message = "Invalid aggregator column")
    private String column;

    private String func;

    public Aggregator() {
    }

    public Aggregator(String column, String func) {
        this.column = column;
        this.func = func;
    }
}
