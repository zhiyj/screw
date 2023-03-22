package com.bmsoft.screw.web.excel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther: Zhiyj
 */
@Getter
@Setter
public class SheetResult<T> {
    private List<T> result;
    private String[] columnNames;
    private List<CellException> exceptions;
}
