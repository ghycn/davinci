

package com.huilan.zhihui.controller;

import com.huilan.core.utils.TokenUtils;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.dto.userDto.UserLogin;
import com.huilan.zhihui.dto.userDto.UserLoginResult;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;


@Api(tags = "login", basePath = Constants.BASE_API_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiResponses({
        @ApiResponse(code = 400, message = "pwd is wrong"),
        @ApiResponse(code = 404, message = "user not found")
})
@RestController
@Slf4j
@RequestMapping(value = Constants.BASE_API_PATH + "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtils tokenUtils;

    /**
     * 登录
     *
     * @param userLogin
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "Login into the server and return token")
    @PostMapping
    public ResponseEntity login(@Valid @RequestBody UserLogin userLogin, @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap().fail().message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        User user = userService.userLogin(userLogin);
        if (!user.getActive()) {
            log.info("this user is not active： {}", userLogin.getUsername());
            ResultMap resultMap = new ResultMap().failWithToken(tokenUtils.generateToken(user)).message("this user is not active");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        return ResponseEntity.ok(new ResultMap().success(tokenUtils.generateToken(user)).payload(new UserLoginResult(user)));
    }
}
