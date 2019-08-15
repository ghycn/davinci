

package com.huilan.zhihui.dto.viewDto;

import com.huilan.core.model.Paginate;
import com.huilan.core.model.QueryColumn;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExecuteSqlResult extends Paginate<Map<String, Object>> {

    private List<QueryColumn> columns;

    public ExecuteSqlResult(List<QueryColumn> columns, Paginate<Map<String, Object>> paginate) {
        this.columns = columns;
        this.setPageNo(paginate.getPageNo());
        this.setPageSize(paginate.getPageSize());
        this.setTotalCount(paginate.getTotalCount());
        this.setResultList(paginate.getResultList());
    }
}
