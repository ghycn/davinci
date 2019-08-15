

package com.huilan.zhihui.core.service;

import com.huilan.zhihui.model.Platform;
import com.huilan.zhihui.model.User;

import java.util.Map;

public interface AuthenticationService {

    User checkUser(Platform platform, Map<String, String[]> parameter) throws RuntimeException;
}
