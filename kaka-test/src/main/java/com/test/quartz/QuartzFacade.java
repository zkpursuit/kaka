package com.test.quartz;

import com.kaka.notice.Facade;
import com.kaka.notice.Message;
import com.kaka.util.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class QuartzFacade extends Facade {

    private StdSchedulerFactory sf;
    private Scheduler scheduler;

    protected QuartzFacade() {
        try {
            this.sf = new StdSchedulerFactory();
            this.scheduler = this.sf.getScheduler();
        } catch (SchedulerException ex) {
            throw new Error(ex.getLocalizedMessage());
        }
    }

    private void addScheduleJob(JobDetail jobDetail, Trigger trigger) {
        try {
            if (!this.scheduler.isShutdown()) {
                this.scheduler.start();
            }
            this.scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String jobName(Object cmd) {
        String cmdStr = String.valueOf(cmd);
        if (StringUtils.isNumeric(cmdStr)) {
            cmdStr = "numeric_" + cmdStr;
        } else {
            cmdStr = "string_" + cmdStr;
        }
        return cmdStr;
    }

    public void sendMessage(final Message msg, String group, Date start) {
        String jobName = jobName(msg.getWhat());
        cancelQuartzSchedule(jobName, group);
        JobDataMap jdm = new JobDataMap();
        jdm.putIfAbsent(jobName, msg);
        jdm.put("facade", this.getName());
        JobDetail jobDetail = JobBuilder.newJob(MessageJob.class)
                .withIdentity(jobName, group)
                .usingJobData(jdm)
                .build();
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        //simpleScheduleBuilder.withIntervalInMilliseconds(intervalInMillis);
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(jobName, group); //触发器名,触发器组
        if (start != null) builder.startAt(start);
        else builder.startNow();
        Trigger trigger = builder.withSchedule(simpleScheduleBuilder).usingJobData(jdm).build();
        addScheduleJob(jobDetail, trigger);
    }

    /**
     * 定点执行一次
     *
     * @param msg
     * @param group
     * @param cronTime
     */
    public void sendMessage(Message msg, String group, String cronTime) {
        String jobName = jobName(msg.getWhat());
        cancelQuartzSchedule(jobName, group);
        JobDataMap jdm = new JobDataMap();
        jdm.putIfAbsent(jobName, msg);
        jdm.put("facade", this.getName());
        JobDetail jobDetail = JobBuilder.newJob(MessageJob.class)
                .withIdentity(jobName, group)
                .usingJobData(jdm)
                .build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, group)//触发器名,触发器组
                .withSchedule(CronScheduleBuilder.cronSchedule(cronTime))
                .startAt(new Date(System.currentTimeMillis() + 500))
                .build();
        addScheduleJob(jobDetail, trigger);
    }

    private void cancelQuartzSchedule(TriggerKey triggerKey, JobKey jobKey) {
        try {
            if (scheduler.checkExists(triggerKey)) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
            }
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void cancelQuartzSchedule(String jobName, String groupName) {
        TriggerKey triggerKey = new TriggerKey(jobName, groupName);
        JobKey jobKey = new JobKey(jobName, groupName);
        cancelQuartzSchedule(triggerKey, jobKey);
    }

    public void cancelQuartzSchedule(Object cmd, String group) {
        String jobName = jobName(cmd);
        cancelQuartzSchedule(jobName, group);
    }

    private void immediateSchedule(String jobName, String groupName) {
        TriggerKey triggerKey = new TriggerKey(jobName, groupName);
        JobKey jobKey = new JobKey(jobName, groupName);
        JobDetail jobDetail = null;
        try {
            jobDetail = scheduler.getJobDetail(jobKey);
        } catch (SchedulerException e) {
        }
        if (jobDetail == null) return;
        cancelQuartzSchedule(triggerKey, jobKey);
        JobDataMap jdm = jobDetail.getJobDataMap();
        jobDetail = JobBuilder.newJob(MessageJob.class)
                .withIdentity(jobName, groupName)
                .usingJobData(jdm)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, groupName)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .usingJobData(jdm)
                .startNow()
                .build();
        addScheduleJob(jobDetail, trigger);
    }

    public void immediateSchedule(Object cmd, String group) {
        String jobName = jobName(cmd);
        immediateSchedule(jobName, group);
    }

    /**
     * 是否存在调度任务
     *
     * @param cmd
     * @param group
     * @return
     */
    public boolean isExistScheduleJob(Object cmd, String group) {
        String jobName = jobName(cmd);
        TriggerKey triggerKey = new TriggerKey(jobName, group);
        JobKey jobKey = new JobKey(jobName, group);
        try {
            return scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey);
        } catch (SchedulerException e) {
            return false;
        }
    }

    public void shutdownScheduler() {
        if (this.scheduler != null) {
            try {
                this.scheduler.shutdown(false);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

}
