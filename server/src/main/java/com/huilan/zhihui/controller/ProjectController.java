

package com.huilan.zhihui.controller;

import com.github.pagehelper.PageInfo;
import com.huilan.core.annotation.CurrentUser;
import com.huilan.zhihui.common.controller.BaseController;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.dto.organizationDto.OrganizationTransfer;
import com.huilan.zhihui.dto.roleDto.RoleBaseInfo;
import com.huilan.zhihui.dto.roleDto.RoleProject;
import com.huilan.zhihui.dto.roleDto.RoleWithProjectPermission;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.service.ProjectService;
import com.huilan.zhihui.service.RoleService;
import com.huilan.zhihui.dto.projectDto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Api(value = "/project", tags = "project", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiResponses(@ApiResponse(code = 404, message = "project not found"))
@Slf4j
@RestController
@RequestMapping(value = Constants.BASE_API_PATH + "/projects", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;


    /**
     * 获取项目列表：用户创建和用户所在组可访问的
     *
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "get projects")
    @GetMapping
    public ResponseEntity getProjects(@ApiIgnore @CurrentUser User user, HttpServletRequest request) {
        List<ProjectInfo> projects = projectService.getProjects(user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(projects));
    }


    @ApiOperation(value = "get roles where proejct is located")
    @GetMapping("/{id}/roles")
    public ResponseEntity getRolesOfProject(@ApiIgnore @CurrentUser User user,
                                            @PathVariable Long id,
                                            HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        List<RoleBaseInfo> list = roleService.getRolesByProjectId(id, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(list));
    }


    @ApiOperation(value = "get roles where proejct is located")
    @GetMapping("/{id}/roles/{roleId}")
    public ResponseEntity getRoleOfProject(@ApiIgnore @CurrentUser User user,
                                           @PathVariable Long id,
                                           @PathVariable Long roleId,
                                           HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        RoleWithProjectPermission permission = roleService.getRoleByProject(id, roleId, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(permission));
    }


    /**
     * 获取项目列表：用户创建和用户所在组可访问的
     *
     * @param id
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "get project info")
    @GetMapping("/{id}")
    public ResponseEntity getProjectInfo(@PathVariable Long id,
                                         @ApiIgnore @CurrentUser User user,
                                         @ApiIgnore HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(projectService.getProjectInfo(id, user)));
    }

    /**
     * 获取项目列表：用户创建和用户所在组可访问的
     *
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "get admins of project")
    @GetMapping("/{id}/admins")
    public ResponseEntity getAdmins(@PathVariable Long id,
                                    @ApiIgnore @CurrentUser User user,
                                    HttpServletRequest request) {
        List<RelProjectAdminDto> admins = projectService.getAdmins(id, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(admins));
    }


    @ApiOperation(value = "search projects by keywords")
    @GetMapping("/search")
    public ResponseEntity searchProjects(@RequestParam(value = "keywords", required = false) String keywords,
                                         @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                         @ApiIgnore @CurrentUser User user,
                                         HttpServletRequest request) {

        PageInfo<ProjectWithCreateBy> pageInfo = projectService.searchProjects(keywords, user, pageNum, pageSize);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(pageInfo));
    }

    /**
     * 创建项目
     *
     * @param projectCreat
     * @param user
     * @param bindingResult
     * @param request
     * @return
     */
    @ApiOperation(value = "create project", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createProject(@Valid @RequestBody ProjectCreat projectCreat,
                                        @ApiIgnore BindingResult bindingResult,
                                        @ApiIgnore @CurrentUser User user,
                                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        ProjectInfo project = projectService.createProject(projectCreat, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(project));
    }

    /**
     * 移交项目
     *
     * @param id
     * @param organizationTransfer
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "transfer projects", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity transferProject(@PathVariable Long id,
                                          @Valid @RequestBody OrganizationTransfer organizationTransfer,
                                          @ApiIgnore BindingResult bindingResult,
                                          @ApiIgnore @CurrentUser User user,
                                          HttpServletRequest request) {


        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        projectService.transferPeoject(id, organizationTransfer.getOrgId(), user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }

    /**
     * 删除project
     *
     * @param id
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "delete project")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteProject(@PathVariable Long id,
                                        @ApiIgnore @CurrentUser User user,
                                        HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        projectService.deleteProject(id, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 更新项目基本信息
     *
     * @param id
     * @param projectUpdate
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "update project", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProjectBaseInfo(@PathVariable Long id,
                                                @Valid @RequestBody ProjectUpdate projectUpdate,
                                                @ApiIgnore BindingResult bindingResult,
                                                @ApiIgnore @CurrentUser User user,
                                                HttpServletRequest request) {

        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        projectService.updateProject(id, projectUpdate, user);

        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 收藏project
     *
     * @param id
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "favorite project", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/favorite/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity favoriteProject(@PathVariable Long id,
                                          @ApiIgnore @CurrentUser User user,
                                          HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        projectService.favoriteProject(id, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 获取已收藏project
     *
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "get favorite projects")
    @GetMapping(value = "/favorites")
    public ResponseEntity getFavoriteProjects(@ApiIgnore @CurrentUser User user,
                                              HttpServletRequest request) {
        List<ProjectInfo> favoriteProjects = projectService.getFavoriteProjects(user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(favoriteProjects));
    }

    /**
     * 获取已收藏project
     *
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "remove favorite projects")
    @DeleteMapping(value = "/remove/favorites")
    public ResponseEntity removeFavoriteProjects(@ApiIgnore @CurrentUser User user,
                                                 @RequestBody Long[] projectIds,
                                                 HttpServletRequest request) {
        for (Long id : projectIds) {
            if (invalidId(id)) {
                ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
                return ResponseEntity.status(resultMap.getCode()).body(resultMap);
            }
        }

        projectService.removeFavoriteProjects(user, projectIds);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }

    /**
     * 为project 添加管理员
     *
     * @param id
     * @param adminId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "add an admin for a project")
    @PostMapping(value = "/{id}/admin/{adminId}")
    public ResponseEntity addProjectAdmin(@PathVariable Long id,
                                          @PathVariable Long adminId,
                                          @ApiIgnore @CurrentUser User user,
                                          HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(adminId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid admin id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        RelProjectAdminDto relProjectAdminDto = projectService.addAdmin(id, adminId, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(relProjectAdminDto));
    }

    /**
     * 删除project admin
     *
     * @param id
     * @param relationId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "remove an admin from a project")
    @DeleteMapping(value = "/{id}/admin/{relationId}")
    public ResponseEntity removeProjectAdmin(@PathVariable Long id,
                                             @PathVariable Long relationId,
                                             @ApiIgnore @CurrentUser User user,
                                             HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(relationId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid admin id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        projectService.removeAdmin(relationId, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 批量添加role
     *
     * @param id
     * @param roleIds
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "add project role relations")
    @PostMapping(value = "/{id}/roles", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addRoles(@PathVariable Long id,
                                   @RequestBody Long[] roleIds,
                                   @ApiIgnore @CurrentUser User user,
                                   HttpServletRequest request) {

        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        List<RoleProject> list = projectService.postRoles(id, Arrays.asList(roleIds), user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(list));
    }
}
