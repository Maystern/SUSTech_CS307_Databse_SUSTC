package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.DBUtils;
import cn.edu.sustech.dbms2.client.interfaces.StaffInfo;
import cn.edu.sustech.dbms2.client.packet.Packet;

public class StaffInfoPacket extends Packet {

	private String context;
	private StaffInfo info;
	
	public StaffInfoPacket(String context) {
		this.context = context;
		this.info = DBUtils.loadStaffInfo(context);
	}
	
	public StaffInfo getInfo() {
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
		return 18;
	}
}
