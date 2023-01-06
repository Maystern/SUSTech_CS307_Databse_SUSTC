package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class ImportTaxRateInfoPacket extends Packet {
	
	private String context;
	private double rate;
	
	public ImportTaxRateInfoPacket(String context) {
		this.context = context;
		this.rate = Double.parseDouble(context);
	}
	
	public double getRate() {
		return this.rate;
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
		return 24;
	}
}
