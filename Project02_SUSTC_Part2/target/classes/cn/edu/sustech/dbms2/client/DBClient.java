package cn.edu.sustech.dbms2.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

import cn.edu.sustech.dbms2.client.packet.Packet;
import cn.edu.sustech.dbms2.client.packet.PacketManager;

public class DBClient {
	
	private static String host;
	private static int port;
	
	static {
		File file = new File("config.properties");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream(file));
			if (pro.get("HOST") == null) {
				pro.setProperty("HOST", "127.0.0.1");
			}
			if (pro.get("PORT") == null) {
				pro.setProperty("PORT", "23333");
			}
			host = pro.getProperty("HOST");
			port = Integer.parseInt(pro.getProperty("PORT"));
			pro.store(new FileOutputStream(file), "Have fun");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public DBClient() {}
	
	public void sendPacket(Packet packet) throws IOException {
		try {
			Socket socket = new Socket();
			socket.setSoTimeout(10000);
			socket.setKeepAlive(true);
			socket.setOOBInline(true);
			socket.connect(new InetSocketAddress(host, port));
			BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
			writer.write((packet.getCode() + "@" + packet.getContext()).getBytes());
			writer.flush();
			writer.close();
			socket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public Packet sendAndReceivePacket(Packet packet) throws IOException {
		try {
			Socket socket = new Socket();
			socket.setSoTimeout(10000);
			socket.setKeepAlive(true);
			socket.setOOBInline(true);
			socket.connect(new InetSocketAddress(host, port));
			BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
			writer.write((packet.getCode() + "@" + packet.getContext()).getBytes());
			writer.flush();
			BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
			byte[] bytes = new byte[1024 * 8];
			int len;
			if ((len = input.read(bytes)) != -1) {
				writer.close();
				input.close();
				socket.close();
				return PacketManager.getInstance().receivePacket(len, bytes);
			}
			writer.close();
			input.close();
			socket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
}
