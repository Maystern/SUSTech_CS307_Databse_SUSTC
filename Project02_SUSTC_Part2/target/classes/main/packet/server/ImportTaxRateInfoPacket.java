package main.packet.server;

import main.packet.Packet;

public class ImportTaxRateInfoPacket extends Packet {
	
	private String context;
	
	public ImportTaxRateInfoPacket(double rate) {
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
		return 24;
	}
	
}
