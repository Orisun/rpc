package protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import serialize.DataTransform;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class Protocol {

	byte lowEndian = 1;// 0大端字节序，1小端字节序
	int totalLen;
	int sessionID;
	byte msgType;// 1请求，2响应
	String msg;
	public final byte[] postfix = new byte[] { (byte) 9, (byte) 11, (byte) 13, (byte) 17, (byte) 18 };

	public byte[] serialize() {
		ByteBuffer bbufer = ByteBuffer.allocate(1024);
		bbufer.put(lowEndian);
		byte[] msgB = msg.getBytes();
		totalLen = 15 + msgB.length;
		if (lowEndian == (byte) 1) {
			bbufer.put(DataTransform.intToBytes(totalLen, true));
			bbufer.put(DataTransform.intToBytes(sessionID, true));
		} else {
			bbufer.put(DataTransform.intToBytes(totalLen, false));
			bbufer.put(DataTransform.intToBytes(sessionID, false));
		}
		bbufer.put(msgType);
		bbufer.put(msgB);
		bbufer.put(postfix);
		byte[] bytes = new byte[bbufer.position()];
		bbufer.flip();
		bbufer.get(bytes);
		return bytes;
	}

	public static Protocol deserialize(byte[] bytes) {
		Protocol protocol = new Protocol();
		int totalLen = bytes.length;
		assert totalLen > 15;
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			byte little = (byte) (bais.read());
			protocol.setLowEndian(little);
			byte[] lenb = new byte[4];
			byte[] sidb = new byte[4];
			bais.read(lenb);
			bais.read(sidb);
			int len = 0;
			int ssid = 0;
			if (little == (byte) 1) {
				len = DataTransform.bytesToInt(lenb, true);
				ssid = DataTransform.bytesToInt(sidb, true);
			} else {
				len = DataTransform.bytesToInt(lenb, false);
				ssid = DataTransform.bytesToInt(sidb, false);
			}
			assert len == totalLen;
			protocol.setTotalLen(len);
			protocol.setSessionID(ssid);
			protocol.setMsgType((byte) (bais.read()));
			byte[] msgB = new byte[len - 15];
			bais.read(msgB);
			protocol.setMsg(new String(msgB));
			byte[] post = new byte[5];
			bais.read(post);
			assert post[0] == (byte) 9;
			assert post[1] == (byte) 11;
			assert post[2] == (byte) 13;
			assert post[3] == (byte) 17;
			assert post[4] == (byte) 18;
			return protocol;
		} catch (Exception e) {
			throw new RuntimeException("deserialize protocol failed.", e);
		} finally {
			try {
				bais.close();
			} catch (IOException e) {
			}
		}
	}

	public byte getLowEndian() {
		return lowEndian;
	}

	public void setLowEndian(byte lowEndian) {
		this.lowEndian = lowEndian;
	}

	public int getTotalLen() {
		return totalLen;
	}

	public void setTotalLen(int totalLen) {
		this.totalLen = totalLen;
	}

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
