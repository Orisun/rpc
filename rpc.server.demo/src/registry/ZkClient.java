package registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

/**
 * 利用zookeeper进行参数配置
 * 
 * @Author:orisun
 * @Since:2016-3-22
 * @Version:1.0
 */
public class ZkClient {

	private final static String ZK_SERVER="10.1.200.232:2181,10.1.200.233:2181,10.1.200.234:2181";//测试C环境的zookeeper集群
	public final static String ZK_BASE_PATH = "/rpc_demo";
	private static CuratorFramework zkClient = null;
	private static volatile ZkClient instance = null;
	

	/**
	 * 若要使用ZkClient，必须在SystemConfig配置文件中指定zookeeper_base_path
	 * 
	 * @return
	 */
	public static ZkClient getInstance() {
		if (!isConnected()) {
			System.out.println("zookeeper connection is lost, and will reconnect");
			instance = null;
		}
		if (instance == null) {
			synchronized (ZkClient.class) {
				if (instance == null) {
						instance = new ZkClient();
				}
			}
		}
		return instance;
	}

	/**
	 * 执行一个checkExists操作，判断zookeeper连接是否正常
	 * 
	 * @return
	 */
	private static boolean isConnected() {
		boolean rect = false;
		try {
			if (zkClient.checkExists().forPath(ZK_BASE_PATH) != null) {
				rect = true;
			}
		} catch (Exception e) {

		}
		return rect;
	}

	private ZkClient() {
		zkClient = CuratorFrameworkFactory.builder().connectString(ZK_SERVER)
				.sessionTimeoutMs(30000).connectionTimeoutMs(30000).retryPolicy(new ExponentialBackoffRetry(1000, 10))
				.defaultData(null).build();
		zkClient.start();
		System.out.println("connect to zookeeper");
		CreateBuilder cb = zkClient.create();
		try {
			if (zkClient.checkExists().forPath(ZK_BASE_PATH) == null) {
				cb.creatingParentsIfNeeded().forPath(ZK_BASE_PATH, new byte[] { 0 });
			}
		} catch (Exception e) {
			System.err.println("create zookeeper base path " + ZK_BASE_PATH + " failed");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public CuratorFramework getZkClient() {
		return zkClient;
	}

	public void releaseResource() {
		CloseableUtils.closeQuietly(zkClient);
		System.out.println("close zookeeper connection quietly");
	}

}
