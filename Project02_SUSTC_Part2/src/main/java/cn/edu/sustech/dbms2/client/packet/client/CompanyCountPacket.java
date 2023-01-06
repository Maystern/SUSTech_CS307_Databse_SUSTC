package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class CompanyCountPacket extends Packet {
	
	private String cookie;
	
	public CompanyCountPacket(String cookie) {
		this.cookie = cookie;
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	@Override
	public String getContext() {
		return cookie;
	}

	@Override
	public int getCode() {
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 3;
	}
	
}
