package com.wkclz.core.helper;

import com.wkclz.util.excel.Excel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class ExcelHelper {

    private static final Logger log = LoggerFactory.getLogger(ExcelHelper.class);

    public static void excelStreamResopnse(HttpServletResponse response, Excel excel) {

        OutputStream fops = null;
        InputStream in = null;
        int len = 0;
        byte[] bytes = new byte[1024];
        try {
            File file = excel.createXlsxByFile();
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            log.info("the excel file is in {}", file.getPath());

            in = new FileInputStream(file);

            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(excel.getTitle().getBytes("utf-8"), "ISO8859-1") + "." + suffix);
            response.setHeader("Content-Length", String.valueOf(file.length()));

            fops = response.getOutputStream();
            while ((len = in.read(bytes)) != -1) {
                fops.write(bytes, 0, len);
            }
            fops.flush();
            response.flushBuffer();
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件有误!");
        } finally {
            try {
                if (fops != null) {
                    fops.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
