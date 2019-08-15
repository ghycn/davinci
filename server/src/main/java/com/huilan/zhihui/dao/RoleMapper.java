

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.roleDto.RoleBaseInfo;
import com.huilan.zhihui.model.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface RoleMapper {
    int insert(Role record);

    @Delete({
            "delete from `role` where id = #{id,jdbcType=BIGINT}"
    })
    int deleteById(Long id);

    @Select({
            "select * from `role` where id = #{id,jdbcType=BIGINT}"
    })
    Role getById(Long id);


    List<Role> getRolesByIds(List<Long> list);

    @Update({
            "update `role`",
            "set `org_id` = #{orgId,jdbcType=BIGINT},",
            "`name` = #{name,jdbcType=VARCHAR},",
            "`description` = #{description,jdbcType=VARCHAR},",
            "`create_by` = #{createBy,jdbcType=BIGINT},",
            "`create_time` = #{createTime,jdbcType=TIMESTAMP},",
            "`update_by` = #{updateBy,jdbcType=BIGINT},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP}",
            "where id = #{id,jdbcType=BIGINT}"
    })
    int update(Role record);


    @Select({
            "select id, `name`, description  from `role` where org_id = #{orgId}"
    })
    List<RoleBaseInfo> getBaseInfoByOrgId(Long orgId);


    List<Role> selectByIdsAndOrgId(@Param("orgId") Long orgId, @Param("roleIds") List<Long> roleIds);


    @Delete({"delete from `role` where org_id = #{orgId}"})
    int deleteByOrg(Long orgId);
}