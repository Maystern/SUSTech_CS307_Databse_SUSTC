package main.packet.server;

import main.packet.Packet;

public class LoginInfoPacket extends Packet {
	
private String context;
	

	public LoginInfoPacket(boolean isSuccess, String session, String user, String staffType, String city, String company, boolean gender, String phone, int birthYear) {
		if (isSuccess) {
			this.context = "1$" + session + "$" + user + "$" + staffType + "$" + (city == null ? "@null@" : city) + "$" + (company == null ? "@null@" : company) + "$" + gender + "$" + phone + "$" + birthYear;
		} else {
			this.context = "0$1";
		}
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
		return 2;
	}

}
