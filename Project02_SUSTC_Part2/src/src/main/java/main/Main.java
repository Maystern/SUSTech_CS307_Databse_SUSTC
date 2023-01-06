package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import main.interfaces.ContainerInfo;
import main.interfaces.LogInfo;
import main.interfaces.StaffInfo;
import main.interfaces.ShipInfo;
import main.interfaces.ItemInfo;

import main.packet.Packet;
import main.packet.PacketManager;
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

public class Main {
	private static String url;
	private static String user;
	private static String pass;
	private static int port;
	private static InetSocketAddress address;
	
	static {
		File file = new File("config.properties");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream(file));
			if (pro.get("HOST") == null) {
				pro.setProperty("HOST", "127.0.0.1");
			}
			if (pro.get("PORT") == null) {
				pro.setProperty("PORT", "23333");
			}
			if (pro.get("DATABASE-URL") == null) {
				pro.setProperty("DATABASE-URL", "localhost:5432/cslab");
			}
			if (pro.get("DATABASE-USER") == null) {
				pro.setProperty("DATABASE-USER", "test");
			}
			if (pro.get("DATABASE-PASSWORD") == null) {
				pro.setProperty("DATABASE-PASSWORD", "123456");
			}
			url = pro.getProperty("DATABASE-URL");
			user = pro.getProperty("DATABASE-USER");
			pass = pro.getProperty("DATABASE-PASSWORD");
			port = Integer.parseInt(pro.getProperty("PORT"));
			address = new InetSocketAddress(pro.getProperty("HOST"), port);
			pro.store(new FileOutputStream(file), "Have fun");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static HashMap<UUID, LogInfo> session;

	public static void main(String[] args) throws IOException {
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
		DBManipulation databaseManager = new DBManipulation(url, user, pass);
		ServerSocket serverSocket = new ServerSocket();
		//serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 23333));
		serverSocket.bind(address);
		debug("Server has been bound to port " + port, false);
		session = new HashMap<>();
		while (true) {
			Socket socket = serverSocket.accept();
			debug("Socket from " + socket.getInetAddress().getHostAddress() +  " connected...", false);
			BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
			byte[] bytes = new byte[1024 * 8];
			int len;
			if ((len = input.read(bytes)) != -1) {
				Packet packet = PacketManager.getInstance().receivePacket(len, bytes);
				debug("Packet received from " + socket.getInetAddress().getHostAddress() + "...", false);
				Packet backPacket = null;
				if (packet instanceof LoginPacket) {
					LoginPacket lp = (LoginPacket) packet;
					String user = lp.getUser();
					String password = lp.getPassword();
					StaffInfo info = databaseManager.getStaffInfoByRoot(user);
					if (info == null || !info.basicInfo().password().equals(password)) {
						backPacket = new LoginInfoPacket(false, null, null, null, null, null, false, null, 0);
					} else {
						UUID newSession = UUID.randomUUID();
						backPacket = new LoginInfoPacket(true, newSession.toString(), user, info.basicInfo().type().toString(), info.city(), info.company(), info.isFemale(), info.phoneNumber(), info.age());
						session.put(newSession, info.basicInfo());
					}
				}
				if (packet instanceof CompanyCountPacket) {
					CompanyCountPacket ccp = (CompanyCountPacket) packet;
					LogInfo info = session.get(UUID.fromString(ccp.getCookie()));
					int count = -1;
					if (info != null) {
						count = databaseManager.getCompanyCount(info);
					}
					backPacket = new CompanyCountInfoPacket(count);
				}
				if (packet instanceof CityCountPacket) {
					CityCountPacket ccp = (CityCountPacket) packet;
					LogInfo info = session.get(UUID.fromString(ccp.getCookie()));
					int count = -1;
					if (info != null) {
						count = databaseManager.getCityCount(info);
					}
					backPacket = new CityCountInfoPacket(count);
				}
				if (packet instanceof CourierCountPacket) {
					CourierCountPacket ccp = (CourierCountPacket) packet;
					LogInfo info = session.get(UUID.fromString(ccp.getCookie()));
					int count = -1;
					if (info != null) {
						count = databaseManager.getCourierCount(info);
					}
					backPacket = new CourierCountInfoPacket(count);
				}
				if (packet instanceof ShipCountPacket) {
					ShipCountPacket scp = (ShipCountPacket) packet;
					LogInfo info = session.get(UUID.fromString(scp.getCookie()));
					int count = -1;
					if (info != null) {
						count = databaseManager.getShipCount(info);
					}
					backPacket = new ShipCountInfoPacket(count);
				}
				if (packet instanceof ContainerPacket) {
					ContainerPacket cp = (ContainerPacket) packet;
					LogInfo info = session.get(UUID.fromString(cp.getCookie()));
					ContainerInfo ci = null;
					if (info != null) {
						ci = databaseManager.getContainerInfo(info, cp.getContainerCode());
					}
					backPacket = new ContainerInfoPacket(ci);
				}
				if (packet instanceof ShipPacket) {
					ShipPacket sp = (ShipPacket) packet;
					LogInfo info = session.get(UUID.fromString(sp.getCookie()));
					ShipInfo si = null;
					if (info != null) {
						si = databaseManager.getShipInfo(info, sp.getShip());
					}
					backPacket = new ShipInfoPacket(si);
				}
				if (packet instanceof ItemPacket) {
					ItemPacket ip = (ItemPacket) packet;
					LogInfo info = session.get(UUID.fromString(ip.getCookie()));
					ItemInfo ii = null;
					if (info != null) {
						ii = databaseManager.getItemInfo(info, ip.getItem());
					}
					backPacket = new ItemInfoPacket(ii);
				}
				if (packet instanceof StaffPacket) {
					StaffPacket sp = (StaffPacket) packet;
					LogInfo info = session.get(UUID.fromString(sp.getCookie()));
					StaffInfo si = null;
					if (info != null) {
						si = databaseManager.getStaffInfo(info, sp.getStaff());
					}
					backPacket = new StaffInfoPacket(si);
				}
				if (packet instanceof NewItemPacket) {
					NewItemPacket np = (NewItemPacket) packet;
					LogInfo info = session.get(UUID.fromString(np.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.newItem(info, np.getInfo());
					}
					backPacket = new NewItemInfoPacket(success);
				}
				if (packet instanceof SetItemStatePacket) {
					SetItemStatePacket sisp = (SetItemStatePacket) packet;
					LogInfo info = session.get(UUID.fromString(sisp.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.setItemState(info, sisp.getItemName(), sisp.getState());
					}
					backPacket = new SetItemStateInfoPacket(success);
				}
				if (packet instanceof ImportTaxRatePacket) {
					ImportTaxRatePacket itp = (ImportTaxRatePacket) packet;
					LogInfo info = session.get(UUID.fromString(itp.getCookie()));
					double rate = -1;
					if (info != null) {
						rate = databaseManager.getImportTaxRate(info, itp.getCity(), itp.getType());
					}
					backPacket = new ImportTaxRateInfoPacket(rate);
				}
				if (packet instanceof ExportTaxRatePacket) {
					ExportTaxRatePacket etp = (ExportTaxRatePacket) packet;
					LogInfo info = session.get(UUID.fromString(etp.getCookie()));
					double rate = -1;
					if (info != null) {
						rate = databaseManager.getExportTaxRate(info, etp.getCity(), etp.getType());
					}
					backPacket = new ExportTaxRateInfoPacket(rate);
				}
				if (packet instanceof ItemWaitForCheckingPacket) {
					ItemWaitForCheckingPacket iwfcp = (ItemWaitForCheckingPacket) packet;
					LogInfo info = session.get(UUID.fromString(iwfcp.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.itemWaitForChecking(info, iwfcp.getItem());
					}
					backPacket = new ItemWaitForCheckingInfoPacket(success);
				}
				if (packet instanceof LoadContainerToShipPacket) {
					LoadContainerToShipPacket lctsp = (LoadContainerToShipPacket) packet;
					LogInfo info = session.get(UUID.fromString(lctsp.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.loadContainerToShip(info, lctsp.getShip(), lctsp.getContainerCode());
					}
					backPacket = new LoadContainerToShipInfoPacket(success);
				}
				if (packet instanceof LoadItemToContainerPacket) {
					LoadItemToContainerPacket litcp = (LoadItemToContainerPacket) packet;
					LogInfo info = session.get(UUID.fromString(litcp.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.loadItemToContainer(info, litcp.getItem(), litcp.getContainerCode());
					}
					backPacket = new LoadItemToContainerInfoPacket(success);
				}
				if (packet instanceof StartShipSailingPacket) {
					StartShipSailingPacket sssp = (StartShipSailingPacket) packet;
					LogInfo info = session.get(UUID.fromString(sssp.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.shipStartSailing(info, sssp.getItem());
					}
					backPacket = new StartShipSailingInfoPacket(success);
				}
				if (packet instanceof UnloadItemPacket) {
					UnloadItemPacket uip = (UnloadItemPacket) packet;
					LogInfo info = session.get(UUID.fromString(uip.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.unloadItem(info, uip.getItem());
					}
					backPacket = new UnloadItemInfoPacket(success);
				}
				if (packet instanceof GetAllItemsAtPortPacket) {
					GetAllItemsAtPortPacket gaiap = (GetAllItemsAtPortPacket) packet;
					LogInfo info = session.get(UUID.fromString(gaiap.getCookie()));
					String[] items = new String[0];
					if (info != null) {
						items = databaseManager.getAllItemsAtPort(info);
					}
					backPacket = new GetAllItemsAtPortInfoPacket(items);
				}
				if (packet instanceof SetItemCheckStatePacket) {
					SetItemCheckStatePacket ssp = (SetItemCheckStatePacket) packet;
					LogInfo info = session.get(UUID.fromString(ssp.getCookie()));
					boolean success = false;
					if (info != null) {
						success = databaseManager.setItemCheckState(info, ssp.getItem(), ssp.getSetState());
					}
					backPacket = new SetItemCheckStateInfoPacket(success);
				}
				
				if (!(packet instanceof LogoutPacket)) {
					BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
					writer.write((backPacket.getCode() + "@" + backPacket.getContext()).getBytes());
					writer.flush();
					writer.close();
				} else {
					LogoutPacket lp = (LogoutPacket) packet;
					try {
						session.remove(UUID.fromString(lp.getCookie()));
					} catch (Exception e) {}
				}
			}
			input.close();
			socket.close();
				
		}
	}
	
	private static PrintWriter writer;
	
	public static void debug(String raw, boolean isWarnning) {
		Calendar c = Calendar.getInstance();
		String msg = String.format("[" + (!isWarnning ? "INFO" : "WARN") + "][%d/%d/%d %02d:%02d:%02d] %s", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), raw);
		writer.println(msg);
		writer.flush();
	}
	
}
