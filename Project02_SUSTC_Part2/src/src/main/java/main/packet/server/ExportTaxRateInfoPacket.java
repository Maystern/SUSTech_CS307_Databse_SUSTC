package main.packet.server;

import main.packet.Packet;

public class ExportTaxRateInfoPacket extends Packet {
	
	private String context;
	
	public ExportTaxRateInfoPacket(double rate) {
		this.context = "" + rate;
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
		return 26;
	}
	
}
