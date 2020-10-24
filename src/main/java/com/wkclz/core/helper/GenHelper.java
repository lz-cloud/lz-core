package com.wkclz.core.helper;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Result;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.entity.GenTaskInfo;
import com.wkclz.core.util.CompressUtil;
import com.wkclz.core.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class GenHelper {

    // 已经部署好的机器
    private static String BASE_URL = "http://gen.wkclz.com/gen";
    private static String AUTH_CODE = null;


    public static boolean genCode(String baseUrl, String authCode) {
        BASE_URL = baseUrl;
        AUTH_CODE = authCode;
        return genCode();
    }

    public static boolean genCode(String authCode) {
        AUTH_CODE = authCode;
        return genCode();
    }


    public static boolean genCode() {
        String authCode = AUTH_CODE;

        if (StringUtils.isBlank(authCode)){
            throw BizException.error("authCode can not be null");
        }

        long start = System.currentTimeMillis();

        try {
            String urlStr = getGenAddr(authCode);
            URL url = new URL(urlStr);
            System.out.println(StrFormatter.format("=======> download addr: {}", url.getPath()));

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(3*1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200){
                throw BizException.error("网络请求错误：" + conn.getResponseMessage());
            }

            //获取文件信息
            byte[] getData = readInputStream(inputStream);
            String savePath = getSavePath(conn).replace("\\","/");

            // 保存文件
            File file = new File(savePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);

            // 解压
            int lastSeparator = savePath.lastIndexOf("/");
            String saveDir = savePath.substring(0, lastSeparator);
            CompressUtil.unZip(file, saveDir);

            // 删除压缩文件
            file.delete();

            // 替换
            int lastPort = savePath.lastIndexOf(".");
            String genSrc = savePath.substring(0, lastPort);
            String tagSrc = System.getProperties().get("user.dir").toString();
            List<GenTaskInfo> taskInfos = getRule(authCode);
            for (GenTaskInfo taskInfo:taskInfos) {

                // 对于不需要生成的 task， 直接跳过
                Integer needCreate = taskInfo.getNeedCreate();
                if (needCreate == null || needCreate != 1){
                    continue;
                }
                // 特例：baseDao 已经在 MBG 生成时已干掉了，代码覆盖不处理
                if ("baseDao".equals(taskInfo.getTaskName())){
                    continue;
                }

                boolean deleteFlag = (taskInfo.getNeedDelete()!=null && taskInfo.getNeedDelete() == 1)? true:false;
                String relativePath = ""
                    + "/" + taskInfo.getProjectBasePath()
                    + "/" + taskInfo.getPackagePath().replaceAll("\\.", "/");

                String genPath = genSrc + relativePath;
                File genPathDirectory = new File(genPath);

                String tagPath = tagSrc + relativePath;
                File tagPathDirectory = new File(tagPath);
                if (!tagPathDirectory.exists()){
                    tagPathDirectory.mkdirs();
                }

                File[] genFiles = genPathDirectory.listFiles();
                for (File genFile:genFiles) {
                    File tagFile = new File(tagPath + "/" + genFile.getName());
                    // 如果存在并定义为删除，直接删除
                    // 特例1. *Example.java 为特例，MBG 生产模型时自动生成，直接删除即可
                    if ((tagFile.exists() && deleteFlag) ||  genFile.getName().endsWith("Example.java")){
                        System.out.println(StrFormatter.format("=======> 正在删除文件: {}", tagFile.getPath()));
                        tagFile.delete();
                    }
                    // 若不存在（或者存在并已删除），都创建
                    if (!tagFile.exists()){
                        System.out.println(StrFormatter.format("=======> 正在复制文件: {}", tagFile.getPath()));
                        Files.copy(genFile.toPath(), tagFile.toPath());
                    }
                }
            }

            FileUtil.delFile(genSrc);


            long end = System.currentTimeMillis();
            System.out.println(StrFormatter.format("=======> 完成代码生成, 耗时 {}ms <=========", (end - start)));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }




    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 获取文件保存路径
     * @param conn
     * @return
     */
    private static String getSavePath(HttpURLConnection conn){

        Object o = System.getProperties().get("user.dir");
        String userDir = o.toString();
        String savePath = userDir + "/temp/gen/";

        //文件保存位置
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdirs();
        }

        System.out.println(StrFormatter.format("=======> save path: {}", savePath));

        String fileName = "gen.zip";
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> dispositions = headerFields.get("Content-Disposition");
        if (dispositions != null){
            String sign = "filename=";
            for (String d:dispositions) {
                if (d.contains(sign)){
                    fileName = d.substring(d.indexOf(sign)+ sign.length());
                    break;
                }
            }
        }

        String filePath = saveDir+File.separator+fileName;

        return filePath;
    }

    /**
     * 读取生成配置
     * @return
     */
    private static List<GenTaskInfo> getRule(String authCode){
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = null;
        try {
            String urlStr = getGenRule(authCode);
            URL url = new URL(urlStr);
            System.out.println(StrFormatter.format("=======> rule addr: {}", url.getPath()));

            HttpURLConnection roleConn = (HttpURLConnection)url.openConnection();
            roleConn.setConnectTimeout(3*1000);
            roleConn.setRequestProperty("Content-Type", "application/json");
            roleConn.setRequestProperty("Connection", "Keep-Alive");
            roleConn.setRequestProperty("Charset", "UTF-8");
            roleConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            int ruleStatus = roleConn.getResponseCode();
            if (ruleStatus != 200){
                throw BizException.error("网络请求错误：" + roleConn.getResponseMessage());
            }

            is = roleConn.getInputStream();
            isr = new InputStreamReader(is,"utf-8");
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            while((str = br.readLine()) != null){
                buffer.append(str);
            }
            str = buffer.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (str == null){
            return null;
        }

        Result result = JSONObject.parseObject(str, Result.class);
        if (!result.isSuccess()){
            throw BizException.error("调用规则查询失败!");
        }
        Object data = result.getData();
        List<GenTaskInfo> genTaskInfos = JSONArray.parseArray(data.toString(), GenTaskInfo.class);
        return genTaskInfos;
    }


    private static String getGenAddr(String authCode){
        if (StringUtils.isBlank(authCode)){
            throw BizException.error("authCode can not be blank");
        }
        return BASE_URL + "/gen/" + authCode;
    }
    private static String getGenRule(String authCode){
        if (StringUtils.isBlank(authCode)){
            throw BizException.error("authCode can not be blank");
        }
        return BASE_URL + "/gen/rule/" + authCode;
    }


    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static void setAuthCode(String authCode) {
        AUTH_CODE = authCode;
    }
}
