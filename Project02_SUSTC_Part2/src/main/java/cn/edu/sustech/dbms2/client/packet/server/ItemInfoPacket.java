package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.DBUtils;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo;
import cn.edu.sustech.dbms2.client.packet.Packet;

public class ItemInfoPacket extends Packet {

	private String context;
	private ItemInfo info;
	
	public ItemInfoPacket(String context) {
		this.context = context;
		this.info = DBUtils.loadItemInfo(context);
	}
	
	public ItemInfo getInfo() {
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
		return 16;
	}
}
