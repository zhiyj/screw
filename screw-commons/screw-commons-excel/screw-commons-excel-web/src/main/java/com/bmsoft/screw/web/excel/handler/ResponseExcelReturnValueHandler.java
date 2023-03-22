package com.bmsoft.screw.web.excel.handler;

import com.bmsoft.screw.web.excel.DefaultSheetStyle;
import com.bmsoft.screw.web.excel.ExcelWriter;
import com.bmsoft.screw.web.excel.SheetInfo;
import com.bmsoft.screw.web.excel.SheetStyle;
import com.bmsoft.screw.web.excel.annotation.ResponseExcel;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * 处理 Spring MVC 框架函数带有 ResponseExcel 注解的返回值，将其解析为文件下载
 *
 * @author Zhiyj
 */
public class ResponseExcelReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final ExcelWriter excelWriter;

    public ResponseExcelReturnValueHandler(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return methodParameter.getMethodAnnotation(ResponseExcel.class) != null;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest nativeWebRequest) throws Exception {
        /* check */
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        ResponseExcel responseExcel = methodParameter.getMethodAnnotation(ResponseExcel.class);
        Assert.state(responseExcel != null, "No @ResponseExcel");
        mavContainer.setRequestHandled(true);

        /* return value check */
        if (!(returnValue instanceof List)) {
            String msg = "return value is null or not support type, can not build excel";
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
            response.getWriter().flush();
            return;
        }
        List<?> returnList = (List<?>) returnValue;

        /* ResponseExcel parameter check */
        String defaultString = "";
        String sheetName = responseExcel.sheetName();
        String[] fieldNames = getFieldNames(responseExcel);
        if (defaultString.equals(sheetName) || fieldNames.length == 0) {
            String msg = "not specify sheet name or fields, can not build excel";
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
            response.getWriter().flush();
            return;
        }

        /* set sheet info */
        String title = responseExcel.title();
        String[] columnNames = responseExcel.columnNames().length == 0 ? fieldNames : responseExcel.columnNames();
        String fileName = defaultString.equals(responseExcel.fileName()) ? responseExcel.sheetName() : responseExcel.fileName();
        String fileSuffix = responseExcel.fileSuffix();
        SheetInfo sheetInfo = new SheetInfo(sheetName, title, columnNames, fieldNames, returnList);

        ResponseExcel.Node[] map = responseExcel.map();
        for (ResponseExcel.Node node : map) {
            sheetInfo.putFieldHandler(node.key(), node.value().getDeclaredConstructor().newInstance());
        }

        /* set sheet style */
        Class<?> sheetStyleClass = responseExcel.sheetStyle();
        if (sheetStyleClass != SheetStyle.class && sheetStyleClass != DefaultSheetStyle.class) {
            sheetInfo.setSheetStyle((SheetStyle) sheetStyleClass.getDeclaredConstructor().newInstance());
        }

        /* set response */
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment;filename="
                        + URLEncoder.encode(fileName, "utf-8")
                        + fileSuffix);

        excelWriter.write(sheetInfo, response.getOutputStream());
        response.getOutputStream().flush();
    }

    private String[] getFieldNames(ResponseExcel responseExcel) {
        if (responseExcel.value().length > 0) {
            return responseExcel.value();
        }
        return responseExcel.fieldNames();
    }
}
