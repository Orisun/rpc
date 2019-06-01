package serialize;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ServiceResponse {

	Class<?> type;
	String value;

	public String toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", type.getCanonicalName());
		jsonObject.put("value", value);
		return jsonObject.toJSONString();
	}

	public static ServiceResponse parse(String json) {
		ServiceResponse response = new ServiceResponse();
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(json);
			String value = jsonObject.get("value").toString();
			response.setValue(value);
			String className = jsonObject.get("type").toString();
			Class<?> type = Class.forName(className);
			response.setType(type);
		} catch (Exception e) {

		}
		return response;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
		if (type.getCanonicalName().equals("protocol.VOID")) {
			this.value = "";
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
