package main.packet.server;

import main.packet.Packet;

public class UnloadItemInfoPacket extends Packet {

	private String context;
	
	public UnloadItemInfoPacket(boolean success) {
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
		return 36;
	}
	
}
