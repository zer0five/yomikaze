package org.yomikaze.web.dto.table;

import lombok.Data;
import org.hibernate.boot.model.source.spi.Sortable;

@Data
public class DataTableHeader {
    private String name;
    private Sortable sortable;
}
