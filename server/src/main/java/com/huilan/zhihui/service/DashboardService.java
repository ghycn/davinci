

package com.huilan.zhihui.service;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.dto.dashboardDto.*;
import com.huilan.zhihui.dto.roleDto.VizVisibility;
import com.huilan.zhihui.model.Dashboard;
import com.huilan.zhihui.model.MemDashboardWidget;
import com.huilan.zhihui.model.Role;
import com.huilan.zhihui.model.User;

import java.util.List;

public interface DashboardService extends CheckEntityService {

    List<Dashboard> getDashboards(Long portalId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    DashboardWithMem getDashboardMemWidgets(Long portalId, Long dashboardId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    Dashboard createDashboard(DashboardCreate dashboardCreate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    void updateDashboards(Long portalId, DashboardDto[] dashboards, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean deleteDashboard(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    List<MemDashboardWidget> createMemDashboardWidget(Long portalId, Long dashboardId, MemDashboardWidgetCreate[] memDashboardWidgetCreates, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean updateMemDashboardWidgets(Long portalId, User user, MemDashboardWidgetDto[] memDashboardWidgets) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean deleteMemDashboardWidget(Long relationId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    String shareDashboard(Long dashboardId, String username, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    void deleteDashboardAndPortalByProject(Long projectId) throws RuntimeException;

    List<Long> getExcludeRoles(Long id);

    boolean postDashboardVisibility(Role role, VizVisibility vizVisibility, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;;
}
