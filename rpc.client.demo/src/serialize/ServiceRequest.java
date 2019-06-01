package serialize;

import java.util.Set;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ServiceRequest {

	Class<?> iface;
	String implClass;
	String methodName;
	Param[] params;

	public String toJson() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("iface", iface.getCanonicalName());
		jsonObject.put("implClass", implClass);
		jsonObject.put("methodName", methodName);
		JSONObject innerJson = new JSONObject();
		for (int i = 0; i < params.length; i++) {
			Param param = params[i];
			innerJson.put(String.valueOf(i), param.toJson());
		}
		jsonObject.put("params", innerJson.toJSONString());
		return jsonObject.toJSONString();
	}

	public static ServiceRequest parse(String json) {
		ServiceRequest request = new ServiceRequest();
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(json);
			String className = jsonObject.get("iface").toString();
			Class<?> type = Class.forName(className);
			request.setIface(type);
			request.setMethodName(jsonObject.get("methodName").toString());
			request.setImplClass(jsonObject.get("implClass").toString());
			JSONObject paramObject = (JSONObject) parser.parse(jsonObject.get("params").toString());
			Set<String> keys = paramObject.keySet();
			Param[] ps=new Param[keys.size()];
			for (String key : keys) {
				int index=Integer.valueOf(key);
				Param param = Param.parse(paramObject.get(key).toString());
				ps[index]=param;
			}
			request.setParams(ps);
		} catch (Exception e) {

		}
		return request;
	}

	public Class<?> getIface() {
		return iface;
	}

	public void setIface(Class<?> iface) {
		this.iface = iface;
	}

	public String getImplClass() {
		return implClass;
	}

	public void setImplClass(String implClass) {
		this.implClass = implClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Param[] getParams() {
		return params;
	}

	public void setParams(Param[] params) {
		this.params = params;
	}

	public static void main(String[] args) {
		ServiceRequest request = new ServiceRequest();
		request.setImplClass("SimpleComputeImpl1");
		request.setMethodName("compute");
		Param para1 = new Param();
		para1.setType(int.class);
		para1.setValue(String.valueOf(64));
		Param para2 = new Param();
		para2.setType(int.class);
		para2.setValue(String.valueOf(16));
		Param[] ps = new Param[] { para1, para2 };
		request.setParams(ps);
		String json = request.toJson();
		System.out.println(json);

		request = ServiceRequest.parse(json);
		System.out.println(request.toJson());
	}
}
