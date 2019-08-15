

package com.huilan.zhihui.service;

import com.huilan.core.exception.ServerException;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.model.Platform;
import com.huilan.zhihui.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

public interface AuthPlatformService {

    ConcurrentHashMap map = new ConcurrentHashMap();

    /**
     * 获取用户可见project 下的 可见vizs
     *
     * @param
     * @return
     */
    ResultMap getProjectVizs(String authCode, String email);

    String getAuthShareToken(Platform platform, String type, Long id, User user) throws ServerException;

    boolean verifyUser(String token, User user);

    boolean verifyPlatform(String token, Platform platform);

    ResultMap getShareContent(String token, User user, Platform platform, HttpServletRequest request);
}
