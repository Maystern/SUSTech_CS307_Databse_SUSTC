package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.packet.Packet;

public class GetAllItemsAtPortInfoPacket extends Packet {
	
	private String context;
	private String[] items;
	
	public GetAllItemsAtPortInfoPacket(String context) {
		this.context = context;
		if (this.context.equals("[]")) {
			this.items = new String[0];
		} else {
			if (this.context.contains(",")) {
				this.items = this.context.substring(1, this.context.length() - 1).split(",");
			} else {
				this.items = new String[] {this.context.substring(1, this.context.length() - 1)};
			}
		}
	}
	
	public String[] getItems() {
		return this.items;
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
		return 38;
	}
	
}
