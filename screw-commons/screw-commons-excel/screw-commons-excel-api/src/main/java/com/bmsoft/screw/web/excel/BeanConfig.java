package com.bmsoft.screw.web.excel;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther: Zhiyj
 */
@Getter
@Setter
public class BeanConfig<T> extends SheetConfig {

    private Class<T> targetClass;
    private String[] fieldNames;
    private final Map<String, CellConverter> converterMap = new HashMap();


    public void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public BeanConfig<T> putConverter(String fieldName, CellConverter converter) {
        if (this.isAtFieldNames(fieldName)) {
            this.converterMap.put(fieldName, converter);
            return this;
        } else {
            throw new IllegalArgumentException("no such fieldName, please put in array fieldNames");
        }
    }

    private boolean isAtFieldNames(String fieldName) {
        if (this.fieldNames != null) {
            String[] var2 = this.fieldNames;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String name = var2[var4];
                if (name.equals(fieldName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
