

package com.huilan.zhihui.dto.starDto;

import com.huilan.zhihui.dto.userDto.UserBaseInfo;
import lombok.Data;

import java.util.Date;

@Data
public class StarUser extends UserBaseInfo {

    private Date starTime;
}
