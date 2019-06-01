package serialize;

import exception.ArgumentException;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class DataTransform {
	
	private static final char[] Digit = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * byte数组转换成int
	 * 
	 * @param bRefArr
	 *            byte数组
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return int值
	 * @throws ArgumentException
	 *             byte数组长度超过4时抛出该异常
	 */
	public static int bytesToInt(byte[] bRefArr, boolean LowEndian)
			throws ArgumentException {
		int len = bRefArr.length;
		if (len > 4) {
			throw new ArgumentException("字节数组长度不能超过4");
		}

		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < len; i++) {
			bLoop = bRefArr[i];
			int shift;
			if (LowEndian) {
				shift = i;
			} else {
				shift = len - 1 - i;
			}
			iOutcome += (bLoop & 0xFF) << (8 * shift);// 之所以要跟0xFF进行与运行是为了把bLoop转换成int,去除符号位的影响
		}
		return iOutcome;
	}

	/**
	 * byte数组转换成long
	 * 
	 * @param bRefArr
	 *            byte数组
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return long值
	 * @throws ArgumentException
	 *             byte数组长度超过8时抛出该异常
	 */
	public static long bytesToLong(byte[] bRefArr, boolean LowEndian)
			throws ArgumentException {
		int len = bRefArr.length;
		if (len > 8) {
			throw new ArgumentException("字节数组长度不能超过8");
		}

		long iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < len; i++) {
			bLoop = bRefArr[i];
			int shift;
			if (LowEndian) {
				shift = i;
			} else {
				shift = len - 1 - i;
			}
			iOutcome += (bLoop & 0xFFL) << (8 * shift);// 之所以要跟0xFFL进行与运行是为了把bLoop转换成long,去除符号位的影响
		}
		return iOutcome;
	}

	/**
	 * byte数组转换成double
	 * 
	 * @param bRefArr
	 *            byte数组
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return double值
	 * @throws ArgumentException
	 *             byte数组长度超过8时抛出该异常
	 */
	public static double bytesToDouble(byte[] bRefArr, boolean LowEndian)
			throws ArgumentException {
		long l = bytesToLong(bRefArr, LowEndian);
		return Double.longBitsToDouble(l);
	}

	/**
	 * int转换为byte数组
	 * 
	 * @param number
	 *            int数
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return byte数组
	 */
	public static byte[] intToBytes(int number, boolean LowEndian) {
		int len = 4;
		byte[] rect = new byte[len];
		for (int i = 0; i < len; i++) {
			rect[i] = (byte) (number >>> (len - 1 - i) * 8);
		}
		if (LowEndian) {
			for (int i = 0; i < len / 2; i++) {
				byte swap = rect[i];
				rect[i] = rect[len - i - 1];
				rect[len - i - 1] = swap;
			}
		}
		return rect;
	}

	/**
	 * long转换为byte数组
	 * 
	 * @param number
	 *            long数
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return byte数组
	 */
	public static byte[] longToBytes(long number, boolean LowEndian) {
		int len = 8;
		byte[] rect = new byte[len];
		for (int i = 0; i < len; i++) {
			rect[i] = (byte) (number >>> (len - 1 - i) * 8);
		}
		if (LowEndian) {
			for (int i = 0; i < len / 2; i++) {
				byte swap = rect[i];
				rect[i] = rect[len - i - 1];
				rect[len - i - 1] = swap;
			}
		}
		return rect;
	}

	/**
	 * double转换为byte数组
	 * 
	 * @param number
	 *            double数值
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return byte数组
	 */
	public static byte[] doubleToBytes(double number, boolean LowEndian) {
		long l = Double.doubleToLongBits(number);
		return longToBytes(l, LowEndian);
	}

	/**
	 * IP转换成int值,int在全域上和IP是一一对应的
	 * 
	 * @param ip
	 * @return
	 * @throws ArgumentException
	 *             IP范围超界时抛出该异常
	 */
	public static int ip2int(String ip) throws ArgumentException {
		String[] arr = ip.trim().split("\\.");
		int part1 = Integer.parseInt(arr[0]);
		int part2 = Integer.parseInt(arr[1]);
		int part3 = Integer.parseInt(arr[2]);
		int part4 = Integer.parseInt(arr[3]);
		if (part1 >= 0 && part1 < 256 && part2 >= 0 && part2 < 256
				&& part3 >= 0 && part3 < 256 && part4 >= 0 && part4 < 256) {
			// 左移，正数左移之后有可能把最高位变为1，从而成为负数
			int rect = part1 << 24;
			rect += part2 << 16;
			rect += part3 << 8;
			rect += part4;
			return rect;
		} else {
			throw new ArgumentException("IP范围超界");
		}
	}

	/**
	 * int值转换成IP,int在全域上和IP是一一对应的
	 * 
	 * @param number
	 * @return
	 */
	public static String int2ip(int number) {
		StringBuilder sb = new StringBuilder();
		int part1 = number >>> 24;// 右移，如果是负数最高位的1会向右移，且最高位变为0
		int part2 = (0x00ff0000 & number) >>> 16;// 位移的优先级高于与运算的优先级
		int part3 = (0x0000ff00 & number) >>> 8;
		int part4 = 0x000000ff & number;
		sb.append(String.valueOf(part1));
		sb.append(".");
		sb.append(String.valueOf(part2));
		sb.append(".");
		sb.append(String.valueOf(part3));
		sb.append(".");
		sb.append(String.valueOf(part4));
		return sb.toString();
	}

	/**
	 * 一个将字节转化为十六进制ASSIC码的函数
	 * 
	 * @param ib
	 * @return
	 */
	public static String byteHEX(byte ib) {
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}
}
