package serialize;

import com.alibaba.fastjson.JSON;


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
		return JSON.toJSONString(this);
	}

	public static ServiceRequest parse(String json) {
		return (ServiceRequest)JSON.parseObject(json, ServiceRequest.class);
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
