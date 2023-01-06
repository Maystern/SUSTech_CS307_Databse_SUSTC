package main.packet.server;

import main.packet.Packet;
import main.DBUtils;
import main.interfaces.ContainerInfo;

public class ContainerInfoPacket extends Packet {

	private String context;
	
	public ContainerInfoPacket(ContainerInfo info) {
		this.context = DBUtils.toContainerInfoString(info);
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
		return 12;
	}

}
