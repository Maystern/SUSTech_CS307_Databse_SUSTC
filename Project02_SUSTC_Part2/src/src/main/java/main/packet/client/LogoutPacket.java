package main.packet.client;

import main.packet.Packet;

public class LogoutPacket extends Packet {
	
	private String cookie;
	
	public LogoutPacket(String context) {
		this.cookie = context;
	}
	
	public String getCookie() {
		return this.cookie;
		
	}
	
	@Override
	public String getContext() {
		// TODO Auto-generated method stub
		return this.cookie;
	}

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 41;
	}
	
}
