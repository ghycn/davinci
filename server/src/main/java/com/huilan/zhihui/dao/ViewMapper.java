

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.viewDto.ViewBaseInfo;
import com.huilan.zhihui.dto.viewDto.ViewWithProjectAndSource;
import com.huilan.zhihui.dto.viewDto.ViewWithSource;
import com.huilan.zhihui.dto.viewDto.ViewWithSourceBaseInfo;
import com.huilan.zhihui.model.View;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface ViewMapper {

    int insert(View view);

    @Select({"select id from `view` where project_id = #{projectId} and `name` = #{name}"})
    Long getByNameWithProjectId(@Param("name") String name, @Param("projectId") Long projectId);


    @Select({
            "SELECT ",
            "	v.*,",
            "	p.id 'project.id',",
            "	p.`name` 'project.name',",
            "	p.`description` 'project.description',",
            "	p.`pic` 'project.pic',",
            "	p.`org_id` 'project.orgId',",
            "	p.`user_id` 'project.userId',",
            "	p.`visibility` 'p.visibility',",
            "	s.`id` 'source.id',",
            "	s.`name` 'source.name',",
            "	s.`description` 'source.description',",
            "	s.`config` 'source.config',",
            "	s.`project_id` 'source.projectId',",
            "	s.`type` 'source.type'",
            "FROM `view` v",
            "	LEFT JOIN project p on p.id = v.project_id",
            "	LEFT JOIN source s on s.id = v.source_id",
            "WHERE v.id = #{id}",
    })
    ViewWithProjectAndSource getViewWithProjectAndSourceById(@Param("id") Long id);


    @Delete({"delete from `view` where id = #{id}"})
    int deleteById(Long id);

    @Select({"select * from `view` where id = #{id}"})
    View getById(Long id);


    @Update({
            "update `view`",
            "set `name` = #{name,jdbcType=VARCHAR},",
            "`description` = #{description,jdbcType=VARCHAR},",
            "`project_id` = #{projectId,jdbcType=BIGINT},",
            "`source_id` = #{sourceId,jdbcType=BIGINT},",
            "`sql` = #{sql,jdbcType=LONGVARCHAR},",
            "`model` = #{model,jdbcType=LONGVARCHAR},",
            "`variable` = #{variable,jdbcType=LONGVARCHAR},",
            "`config` = #{config,jdbcType=LONGVARCHAR},",
            "`update_by` = #{updateBy,jdbcType=BIGINT},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(View view);

    @Select({"select * from `view` where source_id = #{sourceId}"})
    List<View> getBySourceId(@Param("sourceId") Long sourceId);

    @Select({
            "select v.*,",
            "s.id as 'source.id', s.`name` as 'source.name' from `view` v ",
            "left join source s on s.id = v.source_id ",
            "where v.id = #{id}"
    })
    ViewWithSourceBaseInfo getViewWithSourceBaseInfo(@Param("id") Long id);


    @Select({
            "select v.id, v.`name`, v.`description`, s.name as 'sourceName'",
            "from `view` v ",
            "left join source s on s.id = v.source_id ",
            "where v.project_id = #{projectId}"
    })
    List<ViewBaseInfo> getViewBaseInfoByProject(@Param("projectId") Long projectId);


    int insertBatch(@Param("list") List<View> sourceList);

    @Delete({"delete from `view` where project_id = #{projectId}"})
    int deleteByPorject(@Param("projectId") Long projectId);

    @Select({
            "SELECT ",
            "	v.*,",
            "	s.`id` 'source.id',",
            "	s.`name` 'source.name',",
            "	s.`description` 'source.description',",
            "	s.`config` 'source.config',",
            "	s.`project_id` 'source.projectId',",
            "	s.`type` 'source.type'",
            "FROM `view` v",
            "	LEFT JOIN project p on p.id = v.project_id",
            "	LEFT JOIN source s on s.id = v.source_id",
            "WHERE v.id = #{id}",
    })
    ViewWithSource getViewWithSource(Long id);

    Set<View> selectByWidgetIds(@Param("widgetIds") Set<Long> widgetIds);
}