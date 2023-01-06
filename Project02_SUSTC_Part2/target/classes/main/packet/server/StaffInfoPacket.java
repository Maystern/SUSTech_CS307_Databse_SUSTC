package main.packet.server;

import main.packet.Packet;
import main.DBUtils;
import main.interfaces.StaffInfo;

public class StaffInfoPacket extends Packet {

	private String context;
	
	public StaffInfoPacket(StaffInfo info) {
		this.context = DBUtils.toStaffInfoString(info);
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
		return 18;
	}

}
