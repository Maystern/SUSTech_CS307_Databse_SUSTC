package cn.edu.sustech.dbms2.client.packet.client;

import cn.edu.sustech.dbms2.client.packet.Packet;
import cn.edu.sustech.dbms2.client.interfaces.ItemState;

public class SetItemStatePacket extends Packet {
	
	private String context;
	
	public SetItemStatePacket(String cookie, String name, ItemState itemInfo) {
		this.context = cookie + "$" + name + "$" + itemInfo.toString();
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
		return 21;
	}
}
