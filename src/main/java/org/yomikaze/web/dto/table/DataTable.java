package org.yomikaze.web.dto.table;

import lombok.Data;

@Data
public class DataTable {
    private DataTableHeader[] headers;
    private DataTableRow[] rows;
}
