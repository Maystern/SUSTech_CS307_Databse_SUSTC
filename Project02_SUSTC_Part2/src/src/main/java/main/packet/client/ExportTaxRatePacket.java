package main.packet.client;

import main.packet.Packet;

public class ExportTaxRatePacket extends Packet {
	
	private String context;
	private String cookie;
	private String city;
	private String type;
	
	public ExportTaxRatePacket(String context) {
		this.context = context;
		String[] info = context.split("[$]");
		this.cookie = info[0];
		this.city = info[1];
		this.type = info[2];
	}
	
	public String getCity() {
		return this.city;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getCookie() {
		return this.cookie;
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
		return 25;
	}
	
}
