

package com.huilan.zhihui.service;

import com.huilan.zhihui.core.enums.DownloadType;
import com.huilan.zhihui.dto.viewDto.DownloadViewExecuteParam;
import com.huilan.zhihui.model.DownloadRecord;
import com.huilan.zhihui.model.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author daemon
 * @Date 19/5/28 09:44
 * To change this template use File | Settings | File Templates.
 */
public interface DownloadService {


    /**
     * 获取下载列表
     *
     * @param userId
     * @return
     */
    List<DownloadRecord> queryDownloadRecordPage(Long userId);


    /**
     * 下载
     *
     * @param id
     * @param token
     * @return
     */
    DownloadRecord downloadById(Long id, String token);


    /**
     * 提交下载任务
     *
     * @param type
     * @param id
     * @param user
     * @param params
     * @return
     */
    Boolean submit(DownloadType type, Long id, User user, List<DownloadViewExecuteParam> params);
}
