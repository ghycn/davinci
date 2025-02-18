

package com.huilan.zhihui.service.excel;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.huilan.core.utils.CollectionUtils;
import com.huilan.core.utils.FileUtils;
import com.huilan.core.utils.SqlUtils;
import com.huilan.zhihui.common.utils.ScriptUtiils;
import com.huilan.zhihui.core.config.SpringContextHolder;
import com.huilan.zhihui.core.enums.FileTypeEnum;
import com.huilan.zhihui.core.model.ExcelHeader;
import com.huilan.zhihui.core.utils.ExcelUtils;
import com.huilan.zhihui.dao.ViewMapper;
import com.huilan.zhihui.dto.viewDto.ViewExecuteParam;
import com.huilan.zhihui.dto.viewDto.ViewWithProjectAndSource;
import com.huilan.zhihui.service.ViewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author daemon
 * @Date 19/5/29 19:29
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class WorkbookWorker<T> extends MsgNotifier implements Callable {


    private WorkBookContext context;


    public WorkbookWorker(WorkBookContext context) {
        this.context = context;
    }


    @Override
    public T call() throws Exception {
        Stopwatch watch = Stopwatch.createStarted();
        Workbook wb = null;
        String filePath = null;
        try {
            List<SheetContext> sheetContextList = buildSheetContextList();
            if (CollectionUtils.isEmpty(sheetContextList)) {
                throw new IllegalArgumentException("sheetContextList is empty");
            }
            wb = new SXSSFWorkbook(1000);
            List<Future> futures = Lists.newArrayList();
            int sheetNo = 0;
            for (SheetContext sheetContext : sheetContextList) {
                sheetNo++;
                String name = String.valueOf(sheetNo) + "-" + sheetContext.getName();
                Sheet sheet = wb.createSheet(name);
                sheetContext.setSheet(sheet);
                sheetContext.setWorkbook(wb);
                sheetContext.setSheetNo(sheetNo);
                Future<Boolean> future = ExecutorUtil.submitSheetTask(sheetContext);
                futures.add(future);
            }
            Boolean rst = false;
            for (Future<Boolean> future : futures) {
                rst = future.get();
            }
            if (rst) {
                filePath = ((FileUtils) SpringContextHolder.getBean(FileUtils.class)).getFilePath(FileTypeEnum.XLSX, this.context.getWrapper().getxId());
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(filePath);
                    wb.write(out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    filePath = null;
                    throw e;
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
            context.getWrapper().setRst(filePath);
            super.tell(context.getWrapper());
        } catch (Exception e) {
            log.error("workbook worker error,e=", e);
            super.tell(context.getWrapper());
            if (StringUtils.isNotEmpty(filePath)) {
                FileUtils.delete(filePath);
            }
        } finally {
            wb = null;
        }
        Object[] args = {StringUtils.isNotEmpty(filePath), context.getWrapper().getAction(), context.getWrapper().getxId(), filePath, watch.elapsed(TimeUnit.MILLISECONDS)};
        log.info("workbook worker complete status={},action={},xid={},filePath={},cost={}ms", args);
        return (T) filePath;
    }


    private List<SheetContext> buildSheetContextList() throws Exception {
        List<SheetContext> sheetContextList = Lists.newArrayList();
        for (WidgetContext context : context.getWidgets()) {

            ViewExecuteParam executeParam = null;
            if (context.isHasExecuteParam() && null != context.getExecuteParam()) {
                executeParam = context.getExecuteParam();
            } else {
                executeParam = ScriptUtiils.getViewExecuteParam(ScriptUtiils.getExecuptParamScriptEngine(),
                        context.getDashboard() != null ? context.getDashboard().getConfig() : null,
                        context.getWidget().getConfig(),
                        context.getMemDashboardWidget() != null ? context.getMemDashboardWidget().getId() : null);
            }

            ViewWithProjectAndSource viewWithProjectAndSource = ((ViewMapper) SpringContextHolder.getBean(ViewMapper.class)).getViewWithProjectAndSourceById(context.getWidget().getViewId());

            SQLContext sqlContext = ((ViewService) SpringContextHolder.getBean(ViewService.class)).getSQLContext(context.getIsMaintainer(), viewWithProjectAndSource, executeParam, this.context.getUser());

            SqlUtils sqlUtils = ((SqlUtils) SpringContextHolder.getBean(SqlUtils.class)).init(viewWithProjectAndSource.getSource());

            boolean isTable;
            List<ExcelHeader> excelHeaders = null;
            if (isTable = ExcelUtils.isTable(context.getWidget().getConfig())) {
                excelHeaders = ScriptUtiils.formatHeader(ScriptUtiils.getCellValueScriptEngine(), context.getWidget().getConfig(),
                        sqlContext.getViewExecuteParam().getParams());
            }
            SheetContext sheetContext = SheetContext.newSheetContextBuilder()
                    .buildExecuteSql(sqlContext.getExecuteSql())
                    .buildQuerySql(sqlContext.getQuerySql())
                    .buildExcludeColumns(sqlContext.getExcludeColumns())
                    .buildContain(Boolean.FALSE)
                    .buildSqlUtils(sqlUtils)
                    .buildIsTable(isTable)
                    .buildHeaders(excelHeaders)
                    .buildDashboardId(null != context.getDashboard() ? context.getDashboard().getId() : null)
                    .buildWidgetId(context.getWidget().getId())
                    .buildName(context.getWidget().getName())
                    .buildWrapper(this.context.getWrapper())
                    .build();
            sheetContextList.add(sheetContext);
        }
        return sheetContextList;
    }
}
