package main.packet.client;

import main.packet.Packet;

public class CourierCountPacket extends Packet {

	private String cookie;
	
	public CourierCountPacket(String cookie) {
		this.cookie = cookie;
	}
	
	public String getCookie() {
		return this.cookie;
	}

	@Override
	public String getContext() {
		return cookie;
	}

	@Override
	public int getCode() {
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 7;
	}
	
}
