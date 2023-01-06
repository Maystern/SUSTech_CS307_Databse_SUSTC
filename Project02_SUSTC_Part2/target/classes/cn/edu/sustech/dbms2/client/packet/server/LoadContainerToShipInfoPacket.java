package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class LoadContainerToShipInfoPacket extends Packet {
	
	private String context;
	private boolean success;
	
	public LoadContainerToShipInfoPacket(String context) {
		this.context = context;
		this.success = Boolean.parseBoolean(context);
	}
	
	public boolean isSuccess() {
		return this.success;
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
		return 30;
	}
}
