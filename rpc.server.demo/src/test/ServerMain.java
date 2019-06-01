package test;

import communication.Receiver;
import communication.Sender;
import proxy.ProxyFactory;
import registry.ZkClient;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ServerMain {

	public static void main(String[] args) {
		ProxyFactory.scanService();
		Thread listenThread=new Thread(){
			public void run() {
				Receiver.work();
			}
		};
		listenThread.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Sender.destroy();
				Receiver.destroy();
				ZkClient.getInstance().releaseResource();
			}
		});
	}
}
