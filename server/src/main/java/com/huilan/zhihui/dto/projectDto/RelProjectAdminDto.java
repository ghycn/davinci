

package com.huilan.zhihui.dto.projectDto;

import com.alibaba.druid.util.StringUtils;
import com.huilan.zhihui.dto.userDto.UserBaseInfo;
import com.huilan.zhihui.model.User;
import lombok.Data;

@Data
public class RelProjectAdminDto {
    private Long id;

    private UserBaseInfo user;

    public RelProjectAdminDto(Long id, User user) {
        this.id = id;
        UserBaseInfo userBaseInfo = new UserBaseInfo();
        userBaseInfo.setId(user.getId());
        userBaseInfo.setAvatar(user.getAvatar());
        userBaseInfo.setUsername(StringUtils.isEmpty(user.getName()) ? user.getUsername() : user.getName());
        this.user = userBaseInfo;
    }


    public RelProjectAdminDto() {
    }
}
