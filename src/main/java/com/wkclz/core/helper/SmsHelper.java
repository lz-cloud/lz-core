package com.wkclz.core.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SmsHelper {

    private static final Logger logger = LoggerFactory.getLogger(SmsHelper.class);


    /**
     * @param templateCode
     * @param signName     短信签名
     * @param params
     * @param mobiles      以逗号分隔的多个号码，最多 1000 个
     * @return 成功：OK，失败，错误详情
     */
    public static String sent(String templateCode, String signName, Map<String, String> params, String mobiles) {

        if (StringUtils.isBlank(templateCode)) {
            return "短信模板不能为空";
        }
        if (StringUtils.isBlank(mobiles)) {
            return "目标手机号不能为空";
        }
        if (StringUtils.isBlank(signName)) {
            return "短信签名不能为空";
        }


        /*
        以下代码参照：
        https://help.aliyun.com/document_detail/55284.html?spm=5176.10629532.106.1.49ef1cbeVdXQUp
        请查看上述文档后再做改动
         */

        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        String smsAccessKeyId = SystemConfigHelper.getSystemConfig("sms_access_key_id");
        String smsAccessKeySecret = SystemConfigHelper.getSystemConfig("sms_access_key_secret");

        try {
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsAccessKeyId, smsAccessKeySecret);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();

            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(mobiles);
            request.setSignName(signName);
            request.setTemplateCode(templateCode);

            // 参数处理
            if (params != null) {
                JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(params));
                System.out.println(jsonObject.toString());
                request.setTemplateParam(jsonObject.toString());
            }

            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            if (sendSmsResponse.getCode() != null && "OK".equals(sendSmsResponse.getCode())) {
                logger.info("短信发送成功：{}", sendSmsResponseToString(sendSmsResponse));
                return "OK";
            } else {
                logger.error("短信发送失败：{}", sendSmsResponseToString(sendSmsResponse));
                return "短信发送失败：" + sendSmsResponseToString(sendSmsResponse);
            }
        } catch (ClientException e) {
            logger.error(e.getMessage(), e);
        }
        return "短信发送失败，未知错误";
    }


    public static String sendSmsResponseToString(SendSmsResponse sendSmsResponse) {
        return "SendSmsResponse{" +
            "requestId='" + sendSmsResponse.getRequestId() + '\'' +
            ", bizId='" + sendSmsResponse.getBizId() + '\'' +
            ", code='" + sendSmsResponse.getCode() + '\'' +
            ", message='" + sendSmsResponse.getMessage() + '\'' +
            '}';
    }

}
