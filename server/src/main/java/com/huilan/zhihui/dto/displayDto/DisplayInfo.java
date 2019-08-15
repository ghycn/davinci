

package com.huilan.zhihui.dto.displayDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NotNull(message = "display info cannot be null")
public class DisplayInfo {

    @NotBlank(message = "display name cannot be EMPTY")
    private String name;

    private String description;

    @Min(value = 1L, message = "Invalid project id")
    private Long projectId;

    private String avatar;

    private Boolean publish = false;

    private List<Long> roleIds;
}
