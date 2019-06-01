package registry;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.zookeeper.CreateMode;

public class RegistService {
	private final static String ZK_SERVICE_PATH = "/servers/orisun_demo_service/";

	public static void regist(String serviceImpl, String ip) {
		String BASE_PATH = ZkClient.ZK_BASE_PATH + ZK_SERVICE_PATH+serviceImpl;
		CuratorFramework zkClient = ZkClient.getInstance().getZkClient();
		boolean exists = false;
		try {
			CreateBuilder cb = zkClient.create();
			if (zkClient.checkExists().forPath(BASE_PATH) == null) {
				cb.creatingParentsIfNeeded().forPath(BASE_PATH, new byte[] { 0 });
			}
			List<String> children = zkClient.getChildren().forPath(BASE_PATH);
			if (children != null && children.indexOf(ip) >= 0) {
				exists = true;
			}
			if (!exists) {
				// EPHEMERAL节点，进程终止时zookeeper连接断开，节点自动被删除
				System.out.println(BASE_PATH + "/" + ip);
				cb.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(BASE_PATH + "/" + ip,
						new byte[0]);
				System.out.println(serviceImpl + "/" + ip + " add to cluster");
			} else {
				// 如果发现cluster上已存在该IP，则5秒后再确认一下
				System.out.println(serviceImpl + "/" + ip + " already in cluster");
				Thread.sleep(1000 * 5);
				children = zkClient.getChildren().forPath(BASE_PATH);
				exists = false;
				if (children != null && children.indexOf(ip) >= 0) {
					exists = true;
				}
				if (!exists) {
					// EPHEMERAL节点，进程终止时zookeeper连接断开，节点自动被删除
					cb.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(BASE_PATH + "/" + ip,
							new byte[0]);
					System.out.println(serviceImpl + "/" + ip + " add to cluster");
				}
			}
		} catch (Exception e) {
			System.err.println("report to cluster failed");
			e.printStackTrace();
		}
	}
}
