package com.bmsoft.screw.web.excel;

import java.util.List;

/**
 * @auther: Zhiyj
 */
public interface ExcelFile {
    List<SheetInfo> getSheetInfoList();

    String getFileName();

    String getFileSuffix();
}
