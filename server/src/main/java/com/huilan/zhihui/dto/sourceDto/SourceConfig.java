

package com.huilan.zhihui.dto.sourceDto;

import com.huilan.zhihui.model.Source;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SourceConfig {

    private String username;

    private String password;

    @NotBlank(message = "connection url cannot be EMPTY")
    private String url;

    private String parameters;


    public SourceConfig(Source source) {
        this.username = source.getUsername();
        this.password = source.getPassword();
        this.url = source.getJdbcUrl();
        this.parameters = source.getConfigParams();
    }

    public SourceConfig(String username, String password, String url, String parameters) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.parameters = parameters;
    }

    public SourceConfig() {
    }
}
