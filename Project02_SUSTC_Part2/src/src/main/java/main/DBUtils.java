package main;

import main.interfaces.ContainerInfo;
import main.interfaces.ItemInfo;
import main.interfaces.ShipInfo;
import main.interfaces.StaffInfo;
import main.interfaces.ItemInfo.ImportExportInfo;
import main.interfaces.ItemInfo.RetrievalDeliveryInfo;
import main.interfaces.ItemState;
import main.interfaces.ContainerInfo.Type;

public class DBUtils {
	
	public static ContainerInfo loadContainerInfo(String s) {
		if (s.equals("null")) {
			return null;
		}
		String info[] = s.split("@");
		for (int i = 0; i < info.length; ++i) {
			if (info[i].equals("#null#")) {
				info[i] = null;
			}
		}
		return new ContainerInfo((info[0] == null ? null : Type.valueOf(info[0])), info[1], Boolean.parseBoolean(info[2]));
	}
	
	public static String toContainerInfoString(ContainerInfo c) {
		if (c == null) {
			return "null";
		}
		return (c.type() == null ? "#null#" : c.type().toString()) + "@" + (c.code() == null ? "#null#" : c.code()) + "@" + c.using();
	}
	
	public static ItemInfo loadItemInfo(String s) {
		if (s.equals("null")) {
			return null;
		}
		String info[] = s.split("@");
		for (int i = 0; i < info.length; ++i) {
			if (info[i].equals("#null#")) {
				info[i] = null;
			}
		}
		return new ItemInfo(info[0], info[1], Double.parseDouble(info[2]), ItemState.valueOf(info[3]), new RetrievalDeliveryInfo(info[4], info[5]), new RetrievalDeliveryInfo(info[6], info[7]), new ImportExportInfo(info[8], info[9], Double.parseDouble(info[10])), new ImportExportInfo(info[11], info[12], Double.parseDouble(info[13])));
	}
	
	public static String toItemInfoString(ItemInfo info) {
		if (info == null) {
			return "null";
		}
		return info.name() + "@" 
				+ info.$class() + "@" 
				+ info.price() + "@" 
				+ info.state().toString() + "@" 
				+ info.retrieval().city() + "@" 
				+ info.retrieval().courier() + "@" 
				+ info.delivery().city() + "@" 
				+ (info.delivery().courier() == null ? "#null#" : info.delivery().courier()) + "@" 
				+ info.$import().city() + "@" 
				+ (info.$import().officer() == null ? "#null#" : info.$import().officer()) + "@"
				+ info.$import().tax() + "@" 
				+ info.export().city() + "@" 
				+ (info.export().officer() == null ? "#null#" : info.export().officer()) + "@"
				+ info.export().tax();
	}
	
	public static ShipInfo loadShipInfo(String s) {
		if (s.equals("null")) {
			return null;
		}
		String info[] = s.split("@");
		return new ShipInfo(info[0], info[1], Boolean.parseBoolean(info[2]));
	}
	
	public static String toShipInfoString(ShipInfo ship) {
		if (ship == null) {
			return "null";
		}
		return ship.name() + "@" + ship.owner() + "@" + ship.sailing();
	}
	
	
	public static String toStaffInfoString(StaffInfo info) {
		if (info == null) {
			return "null";
		}
		return info.basicInfo().name() + "@" + info.basicInfo().type().toString() + "@" + (info.company() == null ? "#null#" : info.company()) + "@" + (info.city() == null ? "#null#" : info.city()) + "@" + info.isFemale() + "@" + info.age() + "@" + info.phoneNumber();
	}
	
	
}
