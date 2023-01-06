package main.packet.client;

import main.packet.Packet;

public class LoadContainerToShipPacket extends Packet {
	
	private String cookie;
	private String ship;
	private String code;
	
	public LoadContainerToShipPacket(String context) {
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.ship = info[1];
		this.code = info[2];
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public String getShip() {
		return this.ship;
	}
	
	public String getContainerCode() {
		return this.code;
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
		return 29;
	}
	
}
