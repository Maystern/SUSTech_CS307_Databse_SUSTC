package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class ContainerPacket extends Packet {
	
	private String context;
	
	public ContainerPacket(String cookie, String code) {
		this.context = cookie + "$" + code;
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
		return 11;
	}

}
