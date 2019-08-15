

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.shareDto.ShareWidget;
import com.huilan.zhihui.dto.widgetDto.WidgetWithRelationDashboardId;
import com.huilan.zhihui.model.Widget;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface WidgetMapper {
    int insert(Widget widget);

    @Delete({"delete from widget where id = #{id}"})
    int deleteById(@Param("id") Long id);

    @Select({"select * from widget where id = #{id}"})
    Widget getById(@Param("id") Long id);

    @Select({"select w.*,v.model from widget w LEFT JOIN `view` v on v.id = w.view_id where w.id = #{id}"})
    ShareWidget getShareWidgetById(@Param("id") Long id);


    int insertBatch(@Param("list") List<Widget> list);


    @Update({
            "update widget",
            "set `name` = #{name,jdbcType=VARCHAR},",
            "`description` = #{description,jdbcType=VARCHAR},",
            "`view_id` = #{viewId,jdbcType=BIGINT},",
            "`project_id` = #{projectId,jdbcType=BIGINT},",
            "`type` = #{type,jdbcType=BIGINT},",
            "`publish` = #{publish,jdbcType=BIT},",
            "`config` = #{config,jdbcType=LONGVARCHAR},",
            "`update_by` = #{updateBy,jdbcType=BIGINT},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(Widget widget);

    List<Widget> getByIds(@Param("list") Set<Long> ids);


    Set<Long> getIdSetByIds(@Param("set") Set<Long> ids);


    @Select({
            "SELECT  w.* FROM widget w ",
            "LEFT JOIN mem_display_slide_widget m on w.id = m.widget_id",
            "LEFT JOIN display_slide s on m.display_slide_id = s.id",
            "WHERE s.display_id = #{displayId}",
    })
    Set<Widget> getByDisplayId(@Param("displayId") Long displayId);

    @Select({
            "SELECT  w.*, v.model FROM widget w ",
            "LEFT JOIN mem_display_slide_widget m on w.id = m.widget_id",
            "LEFT JOIN display_slide s on m.display_slide_id = s.id",
            "LEFT JOIN `view` v on v.id = w.view_id",
            "WHERE s.display_id = #{displayId}",
    })
    Set<ShareWidget> getShareWidgetsByDisplayId(@Param("displayId") Long displayId);

    @Select({"select id from widget where project_id = #{projectId} and `name` = #{name}"})
    Long getByNameWithProjectId(@Param("name") String name, @Param("projectId") Long projectId);

    @Select({"select * from widget where project_id = #{projectId}"})
    List<Widget> getByProject(@Param("projectId") Long projectId);

    @Select({"SELECT w.*, m.id as 'relationId' FROM mem_dashboard_widget m LEFT JOIN widget w on w.id = m.widget_Id WHERE m.dashboard_id = #{dashboardId}"})
    Set<WidgetWithRelationDashboardId> getByDashboard(@Param("dashboardId") Long dashboardId);

    @Select({"SELECT w.*, v.model FROM mem_dashboard_widget m ",
            "LEFT JOIN widget w on w.id = m.widget_Id ",
            "LEFT JOIN `view` v on v.id = w.view_id",
            "WHERE m.dashboard_id = #{dashboardId}"})
    Set<ShareWidget> getShareWidgetsByDashboard(@Param("dashboardId") Long dashboardId);

    @Delete({"delete from widget where project_id = #{projectId}"})
    int deleteByProject(@Param("projectId") Long projectId);

    @Select({"select * from widget where view_id = #{viewId}"})
    List<Widget> getWidgetsByWiew(@Param("viewId") Long viewId);


    int updateConfigBatch(@Param("list") List<Widget> list);
}