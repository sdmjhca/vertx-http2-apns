package push;

import io.vertx.core.json.JsonObject;

public class Push {

	private String title;
	
	private String body;
	
	private JsonObject extend;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public JsonObject getExtend() {
		return extend;
	}

	public void setExtend(JsonObject extend) {
		this.extend = extend;
	}
}
