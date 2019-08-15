

package com.huilan.zhihui.service;

import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.model.User;

import javax.servlet.http.HttpServletRequest;

public interface StarService {

    ResultMap starAndUnstar(String target, Long targetId, User user, HttpServletRequest request);


    ResultMap getStarListByUser(String target, User user, HttpServletRequest request);


    ResultMap getStarUserListByTarget(String target, Long targetId, HttpServletRequest request);

}
