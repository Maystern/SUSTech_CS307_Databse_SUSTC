package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.DBUtils;
import cn.edu.sustech.dbms2.client.interfaces.ShipInfo;
import cn.edu.sustech.dbms2.client.packet.Packet;

public class ShipInfoPacket extends Packet {

	private String context;
	private ShipInfo info;
	
	public ShipInfoPacket(String context) {
		this.context = context;
		this.info = DBUtils.loadShipInfo(context);
	}
	
	public ShipInfo getInfo() {
		return this.info;
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
		return 14;
	}
	
}
