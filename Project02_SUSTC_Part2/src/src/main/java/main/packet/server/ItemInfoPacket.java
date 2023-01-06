package main.packet.server;

import main.packet.Packet;
import main.DBUtils;
import main.interfaces.ItemInfo;

public class ItemInfoPacket extends Packet {

	private String context;
	
	public ItemInfoPacket(ItemInfo info) {
		this.context = DBUtils.toItemInfoString(info);
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
		return 16;
	}

}
