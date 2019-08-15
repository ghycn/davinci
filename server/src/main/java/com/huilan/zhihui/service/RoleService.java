

package com.huilan.zhihui.service;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.zhihui.dto.roleDto.*;
import com.huilan.zhihui.model.Role;
import com.huilan.zhihui.model.User;

import java.util.List;

public interface RoleService {


    /**
     * 新建Role
     *
     * @param roleCreate
     * @param user
     * @return
     */
    Role createRole(RoleCreate roleCreate, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;


    /**
     * 删除 Role
     *
     * @param id
     * @param user
     * @return
     */
    boolean deleteRole(Long id, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;


    /**
     * 修改Role
     *
     * @param id
     * @param roleUpdate
     * @param user
     * @return
     */
    boolean updateRole(Long id, RoleUpdate roleUpdate, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;


    /**
     * 获取单条Role详情
     *
     * @param id
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    Role getRoleInfo(Long id, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;


    /**
     * 添加Role与User关联
     *
     * @param id
     * @param memberIds
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    List<RelRoleMember> addMembers(Long id, List<Long> memberIds, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    /**
     * 删除Role与User关联
     *
     * @param relationId
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    boolean deleteMember(Long relationId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;


    /**
     * 更新role member
     *
     * @param roleId
     * @param memberIds
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    List<RelRoleMember> updateMembers(Long roleId, List<Long> memberIds, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;


    /**
     * 获取单条Role所关联的Users
     *
     * @param id
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    List<RelRoleMember> getMembers(Long id, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    /**
     * 添加Role与Project关联
     *
     * @param id
     * @param projectId
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    RoleProject addProject(Long id, Long projectId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    /**
     * 删除Role与Project关联
     *
     * @param roleId
     * @param projectId
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    boolean deleteProject(Long roleId, Long projectId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    /**
     * 修改Role与Project关联信息
     *
     * @param roleId
     * @param projectId
     * @param user
     * @param projectRoleDto
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    boolean updateProjectRole(Long roleId, Long projectId, User user, RelRoleProjectDto projectRoleDto) throws ServerException, UnAuthorizedExecption, NotFoundException;


    /**
     * 获取单个Organization里的Role列表
     *
     * @param orgId
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    List<RoleBaseInfo> getRolesByOrgId(Long orgId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    /**
     * 获取单个关联的Role列表
     *
     * @param projectId
     * @param user
     * @return
     * @throws ServerException
     * @throws UnAuthorizedExecption
     * @throws NotFoundException
     */
    List<RoleBaseInfo> getRolesByProjectId(Long projectId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    RoleWithProjectPermission getRoleByProject(Long projectId, Long roleId, User user);

    VizPermission getVizPermission(Long id, Long projectId, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;

    boolean postVizvisibility(Long id, VizVisibility vizVisibility, User user) throws ServerException, UnAuthorizedExecption, NotFoundException;
}
