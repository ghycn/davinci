

package com.huilan.zhihui.service;

import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.core.enums.CheckEntityEnum;

import javax.servlet.http.HttpServletRequest;

public interface CheckService {
    ResultMap checkSource(String name, Long id, CheckEntityEnum checkEntityEnum, Long scopId, HttpServletRequest request);
}
