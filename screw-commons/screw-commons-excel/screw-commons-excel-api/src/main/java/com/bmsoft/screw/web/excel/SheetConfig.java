package com.bmsoft.screw.web.excel;


import lombok.Getter;
import lombok.Setter;

/**
 * @auther: Zhiyj
 */
@Getter
@Setter
public class SheetConfig {
    private int listFirstRowNum = -1;
    private int listLastRowNum = Integer.MAX_VALUE;
    private boolean isIgnoreException = false;

}
