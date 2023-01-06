package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class LoginPacket extends Packet {
	
	private String context;
	
	public LoginPacket(String user, String password) {
		this.context = user + "$" + password;
	}
	
	@Override
	public String getContext() {
		return this.context;
	}

	@Override
	public int getCode() {
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 1;
	}
	
}
