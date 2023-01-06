package main.packet.server;

import main.packet.Packet;

public class CityCountInfoPacket extends Packet {
	
	private String context;
	
	public CityCountInfoPacket(int count) {
		this.context = "" + count;
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
		return 6;
	}
}
