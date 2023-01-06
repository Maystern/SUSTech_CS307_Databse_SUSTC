package cn.edu.sustech.dbms2.client.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import cn.edu.sustech.dbms2.client.ThrowableHandler;
import cn.edu.sustech.dbms2.client.packet.client.CityCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CompanyCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CourierCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ExportTaxRatePacket;
import cn.edu.sustech.dbms2.client.packet.client.GetAllItemsAtPortPacket;
import cn.edu.sustech.dbms2.client.packet.client.ImportTaxRatePacket;
import cn.edu.sustech.dbms2.client.packet.client.ItemPacket;
import cn.edu.sustech.dbms2.client.packet.client.ItemWaitForCheckingPacket;
import cn.edu.sustech.dbms2.client.packet.client.LoadContainerToShipPacket;
import cn.edu.sustech.dbms2.client.packet.client.LoadItemToContainerPacket;
import cn.edu.sustech.dbms2.client.packet.client.LoginPacket;
import cn.edu.sustech.dbms2.client.packet.client.LogoutPacket;
import cn.edu.sustech.dbms2.client.packet.client.NewItemPacket;
import cn.edu.sustech.dbms2.client.packet.client.SetItemCheckStatePacket;
import cn.edu.sustech.dbms2.client.packet.client.SetItemStatePacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipPacket;
import cn.edu.sustech.dbms2.client.packet.client.StaffPacket;
import cn.edu.sustech.dbms2.client.packet.client.StartShipSailingPacket;
import cn.edu.sustech.dbms2.client.packet.client.UnloadItemPacket;
import cn.edu.sustech.dbms2.client.packet.server.CityCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CompanyCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ContainerInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CourierCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ExportTaxRateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.GetAllItemsAtPortInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ImportTaxRateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ItemInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ItemWaitForCheckingInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoadContainerToShipInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoadItemToContainerInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoginInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.NewItemInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.SetItemCheckStateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.SetItemStateInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.StaffInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.StartShipSailingInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.UnloadItemInfoPacket;
import cn.edu.sustech.dbms2.client.packet.client.ContainerPacket;

public class PacketManager {
	
	private static PacketManager manager = new PacketManager();
	private HashMap<Integer, Class<? extends Packet>> packetCodes;
	
	private PacketManager() {
		this.init();
	}
	
	private void init() {
		packetCodes = new HashMap<>();
		packetCodes.put(LoginPacket.getStaticCode(), LoginPacket.class);
		packetCodes.put(LoginInfoPacket.getStaticCode(), LoginInfoPacket.class);
		packetCodes.put(CompanyCountPacket.getStaticCode(), CompanyCountPacket.class);
		packetCodes.put(CompanyCountInfoPacket.getStaticCode(), CompanyCountInfoPacket.class);
		packetCodes.put(CityCountPacket.getStaticCode(), CityCountPacket.class);
		packetCodes.put(CityCountInfoPacket.getStaticCode(), CityCountInfoPacket.class);
		packetCodes.put(CourierCountPacket.getStaticCode(), CourierCountPacket.class);
		packetCodes.put(CourierCountInfoPacket.getStaticCode(), CourierCountInfoPacket.class);
		packetCodes.put(ShipCountPacket.getStaticCode(), ShipCountPacket.class);
		packetCodes.put(ShipCountInfoPacket.getStaticCode(), ShipCountInfoPacket.class);
		packetCodes.put(ContainerPacket.getStaticCode(), ContainerPacket.class);
		packetCodes.put(ContainerInfoPacket.getStaticCode(), ContainerInfoPacket.class);
		packetCodes.put(ShipPacket.getStaticCode(), ShipPacket.class);
		packetCodes.put(ShipInfoPacket.getStaticCode(), ShipInfoPacket.class);
		packetCodes.put(ItemPacket.getStaticCode(), ItemPacket.class);
		packetCodes.put(ItemInfoPacket.getStaticCode(), ItemInfoPacket.class);
		packetCodes.put(StaffPacket.getStaticCode(), StaffPacket.class);
		packetCodes.put(StaffInfoPacket.getStaticCode(), StaffInfoPacket.class);
		packetCodes.put(NewItemPacket.getStaticCode(), NewItemPacket.class);
		packetCodes.put(NewItemInfoPacket.getStaticCode(), NewItemInfoPacket.class);
		packetCodes.put(SetItemStatePacket.getStaticCode(), SetItemStatePacket.class);
		packetCodes.put(SetItemStateInfoPacket.getStaticCode(), SetItemStateInfoPacket.class);
		packetCodes.put(ImportTaxRatePacket.getStaticCode(), ImportTaxRatePacket.class);
		packetCodes.put(ImportTaxRateInfoPacket.getStaticCode(), ImportTaxRateInfoPacket.class);
		packetCodes.put(ExportTaxRatePacket.getStaticCode(), ExportTaxRatePacket.class);
		packetCodes.put(ExportTaxRateInfoPacket.getStaticCode(), ExportTaxRateInfoPacket.class);
		packetCodes.put(ItemWaitForCheckingPacket.getStaticCode(), ItemWaitForCheckingPacket.class);
		packetCodes.put(ItemWaitForCheckingInfoPacket.getStaticCode(), ItemWaitForCheckingInfoPacket.class);
		packetCodes.put(LoadContainerToShipPacket.getStaticCode(), LoadContainerToShipPacket.class);
		packetCodes.put(LoadContainerToShipInfoPacket.getStaticCode(), LoadContainerToShipInfoPacket.class);
		packetCodes.put(LoadItemToContainerPacket.getStaticCode(), LoadItemToContainerPacket.class);
		packetCodes.put(LoadItemToContainerInfoPacket.getStaticCode(), LoadItemToContainerInfoPacket.class);
		packetCodes.put(StartShipSailingPacket.getStaticCode(), StartShipSailingPacket.class);
		packetCodes.put(UnloadItemPacket.getStaticCode(), UnloadItemPacket.class);
		packetCodes.put(StartShipSailingInfoPacket.getStaticCode(), StartShipSailingInfoPacket.class);
		packetCodes.put(UnloadItemInfoPacket.getStaticCode(), UnloadItemInfoPacket.class);
		packetCodes.put(GetAllItemsAtPortPacket.getStaticCode(), GetAllItemsAtPortPacket.class);
		packetCodes.put(GetAllItemsAtPortInfoPacket.getStaticCode(), GetAllItemsAtPortInfoPacket.class);
		packetCodes.put(SetItemCheckStatePacket.getStaticCode(), SetItemCheckStatePacket.class);
		packetCodes.put(SetItemCheckStateInfoPacket.getStaticCode(), SetItemCheckStateInfoPacket.class);
		packetCodes.put(LogoutPacket.getStaticCode(), LogoutPacket.class);

		
	}
	
	public static PacketManager getInstance() {
		return manager;
	}
	
	public Packet receivePacket(int len, byte[] packetBytes) {
		String msg = new String(packetBytes, 0, len);
		int index = msg.indexOf('@');
		if (index == -1) {
			return null;
		}
		int code = Integer.parseInt(msg.substring(0, index));
		String context = msg.substring(index + 1);
		Class<? extends Packet> packetClazz = packetCodes.get(code);
		try {
			Constructor<? extends Packet> constructor = packetClazz.getConstructor(String.class);
			return constructor.newInstance(context);
		} catch (Exception e) {
			ThrowableHandler.handleThrowable(e);
		}
		return null;
	}
	
}
