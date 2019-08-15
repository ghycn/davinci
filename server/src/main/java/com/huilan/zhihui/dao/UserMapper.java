

package com.huilan.zhihui.dao;

import com.huilan.zhihui.dto.userDto.UserBaseInfo;
import com.huilan.zhihui.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {

    int insert(User user);


    @Select({"select * from `user` where id = #{id}"})
    User getById(@Param("id") Long id);

    @Select({"select * from `user` where `username` = #{username} or `email` = #{username} or `name` = #{username}"})
    User selectByUsername(@Param("username") String username);

    @Select({"select * from `user` where `email` = #{email}"})
    User selectByEmail(@Param("email") String email);


    List<UserBaseInfo> getUsersByKeyword(@Param("keyword") String keyword, @Param("orgId") Long orgId);


    @Update({"update `user` set `name` = #{name}, description = #{description}, department = #{department}, update_time = #{updateTime}",
            "where id = #{id}"})
    int updateBaseInfo(User user);

    @Update({"update user set `avatar` = #{avatar}, update_time = #{updateTime}  where id = #{id}"})
    int updateAvatar(User user);

    @Select({"select id from user where (LOWER(`username`) = LOWER(#{name}) or LOWER(`email`) = LOWER(#{name}) or LOWER(`name`) = LOWER(#{name}))"})
    Long getIdByName(@Param("name") String name);

    @Update({"update `user` set `active` = #{active}, `update_time` = #{updateTime}  where id = #{id}"})
    int activeUser(User user);

    @Update({"update `user` set `password` = #{password}, `update_time` = #{updateTime}  where id = #{id}"})
    int changePassword(User user);

    List<User> getByIds(@Param("userIds") List<Long> userIds);

    @Select({"select count(id) from `user` where `email` = #{email}"})
    boolean existEmail(@Param("email") String email);


    @Select({"select count(id) from `user` where `username` = #{username}"})
    boolean existUsername(@Param("username") String username);
}