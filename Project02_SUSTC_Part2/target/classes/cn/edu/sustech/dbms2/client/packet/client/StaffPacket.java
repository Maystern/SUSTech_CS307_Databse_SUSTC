package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class StaffPacket extends Packet {
	
	private String context;
	
	public StaffPacket(String cookie, String name) {
		this.context = cookie + "$" + name;
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
		return 17;
	}
}
