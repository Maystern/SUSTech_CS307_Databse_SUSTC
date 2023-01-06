package main.packet.server;

import main.packet.Packet;
import main.DBUtils;
import main.interfaces.ShipInfo;

public class ShipInfoPacket extends Packet {

	private String context;
	
	public ShipInfoPacket(ShipInfo info) {
		this.context = DBUtils.toShipInfoString(info);
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
		return 14;
	}

}
