package com.bmsoft.screw.web.excel.annotation;


import com.bmsoft.screw.web.excel.DefaultSheetStyle;
import com.bmsoft.screw.web.excel.FieldHandler;
import com.bmsoft.screw.web.excel.SheetStyle;

import java.lang.annotation.*;

/**
 * @author Zhiyj
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseExcel {

    String sheetName() default "default";

    String title() default "";

    /**
     * columnNames 为空的时候，将会使用 fieldNames
     */
    String[] columnNames() default {};

    /**
     * 此处的 value 指的是 fieldNames
     *
     * @see #fieldNames()
     */
    String[] value() default {};

    /**
     * @since 1.1
     */
    String[] fieldNames() default {};


    Class<? extends SheetStyle> sheetStyle() default DefaultSheetStyle.class;

    /**
     * fileName 为空的时候将会使用 sheetName
     */
    String fileName() default "";

    String fileSuffix() default ".xlsx";

    /**
     * fieldName 和 FieldHandler 的映射关系
     */
    Node[] map() default {};

    @interface Node {
        String key();

        Class<? extends FieldHandler<?>> value();
    }
}
