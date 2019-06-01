package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import serialize.Param;
import serialize.ServiceRequest;
import serialize.ServiceResponse;
import annotation.OperationContract;

import communication.Sender;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ServiceProxy implements InvocationHandler {
	private Class<?> iface;
	private String implClass;

	public Object bind(Class<?> iface, String impl) {
		this.iface = iface;
		this.implClass = impl;
		return Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface }, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
		if (!method.isAnnotationPresent(OperationContract.class)) {
			throw new Exception("方法 " + method.getName() + " 在接口中没有被标注为OperationContract，不能调用");
		}
		long begin = System.currentTimeMillis();
		try {
			ServiceRequest request = new ServiceRequest();
			request.setIface(iface);
			request.setImplClass(implClass);
			request.setMethodName(method.getName());
			Param[] params = new Param[args.length];
			for (int i = 0; i < args.length; i++) {
				Object obj = args[i];
				Param param = new Param();
				param.setType(obj.getClass());
				param.setValue(String.valueOf(obj));
				params[i] = param;
			}
			request.setParams(params);
			int sessionID = Sender.send(request);

			if (Sender.sessionMap.get(sessionID).waitResponse(200)) {
				ServiceResponse response = Sender.sessionMap.get(sessionID).getResponse();
				Param param = new Param();
				param.setType(response.getType());
				param.setValue(response.getValue());
				Object result = param.toValue();
				return result;
			} else {
				System.err.println("call service " + iface.getCanonicalName() + " timeout!");
				Sender.sessionMap.remove(sessionID);
			}
		} catch (Exception e) {
			System.err.println("remote service invocation failed."+e.getMessage());
			System.exit(1);
		}
		long end = System.currentTimeMillis();
		if (end - begin > 200) {
			System.err.println(method.getName() + " execute over 200 miliseconds!");
		}
		return null;
	}

}
