

package com.huilan.zhihui.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.huilan.core.utils.TokenUtils;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.dao.ProjectMapper;
import com.huilan.zhihui.dao.StarMapper;
import com.huilan.zhihui.dto.projectDto.ProjectWithCreateBy;
import com.huilan.zhihui.dto.starDto.StarUser;
import com.huilan.zhihui.model.Project;
import com.huilan.zhihui.model.Star;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class StarServiceImpl implements StarService {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private StarMapper starMapper;

    @Override
    @Transactional
    public ResultMap starAndUnstar(String target, Long targetId, User user, HttpServletRequest request) {
        ResultMap resultMap = new ResultMap(tokenUtils);

        if (!StringUtils.isEmpty(target)) {
            if (Constants.STAR_TARGET_PROJECT.equals(target)) {
                Project project = projectMapper.getById(targetId);
                if (null == project) {
                    return resultMap.failAndRefreshToken(request).message("project not found");
                }

                Star star = starMapper.select(user.getId(), targetId, target);
                if (null == star) {
                    //star
                    star = new Star(target, targetId, user.getId());
                    int i = starMapper.insert(star);
                    if (i > 0) {
                        synchronized (project) {
                            projectMapper.starNumAdd(project.getId());
                        }
                        return resultMap.successAndRefreshToken(request);
                    }
                } else {
                    //unstar
                    int i = starMapper.deleteById(star.getId());
                    if (i > 0) {
                        synchronized (project) {
                            projectMapper.starNumReduce(project.getId());
                        }
                    }
                    return resultMap.successAndRefreshToken(request);
                }
            }
        }

        return resultMap.failAndRefreshToken(request);
    }

    @Override
    public ResultMap getStarListByUser(String target, User user, HttpServletRequest request) {
        ResultMap resultMap = new ResultMap(tokenUtils);

        if (!StringUtils.isEmpty(target)) {
            if (Constants.STAR_TARGET_PROJECT.equals(target)) {
                List<ProjectWithCreateBy> starProjectList = starMapper.getStarProjectListByUser(user.getId(), target);
                return resultMap.successAndRefreshToken(request).payloads(starProjectList);
            }
        }
        return resultMap.failAndRefreshToken(request);
    }

    @Override
    public ResultMap getStarUserListByTarget(String target, Long targetId, HttpServletRequest request) {
        ResultMap resultMap = new ResultMap(tokenUtils);
        if (!StringUtils.isEmpty(target)) {
            if (Constants.STAR_TARGET_PROJECT.equals(target)) {
                Project project = projectMapper.getById(targetId);
                if (null == project) {
                    return resultMap.failAndRefreshToken(request).message("project not found");
                }

                List<StarUser> starUserList = starMapper.getStarUserListByTarget(targetId, target);
                return resultMap.successAndRefreshToken(request).payloads(starUserList);
            }
        }

        return resultMap.failAndRefreshToken(request);
    }
}
