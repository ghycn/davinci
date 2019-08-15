

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.organizationDto.OrganizationMember;
import com.huilan.zhihui.model.RelUserOrganization;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface RelUserOrganizationMapper {

    int insert(RelUserOrganization relUserOrganization);

    @Select({"select * from rel_user_organization where user_id = #{userId} and org_id = #{orgId}"})
    RelUserOrganization getRel(@Param("userId") Long userId, @Param("orgId") Long orgId);

    @Delete("delete from rel_user_organization where org_id = #{orgId}")
    int deleteByOrgId(@Param("orgId") Long orgId);


    @Select({
            "SELECT ruo.id, u.id AS 'user.id', ",
            "    IF(u.`name` is NULL,u.username,u.`name`) AS 'user.username', ",
            "    u.email, u.avatar AS 'user.avatar', ruo.role AS 'user.role'",
            "FROM `user` u",
            "LEFT JOIN rel_user_organization ruo on ruo.user_id = u.id",
            "LEFT JOIN organization o on o.id = ruo.org_id",
            "WHERE ruo.org_id = #{orgId}"
    })
    List<OrganizationMember> getOrgMembers(@Param("orgId") Long orgId);


    @Select({"select * from rel_user_organization where id = #{id}"})
    RelUserOrganization getById(@Param("id") Long id);

    @Delete({"delete from rel_user_organization where id = #{id}"})
    int deleteById(@Param("id") Long id);

    @Update({
            "update rel_user_organization set role = #{role},",
            "update_by = #{updateBy,jdbcType=BIGINT},",
            "update_time = #{updateTime,jdbcType=TIMESTAMP}",
            "where id= #{id}"
    })
    int updateMemberRole(RelUserOrganization relUserOrganization);

    int insertBatch(@Param("set") Set<RelUserOrganization> set);


    int deleteBatch(@Param("set") Set<Long> set);

}