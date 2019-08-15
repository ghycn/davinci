

package com.huilan.zhihui.dao;

import com.huilan.zhihui.model.MemDashboardWidget;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MemDashboardWidgetMapper {
    int insert(MemDashboardWidget memDashboardWidget);

    @Delete({"delete from mem_dashboard_widget where id = #{id}"})
    int deleteById(@Param("id") Long id);


    @Select({
            "select * from mem_dashboard_widget where id = #{id}"
    })
    MemDashboardWidget getById(@Param("id") Long id);


    @Update({
            "update mem_dashboard_widget",
            "set dashboard_id = #{dashboardId,jdbcType=BIGINT},",
            "widget_Id = #{widgetId,jdbcType=BIGINT},",
            "x = #{x,jdbcType=INTEGER},",
            "y = #{y,jdbcType=INTEGER},",
            "width = #{width,jdbcType=INTEGER},",
            "height = #{height,jdbcType=INTEGER},",
            "polling = #{polling,jdbcType=BIT},",
            "frequency = #{frequency,jdbcType=INTEGER},",
            "`config` = #{config,jdbcType=LONGVARCHAR}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(MemDashboardWidget memDashboardWidget);

    @Select({"select * from mem_dashboard_widget where dashboard_id = #{dashboardId}"})
    List<MemDashboardWidget> getByDashboardId(@Param("dashboardId") Long dashboardId);

    @Delete({
            "delete from mem_dashboard_widget where dashboard_id in ",
            "(SELECT d.id FROM dashboard d LEFT JOIN dashboard_portal p on d.dashboard_portal_id = p.id where p.project_id = #{projectId})"
    })
    int deleteByProject(@Param("projectId") Long projectId);

    int insertBatch(@Param("list") List<MemDashboardWidget> list);

    int updateBatch(List<MemDashboardWidget> list);

    @Delete("delete from mem_dashboard_widget where widget_Id = #{widgetId}")
    int deleteByWidget(@Param("widgetId") Long widgetId);

    @Delete({"DELETE mdw FROM mem_dashboard_widget mdw WHERE mdw.dashboard_id IN " +
            "( " +
            "SELECT d.id " +
            "FROM dashboard d " +
            "WHERE d.dashboard_portal_id = #{portalId} " +
            ") "})
    int deleteByPortalId(@Param("portalId") Long portalId);

    @Delete("delete from mem_dashboard_widget where dashboard_id = #{dashboardId}")
    int deleteByDashboardId(@Param("dashboardId") Long dashboardId);
}