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
import proxy.ProxyFactory;
import proxy.ServiceProxy;
import serialize.Param;
import serialize.ServiceRequest;
import serialize.ServiceResponse;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class Receiver {

	private static final int port = 5858;
	private static Selector selector = null;
	private static ServerSocketChannel serverChannel = null;

	public static void work() {
		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().setReuseAddress(true);
			serverChannel.socket().bind(new InetSocketAddress(port));
			System.out.println("===========Service listen on port " + port + " waiting for request===========");
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
			String remote = socketChannel.getRemoteAddress().toString();
			String remoteIP=remote.substring(1,remote.lastIndexOf(':'));
//			int remotePort=Integer.parseInt(remote.substring(remote.lastIndexOf(':')+1));
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
				assert protocol.getMsgType() == (byte) 1;
				ServiceRequest request = ServiceRequest.parse(protocol.getMsg());
				int sessionID = protocol.getSessionID();
				System.out.print("session " + sessionID + " get request:" + request.getImplClass() + "\t"
						+ request.getMethodName() + "(");
				int i = 0;
				for (; i < request.getParams().length - 1; i++) {
					Param param = request.getParams()[i];
					System.out.print(param.getType().getCanonicalName() + ",");
				}
				Param param = request.getParams()[i];
				System.out.println(param.getType().getCanonicalName() + ")");

				ServiceResponse response = new ServiceResponse();
				Class<?> iface = request.getIface();
				ServiceProxy proxy = ProxyFactory.create(iface, request.getImplClass());
				Object[] args = new Object[request.getParams().length];
				for (i = 0; i < request.getParams().length; i++) {
					args[i] = request.getParams()[i].toValue();
				}
				try {
					Object result = proxy.invoke(request.getMethodName(), args);
					response.setType(result.getClass());
					response.setValue(String.valueOf(result));
				} catch (Throwable e) {
					e.printStackTrace();
				}
				Sender.send(remoteIP, port+1,response, sessionID);
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
