package proxy;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ProxyFactory {

	public static Object create(Class<?> iface, String classimpl) {
		ServiceProxy proxy = new ServiceProxy();
		try {
			return proxy.bind(iface, classimpl);
		} catch (Exception e) {
			throw new RuntimeException("can't create proxy of " + iface.getCanonicalName(), e);
		}
	}
}
