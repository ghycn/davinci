

package com.huilan.core.utils;

import com.alibaba.druid.util.StringUtils;
import com.huilan.core.consts.Consts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerUtils {
    @Value("${server.protocol:http}")
    private String protocol;

    @Value("${server.address}")
    private String address;

    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${server.access.address:}")
    private String accessAddress;

    @Value("${server.access.port:}")
    private String accessPort;

    public String getHost() {
        String pro = protocol.trim().toLowerCase();
        String accAddress = StringUtils.isEmpty(accessAddress) ? address : accessAddress;
        String accPort = StringUtils.isEmpty(accessPort) ? port : accessPort;

        if (pro.equals(Consts.HTTP_PROTOCOL) && "80".equals(accPort)) {
            accPort = null;
        }

        if (pro.equals(Consts.HTTPS_PROTOCOL) && "443".equals(accPort)) {
            accPort = null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(pro).append(Consts.PROTOCOL_SEPARATOR).append(accAddress);
        if (!StringUtils.isEmpty(accPort)) {
            sb.append(":" + accPort);
        }

        if (!StringUtils.isEmpty(contextPath)) {
            contextPath.replaceAll(Consts.SLASH, Consts.EMPTY);
            sb.append(Consts.SLASH);
            sb.append(contextPath);
        }

        return sb.toString();
    }
}
