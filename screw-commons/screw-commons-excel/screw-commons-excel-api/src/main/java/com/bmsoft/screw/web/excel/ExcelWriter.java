package com.bmsoft.screw.web.excel;

import java.io.OutputStream;
import java.util.List;

/**
 * @auther: Zhiyj
 */
public interface ExcelWriter {

    void write(SheetInfo sheetInfo, OutputStream outputStream);

    void write(List<SheetInfo> sheetInfoList, OutputStream outputStream);
}
