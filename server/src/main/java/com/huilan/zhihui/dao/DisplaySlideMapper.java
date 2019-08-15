

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.displayDto.SlideWithDisplayAndProject;
import com.huilan.zhihui.model.DisplaySlide;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DisplaySlideMapper {

    int insert(DisplaySlide displaySlide);


    @Delete({"delete from display_slide where id = #{id}"})
    int deleteById(@Param("id") Long id);


    @Delete({"DELETE FROM display_slide where display_id in (SELECT id from display WHERE project_id = #{projectId})"})
    int deleteByProjectId(@Param("projectId") Long projectId);


    @Select({"select * from display_slide where id = #{id}"})
    DisplaySlide getById(Long id);


    @Update({
            "update display_slide",
            "set display_id = #{displayId,jdbcType=BIGINT},",
            "`index` = #{index,jdbcType=INTEGER},",
            "`config` = #{config,jdbcType=LONGVARCHAR},",
            "`update_by` = #{updateBy,jdbcType=BIGINT},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(DisplaySlide record);

    int updateBatch(List<DisplaySlide> list);


    @Select({"select * from display_slide where display_id = #{displayId} order by `index`"})
    List<DisplaySlide> selectByDisplayId(@Param("displayId") Long displayId);

    @Delete({"delete from display_slide where display_id = #{displayId}"})
    int deleteByDisplayId(@Param("displayId") Long displayId);


    @Select({
            "SELECT ",
            "	s.*,",
            "	d.id as 'display.id',",
            "	d.`name` as 'display.name',",
            "	d.description as 'display.description',",
            "	d.project_id as 'display.projectId',",
            "	d.avatar as 'display.avatar',",
            "	d.publish as 'display.publish',",
            "	p.id 'project.id',",
            "	p.`name` 'project.name',",
            "	p.description 'project.description',",
            "	p.pic 'project.pic',",
            "	p.org_id 'project.orgId',",
            "	p.user_id 'project.userId',",
            "	p.visibility 'p.visibility'",
            "FROM display_slide s ",
            "   LEFT JOIN display d on d.id = s.display_id",
            "   LEFT JOIN project p on p.id = d.project_id",
            "where s.id = #{slideId}",
    })
    SlideWithDisplayAndProject getSlideWithDipalyAndProjectById(@Param("slideId") Long slideId);
}