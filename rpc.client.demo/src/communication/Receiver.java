package communication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import protocol.Protocol;
import serialize.ServiceResponse;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class Receiver {

	private static final int port = 5859;
	private static Selector selector = null;
	private static ServerSocketChannel serverChannel = null;
	private static final byte RESPONSE_MEG = (byte) 2;// 响应

	public static void work() {
		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().setReuseAddress(true);
			serverChannel.socket().bind(new InetSocketAddress(port));
			System.out.println("===========Client listen on port " + port + " waiting for response===========");
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			while (selector.select() > 0) {
				Iterator<SelectionKey> itr = selector.selectedKeys().iterator();
				while (itr.hasNext()) {
					SelectionKey key = itr.next();
					if (key.isAcceptable()) {
						ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
						handleRequest(ssc);
					}
					itr.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				selector.close();
			} catch (Exception ex) {
				try {
					serverChannel.close();
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	private static void handleRequest(ServerSocketChannel ssc) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = ssc.accept();
			BufferedInputStream bis = new BufferedInputStream(socketChannel.socket().getInputStream());
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			int b = -1;
			while ((b = bis.read()) != -1) {
				buffer.put((byte) b);
			}
			byte[] bytes = new byte[buffer.position()];
			buffer.flip();
			buffer.get(bytes);
			if (bytes.length > 0) {
				Protocol protocol = Protocol.deserialize(bytes);
				assert protocol.getMsgType() == RESPONSE_MEG;
				ServiceResponse response = ServiceResponse.parse(protocol.getMsg());
				int sessionID = protocol.getSessionID();
//				System.out.println("session " + sessionID + " get result:" + response.getValue() + "\t" + response.getType().getCanonicalName());
				ResponseWaiter waiter=Sender.sessionMap.get(sessionID);
				if(waiter!=null){
					waiter.setResponse(response);
					waiter.set();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socketChannel.socket().shutdownInput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void destroy() {
		try {
			if (selector.isOpen()) {
				selector.close();
			}
			if (!serverChannel.socket().isClosed()) {
				serverChannel.socket().close();
			}
			if (serverChannel.isOpen()) {
				serverChannel.close();
			}
		} catch (IOException e) {
		}
	}
}
