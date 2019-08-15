

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.sourceDto.SourceWithProject;
import com.huilan.zhihui.model.Source;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SourceMapper {

    int insert(Source source);

    @Delete({"delete from `source` where id = #{id}"})
    int deleteById(@Param("id") Long id);


    @Select({"select * from `source` where id = #{id}"})
    Source getById(@Param("id") Long id);

    @Update({
            "update `source`",
            "set `name` = #{name,jdbcType=VARCHAR},",
            "`description` = #{description,jdbcType=VARCHAR},",
            "`type` = #{type,jdbcType=VARCHAR},",
            "`project_id` = #{projectId,jdbcType=BIGINT},",
            "`config` = #{config,jdbcType=LONGVARCHAR},",
            "`update_by` = #{updateBy,jdbcType=BIGINT},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(Source source);

    @Select({"select id from `source` where project_id = #{projectId} and name = #{name}"})
    Long getByNameWithProjectId(@Param("name") String name, @Param("projectId") Long projectId);

    @Select({"select * from `source` where project_id = #{projectId}"})
    List<Source> getByProject(@Param("projectId") Long projectId);

    @Select({
            "SELECT s.id, s.`name`, s.`type`, s.`config`,",
            "	p.id 'project.id',",
            "	p.`name` 'project.name',",
            "	p.`description` 'project.description',",
            "	p.`pic` 'project.pic',",
            "	p.`user_id` 'project.userId',",
            "	p.`org_id` 'project.orgId',",
            "	p.`visibility` 'p.visibility'",
            "FROM source s INNER JOIN project p on p.id = s.project_id",
            "where s.id = #{souceId}"
    })
    SourceWithProject getSourceWithProjectById(@Param("souceId") Long souceId);

    int insertBatch(@Param("list") List<Source> sourceList);

    @Delete({"delete from `source` where project_id = #{projectId}"})
    int deleteByProject(@Param("projectId") Long projectId);
}