import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import push.apns.ApnsMsg;
import push.apns.ApnsShouYueVerticle;
import push.apns.Extend;

public class ApnsVerticleTest {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		DeploymentOptions options = new DeploymentOptions();
		//请注意：Worker Verticle 和 HTTP/2 不兼容
		vertx.deployVerticle(ApnsShouYueVerticle.class.getName(), options.setWorker(false));

		EventBus eb = vertx.eventBus();

		ApnsMsg msg = new ApnsMsg();
		msg.setDeviceToken("564d6ea438cbbab36asde34fa8f2c79e7fbdb29eebd643d254da");
		msg.setTitle("消息标题");
		msg.setBody("test 发送消息内容");
		Extend extend = new Extend();
		extend.setJumpPage("8");
		msg.setExtend(extend);

		DeliveryOptions option = new DeliveryOptions();
		option.addHeader("action", "apnsSend");

		eb.send(ApnsShouYueVerticle.class.getName() + "local", JsonObject.mapFrom(msg), option);
	}

}
