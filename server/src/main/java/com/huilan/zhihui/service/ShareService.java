

package com.huilan.zhihui.service;

import com.huilan.core.exception.ForbiddenExecption;
import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.core.model.Paginate;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.dto.shareDto.ShareDashboard;
import com.huilan.zhihui.dto.shareDto.ShareDisplay;
import com.huilan.zhihui.dto.shareDto.ShareWidget;
import com.huilan.zhihui.dto.userDto.UserLogin;
import com.huilan.zhihui.dto.viewDto.DistinctParam;
import com.huilan.zhihui.dto.viewDto.ViewExecuteParam;
import com.huilan.zhihui.model.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Map;

public interface ShareService {
    ShareWidget getShareWidget(String token, User user) throws NotFoundException, ServerException, ForbiddenExecption, UnAuthorizedExecption;

    String generateShareToken(Long shareEntityId, String username, Long userId) throws ServerException;

    User shareLogin(String token, UserLogin userLogin) throws NotFoundException, ServerException, UnAuthorizedExecption;

    ShareDisplay getShareDisplay(String token, User user) throws NotFoundException, ServerException, ForbiddenExecption, UnAuthorizedExecption;

    ShareDashboard getShareDashboard(String token, User user) throws NotFoundException, ServerException, ForbiddenExecption, UnAuthorizedExecption;

    Paginate<Map<String, Object>> getShareData(String token, ViewExecuteParam executeParam, User user) throws NotFoundException, ServerException, ForbiddenExecption, UnAuthorizedExecption, SQLException;

    String generationShareDataCsv(ViewExecuteParam executeParam, User user, String token) throws NotFoundException, ServerException, ForbiddenExecption, UnAuthorizedExecption;

    ResultMap getDistinctValue(String token, Long viewId, DistinctParam param, User user, HttpServletRequest request);
}
