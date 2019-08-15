

package com.huilan.zhihui.service;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.dto.organizationDto.*;
import com.huilan.zhihui.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface OrganizationService extends CheckEntityService {

    List<OrganizationInfo> getOrganizations(User user);

    boolean updateOrganization(OrganizationPut organizationPut, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    OrganizationBaseInfo createOrganization(OrganizationCreate organizationCreate, User user) throws ServerException;

    Map<String, String> uploadAvatar(Long id, MultipartFile file, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean deleteOrganization(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    OrganizationInfo getOrganization(Long id, User user) throws NotFoundException, UnAuthorizedExecption;

    List<OrganizationMember> getOrgMembers(Long id);

    void inviteMember(Long orgId, Long memId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    OrganizationInfo confirmInvite(String token, User user) throws ServerException;

    boolean deleteOrgMember(Long relationId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean updateMemberRole(Long relationId, User user, int role) throws NotFoundException, UnAuthorizedExecption, ServerException;

    void confirmInviteNoLogin(String token) throws NotFoundException, ServerException;
}

