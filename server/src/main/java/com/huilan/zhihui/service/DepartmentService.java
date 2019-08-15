

package com.huilan.zhihui.service;

import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.model.User;

import javax.servlet.http.HttpServletRequest;

public interface DepartmentService {

    ResultMap getdepartments(Long projectId, String type, User user, HttpServletRequest request);
}
