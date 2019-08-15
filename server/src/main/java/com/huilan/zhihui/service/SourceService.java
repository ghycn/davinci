

package com.huilan.zhihui.service;

import com.huilan.core.exception.NotFoundException;
import com.huilan.core.exception.ServerException;
import com.huilan.core.exception.UnAuthorizedExecption;
import com.huilan.core.model.DBTables;
import com.huilan.core.model.TableInfo;
import com.huilan.zhihui.core.service.CheckEntityService;
import com.huilan.zhihui.dto.sourceDto.*;
import com.huilan.zhihui.model.Source;
import com.huilan.zhihui.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SourceService extends CheckEntityService {


    List<Source> getSources(Long projectId, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    Source createSource(SourceCreate sourceCreate, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    Source updateSource(SourceInfo sourceInfo, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean deleteSrouce(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    boolean testSource(SourceTest sourceTest) throws ServerException;

    void validCsvmeta(Long sourceId, UploadMeta uploadMeta, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;

    Boolean dataUpload(Long sourceId, SourceDataUpload sourceDataUpload, MultipartFile file, User user, String type) throws NotFoundException, UnAuthorizedExecption, ServerException;

    List<String> getSourceDbs(Long id, User user) throws NotFoundException, ServerException;

    DBTables getSourceTables(Long id, String dbName, User user) throws NotFoundException;

    TableInfo getTableInfo(Long id, String dbName, String tableName, User user) throws NotFoundException;

    boolean isTestConnection(SourceConfig config) throws ServerException;

    SourceDetail getSourceDetail(Long id, User user) throws NotFoundException, UnAuthorizedExecption, ServerException;
}
