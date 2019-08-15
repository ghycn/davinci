

package com.huilan.core.model;

import com.huilan.zhihui.common.model.RecordInfo;
import com.huilan.zhihui.model.Source;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class BaseSource extends RecordInfo<Source> {


    public abstract String getJdbcUrl();


    public abstract String getUsername();


    public abstract String getPassword();
}
