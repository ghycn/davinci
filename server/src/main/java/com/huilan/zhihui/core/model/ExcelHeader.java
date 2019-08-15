

package com.huilan.zhihui.core.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ExcelHeader {
    private String key;
    private String alias;
    private String type;
    private boolean isMerged;
    private int row;
    private int col;
    private int rowspan;
    private int colspan;
    private int[] range;
    private List style;
    private Object format;

    public void setKey(String key) {
        this.key = key;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setIsMerged(boolean merged) {
        isMerged = merged;
    }

    public void setIsMerged(Boolean merged) {
        isMerged = merged;
    }

    public void setRow(String row) {
        this.row = Integer.parseInt(row);
    }

    public void setCol(String col) {
        this.col = Integer.parseInt(col);
    }

    public void setRowspan(String rowspan) {
        this.rowspan = Integer.parseInt(rowspan);
    }

    public void setColspan(String colspan) {
        this.colspan = Integer.parseInt(colspan);
    }

    public void setRange(int[] range) {
        this.range = range;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }
}
