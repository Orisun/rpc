package serialize;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ServiceResponse {

	Class<?> type;
	String value;

	public String toJson() {
		return JSON.toJSONString(this);
	}

	public static ServiceResponse parse(String json) {
		return (ServiceResponse)JSON.parseObject(json, ServiceResponse.class);
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
