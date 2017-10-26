package push.apns.header;

import java.util.UUID;

/**
 * @author JHMI on 2017/10/26.
 * APNS服务请求头
 */
public class ApnsHeader {
    /**
     * 当使用证书建立连接时，将忽略此请求标头
     */
    private String authorization;
    /**
     * 如果省略此标题，则新的UUID由APN创建并在响应中返回。
     * UUID如果发送通知时发生错误，APN将使用此值来识别到您的服务器的通知
     */
    private String apnsID;
    /**
     * UNIX纪元日期以秒（UTC）表示。此标题标识通知不再有效并可以丢弃的日期。
     * 如果此值不为零，APN将存储通知，并尝试至少传送一次，如果第一次无法传送通知，
     * 则根据需要重复尝试。如果值是0，APN将通知视为立即到期，不存储通知或尝试重新发送通知
     */
    private String apnsExpiration;
    /**
     * 优先级默认为10
     */
    private String apnsPriority;
    /**
     * 省略此请求标头，并且您的APN证书不指定多个主题，则APN服务器将使用证书的主题作为默认主题
     */
    private String apnsTopic;
    /**
     * 如果多个请求的apnsCollapseId相同，作为单个通知显示给用户
     * 该键的值不得超过64字节
     */
    private String apnsCollapseId;


    /**
     * 生成UUID
     * @return
     */
    public static String getApnsID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 返回当前系统时间 单位秒
     * @return
     */
    public static String getApnsExpiration() {
        return System.currentTimeMillis()/1000+"";
    }
}
