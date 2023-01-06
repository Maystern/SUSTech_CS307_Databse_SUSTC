package main.packet.client;

import main.packet.Packet;

public class ContainerPacket extends Packet {
	
	private String context;
	private String cookie;
	private String code;
	
	public ContainerPacket(String context) {
		this.context = context;
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.code = info[1];
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public String getContainerCode() {
		return this.code;
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
		return 11;
	}

}
