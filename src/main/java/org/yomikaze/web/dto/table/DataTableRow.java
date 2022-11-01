package org.yomikaze.web.dto.table;

import lombok.Data;

@Data
public class DataTableRow {

    private Object[] cells;

    public DataTableRow(Object... cells) {
        this.cells = cells;
    }
}
