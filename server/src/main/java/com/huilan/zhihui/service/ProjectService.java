

package com.huilan.zhihui.service;

import com.github.pagehelper.PageInfo;
import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.dto.projectDto.*;
import com.huilan.zhihui.dto.roleDto.RoleProject;
import com.huilan.zhihui.model.Project;
import com.huilan.zhihui.model.User;

import java.util.List;

public interface ProjectService extends CheckEntityService {

    ProjectInfo getProjectInfo(Long id, User user);

    List<ProjectInfo> getProjects(User user) throws ServerException;

    ProjectInfo createProject(ProjectCreat projectCreat, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    Project updateProject(Long id, ProjectUpdate projectUpdate, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    boolean deleteProject(Long id, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    Project transferPeoject(Long id, Long orgId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    PageInfo<ProjectWithCreateBy> searchProjects(String keywords, User user, int pageNum, int pageSize);

    boolean favoriteProject(Long id, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    List<ProjectInfo> getFavoriteProjects(User user);

    boolean removeFavoriteProjects(User user, Long[] projectIds) throws ServerException, UnAuthorizedExecption, NotFoundException;

    RelProjectAdminDto addAdmin(Long id, Long adminId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    boolean removeAdmin(Long relationId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    ProjectDetail getProjectDetail(Long id, User user, boolean modify) throws NotFoundException, UnAuthorizedExecption;

    List<RoleProject> postRoles(Long id, List<Long> roleIds, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    PageInfo<ProjectWithCreateBy> getProjectsByOrg(Long id, User user, String keyword, int pageNum, int pageSize);

    ProjectPermission getProjectPermission(ProjectDetail projectDetail, User user);

    boolean allowGetData(ProjectDetail projectDetail, User user);

    List<RelProjectAdminDto> getAdmins(Long id, User user) throws NotFoundException, UnAuthorizedExecption;

    boolean isMaintainer(ProjectDetail projectDetail, User user);
}
