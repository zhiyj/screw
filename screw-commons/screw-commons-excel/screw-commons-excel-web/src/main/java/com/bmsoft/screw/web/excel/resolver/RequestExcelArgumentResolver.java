package com.bmsoft.screw.web.excel.resolver;

import com.bmsoft.screw.web.excel.BeanConfig;
import com.bmsoft.screw.web.excel.ExcelReader;
import com.bmsoft.screw.web.excel.SheetConfig;
import com.bmsoft.screw.web.excel.SheetResult;
import com.bmsoft.screw.web.excel.annotation.RequestExcel;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author Zhiyj
 */
public class RequestExcelArgumentResolver implements HandlerMethodArgumentResolver {

    private final ExcelReader excelReader;

    public RequestExcelArgumentResolver(ExcelReader excelReader) {
        this.excelReader = excelReader;
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(RequestExcel.class) != null;
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        RequestExcel requestExcel = parameter.getParameterAnnotation(RequestExcel.class);
        Assert.state(requestExcel != null, "No @RequestExcel");
        boolean required = requestExcel.required();
        MultipartRequest request = webRequest.getNativeRequest(MultipartRequest.class);
        if (request == null) {
            if (required) {
                throw new NullPointerException();
            }
            return null;
        }
        MultipartFile multipartFile = request.getFile(requestExcel.value());
        if (multipartFile == null) {
            if (required) {
                throw new NullPointerException();
            }
            return null;
        }

        Class<?> targetClass = requestExcel.targetClass();

        if (List.class.isAssignableFrom(parameter.getParameterType())) {
            if (Map.class.isAssignableFrom(targetClass)) {
                return excelReader.parseList(multipartFile.getInputStream(), getSheetConfig(requestExcel));
            } else {
                return excelReader.parseList(multipartFile.getInputStream(), getBeanConfig(requestExcel));
            }
        } else if (SheetResult.class.isAssignableFrom(parameter.getParameterType())) {
            if (Map.class.isAssignableFrom(targetClass)) {
                return excelReader.parseSheetResult(multipartFile.getInputStream(), getSheetConfig(requestExcel));
            } else {
                return excelReader.parseSheetResult(multipartFile.getInputStream(), getBeanConfig(requestExcel));
            }
        } else {
            String s = "@RequestExcel not support " + parameter.getParameterType().getName();
            if (required) {
                throw new IllegalArgumentException(s);
            }
            return null;
        }
    }

    private SheetConfig getSheetConfig(RequestExcel requestExcel) {
        int listFirstRowNum = requestExcel.listFirstRowNum();
        int listLastRowNum = requestExcel.listLastRowNum();
        boolean isIgnoreException = requestExcel.isIgnoreException();
        SheetConfig sheetConfig = new SheetConfig();
        sheetConfig.setListFirstRowNum(listFirstRowNum);
        sheetConfig.setListLastRowNum(listLastRowNum);
        sheetConfig.setIgnoreException(isIgnoreException);
        return sheetConfig;
    }

    @SuppressWarnings("unchecked")
    private BeanConfig<?> getBeanConfig(RequestExcel requestExcel) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        int listFirstRowNum = requestExcel.listFirstRowNum();
        int listLastRowNum = requestExcel.listLastRowNum();
        boolean isIgnoreException = requestExcel.isIgnoreException();
        Class<?> targetClass = requestExcel.targetClass();
        String[] filedNames = requestExcel.fieldNames();
        BeanConfig<Object> beanConfig = new BeanConfig<>();
        beanConfig.setListFirstRowNum(listFirstRowNum);
        beanConfig.setListLastRowNum(listLastRowNum);
        beanConfig.setIgnoreException(isIgnoreException);
        beanConfig.setTargetClass((Class<Object>) targetClass);
        beanConfig.setFieldNames(filedNames);

        RequestExcel.Node[] map = requestExcel.map();
        for (RequestExcel.Node node : map) {
            beanConfig.putConverter(node.key(), node.value().getDeclaredConstructor().newInstance());
        }

        return beanConfig;
    }
}
