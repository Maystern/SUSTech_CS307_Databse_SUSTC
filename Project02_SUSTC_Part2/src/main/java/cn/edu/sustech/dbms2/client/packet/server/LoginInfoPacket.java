package cn.edu.sustech.dbms2.client.packet.server;

import cn.edu.sustech.dbms2.client.packet.Packet;

import java.util.Calendar;

public class LoginInfoPacket extends Packet {
	
	private String context;
	private boolean isSuccess;
	private String info[];
	private String cookie;
	private String staffType;
	private String company;
	private String user;
	private String city;
	private boolean gender;
	private String phone;
	private int age;
	
	public String getCompany() {
		return company;
	}

	public String getUserName() {
		return user;
	}

	public String getCity() {
		return city;
	}

	public boolean getGender() {
		return gender;
	}

	public String getPhoneNumber() {
		return phone;
	}

	public int getAge() {
		return age;
	}

	public LoginInfoPacket(String context) {
		this.context = context;
		this.info = this.context.split("[$]");
		this.isSuccess = this.info[0].equals("1");
		if (!isSuccess) {
			return;
		}
		this.cookie = this.info[1];
		this.user = this.info[2];
		this.staffType = this.info[3];
		this.city = this.info[4].equals("@null@") ? null : this.info[4];
		this.company = this.info[5].equals("@null@") ? null : this.info[5];
		this.gender = Boolean.parseBoolean(this.info[6]);
		this.phone = this.info[7];
		this.age = Integer.parseInt(this.info[8]);
	}
	
	@Override
	public String getContext() {
		return this.context;
	}
	
	public String getCookie() {
		return this.cookie;
	}
	
	public String getStaffType() {
		return this.staffType;
	}
	
	public boolean isSuccess() {
		return this.isSuccess;
	}

	@Override
	public int getCode() {
		return getStaticCode();
	}
	
	public static int getStaticCode() {
		return 2;
	}

}
