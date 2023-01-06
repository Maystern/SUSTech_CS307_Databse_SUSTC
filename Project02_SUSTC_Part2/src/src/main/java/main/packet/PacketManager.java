package main.packet;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import main.packet.client.CityCountPacket;
import main.packet.client.CompanyCountPacket;
import main.packet.client.ContainerPacket;
import main.packet.client.CourierCountPacket;
import main.packet.client.ExportTaxRatePacket;
import main.packet.client.GetAllItemsAtPortPacket;
import main.packet.client.ImportTaxRatePacket;
import main.packet.client.ItemPacket;
import main.packet.client.ItemWaitForCheckingPacket;
import main.packet.client.LoadContainerToShipPacket;
import main.packet.client.LoadItemToContainerPacket;
import main.packet.client.LoginPacket;
import main.packet.client.LogoutPacket;
import main.packet.client.NewItemPacket;
import main.packet.client.SetItemCheckStatePacket;
import main.packet.client.SetItemStatePacket;
import main.packet.client.ShipCountPacket;
import main.packet.client.ShipPacket;
import main.packet.client.StaffPacket;
import main.packet.client.StartShipSailingPacket;
import main.packet.client.UnloadItemPacket;
import main.packet.server.CityCountInfoPacket;
import main.packet.server.CompanyCountInfoPacket;
import main.packet.server.ContainerInfoPacket;
import main.packet.server.CourierCountInfoPacket;
import main.packet.server.ExportTaxRateInfoPacket;
import main.packet.server.GetAllItemsAtPortInfoPacket;
import main.packet.server.ImportTaxRateInfoPacket;
import main.packet.server.ItemInfoPacket;
import main.packet.server.ItemWaitForCheckingInfoPacket;
import main.packet.server.LoadContainerToShipInfoPacket;
import main.packet.server.LoadItemToContainerInfoPacket;
import main.packet.server.LoginInfoPacket;
import main.packet.server.NewItemInfoPacket;
import main.packet.server.SetItemCheckStateInfoPacket;
import main.packet.server.SetItemStateInfoPacket;
import main.packet.server.ShipCountInfoPacket;
import main.packet.server.ShipInfoPacket;
import main.packet.server.StaffInfoPacket;
import main.packet.server.StartShipSailingInfoPacket;
import main.packet.server.UnloadItemInfoPacket;



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
			//ThrowableHandler.handleThrowable(e);
		}
		return null;
	}
	
}
