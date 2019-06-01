package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ServiceProxy implements InvocationHandler {
	private Object target;

	public Object bind(Object target) {
		this.target = target;
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		long begin = System.currentTimeMillis();
		Object rect = null;
		try {
			rect = method.invoke(target, args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		long end = System.currentTimeMillis();
		if (end - begin > 200) {
			System.err.println(method.getName() + " execute over 200 miliseconds!");
		}
		return rect;
	}

	public Object invoke(String methodName, Object[] args) throws Throwable {
		Class<?>[] vc = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			vc[i] = args[i].getClass();
		}
		Method md = target.getClass().getDeclaredMethod(methodName, vc);
		if (md != null) {
			return invoke(target, md, args);
		}
		return null;
	}

}
