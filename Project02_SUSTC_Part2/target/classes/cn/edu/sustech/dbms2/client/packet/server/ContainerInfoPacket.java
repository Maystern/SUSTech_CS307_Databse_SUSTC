package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.packet.Packet;
import cn.edu.sustech.dbms2.client.DBUtils;
import cn.edu.sustech.dbms2.client.interfaces.ContainerInfo;

public class ContainerInfoPacket extends Packet {

	private String context;
	private ContainerInfo info;
	
	public ContainerInfoPacket(String context) {
		this.context = context;
		this.info = DBUtils.loadContainerInfo(context);
	}
	
	public ContainerInfo getInfo() {
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
		return 12;
	}

}
