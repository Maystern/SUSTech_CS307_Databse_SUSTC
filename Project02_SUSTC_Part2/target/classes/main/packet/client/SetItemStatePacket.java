package main.packet.client;

import main.packet.Packet;
import main.DBUtils;
import main.interfaces.ItemState;

public class SetItemStatePacket extends Packet {
	
	private String cookie;
	private String itemName;
	private ItemState state;
	
	public SetItemStatePacket(String context) {
		String info[] = context.split("[$]");
		this.cookie = info[0];
		this.itemName = info[1];
		this.state = ItemState.valueOf(info[2]);
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public ItemState getState() {
		return this.state;
	}
	
	public String getItemName() {
		return this.itemName;
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
		return 21;
	}
	
}
