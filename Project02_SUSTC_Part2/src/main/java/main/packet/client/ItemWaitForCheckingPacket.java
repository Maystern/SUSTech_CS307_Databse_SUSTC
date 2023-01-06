package main.packet.client;

import main.packet.Packet;

public class ItemWaitForCheckingPacket extends Packet {

	private String cookie;
	private String item;
	
	public ItemWaitForCheckingPacket(String context) {
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.item = info[1];
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public String getItem() {
		return this.item;
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
		return 27;
	}
	
}
