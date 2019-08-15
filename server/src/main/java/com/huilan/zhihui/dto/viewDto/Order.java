

package com.huilan.zhihui.dto.viewDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Order {
    @NotBlank(message = "Invalid order column")
    private String column;

    private String direction = "ASC";

    public Order(String column, String direction) {
        this.column = column;
        this.direction = direction;
    }

    public Order() {
    }
}
