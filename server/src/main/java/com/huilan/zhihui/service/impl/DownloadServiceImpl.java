

package com.huilan.zhihui.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.core.utils.CollectionUtils;
import com.huilan.core.utils.DateUtils;
import com.huilan.core.utils.TokenUtils;
import com.huilan.zhihui.core.enums.ActionEnum;
import com.huilan.zhihui.core.enums.DownloadTaskStatus;
import com.huilan.zhihui.core.enums.DownloadType;
import com.huilan.zhihui.dao.*;
import com.huilan.zhihui.dto.projectDto.ProjectDetail;
import com.huilan.zhihui.dto.projectDto.ProjectPermission;
import com.huilan.zhihui.dto.viewDto.DownloadViewExecuteParam;
import com.huilan.zhihui.dto.viewDto.ViewExecuteParam;
import com.huilan.zhihui.model.*;
import com.huilan.zhihui.service.DownloadService;
import com.huilan.zhihui.service.ProjectService;
import com.huilan.zhihui.service.excel.ExecutorUtil;
import com.huilan.zhihui.service.excel.MsgWrapper;
import com.huilan.zhihui.service.excel.WidgetContext;
import com.huilan.zhihui.service.excel.WorkBookContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.huilan.core.consts.Consts.UNDERLINE;


/**
 * Created by IntelliJ IDEA.
 *
 * @Author daemon
 * @Date 19/5/28 10:04
 * To change this template use File | Settings | File Templates.
 */
@Service
@Slf4j
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private DownloadRecordMapper downloadRecordMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private WidgetMapper widgetMapper;

    @Autowired
    private MemDashboardWidgetMapper memDashboardWidgetMapper;

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<DownloadRecord> queryDownloadRecordPage(Long userId) {
        return downloadRecordMapper.getDownloadRecordsByUser(userId);
    }

    @Override
    public DownloadRecord downloadById(Long id, String token) throws UnAuthorizedExecption {
        if (StringUtils.isEmpty(token)) {
            throw new UnAuthorizedExecption();
        }

        String username = tokenUtils.getUsername(token);
        if (StringUtils.isEmpty(username)) {
            throw new UnAuthorizedExecption();
        }

        User user = userMapper.selectByUsername(username);
        if (null == user) {
            throw new UnAuthorizedExecption();
        }

        DownloadRecord record = downloadRecordMapper.getById(id);

        if (!record.getUserId().equals(user.getId())) {
            throw new UnAuthorizedExecption();
        }

        record.setLastDownloadTime(new Date());
        record.setStatus(DownloadTaskStatus.DOWNLOADED.getStatus());
        downloadRecordMapper.updateById(record);
        return record;
    }

    @Override
    public Boolean submit(DownloadType type, Long id, User user, List<DownloadViewExecuteParam> params) {
        try {
            List<WidgetContext> widgetList = Lists.newArrayList();
            switch (type) {
                case Widget:
                    Widget widget = widgetMapper.getById(id);
                    if (widget != null) {
                        ViewExecuteParam executeParam = null;
                        if (!CollectionUtils.isEmpty(params)) {
                            try {
                                executeParam = params.stream().filter(p -> null != p.getParam() && p.getId().equals(widget.getId())).findFirst().get().getParam();
                            } catch (Exception e) {
                            }
                        }
                        widgetList.add(new WidgetContext(widget, null, null, executeParam));
                    }
                    break;
                case DashBoard:
                    List<WidgetContext> widgets = getWidgetContextListByDashBoardId(Lists.newArrayList(id), params);
                    if (!CollectionUtils.isEmpty(widgets)) {
                        widgetList.addAll(widgets);
                    }
                    break;
                case DashBoardFolder:
                    List<WidgetContext> widgets1 = getWidgetContextListByFolderDashBoardId(id);
                    if (!CollectionUtils.isEmpty(widgets1)) {
                        widgetList.addAll(widgets1);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("unsupported DownloadType=" + type.name());
            }
            if (CollectionUtils.isEmpty(widgetList)) {
                throw new IllegalArgumentException("has no widget to download");
            }
            for (WidgetContext context : widgetList) {
                ProjectDetail projectDetail = projectService.getProjectDetail(context.getWidget().getProjectId(), user, false);
                ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
                //校验权限
                if (!projectPermission.getDownloadPermission()) {
                    log.info("user {} have not permisson to download the widget {}", user.getUsername(), id);
                    throw new UnAuthorizedExecption("you have not permission to download the widget");
                }
                context.setIsMaintainer(projectService.isMaintainer(projectDetail, user));
            }
            String fileName;
            switch (type) {
                case Widget:
                    Widget widget = widgetMapper.getById(id);
                    fileName = widget.getName();
                    break;
                case DashBoard:
                case DashBoardFolder:
                    Dashboard dashboard = dashboardMapper.getById(id);
                    fileName = dashboard.getName();
                    break;
                default:
                    throw new IllegalArgumentException("unsupported DownloadType=" + type.name());
            }
            DownloadRecord record = new DownloadRecord();
            record.setName(fileName + UNDERLINE + DateUtils.toyyyyMMddHHmmss(System.currentTimeMillis()));
            record.setUserId(user.getId());
            record.setCreateTime(new Date());
            record.setStatus(DownloadTaskStatus.PROCESSING.getStatus());
            downloadRecordMapper.insert(record);
            ExecutorUtil.submitWorkbookTask(WorkBookContext.newWorkBookContext(new MsgWrapper(record, ActionEnum.DOWNLOAD, record.getId()), widgetList, user));
        } catch (Exception e) {
            log.error("submit download task error,e=", e);
            return false;
        }
        return true;
    }


    private List<WidgetContext> getWidgetContextListByDashBoardId(List<Long> dashboardIds, List<DownloadViewExecuteParam> params) {
        List<WidgetContext> widgetList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(dashboardIds)) {
            return widgetList;
        }
        for (Long dashboardId : dashboardIds) {
            if (dashboardId == null || dashboardId.longValue() <= 0) {
                continue;
            }
            Dashboard dashboard = dashboardMapper.getById(dashboardId);
            if (dashboard == null) {
                continue;
            }
            List<MemDashboardWidget> mdw = memDashboardWidgetMapper.getByDashboardId(dashboardId);
            if (CollectionUtils.isEmpty(mdw)) {
                continue;
            }
            Set<Long> widgetIds = mdw.stream().filter(y -> y != null).map(y -> y.getWidgetId()).collect(Collectors.toSet());
            List<Widget> widgets = widgetMapper.getByIds(widgetIds);
            if (!CollectionUtils.isEmpty(widgets)) {
                Map<Long, MemDashboardWidget> map = mdw.stream().collect(Collectors.toMap(o -> o.getWidgetId(), o -> o));
                widgets.stream().forEach(t -> {
                    ViewExecuteParam executeParam = null;
                    if (!CollectionUtils.isEmpty(params) && map.containsKey(t.getId())) {
                        MemDashboardWidget memDashboardWidget = map.get(t.getId());
                        try {
                            executeParam = params.stream().filter(p -> null != p.getParam() && p.getId().equals(memDashboardWidget.getId())).findFirst().get().getParam();
                        } catch (Exception e) {
                        }
                    }
                    widgetList.add(new WidgetContext(t, dashboard, map.get(t.getId()), executeParam));
                });
            }
        }
        return widgetList;
    }

    private List<WidgetContext> getWidgetContextListByFolderDashBoardId(Long id) {
        List<WidgetContext> widgetList = Lists.newArrayList();
        if (id == null || id.longValue() < 0L) {
            return widgetList;
        }
        List<Dashboard> dashboardList = dashboardMapper.getSubDashboardById(id);
        if (CollectionUtils.isEmpty(dashboardList)) {
            return widgetList;
        }
        List<Long> dashboardIds = dashboardList.stream().filter(x -> x != null).map(x -> x.getId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dashboardIds)) {
            return widgetList;
        }
        return getWidgetContextListByDashBoardId(dashboardIds, null);
    }


}
