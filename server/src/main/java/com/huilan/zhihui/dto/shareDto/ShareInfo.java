

package com.huilan.zhihui.dto.shareDto;

import com.huilan.zhihui.model.User;
import lombok.Data;

@Data
public class ShareInfo {
    /**
     * 分享实体Id
     */
    private Long shareId;

    /**
     * 分享人
     */
    private User shareUser;

    /**
     * 被分享人用户名
     */
    private String sharedUserName;


    public ShareInfo() {
    }

    public ShareInfo(Long shareId, User shareUser, String sharedUserName) {
        this.shareId = shareId;
        this.shareUser = shareUser;
        this.sharedUserName = sharedUserName;
    }
}
