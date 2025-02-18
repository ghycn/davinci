

package com.huilan.zhihui.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.huilan.core.common.job.ScheduleService;
import com.huilan.core.exception.ServerException;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.enums.CheckEntityEnum;
import com.huilan.zhihui.core.enums.CronJobMediaType;
import com.huilan.zhihui.core.enums.FileTypeEnum;
import com.huilan.zhihui.dto.cronJobDto.CronJobConfig;
import com.huilan.zhihui.dto.cronJobDto.CronJobContent;
import com.huilan.zhihui.dto.dashboardDto.DashboardWithPortal;
import com.huilan.zhihui.dto.projectDto.ProjectDetail;
import com.huilan.zhihui.dto.viewDto.ViewExecuteParam;
import com.huilan.zhihui.dto.widgetDto.WidgetWithRelationDashboardId;
import com.huilan.zhihui.model.CronJob;
import com.huilan.zhihui.model.Display;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.model.Widget;
import com.huilan.zhihui.service.ProjectService;
import com.huilan.zhihui.service.ShareService;
import com.huilan.zhihui.service.WidgetService;
import com.huilan.core.consts.Consts;
import com.huilan.core.utils.*;
import com.huilan.zhihui.common.utils.ScriptUtiils;
import com.huilan.zhihui.dao.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("emailScheduleService")
public class EmailScheduleServiceImpl implements ScheduleService {

    @Autowired
    private CronJobMapper cronJobMapper;

    @Autowired
    private MailUtils mailUtils;

    @Value("${file.phantomJs-path}")
    private String phantomJsFile;

    @Value("${phantomjs_home}")
    private String phantomJsHome;

    @Value("${file.userfiles-path}")
    private String fileBasePath;

    @Autowired
    private WidgetMapper widgetMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private DisplayMapper displayMapper;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private ServerUtils serverUtils;


    private volatile boolean imageExit = false;

    private static final String portal = "PORTAL";

    /**
     * outlook最大图片显示高度为1728px
     */
    private final int imageMaxHeight = 1720;

    private final String baseUrl = File.separator + "tempFiles" + File.separator;

    @Override
    public void execute(long jobId) throws Exception {
        CronJob cronJob = cronJobMapper.getById(jobId);
        if (null != cronJob && !StringUtils.isEmpty(cronJob.getConfig())) {
            CronJobConfig cronJobConfig = JSONObject.parseObject(cronJob.getConfig(), CronJobConfig.class);
            if (null != cronJobConfig && !StringUtils.isEmpty(cronJobConfig.getType())) {
                Map<String, Object> content = new HashMap<>();
                User user = userMapper.selectByEmail(cronJobConfig.getTo());
                String username = cronJobConfig.getTo().split("@")[0];
                if (null != user) {
                    username = StringUtils.isEmpty(user.getName()) ? user.getUsername() : user.getName();
                }
                content.put("username", username);

                List<File> attachments = null;
                if (cronJobConfig.getType().equals(CronJobMediaType.IMAGE.getType())) {
                    attachments = generateImages(cronJobConfig, cronJob.getCreateBy());
                } else if (cronJobConfig.getType().equals(CronJobMediaType.EXCEL.getType())) {
                    attachments = generateExcels(cronJobConfig, user);
                } else if (cronJobConfig.getType().equals(CronJobMediaType.IMAGEANDEXCEL.getType())) {
                    attachments = new ArrayList<>();
                    attachments.addAll(generateImages(cronJobConfig, cronJob.getCreateBy()));
                    attachments.addAll(generateExcels(cronJobConfig, user));
                }

                String[] cc = null, bcc = null;
                if (!StringUtils.isEmpty(cronJobConfig.getCc())) {
                    cc = cronJobConfig.getCc().split(Consts.SEMICOLON);
                }

                if (!StringUtils.isEmpty(cronJobConfig.getBcc())) {
                    bcc = cronJobConfig.getBcc().split(Consts.SEMICOLON);
                }

                mailUtils.sendTemplateAttachmentsEmail(
                        cronJobConfig.getSubject(),
                        cronJobConfig.getTo(),
                        cc,
                        bcc,
                        Constants.SCHEDULE_MAIL_TEMPLATE,
                        content,
                        attachments);
            }
        }
    }


    /**
     * 根据job配置截取图片
     *
     * @param cronJobConfig
     * @param userId
     * @return
     * @throws Exception
     */
    private List<File> generateImages(CronJobConfig cronJobConfig, Long userId) throws Exception {
        List<File> files = new ArrayList<>();
        for (CronJobContent cronJobContent : cronJobConfig.getContentList()) {
            String imageName = UUID.randomUUID() + ".png";
            String imageUrl = baseUrl + DateUtils.getNowDateYYYYMM() + File.separator + imageName;
            String imagePath = fileBasePath + imageUrl;
            File file = new File(fileBasePath + baseUrl + DateUtils.getNowDateYYYYMM());
            if (!file.exists()) {
                file.mkdirs();
            }

            imagePath = imagePath.replaceAll(File.separator + "{2,}", File.separator);

            String url = getContentUrl(userId, cronJobContent.getContentType(), cronJobContent.getId());
            boolean bol = phantomRender(url, imagePath);
            if (bol) {
                File image = new File(imagePath);
                files.add(image);
            }
        }
        return files;
    }

    /**
     * phantom打开连接并截图
     *
     * @param url
     * @param imgPath
     * @return
     * @throws Exception
     */
    private boolean phantomRender(String url, String imgPath) throws Exception {
        boolean result = false;
        if (!StringUtils.isEmpty(phantomJsHome) && !StringUtils.isEmpty(phantomJsFile)) {
            String rendJsPath = URLDecoder.decode(phantomJsFile, "UTF-8");
            String cmd = buildCmd(phantomJsHome, rendJsPath, url, imgPath);
            log.info("phantom command : {}", cmd);
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader isr = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(isr);
            String line = input.readLine();
            while (null != line) {
                log.info(line);
                line = input.readLine();
            }
            log.info("Finished command: {}", cmd);
            process.destroy();
            result = checkFileExists(imgPath);
        }
        return result;
    }

    /**
     * 生成运行命令
     *
     * @param args
     * @return
     */
    private String buildCmd(String... args) {
        StringBuilder sb = new StringBuilder();
        for (String exp : args) {
            sb.append(exp).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * 检查文件
     *
     * @param filePath
     * @return
     */
    private boolean checkFileExists(String filePath) {
        boolean result = false;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            while (!imageExit && !new File(filePath).exists()) {
                Thread.sleep(1000);
            }
            return true;
        });

        try {
            result = future.get(10 * 60 * 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        return result;
    }

    private String getContentUrl(Long userId, String contentType, Long contengId) {
        String shareToken = shareService.generateShareToken(contengId, null, userId);
        StringBuilder sb = new StringBuilder();

        String type = "";
        if ("widget".equalsIgnoreCase(contentType)) {
            type = "widget";
        } else if ("portal".equalsIgnoreCase(contentType) || "dashboard".equalsIgnoreCase(contentType)) {
            type = "dashboard";
        } else {
            type = "";
        }

        sb.append(serverUtils.getHost())
                .append("/share.html#/share/")
                .append(contentType.equalsIgnoreCase("widget") || contentType.equalsIgnoreCase("portal") ? "dashboard" : contentType)
                .append("?shareInfo=")
                .append(shareToken);

        if (!StringUtils.isEmpty(type)) {
            sb.append("&type=" + type);
        }

        return sb.toString();
    }


    /**
     * 根据job配置生成excel ，多个excel压缩至zip包
     *
     * @param cronJobConfig
     * @return
     * @throws Exception
     */
    private List<File> generateExcels(CronJobConfig cronJobConfig, User user) throws Exception {
        List<File> files = new ArrayList<>();
        for (CronJobContent cronJobContent : cronJobConfig.getContentList()) {
            if (CheckEntityEnum.DASHBOARD.getSource().equalsIgnoreCase(cronJobContent.getContentType().trim())
                    || portal.equalsIgnoreCase(cronJobContent.getContentType().trim())) {
                DashboardWithPortal dashboard = dashboardMapper.getDashboardWithPortalAndProject(cronJobContent.getId());
                if (dashboard != null) {
                    ScriptEngine engine = ScriptUtiils.getExecuptParamScriptEngine();
                    Map<Long, ViewExecuteParam> executeParamMap = new HashMap<>();
                    Set<WidgetWithRelationDashboardId> set = widgetMapper.getByDashboard(dashboard.getId());
                    Set<Widget> widgets = new HashSet<>();
                    if (!CollectionUtils.isEmpty(set)) {
                        set.forEach(w -> {
                            Widget widget = new Widget();
                            BeanUtils.copyProperties(w, widget);
                            widgets.add(widget);
                            executeParamMap.put(w.getId(), ScriptUtiils.getViewExecuteParam((engine), dashboard.getConfig(), widget.getConfig(), w.getRelationId()));
                        });
                    }
                    if (!CollectionUtils.isEmpty(widgets)) {
                        String filePath = fileBasePath + baseUrl + DateUtils.getNowDateYYYYMM() + File.separator + dashboard.getName() + "-" + UUID.randomUUID() + FileTypeEnum.XLSX.getFormat();

                        filePath = filePath.replaceAll(File.separator + "{2,}", File.separator);

                        ProjectDetail projectDetail = projectService.getProjectDetail(dashboard.getProject().getId(), user, false);
                        File file = widgetService.writeExcel(widgets, projectDetail, executeParamMap, filePath, user, false);
                        files.add(file);
                    }
                }
            } else if (CheckEntityEnum.DISPLAY.getSource().equalsIgnoreCase(cronJobContent.getContentType().trim())) {
                Display display = displayMapper.getById(cronJobContent.getId());
                if (display != null) {
                    Set<Widget> widgets = widgetMapper.getByDisplayId(display.getId());
                    if (!CollectionUtils.isEmpty(widgets)) {
                        String filePath = fileBasePath + baseUrl + DateUtils.getNowDateYYYYMM() + File.separator + display.getName() + "-" + UUID.randomUUID() + FileTypeEnum.XLSX.getFormat();
                        filePath = filePath.replaceAll(File.separator + "{2,}", File.separator);
                        ProjectDetail projectDetail = projectService.getProjectDetail(display.getProjectId(), user, false);
                        File file = widgetService.writeExcel(widgets, projectDetail, null, filePath, user, false);
                        files.add(file);
                    }
                }
            }
        }

        //多个文件压缩至zip包
        if (null != files && files.size() > 1) {
            File zipFile = new File(fileBasePath + baseUrl + DateUtils.getNowDateYYYYMM() + File.separator + UUID.randomUUID() + ".zip");
            FileUtils.zipFile(files, zipFile);
            files.clear();
            files.add(zipFile);
        }
        return files;
    }

}
