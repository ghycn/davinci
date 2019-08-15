

package com.huilan.zhihui.service;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.dto.projectDto.ProjectDetail;
import com.huilan.zhihui.dto.viewDto.ViewExecuteParam;
import com.huilan.zhihui.dto.widgetDto.WidgetCreate;
import com.huilan.zhihui.dto.widgetDto.WidgetUpdate;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.model.Widget;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WidgetService extends CheckEntityService {
    List<Widget> getWidgets(Long projectId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    Widget createWidget(WidgetCreate widgetCreate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean updateWidget(WidgetUpdate widgetUpdate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean deleteWidget(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    String shareWidget(Long id, User user, String username) throws NotFoundException, UnAuthorizedExecption, ServerException;

    Widget getWidget(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    String generationFile(Long id, ViewExecuteParam executeParam, User user, String type) throws NotFoundException, ServerException, UnAuthorizedExecption;

    File writeExcel(Set<Widget> widgets, ProjectDetail projectDetail, Map<Long, ViewExecuteParam> executeParamMap, String filePath, User user, boolean containType) throws Exception;
}
