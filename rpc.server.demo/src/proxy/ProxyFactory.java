package proxy;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import annotation.OperationContract;
import annotation.ServiceBehavior;
import annotation.ServiceContract;
import registry.NIC;
import registry.RegistService;
import service.IComplicatedCompute;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ProxyFactory {

	private static Set<Class<?>> faces = new HashSet<Class<?>>();
	// server启动的时候就把实现类的名称和实现类对应起来
	private static Map<String, Class<?>> impls = new HashMap<String, Class<?>>();
	// 一个接口下有哪些实现类
	private static Map<Class<?>, List<Class<?>>> iface_impl_map = new HashMap<Class<?>, List<Class<?>>>();
	// 一个接口中定义了哪些方法
	private static Map<Class<?>, List<Method>> iface_method_map = new HashMap<Class<?>, List<Method>>();

	public static void scanService() {
		try {
			scanPackage("service");

			// TODO 打包部署到服務器上後，scanPackage()會失效，所以需要硬編碼給impls和iface_impl_map賦值
			String selfIP = NIC.getLocalIP();
			System.out.println("sele ip is " + selfIP);
			impls.put("ComplicatedComputeImpl1", service.ComplicatedComputeImpl1.class);
			impls.put("ComplicatedComputeImpl2", service.ComplicatedComputeImpl2.class);
			impls.put("SimpleComputeImpl1", service.SimpleComputeImpl1.class);
			impls.put("SimpleComputeImpl2", service.SimpleComputeImpl2.class);
			RegistService.regist("ComplicatedComputeImpl1", selfIP);
			RegistService.regist("ComplicatedComputeImpl2", selfIP);
			RegistService.regist("SimpleComputeImpl1", selfIP);
			RegistService.regist("SimpleComputeImpl2", selfIP);
			List<Class<?>> clzList1 = new ArrayList<Class<?>>();
			clzList1.add(service.ComplicatedComputeImpl1.class);
			clzList1.add(service.ComplicatedComputeImpl2.class);
			iface_impl_map.put(IComplicatedCompute.class, clzList1);
			List<Class<?>> clzList2 = new ArrayList<Class<?>>();
			clzList2.add(service.SimpleComputeImpl1.class);
			clzList2.add(service.SimpleComputeImpl2.class);
			iface_impl_map.put(service.ISimpleCompute.class, clzList2);

			for (Class<?> face : faces) {
				Method[] methods = face.getDeclaredMethods();
				List<Method> methodList = new ArrayList<Method>();
				for (Method method : methods) {
					if ("public abstract".equals(Modifier.toString(method.getModifiers()))) {
						if (method.isAnnotationPresent(OperationContract.class)) {
							methodList.add(method);
						}
					}
				}
				iface_method_map.put(face, methodList);
				List<Class<?>> implList = new ArrayList<Class<?>>();
				for (Entry<String, Class<?>> entry : impls.entrySet()) {
					Class<?> impl = entry.getValue();
					if (face.isAssignableFrom(impl)) {
						implList.add(impl);
					}
				}
				iface_impl_map.put(face, implList);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static ServiceProxy create(Class<?> iface, String classimpl) {
		Class<?> impl = impls.get(classimpl);
		if (impl == null) {
			throw new RuntimeException("interface implementation class " + classimpl + " not found.");
		}
		if (!iface_impl_map.containsKey(iface)) {
			throw new RuntimeException("interface " + iface.getCanonicalName() + " not found.");
		}
		if (!iface_impl_map.get(iface).contains(impl)) {
			throw new RuntimeException(classimpl + " is not a implementation of interface " + iface.getCanonicalName());
		}
		ServiceProxy proxy = new ServiceProxy();
		try {
			proxy.bind(impl.newInstance());
			return proxy;
		} catch (Exception e) {
			throw new RuntimeException("can't create instance of " + impl.getCanonicalName(), e);
		}
	}

	/**
	 * TODO 只能用于本机扫描，打包后把jar放到服务器该方法不适用
	 * 
	 * @param packageName
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void scanPackage(String packageName) throws IOException, ClassNotFoundException {
		String selfIP = NIC.getLocalIP();
		System.out.println("sele ip is " + selfIP);
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					scanPackageInDir(packageName, filePath, faces, impls);
				} else if ("jar".equals(protocol)) {
					JarFile jar;
					try {
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							if (name.charAt(0) == '/') {
								name = name.substring(1);
							}
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								if (idx != -1) {
									packageName = name.substring(0, idx).replace('/', '.');
								}

								if (name.endsWith(".class") && !entry.isDirectory()) {
									String className = name.substring(packageName.length() + 1, name.length() - 6);
									try {
										Class<?> clz = Class.forName(packageName + '.' + className);
										System.out.println("scanning " + packageName + '.' + className);
										if (clz.isInterface() && clz.isAnnotationPresent(ServiceContract.class)) {
											faces.add(clz);
										} else if (clz.isAnnotationPresent(ServiceBehavior.class)) {
											impls.put(clz.getSimpleName(), clz);
											// 把服务实现类和本机IP注册到zookeeper上
											RegistService.regist(clz.getSimpleName(), selfIP);
										}
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void scanPackageInDir(String packageName, String packagePath, Set<Class<?>> faces,
			Map<String, Class<?>> impls) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				scanPackageInDir(packageName + "." + file.getName(), file.getAbsolutePath(), faces, impls);
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					Class<?> clz = Class.forName(packageName + '.' + className);
					System.out.println("scanning " + packageName + '.' + className);
					if (clz.isInterface() && clz.isAnnotationPresent(ServiceContract.class)) {
						faces.add(clz);
					} else if (clz.isAnnotationPresent(ServiceBehavior.class)) {
						impls.put(clz.getSimpleName(), clz);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
