package com.wkclz.core.helper;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.PutObjectResult;
import com.wkclz.core.base.Sys;
import com.wkclz.core.util.RegularUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Description:
 * Created: wangkaicun @ 2017-12-23 下午2:48
 */
public class OssHelper {
    private static final Logger logger = LoggerFactory.getLogger(OssHelper.class);

    /**
     * 生产上传文件名
     *
     * @param originalFilename
     * @return
     */
    public static String genFileName(String originalFilename) {
        Calendar calendar = Calendar.getInstance();
        StringBuffer fileName = new StringBuffer();
        fileName.append(calendar.get(Calendar.DAY_OF_MONTH))
            .append(calendar.get(Calendar.HOUR_OF_DAY))
            .append(calendar.get(Calendar.MINUTE))
            .append(calendar.get(Calendar.SECOND))
            .append(calendar.get(Calendar.MILLISECOND))
            .append(UUID.randomUUID().toString())
            .append(getExtFileName(originalFilename));
        return fileName.toString();
    }

    private static String getExtFileName(String originFileName) {
        return originFileName.substring(originFileName.lastIndexOf("."));
    }

    /**
     * 生成文件的全路径
     *
     * @param businessType
     * @param fileName
     * @return
     */
    public static String genAllPath(String businessType, String fileName) {
        Calendar calendar = Calendar.getInstance();
        StringBuffer path = new StringBuffer(Sys.CURRENT_ENV.toString().toLowerCase());
        path.append("/")
            .append(businessType)
            .append("/")
            .append(calendar.get(Calendar.YEAR))
            .append(calendar.get(Calendar.MONTH) + 1)
            .append("/")
            .append(fileName);
        return path.toString();
    }


    /**
     * 文件上传到OSS
     *
     * @param file
     * @param fileFullPath
     */
    public static String uploadFiles(MultipartFile file, String fileFullPath) {
        try {
            uploadFiles(file.getInputStream(), fileFullPath);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        SystemConfigHelper helper = Sys.getBean(SystemConfigHelper.class);
        String outerEndpoint = helper.getSystemConfig("oss_outer_endpoint");

        // 此处返回外网地址
        outerEndpoint = outerEndpoint == null ? "" : outerEndpoint;
        outerEndpoint = outerEndpoint.endsWith("/") ? outerEndpoint : outerEndpoint + "/";
        return outerEndpoint + fileFullPath;
    }


    /**
     * 文件上传到OSS
     *
     * @param ins
     * @param fileFullPath
     */
    public static void uploadFiles(InputStream ins, String fileFullPath) {

        SystemConfigHelper helper = Sys.getBean(SystemConfigHelper.class);
        String innerEndpoint = helper.getSystemConfig("oss_inner_endpoint");
        String accessKeyId = helper.getSystemConfig("oss_access_key_id");
        String accessKeySecret = helper.getSystemConfig("oss_access_key_secret");
        String bucketName = helper.getSystemConfig("oss_bucket_name");

        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建

        // 创建ClientConfiguration实例，按照您的需要修改默认参数
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        // 开启支持CNAME选项
        conf.setSupportCname(true);
        // 创建OSSClient实例
        OSS client = new OSSClientBuilder().build(innerEndpoint, accessKeyId, accessKeySecret, conf);
        // 使用访问OSS
        PutObjectResult result = client.putObject(bucketName, fileFullPath, ins);

        // 关闭client
        client.shutdown();
    }


    /**
     * OSS 单文件删除
     *
     * @param objectName
     */
    public static Integer deleteFile(String objectName) {
        List<String> objectNames = new ArrayList<>();
        objectNames.add(objectName);
        return deleteFiles(objectNames);
    }

    /**
     * OSS 多文件删除
     *
     * @param objectNames 要删除的名称列表
     * @return 删除失败的条数。全部成功时返回0
     */
    public static Integer deleteFiles(List<String> objectNames) {

        SystemConfigHelper helper = Sys.getBean(SystemConfigHelper.class);
        String innerEndpoint = helper.getSystemConfig("oss_inner_endpoint");
        String accessKeyId = helper.getSystemConfig("oss_access_key_id");
        String accessKeySecret = helper.getSystemConfig("oss_access_key_secret");
        String bucketName = helper.getSystemConfig("oss_bucket_name");

        objectNames = removeProAndEnCode(objectNames);
        OSS client = new OSSClientBuilder().build(innerEndpoint, accessKeyId, accessKeySecret);

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(objectNames);
        // 返回模式。true表示简单模式，false表示详细模式。默认为详细模式
        deleteObjectsRequest.setQuiet(true);
        DeleteObjectsResult deleteObjectsResult = client.deleteObjects(deleteObjectsRequest);
        // 删除结果。详细模式下为删除成功的文件列表，简单模式下为删除失败的文件列表。
        List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
        client.shutdown();
        Integer result = 0;
        if (deletedObjects != null && deletedObjects.size() > 0) {
            result = deletedObjects.size();
        }
        return result;
    }

    private static List<String> removeProAndEnCode(List<String> strs) {
        return strs.stream().map(str -> removeProAndEnCode(str)).collect(Collectors.toList());
    }

    private static String removeProAndEnCode(String str) {
        if (str == null) {
            return null;
        }

        // 去掉域名等信息
        if (str != null && str.indexOf("://") > 0) {
            str = str.substring(str.indexOf("://") + 3, str.length());
            str = str.substring(str.indexOf("/") + 1, str.length());
        }

        // 考虑还有两 / 个的情况
        if (str.startsWith("/")) {
            str = str.substring(1);
        }
        if (str.startsWith("/")) {
            str = str.substring(1);
        }


        if (RegularUtil.haveDoubleByte(str)) {
            StringBuffer sb = new StringBuffer();
            try {
                for (char c : str.toCharArray()) {
                    if (RegularUtil.isDoubleByte(c)) {
                        sb.append(URLEncoder.encode(String.valueOf(c), "UTF-8"));
                    } else {
                        sb.append(c);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);

            }
            str = sb.toString();
        }
        return str;
    }

}
