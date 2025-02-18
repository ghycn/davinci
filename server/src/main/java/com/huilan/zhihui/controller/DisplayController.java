

package com.huilan.zhihui.controller;

import com.alibaba.druid.util.StringUtils;
import com.huilan.core.annotation.CurrentUser;
import com.huilan.zhihui.common.controller.BaseController;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.common.ResultMap;
import com.huilan.zhihui.dto.displayDto.*;
import com.huilan.zhihui.model.Display;
import com.huilan.zhihui.model.DisplaySlide;
import com.huilan.zhihui.model.MemDisplaySlideWidget;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.service.DisplayService;
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
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Api(value = "/displays", tags = "displays", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ApiResponses(@ApiResponse(code = 404, message = "display not found"))
@Slf4j
@RestController
@RequestMapping(value = Constants.BASE_API_PATH + "/displays", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DisplayController extends BaseController {

    @Autowired
    private DisplayService displayService;

    /**
     * 新建display
     *
     * @param displayInfo
     * @param bindingResult
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "create new display", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createDisplay(@Valid @RequestBody DisplayInfo displayInfo,
                                        @ApiIgnore BindingResult bindingResult,
                                        @ApiIgnore @CurrentUser User user,
                                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        Display display = displayService.createDisplay(displayInfo, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(display));
    }

    /**
     * 更新display 信息
     *
     * @param display
     * @param bindingResult
     * @param user
     * @param id
     * @param request
     * @return
     */
    @ApiOperation(value = "update display info", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDisplay(@Valid @RequestBody DisplayUpdate display,
                                        @ApiIgnore BindingResult bindingResult,
                                        @ApiIgnore @CurrentUser User user,
                                        @PathVariable Long id, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(id) || !id.equals(display.getId())) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.updateDisplay(display, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }

    /**
     * 删除display
     *
     * @param id
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "delete a display", consumes = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping("/{id}")
    public ResponseEntity deleteDisplay(@PathVariable Long id,
                                        @ApiIgnore @CurrentUser User user,
                                        HttpServletRequest request) {

        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.deleteDisplay(id, user);

        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 新建displaySlide
     *
     * @param displaySlideCreate
     * @param bindingResult
     * @param displayId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "create new display slide", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/{id}/slides", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createDisplaySlide(@Valid @RequestBody DisplaySlideCreate displaySlideCreate,
                                             @ApiIgnore BindingResult bindingResult,
                                             @PathVariable("id") Long displayId,
                                             @ApiIgnore @CurrentUser User user,
                                             HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(displayId) || !displayId.equals(displaySlideCreate.getDisplayId())) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        DisplaySlide displaySlide = displayService.createDisplaySlide(displaySlideCreate, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(displaySlide));
    }

    /**
     * 更新displayslides信息
     *
     * @param displaySlides
     * @param bindingResult
     * @param user
     * @param displayId
     * @param request
     * @return
     */
    @ApiOperation(value = "update display slides info", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{id}/slides", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateDisplaySlide(@Valid @RequestBody DisplaySlide[] displaySlides,
                                             @ApiIgnore BindingResult bindingResult,
                                             @ApiIgnore @CurrentUser User user,
                                             @PathVariable("id") Long displayId, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(displayId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (null == displaySlides || displaySlides.length < 1) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("display slide info cannot be EMPTY");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.updateDisplaySildes(displayId, displaySlides, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 删除DisplaySlide
     *
     * @param slideId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "delete display slide", consumes = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping("/slides/{slideId}")
    public ResponseEntity deleteDisplaySlide(@PathVariable("slideId") Long slideId,
                                             @ApiIgnore @CurrentUser User user,
                                             HttpServletRequest request) {

        if (invalidId(slideId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display slide id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.deleteDisplaySlide(slideId, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 在displaySlide下新建widget关联
     *
     * @param slideWidgetCreates
     * @param displayId
     * @param slideId
     * @param bindingResult
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "add display slide widgets", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/{displayId}/slides/{slideId}/widgets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addMemDisplaySlideWidgets(@PathVariable("displayId") Long displayId,
                                                    @PathVariable("slideId") Long slideId,
                                                    @Valid @RequestBody MemDisplaySlideWidgetCreate[] slideWidgetCreates,
                                                    @ApiIgnore BindingResult bindingResult,
                                                    @ApiIgnore @CurrentUser User user,
                                                    HttpServletRequest request) {

        if (invalidId(displayId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(slideId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display slide id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (null == slideWidgetCreates || slideWidgetCreates.length < 1) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("display slide widget info cannot be EMPTY");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        for (MemDisplaySlideWidgetCreate slideWidgetCreate : slideWidgetCreates) {
            if (!slideWidgetCreate.getDisplaySlideId().equals(slideId)) {
                ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display slide id");
                return ResponseEntity.status(resultMap.getCode()).body(resultMap);
            }
            if (slideWidgetCreate.getType() == 1 && invalidId(slideWidgetCreate.getWidgetId())) {
                ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid widget id");
                return ResponseEntity.status(resultMap.getCode()).body(resultMap);
            }
        }

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        List<MemDisplaySlideWidget> memDisplaySlideWidgets = displayService.addMemDisplaySlideWidgets(displayId, slideId, slideWidgetCreates, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(memDisplaySlideWidgets));
    }

    /**
     * 批量修改widget关联
     *
     * @param memDisplaySlideWidgets
     * @param displayId
     * @param slideId
     * @param bindingResult
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "update display slide widgets", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/{displayId}/slides/{slideId}/widgets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateMemDisplaySlideWidgets(@PathVariable("displayId") Long displayId,
                                                       @PathVariable("slideId") Long slideId,
                                                       @Valid @RequestBody MemDisplaySlideWidgetDto[] memDisplaySlideWidgets,
                                                       @ApiIgnore BindingResult bindingResult,
                                                       @ApiIgnore @CurrentUser User user,
                                                       HttpServletRequest request) {

        if (invalidId(displayId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(slideId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display slide id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (null == memDisplaySlideWidgets || memDisplaySlideWidgets.length < 1) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("display slide widget info cannot be EMPTY");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        for (MemDisplaySlideWidget slideWidgetCreate : memDisplaySlideWidgets) {
            if (!slideWidgetCreate.getDisplaySlideId().equals(slideId)) {
                ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid display slide id");
                return ResponseEntity.status(resultMap.getCode()).body(resultMap);
            }
            if (1 == slideWidgetCreate.getType() && invalidId(slideWidgetCreate.getWidgetId())) {
                ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid widget id");
                return ResponseEntity.status(resultMap.getCode()).body(resultMap);
            }
        }

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.updateMemDisplaySlideWidgets(displayId, slideId, memDisplaySlideWidgets, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 修改displaySlide下的widget关联信息
     *
     * @param memDisplaySlideWidget
     * @param bindingResult
     * @param relationId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "update display slide widget", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/slides/widgets/{relationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateMemDisplaySlideWidget(@PathVariable("relationId") Long relationId,
                                                      @Valid @RequestBody MemDisplaySlideWidget memDisplaySlideWidget,
                                                      @ApiIgnore BindingResult bindingResult,
                                                      @ApiIgnore @CurrentUser User user,
                                                      HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message(bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(relationId) || !memDisplaySlideWidget.getId().equals(relationId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid relation id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.updateMemDisplaySlideWidget(memDisplaySlideWidget, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 删除displaySlide下的widget关联信息
     *
     * @param relationId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "delete display slide widget", consumes = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping("/slides/widgets/{relationId}")
    public ResponseEntity deleteMemDisplaySlideWidget(@PathVariable("relationId") Long relationId,
                                                      @ApiIgnore @CurrentUser User user,
                                                      HttpServletRequest request) {

        if (invalidId(relationId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid relation id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.deleteMemDisplaySlideWidget(relationId, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }

    /**
     * 获取display列表
     *
     * @param projectId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "get displays")
    @GetMapping
    public ResponseEntity getDisplays(@RequestParam Long projectId,
                                      @ApiIgnore @CurrentUser User user,
                                      HttpServletRequest request) {

        if (invalidId(projectId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid project id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }
        List<Display> displayList = displayService.getDisplayListByProject(projectId, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(displayList));
    }


    /**
     * 获取display slide列表
     *
     * @param id
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "get display slides")
    @GetMapping("/{id}/slides")
    public ResponseEntity getDisplaySlide(@PathVariable Long id,
                                          @ApiIgnore @CurrentUser User user,
                                          HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid Display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }
        DisplayWithSlides displayWithSlides = displayService.getDisplaySlideList(id, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(displayWithSlides));
    }


    /**
     * 获取displaySlide下widgets关联信息列表
     *
     * @param displayId
     * @param slideId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "get display slide widgets")
    @GetMapping("/{displayId}/slides/{slideId}")
    public ResponseEntity getDisplaySlideWidgets(@PathVariable("displayId") Long displayId,
                                                 @PathVariable("slideId") Long slideId,
                                                 @ApiIgnore @CurrentUser User user,
                                                 HttpServletRequest request) {

        if (invalidId(displayId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid Display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(slideId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid Display Slide id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        SlideWithMem displaySlideMem = displayService.getDisplaySlideMem(displayId, slideId, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(displaySlideMem));
    }


    /**
     * 删除displaySlide下widgets关联信息列表
     *
     * @param displayId
     * @param slideId
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "delete display slide widgets")
    @DeleteMapping("/{displayId}/slides/{slideId}/widgets")
    public ResponseEntity deleteDisplaySlideWeight(@PathVariable("displayId") Long displayId,
                                                   @PathVariable("slideId") Long slideId,
                                                   @RequestBody Long[] ids,
                                                   @ApiIgnore @CurrentUser User user,
                                                   HttpServletRequest request) {

        if (invalidId(displayId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid Display id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (invalidId(slideId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid Display Slide id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (null == ids || ids.length < 1) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("nothing be deleted");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        displayService.deleteDisplaySlideWidgetList(displayId, slideId, ids, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request));
    }


    /**
     * 上传封面图
     *
     * @param file
     * @param request
     * @return
     */
    @ApiOperation(value = "upload avatar")
    @PostMapping(value = "/upload/coverImage")
    public ResponseEntity uploadAvatar(@RequestParam("coverImage") MultipartFile file,
                                       HttpServletRequest request) {


        if (file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("file can not be EMPTY");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        String avatar = displayService.uploadAvatar(file);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(avatar));
    }


    /**
     * 上传slide背景图
     *
     * @param slideId
     * @param file
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "upload avatar")
    @PostMapping(value = "/slide/{slideId}/upload/bgImage")
    public ResponseEntity uploadSlideBGImage(@PathVariable Long slideId,
                                             @RequestParam("backgroundImage") MultipartFile file,
                                             @ApiIgnore @CurrentUser User user,
                                             HttpServletRequest request) {

        if (invalidId(slideId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid slide id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("file can not be EMPTY");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        String slideBGImage = displayService.uploadSlideBGImage(slideId, file, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(slideBGImage));
    }

    /**
     * 上传slide背景图
     *
     * @param relationId
     * @param file
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "upload subwidget bgImage")
    @PostMapping(value = "/slide/widget/{relationId}/bgImage")
    public ResponseEntity uploadSlideSubWidgetBGImage(@PathVariable Long relationId,
                                                      @RequestParam("backgroundImage") MultipartFile file,
                                                      @ApiIgnore @CurrentUser User user,
                                                      HttpServletRequest request) {

        if (invalidId(relationId)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid relation id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        if (file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("file can not be EMPTY");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        String bgImage = displayService.uploadSlideSubWidgetBGImage(relationId, file, user);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(bgImage));
    }

    /**
     * 共享display
     *
     * @param id
     * @param username
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "share display")
    @GetMapping("/{id}/share")
    public ResponseEntity shareDisplay(@PathVariable Long id,
                                       @RequestParam(required = false) String username,
                                       @ApiIgnore @CurrentUser User user,
                                       HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        String shareToken = displayService.shareDisplay(id, user, username);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payload(shareToken));
    }


    /**
     * 获取Display 排除访问的团队列表
     *
     * @param id
     * @param request
     * @return
     */
    @ApiOperation(value = "get display  exclude roles")
    @GetMapping("/{id}/exclude/roles")
    public ResponseEntity getDisplayExcludeRoles(@PathVariable Long id,
                                                 HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        List<Long> excludeRoles = displayService.getDisplayExcludeRoles(id);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(excludeRoles));
    }


    /**
     * 获取Display 排除访问的团队列表
     *
     * @param id
     * @param request
     * @return
     */
    @ApiOperation(value = "get display slide exclude roles")
    @GetMapping("/slide/{id}/exclude/roles")
    public ResponseEntity getSlideExcludeRoles(@PathVariable Long id,
                                               HttpServletRequest request) {
        if (invalidId(id)) {
            ResultMap resultMap = new ResultMap(tokenUtils).failAndRefreshToken(request).message("Invalid id");
            return ResponseEntity.status(resultMap.getCode()).body(resultMap);
        }

        List<Long> excludeRoles = displayService.getSlideExecludeRoles(id);
        return ResponseEntity.ok(new ResultMap(tokenUtils).successAndRefreshToken(request).payloads(excludeRoles));
    }
}
