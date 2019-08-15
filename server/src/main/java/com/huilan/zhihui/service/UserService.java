

package com.huilan.zhihui.service;


import com.huilan.core.exception.ServerException;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.dto.userDto.UserBaseInfo;
import com.huilan.zhihui.dto.userDto.UserLogin;
import com.huilan.zhihui.dto.userDto.UserRegist;
import com.huilan.zhihui.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService extends CheckEntityService {

    User getByUsername(String username);

    User userLogin(UserLogin userLogin) throws ServerException;

    List<UserBaseInfo> getUsersByKeyword(String keyword, User user, Long orgId);

    boolean updateUser(User user) throws ServerException;

    User regist(UserRegist userRegist) throws ServerException;

//    ResultMap activateUser(User user, String token, HttpServletRequest request);

    boolean sendMail(String email, User user) throws ServerException;

    ResultMap changeUserPassword(User user, String oldPassword, String password, HttpServletRequest request);

    ResultMap uploadAvatar(User user, MultipartFile file, HttpServletRequest request);

    ResultMap activateUserNoLogin(String token, HttpServletRequest request);

    ResultMap getUserProfile(Long id, User user, HttpServletRequest request);
}
