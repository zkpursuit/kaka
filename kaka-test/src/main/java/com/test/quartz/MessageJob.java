package com.test.quartz;

import com.kaka.notice.Facade;
import com.kaka.notice.FacadeFactory;
import com.kaka.notice.Message;
import org.quartz.*;

public class MessageJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        JobKey jobKey = jobDetail.getKey();
        String jobName = jobKey.getName(); //对应Message.what
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        Object obj = jobDataMap.get(jobName);
        if (!(obj instanceof Message)) {
            return;
        }
        Message msg = (Message) obj;
        String facadeName = jobDataMap.getString("facade");
        Facade facade = FacadeFactory.getFacade(facadeName);
        facade.sendMessage(msg);
    }
}
