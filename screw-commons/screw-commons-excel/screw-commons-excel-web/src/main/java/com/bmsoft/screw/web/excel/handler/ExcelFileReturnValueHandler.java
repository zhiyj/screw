package com.bmsoft.screw.web.excel.handler;

import com.bmsoft.screw.web.excel.ExcelFile;
import com.bmsoft.screw.web.excel.ExcelWriter;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.net.URLEncoder;

/**
 * 处理 Spring MVC 框架 ExcelFile 类型的返回值，将其解析为文件下载
 *
 * @author Zhiyj
 */
public class ExcelFileReturnValueHandler implements HandlerMethodReturnValueHandler {


    private final ExcelWriter excelWriter;

    public ExcelFileReturnValueHandler(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }


    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        Method m = methodParameter.getMethod();
        return m != null && ExcelFile.class.equals(m.getReturnType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest nativeWebRequest) throws Exception {
        /* check */
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        mavContainer.setRequestHandled(true);

        /* return value check */
        ExcelFile excelFile = (ExcelFile) returnValue;
        if (excelFile == null || excelFile.getSheetInfoList() == null) {
            String msg = "return value or sheet info is null, can not build excel";
//            LOGGER.warn(msg);
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
            response.getWriter().flush();
            return;
        }

        /* set response */
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader("Content-Disposition",
                "attachment;filename="
                        + URLEncoder.encode(excelFile.getFileName(), "utf-8")
                        + excelFile.getFileSuffix());
        excelWriter.write(excelFile.getSheetInfoList(), response.getOutputStream());
        response.getOutputStream().flush();
    }
}