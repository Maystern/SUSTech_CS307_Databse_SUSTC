package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;
import cn.edu.sustech.dbms2.client.DBUtils;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo;

public class NewItemPacket extends Packet {
	
	private String context;
	
	public NewItemPacket(String cookie, ItemInfo itemInfo) {
		this.context = cookie + "$" + DBUtils.toItemInfoString(itemInfo);
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
		return 19;
	}
}
