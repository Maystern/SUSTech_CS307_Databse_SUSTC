package main.packet.server;

import main.packet.Packet;

public class ItemWaitForCheckingInfoPacket extends Packet {
	
	private String context;
	
	public ItemWaitForCheckingInfoPacket(boolean success) {
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
		return 28;
	}
	
}
