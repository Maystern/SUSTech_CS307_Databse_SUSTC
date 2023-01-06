package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class SetItemCheckStatePacket extends Packet {
	
	private String context;
	
	public SetItemCheckStatePacket(String cookie, String item, boolean set) {
		this.context = cookie + "$" + item + "$" + set;
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
		return 39;
	}
	
}
