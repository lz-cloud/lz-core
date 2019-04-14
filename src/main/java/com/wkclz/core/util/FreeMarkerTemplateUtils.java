package com.wkclz.core.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2018-03-20 下午11:47
 */
public class FreeMarkerTemplateUtils {

    private FreeMarkerTemplateUtils(){
    }

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    static {
        //这里比较重要，用来指定加载模板所在的路径
        CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtils.class, "/templates"));
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    public static Template getTemplate(String templateName) throws IOException {
        try {
            CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtils.class, "/templates"));
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 自定义路径
     * @param templateName
     * @param templatesDir
     * @return
     * @throws IOException
     */
    public static Template getTemplate(String templateName, String templatesDir) throws IOException {
        if (templatesDir!=null&&templatesDir!=""){
            CONFIGURATION.setDirectoryForTemplateLoading(new File(templatesDir));
        }
        try {
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }


    public static String parseString(String content, Map<String, Object> params) {
        try {
            Configuration stringConfig = new Configuration(Configuration.VERSION_2_3_23);
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            stringLoader.putTemplate("_template_", content);
            stringConfig.setTemplateLoader(stringLoader);
            Template tpl = stringConfig.getTemplate("_template_", "utf-8");
            return org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString(tpl, params);
        } catch (TemplateNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }
}
