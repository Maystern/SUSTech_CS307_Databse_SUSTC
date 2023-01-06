package main.packet.server;

import main.packet.Packet;

public class StartShipSailingInfoPacket extends Packet {
	
	private String context;
	
	public StartShipSailingInfoPacket(boolean success) {
		this.context = "" + success;
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
		return 34;
	}
	
}
