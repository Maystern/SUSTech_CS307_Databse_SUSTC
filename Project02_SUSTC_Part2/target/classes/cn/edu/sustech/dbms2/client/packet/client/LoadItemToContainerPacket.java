package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class LoadItemToContainerPacket extends Packet {
	
	private String context;
	
	public LoadItemToContainerPacket(String cookie, String item, String code) {
		this.context = cookie + "$" + item + "$" + code;
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
		return 31;
	}
	
}
