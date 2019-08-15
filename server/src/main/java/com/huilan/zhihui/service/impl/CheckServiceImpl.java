

package com.huilan.zhihui.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.huilan.core.utils.TokenUtils;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.core.enums.CheckEntityEnum;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.service.CheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class CheckServiceImpl implements CheckService {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private TokenUtils tokenUtils;

    @Override
    public ResultMap checkSource(String name, Long id, CheckEntityEnum checkEntityEnum, Long scopeId, HttpServletRequest request) {
        ResultMap resultMap = new ResultMap(tokenUtils);

        if (StringUtils.isEmpty(name)) {
            log.info("the name of entity({}) ie EMPTY", checkEntityEnum.getClazz());
            if (checkEntityEnum.equals(CheckEntityEnum.USER)) {
                return resultMap.fail().message("name is EMPTY");
            }
            return resultMap.failAndRefreshToken(request).message("name is EMPTY");
        }

        try {
            String clazz = Class.forName(checkEntityEnum.getClazz()).getTypeName();
            if (StringUtils.isEmpty(clazz)) {
                log.info("not found entity type : {}", checkEntityEnum.getClazz());
                if (checkEntityEnum.equals(CheckEntityEnum.USER)) {
                    return resultMap.fail().message("not supported entity type");
                }
                return resultMap.failAndRefreshToken(request).message("not supported entity type");
            }
        } catch (ClassNotFoundException e) {
            log.error("not supported entity type : {}", checkEntityEnum.getClazz());
            if (checkEntityEnum.equals(CheckEntityEnum.USER)) {
                resultMap.fail().message("not supported entity type");
            }
            return resultMap.failAndRefreshToken(request).message("not supported entity type");
        }

        CheckEntityService checkEntityService = (CheckEntityService) beanFactory.getBean(checkEntityEnum.getService());
        if (checkEntityService.isExist(name, id, scopeId)) {
            if (checkEntityEnum.equals(CheckEntityEnum.USER)) {
                return resultMap.fail().message("the current " + checkEntityEnum.getSource() + " name is already taken");
            }
            return resultMap.failAndRefreshToken(request)
                    .message("the current " + checkEntityEnum.getSource() + " name is already taken");
        } else {
            if (checkEntityEnum == CheckEntityEnum.USER) {
                return resultMap.success();
            }
            return resultMap.successAndRefreshToken(request);
        }
    }
}
