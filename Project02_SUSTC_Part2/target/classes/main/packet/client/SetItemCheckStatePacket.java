package main.packet.client;

import main.packet.Packet;

public class SetItemCheckStatePacket extends Packet {
	
	private String cookie;
	private String item;
	private boolean set;
	
	public SetItemCheckStatePacket(String context) {
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.item = info[1];
		this.set = Boolean.parseBoolean(info[2]);
	}
	
	public boolean getSetState() {
		return this.set;
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
		return 39;
	}
	
}
