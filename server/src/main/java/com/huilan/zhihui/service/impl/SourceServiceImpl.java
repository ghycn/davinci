

package com.huilan.zhihui.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.huilan.core.enums.DataTypeEnum;
import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.SourceException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.core.model.DBTables;
import com.huilan.core.model.QueryColumn;
import com.huilan.core.model.TableInfo;
import com.huilan.core.utils.CollectionUtils;
import com.huilan.core.utils.DateUtils;
import com.huilan.core.utils.FileUtils;
import com.huilan.core.utils.SqlUtils;
import com.huilan.zhihui.core.common.Constants;
import com.huilan.zhihui.core.model.DataUploadEntity;
import com.huilan.zhihui.core.utils.CsvUtils;
import com.huilan.zhihui.core.utils.ExcelUtils;
import com.huilan.zhihui.dao.SourceMapper;
import com.huilan.zhihui.dao.ViewMapper;
import com.huilan.zhihui.dto.projectDto.ProjectDetail;
import com.huilan.zhihui.dto.projectDto.ProjectPermission;
import com.huilan.zhihui.model.Source;
import com.huilan.zhihui.model.User;
import com.huilan.zhihui.model.View;
import com.huilan.zhihui.service.ProjectService;
import com.huilan.zhihui.service.SourceService;
import com.huilan.zhihui.core.enums.*;
import com.huilan.zhihui.dto.sourceDto.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Slf4j
@Service("sourceService")
public class SourceServiceImpl implements SourceService {

    private static final Logger optLogger = LoggerFactory.getLogger(LogNameEnum.BUSINESS_OPERATION.getName());

    @Autowired
    private SourceMapper sourceMapper;

    @Autowired
    private SqlUtils sqlUtils;

    @Autowired
    private ViewMapper viewMapper;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private ProjectService projectService;

    @Override
    public synchronized boolean isExist(String name, Long id, Long projectId) {
        Long sourceId = sourceMapper.getByNameWithProjectId(name, projectId);
        if (null != id && null != sourceId) {
            return !id.equals(sourceId);
        }
        return null != sourceId && sourceId.longValue() > 0L;
    }

    /**
     * 获取source列表
     *
     * @param projectId
     * @param user
     * @return
     */
    @Override
    public List<Source> getSources(Long projectId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        ProjectDetail projectDetail = null;
        try {
            projectDetail = projectService.getProjectDetail(projectId, user, false);
        } catch (NotFoundException e) {
            throw e;
        } catch (UnAuthorizedExecption e) {
            return null;
        }

        List<Source> sources = sourceMapper.getByProject(projectId);

        if (!CollectionUtils.isEmpty(sources)) {
            ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
            if (projectPermission.getSourcePermission() == UserPermissionEnum.HIDDEN.getPermission()) {
                sources = null;
            }
        }
        return sources;
    }

    @Override
    public SourceDetail getSourceDetail(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {

        Source source = sourceMapper.getById(id);

        if (null == source) {
            throw new NotFoundException("source is not found");
        }

        ProjectPermission projectPermission = projectService.getProjectPermission(projectService.getProjectDetail(source.getProjectId(), user, false), user);

        if (projectPermission.getSourcePermission() == UserPermissionEnum.HIDDEN.getPermission()) {
            throw new UnAuthorizedExecption();
        }

        SourceDetail sourceDetail = new SourceDetail();
        BeanUtils.copyProperties(source, sourceDetail);

        if (projectPermission.getSourcePermission() == UserPermissionEnum.READ.getPermission()) {
            sourceDetail.setConfig(null);
        }

        return sourceDetail;
    }

    /**
     * 创建source
     *
     * @param sourceCreate
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Source createSource(SourceCreate sourceCreate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        ProjectDetail projectDetail = projectService.getProjectDetail(sourceCreate.getProjectId(), user, false);

        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
        if (projectPermission.getSourcePermission() < UserPermissionEnum.WRITE.getPermission()) {
            throw new UnAuthorizedExecption("you have not permission to create source");
        }

        if (isExist(sourceCreate.getName(), null, sourceCreate.getProjectId())) {
            log.info("the source {} name is already taken", sourceCreate.getName());
            throw new ServerException("the source name is already taken");
        }

        if (null == SourceTypeEnum.typeOf(sourceCreate.getType())) {
            throw new ServerException("Invalid source type");
        }

        //测试连接
        boolean testConnection = isTestConnection(sourceCreate.getConfig());

        if (testConnection) {
            Source source = new Source().createdBy(user.getId());
            BeanUtils.copyProperties(sourceCreate, source);
            source.setConfig(JSONObject.toJSONString(sourceCreate.getConfig()));

            int insert = sourceMapper.insert(source);
            if (insert > 0) {
                optLogger.info("source ({}) create by user (:{})", source.toString(), user.getId());
                return source;
            } else {
                throw new ServerException("create source fail");
            }
        } else {
            throw new ServerException("get source connection fail");
        }
    }

    /**
     * 修改source
     *
     * @param sourceInfo
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Source updateSource(SourceInfo sourceInfo, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        Source source = sourceMapper.getById(sourceInfo.getId());
        if (null == source) {
            log.warn("source (:{}) is not found", sourceInfo.getId());
            throw new NotFoundException("this source is not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(source.getProjectId(), user, false);

        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);

        if (projectPermission.getSourcePermission() < UserPermissionEnum.WRITE.getPermission()) {
            throw new UnAuthorizedExecption("you have not permission to update this source");
        }

        if (isExist(sourceInfo.getName(), sourceInfo.getId(), projectDetail.getId())) {
            log.info("the source {} name is already taken", sourceInfo.getName());
            throw new ServerException("the source name is already taken");
        }

        //测试连接
        boolean testConnection = isTestConnection(sourceInfo.getConfig());

        if (testConnection) {
            String origin = source.toString();

            BeanUtils.copyProperties(sourceInfo, source);
            source.updatedBy(user.getId());
            source.setConfig(JSONObject.toJSONString(sourceInfo.getConfig()));

            int update = sourceMapper.update(source);
            if (update > 0) {
                optLogger.info("source ({}) update by user(:{}), origin ( {} )", source.toString(), user.getId(), origin);
                return source;
            } else {
                log.info("update source fail: {}", source.toString());
                throw new ServerException("update source fail: unspecified error");
            }
        } else {
            throw new ServerException("get source connection fail");
        }
    }

    /**
     * 删除source
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    @Transactional
    public boolean deleteSrouce(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {

        Source source = sourceMapper.getById(id);
        if (null == source) {
            log.info("source (:{}) is not found", id);
            throw new NotFoundException("this source is not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(source.getProjectId(), user, false);

        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);

        if (projectPermission.getSourcePermission() < UserPermissionEnum.DELETE.getPermission()) {
            throw new UnAuthorizedExecption("you have not permission to delete this source");
        }

        List<View> viewList = viewMapper.getBySourceId(id);
        if (!CollectionUtils.isEmpty(viewList)) {
            log.warn("There is at least one view using the source({}), it is can not be deleted", id);
            throw new ServerException("There is at least one view using the source, it is can not be deleted");
        }

        int i = sourceMapper.deleteById(id);
        if (i > 0) {
            optLogger.info("source ({}) delete by user(:{})", source.toString(), user.getId());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 测试连接
     *
     * @param sourceTest
     * @return
     */
    @Override
    public boolean testSource(SourceTest sourceTest) throws ServerException {
        boolean testConnection = false;
        try {
            testConnection = sqlUtils.init(sourceTest.getUrl(), sourceTest.getUsername(), sourceTest.getPassword()).testConnection();
        } catch (SourceException e) {
            log.error(e.getMessage());
            throw new ServerException(e.getMessage());
        }
        if (testConnection) {
            return true;
        } else {
            throw new ServerException("get source connection fail");
        }
    }

    /**
     * 生成csv对应的表结构
     *
     * @param sourceId
     * @param uploadMeta
     * @param user
     * @return
     */
    @Override
    public void validCsvmeta(Long sourceId, UploadMeta uploadMeta, User user) throws NotFoundException, UnAuthorizedExecption, ServerException {
        Source source = sourceMapper.getById(sourceId);
        if (null == source) {
            log.info("source (:{}) not found", sourceId);
            throw new ServerException("source not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(source.getProjectId(), user, false);

        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);

        if (projectPermission.getSourcePermission() < UserPermissionEnum.WRITE.getPermission()) {
            throw new UnAuthorizedExecption("you have not permisson to upload csv file in this source");
        }

        if (uploadMeta.getMode() != UploadModeEnum.REPLACE.getMode()) {
            try {
                boolean tableIsExist = sqlUtils.init(source).tableIsExist(uploadMeta.getTableName());
                if (uploadMeta.getMode() == UploadModeEnum.NEW.getMode()) {
                    if (tableIsExist) {
                        throw new ServerException("table " + uploadMeta.getTableName() + " is already exist");
                    }
                } else {
                    if (!tableIsExist) {
                        throw new ServerException("table " + uploadMeta.getTableName() + " is not exist");
                    }
                }
            } catch (SourceException e) {
                log.error(e.getMessage());
                throw new ServerException(e.getMessage());
            }
        }
    }

    /**
     * 上传csv文件
     *
     * @param sourceId
     * @param sourceDataUpload
     * @param file
     * @param user
     * @param type
     * @return
     */
    @Override
    @Transactional
    public Boolean dataUpload(Long sourceId, SourceDataUpload sourceDataUpload, MultipartFile file, User user, String type) throws NotFoundException, UnAuthorizedExecption, ServerException {

        Source source = sourceMapper.getById(sourceId);
        if (null == source) {
            log.info("source (:{}) not found", sourceId);
            throw new NotFoundException("source is not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(source.getProjectId(), user, false);

        ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);

        if (projectPermission.getSourcePermission() < UserPermissionEnum.WRITE.getPermission()) {
            throw new UnAuthorizedExecption("you have not permission to upload data in this source");
        }

        if (!type.equals(FileTypeEnum.CSV.getType()) && !type.equals(FileTypeEnum.XLSX.getType()) && !type.equals(FileTypeEnum.XLS.getType())) {
            throw new ServerException("Unsupported file format");
        }

        //校验文件是否csv文件
        if (type.equals(FileTypeEnum.CSV.getType()) && !fileUtils.isCsv(file)) {
            throw new ServerException("Please upload csv file");
        }

        if (type.equals(FileTypeEnum.XLSX.getType()) && !fileUtils.isExcel(file)) {
            throw new ServerException("Please upload excel file");
        }

        DataTypeEnum dataTypeEnum = DataTypeEnum.urlOf(source.getJdbcUrl());
        if (dataTypeEnum != DataTypeEnum.MYSQL) {
            log.info("Unsupported data source， {}", source.getJdbcUrl());
            throw new ServerException("Unsupported data source: " + source.getJdbcUrl());
        }


        try {
            DataUploadEntity dataUploadEntity = null;
            if (type.equals(FileTypeEnum.CSV.getType())) {
                //解析csv文件
                dataUploadEntity = CsvUtils.parseCsvWithFirstAsHeader(file, "UTF-8");
            } else {
                //解析excel文件
                dataUploadEntity = ExcelUtils.parseExcelWithFirstAsHeader(file);
            }

            if (null != dataUploadEntity && !CollectionUtils.isEmpty(dataUploadEntity.getHeaders())) {
                //建表
                createTable(dataUploadEntity.getHeaders(), sourceDataUpload, source);

                //传输数据
                insertData(dataUploadEntity.getHeaders(), dataUploadEntity.getValues(), sourceDataUpload, source);
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }

        return true;
    }


    /**
     * 获取Source 的 db
     *
     * @param id
     * @param user
     * @return
     * @throws NotFoundException
     * @throws ServerException
     */
    @Override
    public List<String> getSourceDbs(Long id, User user) throws NotFoundException, ServerException {
        Source source = sourceMapper.getById(id);

        if (null == source) {
            log.info("source (:{}) not found", id);
            throw new NotFoundException("source is not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(source.getProjectId(), user, false);

        List<String> dbList = null;

        try {
            dbList = sqlUtils.init(source.getJdbcUrl(), source.getUsername(), source.getPassword()).getDatabases();
        } catch (SourceException e) {
            throw new ServerException(e.getMessage());
        }

        if (null != dbList) {
            ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
            if (projectPermission.getSourcePermission() == UserPermissionEnum.HIDDEN.getPermission()) {
                log.info("user (:{}) have not permission to get source(:{}) databases", user.getId(), source.getId());
                dbList = null;
            }
        }

        return dbList;
    }

    /**
     * 获取Source的data tables
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    public DBTables getSourceTables(Long id, String dbName, User user) throws NotFoundException {


        DBTables dbTable = new DBTables(dbName);

        Source source = sourceMapper.getById(id);

        if (null == source) {
            log.info("source (:{}) not found", id);
            throw new NotFoundException("source is not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(source.getProjectId(), user, false);


        List<QueryColumn> tableList = null;
        try {
            tableList = sqlUtils.init(source.getJdbcUrl(), source.getUsername(), source.getPassword()).getTableList(dbName);
        } catch (SourceException e) {
            throw new ServerException(e.getMessage());
        }

        if (null != tableList) {
            ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
            if (projectPermission.getSourcePermission() == UserPermissionEnum.HIDDEN.getPermission()) {
                log.info("user (:{}) have not permission to get source(:{}) tables", user.getId(), source.getId());
                tableList = null;
            }
        }

        if (null != tableList) {
            dbTable.setTables(tableList);
        }
        return dbTable;
    }


    /**
     * 获取Source的data tables
     *
     * @param id
     * @param user
     * @return
     */
    @Override
    public TableInfo getTableInfo(Long id, String dbName, String tableName, User user) throws NotFoundException {

        Source source = sourceMapper.getById(id);
        if (null == source) {
            log.info("source (:{}) is not found", id);
            throw new NotFoundException("source is not found");
        }

        ProjectDetail projectDetail = projectService.getProjectDetail(source.getProjectId(), user, false);

        TableInfo tableInfo = null;
        try {
            tableInfo = sqlUtils.init(source.getJdbcUrl(), source.getUsername(), source.getPassword()).getTableInfo(dbName, tableName);
        } catch (SourceException e) {
            e.printStackTrace();
            throw new ServerException(e.getMessage());
        }

        if (null != tableInfo) {
            ProjectPermission projectPermission = projectService.getProjectPermission(projectDetail, user);
            if (projectPermission.getSourcePermission() == UserPermissionEnum.HIDDEN.getPermission()) {
                log.info("user (:{}) have not permission to get source(:{}) table columns", user.getId(), source.getId());
                tableInfo = null;
            }
        }


        return tableInfo;
    }

    public boolean isTestConnection(SourceConfig config) throws ServerException {
        try {
            return sqlUtils.init(config.getUrl(), config.getUsername(), config.getPassword()).testConnection();
        } catch (SourceException e) {
            log.error(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 建表
     *
     * @param fileds
     * @param sourceDataUpload
     * @param source
     * @throws ServerException
     */
    private void createTable(Set<QueryColumn> fileds, SourceDataUpload sourceDataUpload, Source source) throws ServerException {

        if (CollectionUtils.isEmpty(fileds)) {
            throw new ServerException("there is have not any fileds");
        }

        SqlUtils sqlUtils = this.sqlUtils.init(source);

        STGroup stg = new STGroupFile(Constants.SQL_TEMPLATE);

        String sql = null;

        if (sourceDataUpload.getMode() == UploadModeEnum.REPLACE.getMode()) {
            ST st = stg.getInstanceOf("createTable");
            st.add("tableName", sourceDataUpload.getTableName());
            st.add("fields", fileds);
            st.add("primaryKeys", StringUtils.isEmpty(sourceDataUpload.getPrimaryKeys()) ? null : sourceDataUpload.getPrimaryKeys().split(","));
            st.add("indexKeys", sourceDataUpload.getIndexList());
            sql = st.render();
            String dropSql = "DROP TABLE IF EXISTS `" + sourceDataUpload.getTableName() + "`";
            sqlUtils.jdbcTemplate().execute(dropSql);
            log.info("drop table sql : {}", dropSql);
        } else {
            boolean tableIsExist = sqlUtils.tableIsExist(sourceDataUpload.getTableName());
            if (sourceDataUpload.getMode() == UploadModeEnum.NEW.getMode()) {
                if (!tableIsExist) {
                    ST st = stg.getInstanceOf("createTable");
                    st.add("tableName", sourceDataUpload.getTableName());
                    st.add("fields", fileds);
                    st.add("primaryKeys", sourceDataUpload.getPrimaryKeys());
                    st.add("indexKeys", sourceDataUpload.getIndexList());

                    sql = st.render();
                } else {
                    throw new ServerException("table " + sourceDataUpload.getTableName() + " is already exist");
                }
            } else {
                if (!tableIsExist) {
                    throw new ServerException("table " + sourceDataUpload.getTableName() + " is not exist");
                }
            }
        }

        log.info("create table sql : {}", sql);
        try {
            if (!StringUtils.isEmpty(sql)) {
                sqlUtils.jdbcTemplate().execute(sql);
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }


    /**
     * 插入数据
     *
     * @param headers
     * @param values
     * @param sourceDataUpload
     * @param source
     */
    private void insertData(Set<QueryColumn> headers, List<Map<String, Object>> values, SourceDataUpload sourceDataUpload, Source source) throws ServerException {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }

        SqlUtils sqlUtils = this.sqlUtils.init(source);

        try {
            if (sourceDataUpload.getMode() == UploadModeEnum.REPLACE.getMode()) {
                //清空表
                sqlUtils.jdbcTemplate().execute("Truncate table `" + sourceDataUpload.getTableName() + "`");
                //插入数据
                executeInsert(sourceDataUpload.getTableName(), headers, values, sqlUtils);
            } else {
                boolean tableIsExist = sqlUtils.tableIsExist(sourceDataUpload.getTableName());
                if (tableIsExist) {
                    executeInsert(sourceDataUpload.getTableName(), headers, values, sqlUtils);
                } else {
                    throw new ServerException("table " + sourceDataUpload.getTableName() + " is not exist");
                }
            }
        } catch (ServerException e) {
            e.printStackTrace();
            throw new ServerException(e.getMessage());
        }

    }


    /**
     * 多线程执行插入数据
     *
     * @param tableName
     * @param headers
     * @param values
     * @param sqlUtils
     * @throws ServerException
     */
    private void executeInsert(String tableName, Set<QueryColumn> headers, List<Map<String, Object>> values, SqlUtils sqlUtils) throws ServerException {
        if (!CollectionUtils.isEmpty(values)) {
            int len = 1000;
            int totalSize = values.size();
            int pageSize = len;
            int totalPage = totalSize / pageSize;
            if (totalSize % pageSize != 0) {
                totalPage += 1;
                if (totalSize < pageSize) {
                    pageSize = values.size();
                }
            }

            ExecutorService executorService = Executors.newFixedThreadPool(8);

            STGroup stg = new STGroupFile(Constants.SQL_TEMPLATE);
            ST st = stg.getInstanceOf("insertData");
            st.add("tableName", tableName);
            st.add("columns", headers);
            String sql = st.render();
            log.info("sql : {}", st.render());
            Future future = null;

            //分页批量插入
            long startTime = System.currentTimeMillis();
            log.info("execute insert start ----  {}", DateUtils.toyyyyMMddHHmmss(startTime));
            for (int pageNum = 1; pageNum < totalPage + 1; pageNum++) {
                int localPageNum = pageNum;
                int localPageSize = pageSize;
                future = executorService.submit(() -> {
                    int starNum = (localPageNum - 1) * localPageSize;
                    int endNum = localPageNum * localPageSize > totalSize ? (totalSize) : localPageNum * localPageSize;
                    log.info("executeInsert thread-{} : start:{}, end: {}", localPageNum, starNum, endNum);
                    sqlUtils.executeBatch(sql, headers, values.subList(starNum, endNum));
                });
            }

            try {
                future.get();

                long endTime = System.currentTimeMillis();
                log.info("execute insert end ----  {}", DateUtils.toyyyyMMddHHmmss(endTime));
                log.info("execution time {} second", (endTime - startTime) / 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new ServerException(e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new ServerException(e.getMessage());
            } finally {
                executorService.shutdown();
            }
        }
    }
}
