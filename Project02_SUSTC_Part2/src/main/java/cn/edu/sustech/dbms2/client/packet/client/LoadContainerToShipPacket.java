package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class LoadContainerToShipPacket extends Packet {
	
	private String context;
	
	public LoadContainerToShipPacket(String cookie, String ship, String code) {
		this.context = cookie + "$" + ship + "$" + code;
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
		return 29;
	}
	
}
