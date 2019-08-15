

package com.huilan.core.utils;

import com.huilan.core.common.job.QuartzJobFactory;
import com.huilan.core.consts.Consts;
import com.huilan.core.exception.ServerException;
import com.huilan.core.model.ScheduleJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class QuartzUtils {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    public void addJob(ScheduleJob scheduleJob) throws ServerException {

        if (null == scheduleJob) {
            throw new ServerException("EMPTY job");
        }

        if (System.currentTimeMillis() < scheduleJob.getStartDate().getTime()
                || System.currentTimeMillis() > scheduleJob.getEndDate().getTime()) {
            throw new ServerException("Current time is not within the planned execution time!");
        }

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getId().toString());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (null != trigger) {
                throw new ServerException("job already started!");
            }

            JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class).withIdentity(scheduleJob.getId().toString()).build();
            jobDetail.getJobDataMap().put(Consts.SCHEDULE_JOB_DATA_KEY, scheduleJob);

            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder
                    .newTrigger()
                    .withIdentity(scheduleJob.getId().toString())
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression()))
                    .endAt(scheduleJob.getEndDate());

            trigger = triggerBuilder.build();
            scheduler.scheduleJob(jobDetail, trigger);

            if (!scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void removeJob(ScheduleJob scheduleJob) throws ServerException {

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getId().toString());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (null != trigger) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(JobKey.jobKey(scheduleJob.getId().toString()));
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void modifyJob(ScheduleJob scheduleJob) throws ServerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getId().toString());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (null == trigger) {
                return;
            }

            String oldExp = trigger.getCronExpression();
            if (!oldExp.equalsIgnoreCase(scheduleJob.getCronExpression())) {
                removeJob(scheduleJob);
                addJob(scheduleJob);
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    public void startJobs() throws ServerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            throw new ServerException(e.getMessage());
        }
    }


    public void shutdownJobs() throws ServerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            throw new ServerException(e.getMessage());
        }
    }

}
