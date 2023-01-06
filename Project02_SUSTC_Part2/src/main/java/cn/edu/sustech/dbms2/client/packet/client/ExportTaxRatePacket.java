package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class ExportTaxRatePacket extends Packet {

	private String context;
	
	public ExportTaxRatePacket(String cookie, String city, String type) {
		this.context = cookie + "$" + city + "$" + type;
	}
	
	@Override
	public String getContext() {
		return context;
	}

	@Override
	public int getCode() {
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 25;
	}
}
