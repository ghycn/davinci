

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.displayDto.MemDisplaySlideWidgetWithSlide;
import com.huilan.zhihui.model.MemDisplaySlideWidget;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MemDisplaySlideWidgetMapper {

    int insert(MemDisplaySlideWidget memDisplaySlideWidget);

    @Delete({"delete from mem_display_slide_widget where id = #{id}"})
    int deleteById(@Param("id") Long id);

    int deleteBatchById(@Param("list") List<Long> list);

    @Delete({
            "delete from mem_display_slide_widget where display_slide_id in ",
            "(SELECT s.id FROM display_slide s LEFT JOIN display d on s.display_id = d.id where d.project_id = #{projectId})"
    })
    int deleteByProject(@Param("projectId") Long projectId);


    @Select({"select * from mem_display_slide_widget where id = #{id}"})
    MemDisplaySlideWidget getById(@Param("id") Long id);


    @Update({
            "update mem_display_slide_widget",
            "set `display_slide_id` = #{displaySlideId,jdbcType=BIGINT},",
            "widget_id = #{widgetId,jdbcType=BIGINT},",
            "`name` = #{name,jdbcType=VARCHAR},",
            "`type` = #{type,jdbcType=SMALLINT},",
            "sub_type = #{subType,jdbcType=SMALLINT},",
            "`index` = #{index,jdbcType=INTEGER},",
            "`params` = #{params,jdbcType=LONGVARCHAR},",
            "update_by = #{updateBy,jdbcType=BIGINT},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(MemDisplaySlideWidget memDisplaySlideWidget);


    @Select({"SELECT m.* FROM mem_display_slide_widget m WHERE m.display_slide_id = #{slideId}"})
    List<MemDisplaySlideWidget> getMemDisplaySlideWidgetListBySlideId(@Param("slideId") Long slideId);

    @Delete({"delete from mem_display_slide_widget where display_slide_id in (select id from display_slide where display_id = #{displayId})"})
    int deleteByDisplayId(@Param("displayId") Long displayId);


    @Delete({"delete from mem_display_slide_widget where display_slide_id = #{slideId}"})
    int deleteBySlideId(@Param("slideId") Long slideId);

    int insertBatch(@Param("list") List<MemDisplaySlideWidget> list);

    int updateBatch(@Param("list") List<MemDisplaySlideWidget> list);

    @Select({
            "SELECT m.*,",
            "	s.id 'displaySlide.id',",
            "	s.display_id 'displaySlide.displayId',",
            "	s.`index` 'displaySlide.index',",
            "	s.`config` 'displaySlide.config'",
            "FROM mem_display_slide_widget m LEFT JOIN display_slide s on m.display_slide_id = s.id",
            "WHERE s.display_id = #{displayId}",
    })
    List<MemDisplaySlideWidgetWithSlide> getMemWithSlideByDisplayId(@Param("displayId") Long displayId);

    @Delete({"delete from mem_display_slide_widget where widget_id = #{widgetId}"})
    int deleteByWidget(@Param("widgetId") Long widgetId);
}