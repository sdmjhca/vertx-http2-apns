package push.apns;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;

// Apple Push Notification Service Implement with Java(java apns)
// https://github.com/teaey/apns4j
public class ApnsVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(ApnsVerticle.class);

	// private ApnsChannelFactory apnsChannelFactory;
	//
	// private PayloadSender<ApnsPayload> payloadSender;

	private int tryTimes = 1;// useless in aync sender

	private static final String APNS_KEYSTORE_PWD = "01zhuanche";

	private EventBus eb;

	@Override
	public void start() throws Exception {
		super.start();

		ClassLoader ctxClsLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = ctxClsLoader.getResourceAsStream("dev/apns_developer.p12");
		// apnsChannelFactory =
		// Apns4j.newChannelFactoryBuilder().keyStoreMeta(is).keyStorePwd(APNS_KEYSTORE_PWD)
		// .apnsGateway(ApnsGateway.DEVELOPMENT).build();
		//
		// payloadSender = new ApnsService(Runtime.getRuntime().availableProcessors(),
		// apnsChannelFactory, tryTimes);

		eb = vertx.eventBus();
		eb.<JsonObject>consumer(ApnsVerticle.class.getName() + "local", res -> {
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

	private void apnsSend(String deviceToken, String title, String body, Extend extend) {
		// ApnsPayload apnsPayload =
		// Apns4j.newPayload().alertTitle(title).alertBody(body).sound("default")
		// .extend("msgbody", "{\\\"jumpPage\\\":8}");
		//
		// payloadSender.send(deviceToken, apnsPayload);
		// System.out.println("11" + Json.encode(apnsPayload));
	}
}
