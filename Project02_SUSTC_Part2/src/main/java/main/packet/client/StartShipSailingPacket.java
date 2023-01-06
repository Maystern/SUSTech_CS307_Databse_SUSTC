package main.packet.client;

import main.packet.Packet;

public class StartShipSailingPacket extends Packet {

	private String cookie;
	private String ship;
	
	public StartShipSailingPacket(String context) {
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.ship = info[1];
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public String getItem() {
		return this.ship;
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
		return 33;
	}
	
}
