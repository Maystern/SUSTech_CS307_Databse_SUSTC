package main.packet.client;

import main.packet.Packet;

public class LoadItemToContainerPacket extends Packet {
	
	private String cookie;
	private String item;
	private String code;
	
	public LoadItemToContainerPacket(String context) {
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.item = info[1];
		this.code = info[2];
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public String getItem() {
		return this.item;
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
		return 31;
	}
	
}
