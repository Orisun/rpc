package registry;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import serialize.DataTransform;

/**
 * 
 * @Author:orisun
 * @Since:2015-6-10
 * @Version:1.0
 */
public class NIC {
	/**
	 * 获取本机MAC
	 * 
	 * @return
	 */
	public static String getMacAddr() {
		String MacAddr = "";
		String str = "";
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (ni.getName().equals("lo")) {
					continue;
				} else {
					byte[] buf = ni.getHardwareAddress();
					if (buf != null) {
						for (int i = 0; i < buf.length; i++) {
							str = str + DataTransform.byteHEX(buf[i]) + ":";
						}
						MacAddr = str.substring(0, str.length() - 1).toUpperCase();
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return MacAddr;
	}

	/**
	 * 获取本机的机器名
	 * 
	 * @return
	 */
	public static String getLocalHostName() {
		InetAddress addr = null;
		String hostName = "";
		try {
			addr = InetAddress.getLocalHost();
			hostName = addr.getHostName().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hostName;
	}

	/**
	 * 获取本机的内网IP
	 * 
	 * @return
	 */
	public static String getLocalIP() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) allNetInterfaces.nextElement();
				 if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
					continue;
				} else {
					Enumeration<?> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia != null && ia instanceof Inet4Address) {
							ip = ia.getHostAddress();
							break;
				        }
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	public static void main(String[] args) {
		System.out.println(getLocalIP());
	}
}
