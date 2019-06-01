package communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import protocol.Protocol;
import serialize.ServiceRequest;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class Sender {
//	private static final String host = "127.0.0.1";//服务提供方的IP地址
	private static final String host = "10.7.0.174";
	private static final int port = 5858;
	private static SocketChannel socketChannel = null;
	private static final byte lowEndian = (byte) 1;// 小端
	private static final byte REQUEST_MSG = (byte) 1;// 请求
	private static AtomicInteger sessionID = new AtomicInteger(0);
	public static Map<Integer,ResponseWaiter> sessionMap=new ConcurrentHashMap<Integer,ResponseWaiter>();

	public static int send(ServiceRequest request) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			SocketAddress address = new InetSocketAddress(host, port);
			socketChannel.connect(address);
			Protocol protocol = new Protocol();
			protocol.setLowEndian(lowEndian);
			protocol.setMsg(request.toJson());
			System.out.println(request.toJson());
			protocol.setMsgType(REQUEST_MSG);
			protocol.setSessionID(sessionID.incrementAndGet());
			sessionMap.put(sessionID.get(), new ResponseWaiter());
			byte[] bytes=protocol.serialize();
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			socketChannel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socketChannel.socket().shutdownOutput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sessionID.get();
	}

	public static void destroy() {
		try {
			if (!socketChannel.socket().isClosed()) {
				socketChannel.socket().close();
			}
			if (socketChannel.isOpen()) {
				socketChannel.close();
			}
		} catch (IOException e) {
		}
	}
}
