package serialize;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class Param {

	Class<?> type;
	String value;

	public String toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", type.getCanonicalName());
		jsonObject.put("value", value);
		return jsonObject.toJSONString();
	}
	
	public Object toValue() {
		if (type == Integer.class || type == Integer.TYPE) {
			return Integer.parseInt(value);
		}
		if (type == Double.class || type == Double.TYPE) {
			return Double.parseDouble(value);
		}
		if (type == Boolean.class || type == Boolean.TYPE) {
			return Boolean.parseBoolean(value);
		}
		if (type == Byte.class || type == Byte.TYPE) {
			return Byte.parseByte(value);
		}
		if (type == Character.class || type == Character.TYPE) {
			return new Character((char) value.charAt(0));
		}
		if (type == Short.class || type == Short.TYPE) {
			return Short.parseShort(value);
		}
		if (type == Long.class || type == Long.TYPE) {
			return Long.parseLong(value);
		}
		if (type == Float.class || type == Float.TYPE) {
			return Float.parseFloat(value);
		}
		if (type == String.class) {
			return value;
		}
		throw new RuntimeException("value convert failed. type is " + type.getCanonicalName() + ", value is " + value);
	}

	public static Param parse(String json) {
		Param param = new Param();
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(json);
			String value = jsonObject.get("value").toString();
			param.setValue(value);
			String className = jsonObject.get("type").toString();
			Class<?> type = Class.forName(className);
			param.setType(type);
		} catch (Exception e) {

		}
		return param;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static void main(String[] args) {
		String className = Integer.class.getCanonicalName();
		System.out.println(className);
		try {
			Class<?> type = Class.forName(className);
			System.out.println(type.getSimpleName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Param param = new Param();
		param.setType(Integer.class);
		param.setValue(String.valueOf(4));
		String json = param.toJson();
		System.out.println(json);

		param = Param.parse(json);
		System.out.println(param.toJson());
	}
}
