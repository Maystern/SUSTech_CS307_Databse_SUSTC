package main.packet.server;

import java.util.Arrays;

import main.packet.Packet;

public class GetAllItemsAtPortInfoPacket extends Packet {
	
	private String context;
	
	public GetAllItemsAtPortInfoPacket(String[] items) {
		this.context = Arrays.deepToString(items).trim();
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
		return 38;
	}
	
}
