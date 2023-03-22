package com.bmsoft.screw.web.excel;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @auther: Zhiyj
 */
public interface ExcelReader {

    List<Map<Integer, Object>> parseList(InputStream inputStream, SheetConfig config);


    <T> List<T> parseList(InputStream inputStream, BeanConfig<T> config);


    SheetResult<Map<Integer, Object>> parseSheetResult(InputStream inputStream, SheetConfig config);


    <T> SheetResult<T> parseSheetResult(InputStream inputStream, BeanConfig<T> config);
}
