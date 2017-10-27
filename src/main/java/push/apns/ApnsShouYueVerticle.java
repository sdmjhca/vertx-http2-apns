package push.apns;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.commons.lang.StringUtils;
import push.apns.data.Payload;
import push.apns.header.ApnsHeader;

/**
 * @author jhmi
 * 通过服务证书向APNS请求消息推送服务
 * 另一种方式：通过服务Token向APNS请求，为确保安全性，APN需要定期生成新的令牌
 */
public class ApnsShouYueVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ApnsShouYueVerticle.class);

	private static final String APNS_KEYSTORE_PWD = "01zhuanche";

	private EventBus eb;

	/**
	 * 定义HTTP请求客户端
	 */
	private WebClient webClient;
	/**
	 * 定义APNS的开发环境URL
	 */
	private static final String APNS_REQ_URL_DEV = "api.development.push.apple.com";
	/**
	 * 定义APNS密钥的存放路径
	 */
	private static final String KEY_STORE_PATH = "dev/apns_developer.p12";
	//private static final String KEY_STORE_PATH = "D:/apnsCert/apns_developer.p12";


	@Override
	public void start() throws Exception {
		super.start();

		//初始化webclient
		this.initWebClient();

		eb = vertx.eventBus();
		eb.<JsonObject>consumer(ApnsShouYueVerticle.class.getName() + "local", res -> {
			String action = res.headers().get("action");
			if (StringUtils.isNotEmpty(action)) {
				JsonObject body = res.body();
				switch (action) {
				case "apnsSend":
					ApnsMsg msg = Json.decodeValue(body.encode(), ApnsMsg.class);
					if (msg != null && StringUtils.isNotEmpty(msg.getDeviceToken())) {
						apnsSend(msg.getDeviceToken(), msg.getTitle(), msg.getBody(), msg.getExtend());
					} else {
						logger.warn("apnsSend, device token is null.");
					}
					break;

				default:
					break;
				}
			}
		});
	}

	/**
	 * 向APNS服务发送推送请求
	 * @param deviceToken 设备token
	 * @param title 消息标题
	 * @param content 消息内容
	 * @param extend 其他
	 */
	private void apnsSend(String deviceToken, String title, String content, Extend extend)  {
		if(webClient == null){
			this.initWebClient();
		}
		//设置请求报文体
		JsonObject reqJson = Payload.newPayload()
									.alertBody(content)//设置alert内容
									.alerTitle(title)//设置通知的标题，建议使用默认的应用标题
									.sound()
									.build();
		//设置请求的路径URL+TOKEN
		HttpRequest<Buffer> request = webClient.post(443,APNS_REQ_URL_DEV,"/3/device/"+deviceToken);

		//设置请求的报文头
		request.putHeader("apnsID", ApnsHeader.getApnsID());
		request.putHeader("apnsExpiration", ApnsHeader.getApnsExpiration());
		//发送请求
		request.sendJsonObject(reqJson,res->{
			if(res.succeeded()){
				HttpResponse response = res.result();
				int stausCode = response.statusCode();
				if(stausCode == 200){
					logger.info("请求APNS服务器成功，结果="+stausCode);
				}else{
					logger.error("请求APNS服务器成功，推送失败code={},msg={},body={}",
							response.statusCode(),response.statusMessage(),response.body());
				}

			}else{
				res.result();
				res.cause().printStackTrace();
				logger.info("请求APNS服务器失败，失败原因={}",res.cause().getMessage());
			}
		});
	}

	/**
	 * 初始化webclient
	 */
	public void initWebClient(){
		WebClientOptions webClientOptions = new WebClientOptions();
		webClientOptions.setProtocolVersion(HttpVersion.HTTP_2);
		webClientOptions.setSsl(true);

		/**
		 * implementations that support HTTP/2 over TLS
		 * MUST use protocol negotiation in TLS [TLS-ALPN].
		 * 标准的Java 8不支持ALPN，所以ALPN应该通过其他方式启用：
		 * OpenSSL支持
		 * Jetty-ALPN支持
		 */
		webClientOptions.setUseAlpn(true);
		//webClientOptions.setSslEngineOptions(new OpenSSLEngineOptions());
		//PKCS＃12格式的密钥/证书（http://en.wikipedia.org/wiki/PKCS_12），通常使用.pfx 或.p12
		PfxOptions pfxOptions = new PfxOptions();
		//设置密钥和路径
		pfxOptions.setPath(KEY_STORE_PATH);
		pfxOptions.setPassword(APNS_KEYSTORE_PWD);
		//设置客户端信任证书
		webClientOptions.setPfxKeyCertOptions(pfxOptions);
		//另一种方式读取密钥
		/*
		Buffer buffer = Buffer.buffer(bytes);*/
		/*Buffer buffer = vertx.fileSystem().readFileBlocking("d://apnsCert/apns_developer.p12");
		pfxOptions.setValue(buffer);*/

		//创建webclient
		webClient = WebClient.create(vertx,webClientOptions);
	}
}
