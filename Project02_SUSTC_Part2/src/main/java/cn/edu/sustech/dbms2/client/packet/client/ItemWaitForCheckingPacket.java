package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class ItemWaitForCheckingPacket extends Packet {
	
	private String context;
	
	public ItemWaitForCheckingPacket(String cookie, String item) {
		this.context = cookie + "$" + item;
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
		return 27;
	}
	
}
