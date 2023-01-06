package main.packet.client;

import main.packet.Packet;

public class LoginPacket extends Packet {
	
	private String context;
	private String user;
	private String password;
	
	public LoginPacket(String context) {
		this.context = context;
		int index = context.indexOf("$");
		user = this.context.substring(0, index);
		password = this.context.substring(index + 1);
	}
	
	public String getUser() {
		return this.user;
	}
	
	public String getPassword() {
		return this.password;
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
		return 1;
	}

}
