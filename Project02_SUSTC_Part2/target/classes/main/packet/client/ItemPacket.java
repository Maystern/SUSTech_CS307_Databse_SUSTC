package main.packet.client;

import main.packet.Packet;

public class ItemPacket extends Packet {
	
	private String context;
	private String cookie;
	private String item;
	
	public ItemPacket(String context) {
		this.context = context;
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
		// TODO Auto-generated method stub
		return this.context;
	}

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 15;
	}

}
