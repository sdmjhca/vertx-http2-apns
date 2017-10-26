package push.apns.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public final class PayloadBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, Object> root;
    private final Map<String, Object> aps;
    //自定义alert：body/title/action-loc-key
    private final Map<String, Object> customAlert;
    private final Map<String, Object> fields;

    /**
     * Constructs a new instance of {@code PayloadBuilder}
     */
    public PayloadBuilder() {
        this.root = new HashMap<>();
        this.aps = new HashMap<>();
        this.customAlert = new HashMap<>();
        this.fields = new HashMap<>();
    }

    /**
     * Sets the alert body text, the text the appears to the user,
     * to the passed value
     *
     * 设置消息提醒的内容
     * @param alert the text to appear to the user
     * @return this
     */
    public PayloadBuilder alertBody(final String alert) {
        customAlert.put("body", alert);
        return this;
    }

    /**
     * 设置消息提醒的标题
     * @param title the text to appear to the user
     * @return this
     */
    public PayloadBuilder alerTitle(final String title) {
        customAlert.put("title", title);
        return this;
    }

    /**
     * Sets the notification badge to be displayed next to the
     * application icon.
     * <p/>
     * The passed value is the value that should be displayed
     * (it will be added to the previous badge number), and
     * a badge of 0 clears the badge indicator.
     *
     * 设置通知APP的图标
     * @param badge the badge number to be displayed
     * @return this
     */
    public PayloadBuilder badge(final int badge) {
        aps.put("badge", badge);
        return this;
    }

    /**
     * Sets
     *设置收到消息时的提示音为默认
     * @return this
     */
    public PayloadBuilder sound() {
        aps.put("sound", "default");
        return this;
    }

    /**
     * Sets
     * 指定收到消息时的提示音
     * @param sound
     * @return this
     */
    public PayloadBuilder sound(final String sound) {
        aps.put("sound", sound);
        return this;
    }

    /**
     * 设置静默通知，如果设置静默：字典必须不包含alert，sound或badge键
     * @return
     */
    public PayloadBuilder contentAvailable(){
        aps.put("content-available", 1);
        return this;
    }

    /**
     * Sets
     *
     * @return this
     */
    public PayloadBuilder addField(final String key, final Object value) {
        fields.put(key, value);
        return this;
    }

    public PayloadBuilder addField(final String key, final Map<String, Object> value) {
        fields.put(key, value);
        return this;
    }

    /**
     * Returns the JSON String representation of the payload
     * according to Apple APNS specification
     *
     * @return the String representation as expected by Apple
     */
    public JsonObject build() {
        if (!root.containsKey("mdm")) {
            insertCustomAlert();
            root.put("aps", aps);
        }
        if (fields.size() > 0) {
            root.putAll(fields);
        }
        try {
            //return mapper.writeValueAsString(root);
            return JsonObject.mapFrom(root);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void insertCustomAlert() {
        switch (customAlert.size()) {
            case 0:
                aps.remove("alert");
                break;
            case 1:
                if (customAlert.containsKey("body")) {
                    aps.put("alert", customAlert.get("body"));
                    break;
                }
                // else follow through
                //$FALL-THROUGH$
            case 2:
                aps.put("alert", JsonObject.mapFrom(customAlert).toString());
                break;
            default:
                aps.put("alert", JsonObject.mapFrom(customAlert).toString());
        }
    }
}
