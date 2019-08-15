

package com.huilan.zhihui.schedule;

import com.huilan.core.consts.Consts;
import com.huilan.core.exception.ServerException;
import com.huilan.core.utils.CollectionUtils;
import com.huilan.core.utils.DateUtils;
import com.huilan.core.utils.FileUtils;
import com.huilan.core.utils.QuartzUtils;
import com.huilan.zhihui.core.enums.FileTypeEnum;
import com.huilan.zhihui.dao.CronJobMapper;
import com.huilan.zhihui.dao.DownloadRecordMapper;
import com.huilan.zhihui.model.CronJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
public class SystemSchedule {

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private CronJobMapper cronJobMapper;

    @Autowired
    private DownloadRecordMapper downloadRecordMapper;

    @Autowired
    private QuartzUtils quartzUtils;


    @Scheduled(cron = "0 0 1 * * *")
    public void clearTempDir() {

        //下载内容文件保留7天，记录保留1月
        downloadRecordMapper.deleteBeforAMonthRecord();
        String downloadDir = fileUtils.fileBasePath + Consts.DIR_DOWNLOAD + DateUtils.getTheDayBeforAWeekYYYYMMDD();
        String tempDir = fileUtils.fileBasePath + Consts.DIR_TEMPL + DateUtils.getTheDayBeforNowDateYYYYMMDD();
        String csvDir = fileUtils.fileBasePath + File.separator + FileTypeEnum.CSV.getType();

        final String download = fileUtils.formatFilePath(downloadDir);
        final String temp = fileUtils.formatFilePath(tempDir);
        final String csv = fileUtils.formatFilePath(csvDir);

        new Thread(() -> fileUtils.deleteDir(new File(download))).start();
        new Thread(() -> fileUtils.deleteDir(new File(temp))).start();
        new Thread(() -> fileUtils.deleteDir(new File(csv))).start();
    }

    @Scheduled(cron = "0 0/2 * * * *")
    public void stopCronJob() {
        List<CronJob> jobs = cronJobMapper.getStopedJob();
        if (!CollectionUtils.isEmpty(jobs)) {
            for (CronJob job : jobs) {
                try {
                    quartzUtils.removeJob(job);
                } catch (ServerException e) {
                }
            }
        }
    }
}
