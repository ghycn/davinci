

package com.huilan.zhihui.dto.sourceDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NotNull(message = "source info cannot be null")
public class SourceInfo {

    @Min(value = 1L, message = "Invalid source id")
    private Long id;

    @NotBlank(message = "source name cannot be EMPTY")
    private String name;

    private String description;

    @NotBlank(message = "source type cannot be EMPTY")
    private String type;

    @NotNull(message = "source config cannot be null")
    private SourceConfig config;
}
