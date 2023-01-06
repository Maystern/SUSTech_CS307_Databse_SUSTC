package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class GetAllItemsAtPortPacket extends Packet{
	
	private String context;
	
	public GetAllItemsAtPortPacket(String cookie) {
		this.context = cookie;
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
		return 37;
	}
	
	
}
