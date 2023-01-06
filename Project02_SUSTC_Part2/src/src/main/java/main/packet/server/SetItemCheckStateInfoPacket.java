package main.packet.server;

import main.packet.Packet;

public class SetItemCheckStateInfoPacket extends Packet {

	private String context;
	
	public SetItemCheckStateInfoPacket(boolean success) {
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
		return 40;
	}
}
