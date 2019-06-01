package communication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import protocol.Protocol;
import serialize.ServiceResponse;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class Sender {
	private static SocketChannel socketChannel = null;
	private static final byte lowEndian = (byte) 1;// 小端
	private static final byte msgType = (byte) 2;// 响应

	public static void send(String host,int port, ServiceResponse response, int sessionID) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = SocketChannel.open();
			System.out.println("send response to " + host + ":" + port);
			SocketAddress address = new InetSocketAddress(host, port);
			socketChannel.connect(address);
			Protocol protocol = new Protocol();
			protocol.setLowEndian(lowEndian);
			protocol.setMsg(response.toJson());
			protocol.setMsgType(msgType);
			protocol.setSessionID(sessionID);
			ByteBuffer buffer = ByteBuffer.wrap(protocol.serialize());
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
