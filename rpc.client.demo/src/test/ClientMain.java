package test;

import proxy.ProxyFactory;
import service.IComplicatedCompute;
import service.ISimpleCompute;

import communication.Receiver;
import communication.Sender;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ClientMain {
	public static void main(String[] args) throws InterruptedException {
		Thread listenThread = new Thread() {
			public void run() {
				Receiver.work();
			}
		};
		listenThread.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Sender.destroy();
				Receiver.destroy();
			}
		});

		Integer a = 64;
		Integer b = 16;
		String name = "Orisun";
		ISimpleCompute service = (ISimpleCompute) ProxyFactory.create(ISimpleCompute.class, "SimpleComputeImpl1");
		System.out.println(service.getClass().getCanonicalName());
		System.out.println("SimpleComputeImpl1 compute: " + service.compute(a, b));

		service = (ISimpleCompute) ProxyFactory.create(ISimpleCompute.class, "SimpleComputeImpl1");
		try {
			System.out.println("SimpleComputeImpl1 concat: " + service.concat(a, b));
		} catch (Exception e) {
			e.printStackTrace();
		}

		service = (ISimpleCompute) ProxyFactory.create(ISimpleCompute.class, "SimpleComputeImpl1");
		System.out.println("SimpleComputeImpl1 sayHello: " + service.sayHello(name));

		service = (ISimpleCompute) ProxyFactory.create(ISimpleCompute.class, "SimpleComputeImpl2");
		System.out.println("SimpleComputeImpl2 compute: " + service.compute(a, b));

		service = (ISimpleCompute) ProxyFactory.create(ISimpleCompute.class, "SimpleComputeImpl2");
		try {
			System.out.println("SimpleComputeImpl2 concat: " + service.concat(a, b));
		} catch (Exception e) {
			e.printStackTrace();
		}

		service = (ISimpleCompute) ProxyFactory.create(ISimpleCompute.class, "SimpleComputeImpl2");
		System.out.println("SimpleComputeImpl2 sayHello: " + service.sayHello(name));

		Double c = 64.0;
		Double d = 16.0;
		IComplicatedCompute service2 = (IComplicatedCompute) ProxyFactory.create(IComplicatedCompute.class,
				"ComplicatedComputeImpl1");
		System.out.println("ComplicatedComputeImpl1 compute: " + service2.compute(c, d));

		service2 = (IComplicatedCompute) ProxyFactory.create(IComplicatedCompute.class, "ComplicatedComputeImpl2");
		System.out.println("ComplicatedComputeImpl2 compute: " + service2.compute(c, d));
	}
}
