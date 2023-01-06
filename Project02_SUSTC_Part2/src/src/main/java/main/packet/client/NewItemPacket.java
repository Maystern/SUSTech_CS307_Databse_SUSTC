package main.packet.client;

import main.packet.Packet;
import main.DBUtils;
import main.interfaces.ItemInfo;

public class NewItemPacket extends Packet {
	
	private String cookie;
	private ItemInfo info;
	
	public NewItemPacket(String context) {
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.info = DBUtils.loadItemInfo(info[1]);
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public ItemInfo getInfo() {
		return this.info;
	}

	@Override
	public String getContext() {
		return cookie;
	}

	@Override
	public int getCode() {
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 19;
	}
	
}
