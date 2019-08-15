

package com.huilan.zhihui.dao;

import com.huilan.zhihui.model.CronJob;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CronJobMapper {

    int insert(CronJob cronJob);

    @Delete({"delete from cron_job where id = #{id,jdbcType=BIGINT}"})
    int deleteById(@Param("id") Long id);


    @Select({"select * from cron_job where id = #{id}"})
    CronJob getById(@Param("id") Long id);


    @Select({"select * from cron_job where  job_status in ('stopped','failed') and update_time > (NOW() - INTERVAL 3 MINUTE)"})
    List<CronJob> getStopedJob();

    @Update({
            "update cron_job",
            "set `name` = #{name,jdbcType=VARCHAR},",
            "`project_id` = #{projectId,jdbcType=BIGINT},",
            "`job_type` = #{jobType,jdbcType=VARCHAR},",
            "`job_status` = #{jobStatus,jdbcType=VARCHAR},",
            "`cron_expression` = #{cronExpression,jdbcType=VARCHAR},",
            "`start_date` = #{startDate,jdbcType=TIMESTAMP},",
            "`end_date` = #{endDate,jdbcType=TIMESTAMP},",
            "`description` = #{description,jdbcType=VARCHAR},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP},",
            "`config` = #{config,jdbcType=LONGVARCHAR},",
            "`exec_log` = #{execLog,jdbcType=LONGVARCHAR},",
            "`update_by` = #{updateBy,jdbcType=BIGINT},",
            "`update_time` = #{updateTime,jdbcType=TIMESTAMP}",
            "where `id` = #{id,jdbcType=BIGINT}"
    })
    int update(CronJob record);


    @Select({"select id from cron_job where project_id = #{projectId} and `name` = #{name}"})
    Long getByNameWithProjectId(@Param("name") String name, @Param("projectId") Long projectId);

    @Select({"select * from cron_job where project_id = #{projectId}"})
    List<CronJob> getByProject(@Param("projectId") Long projectId);

    @Select({"select * from cron_job where job_status = 'started'"})
    List<CronJob> getStartedJobs();

}