package com.bmsoft.screw.web.excel;

/**
 * @auther: Zhiyj
 */
public class CellException extends RuntimeException{
    public CellException() {
    }

    public CellException(String message) {
        super(message);
    }

    public CellException(Throwable cause) {
        super(cause);
    }

    public CellException(String message, Throwable cause) {
        super(message, cause);
    }

    public CellException(Throwable cause, int i, int j) {
        super("[rowIndex:" + i + ",columnIndex:" + j + "]" + cause.getMessage(), cause);
    }
}
