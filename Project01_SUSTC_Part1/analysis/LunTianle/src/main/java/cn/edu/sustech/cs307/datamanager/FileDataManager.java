package cn.edu.sustech.cs307.datamanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.edu.sustech.cs307.Main;
import cn.edu.sustech.cs307.datamanager.DataRecord.RecordAttribute;
import cn.edu.sustech.cs307.utils.CalendarUtils;

public class FileDataManager extends DataManager {
	
	
	private HashSet<String> courierSet;
	private HashSet<String> containerSet;
	private HashSet<String> shipSet;
	private File dir;
	
	public FileDataManager(File directory) {
		this.dir = directory;
		courierSet = new HashSet<>();
		shipSet = new HashSet<>();
		containerSet = new HashSet<>();
		
	}
	
	public boolean deleteRetrievalInformationByItem(String item) {
		try {
			this.dir.mkdirs();
			File retrievalInformationFile = new File(dir, "retrieval_information.json");
			if (!retrievalInformationFile.exists()) {
				try {
					retrievalInformationFile.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(retrievalInformationFile, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
			retrievalInformation.remove(item);
			FileOutputStream out = new FileOutputStream(retrievalInformationFile);
			out.write(retrievalInformation.toJSONString().getBytes());
			out.close();
			return true;
		} catch (IOException e) {
			Main.debug("File Deleting failed...", true);
			return false;
		}
	}
	
	public int deleteRetrievalInformationByCourier(String cpn) {
		try {
			this.dir.mkdirs();
			File retrievalInformationFile = new File(dir, "retrieval_information.json");
			if (!retrievalInformationFile.exists()) {
				try {
					retrievalInformationFile.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(retrievalInformationFile, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
			List<String> removeList = new ArrayList<>();
			for (Map.Entry<String, Object> entry : retrievalInformation.entrySet()) {
				JSONObject object = (JSONObject) entry.getValue();
				if (object.getString("courier_phone_number").equals(cpn)) {
					removeList.add(entry.getKey());
				}
			}
			for (String key : removeList) {
				retrievalInformation.remove(key);
			}
			FileOutputStream out = new FileOutputStream(retrievalInformationFile);
			out.write(retrievalInformation.toJSONString().getBytes());
			out.close();
			return removeList.size();
		} catch (IOException e) {
			Main.debug("File Deleting failed...", true);
			return -1;
		}
	}
	
	public boolean insertRetrievalInformation(DataRecord record) {
		try {
			this.dir.mkdirs();
			File retrievalInformationFile = new File(dir, "retrieval_information.json");
			if (!retrievalInformationFile.exists()) {
				try {
					retrievalInformationFile.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(retrievalInformationFile, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
			JSONObject rinfObject = new JSONObject();
			rinfObject.put("city", (String) record.getValue(RecordAttribute.RETRIEVAL_CITY));
			rinfObject.put("start_time", (String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME));
			rinfObject.put("courier_phone_number", (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER));
			retrievalInformation.put((String) record.getValue(RecordAttribute.ITEM_NAME), rinfObject);
			FileOutputStream out = new FileOutputStream(retrievalInformationFile);
			out.write(retrievalInformation.toJSONString().getBytes());
			out.close();
			return true;
		} catch (IOException e) {
			Main.debug("File Inserting failed...", true);
			return false;
		}
	}
	
	public boolean multiInsertRetrievalInformation(List<DataRecord> records) {
		try {
			this.dir.mkdirs();
			File retrievalInformationFile = new File(dir, "retrieval_information.json");
			if (!retrievalInformationFile.exists()) {
				try {
					retrievalInformationFile.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(retrievalInformationFile, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
			for (DataRecord record : records) {
				JSONObject rinfObject = new JSONObject();
				rinfObject.put("city", (String) record.getValue(RecordAttribute.RETRIEVAL_CITY));
				rinfObject.put("start_time", (String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME));
				rinfObject.put("courier_phone_number", (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER));
				retrievalInformation.put((String) record.getValue(RecordAttribute.ITEM_NAME), rinfObject);
			}
			FileOutputStream out = new FileOutputStream(retrievalInformationFile);
			out.write(retrievalInformation.toJSONString().getBytes());
			out.close();
			return true;
		} catch (IOException e) {
			Main.debug("File Inserting failed...", true);
			return false;
		}
	}
	
	public int selectRetrievalInformationByItem(String item) {
		this.dir.mkdirs();
		File retrievalInformationFile = new File(dir, "retrieval_information.json");
		if (!retrievalInformationFile.exists()) {
			try {
				retrievalInformationFile.createNewFile();
				JSONObject object = new JSONObject();
				writeContentToFile(retrievalInformationFile, object.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
		return (retrievalInformation.containsKey(item) ? 1 : 0);
	}
	
	public int selectRetrievalInformationByCourier(String courierCPN) {
		this.dir.mkdirs();
		File retrievalInformationFile = new File(dir, "retrieval_information.json");
		if (!retrievalInformationFile.exists()) {
			try {
				retrievalInformationFile.createNewFile();
				JSONObject object = new JSONObject();
				writeContentToFile(retrievalInformationFile, object.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
		List<String> selectList = new ArrayList<>();
		for (Map.Entry<String, Object> entry : retrievalInformation.entrySet()) {
			JSONObject object = (JSONObject) entry.getValue();
			if (object.getString("courier_phone_number").equals(courierCPN)) {
				selectList.add(entry.getKey());
			}
		}
		return selectList.size();
	}
	
	public int updateRetrievalCourierByItem(String item, String newCourierPN) {
		try {
			this.dir.mkdirs();
			File retrievalInformationFile = new File(dir, "retrieval_information.json");
			if (!retrievalInformationFile.exists()) {
				try {
					retrievalInformationFile.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(retrievalInformationFile, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
			JSONObject object = retrievalInformation.getJSONObject(item);
			object.put("courier_phone_number", newCourierPN);
			retrievalInformation.put(item, object);
			FileOutputStream out = new FileOutputStream(retrievalInformationFile);
			out.write(retrievalInformation.toJSONString().getBytes());
			out.close();
			return 1;
		} catch (IOException e) {
			Main.debug("Updating failed...", true);
			return -1;
		}
	}
	
	public int updateRetrievalCourierByCourier(String oldCourierPN, String newCourierPN) {
		try {
			this.dir.mkdirs();
			File retrievalInformationFile = new File(dir, "retrieval_information.json");
			if (!retrievalInformationFile.exists()) {
				try {
					retrievalInformationFile.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(retrievalInformationFile, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
			int cnt = 0;
			for (Map.Entry<String, Object> entry : retrievalInformation.entrySet()) {
				JSONObject object = (JSONObject) entry.getValue();
				if (object.getString("courier_phone_number").equals(oldCourierPN)) {
					object.put("courier_phone_number", newCourierPN);
					++cnt;
				}
				retrievalInformation.put(entry.getKey(), object);
			}
			FileOutputStream out = new FileOutputStream(retrievalInformationFile);
			out.write(retrievalInformation.toJSONString().getBytes());
			out.close();
			return cnt;
		} catch (IOException e) {
			Main.debug("Updating failed...", true);
			return -1;
		}
	}
	
	public boolean addCourier(String company, String name, char gender, int birthYear, String pn) {
		try {
			this.dir.mkdirs();
			File courierFile = new File(dir, "courier.json");
			if (!courierFile.exists()) {
				try {
					courierFile.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(courierFile, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			JSONObject courier = JSONObject.parseObject(loadContentFromFile(courierFile));
			JSONObject object = new JSONObject();
			object.put("company", company);
			object.put("gender", gender);
			object.put("birth_year", birthYear);
			object.put("name", name);
			courier.put(pn, object);
			FileOutputStream out = new FileOutputStream(courierFile);
			out.write(courier.toJSONString().getBytes());
			out.close();
			return true;
		} catch (IOException e) {
			Main.debug("Courier \"" + name + "\"adding failed...", true);
			return false;	
		}
	}
	
	@Override
	public void init(List<DataRecord> records) {
		dir.mkdirs();
		File containerFile = new File(dir, "container.json");
		File shipFile = new File(dir, "ship.json");
		File itemFile = new File(dir, "item.json");
		File importInformationFile = new File(dir, "import_information.json");
		File exportInformationFile = new File(dir, "export_information.json");
		File courierFile = new File(dir, "courier.json");
		File deliveryInformationFile = new File(dir, "delivery_information.json");
		File retrievalInformationFile = new File(dir, "retrieval_information.json");
		List<File> fileList = Arrays.asList(containerFile, shipFile, itemFile, 
				importInformationFile, exportInformationFile,
				courierFile, deliveryInformationFile, retrievalInformationFile);
		fileList.forEach(file -> {
			if (!file.exists()) {
				try {
					file.createNewFile();
					JSONObject object = new JSONObject();
					writeContentToFile(file, object.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		JSONObject container = JSONObject.parseObject(loadContentFromFile(containerFile));
		JSONObject ship = JSONObject.parseObject(loadContentFromFile(shipFile));
		JSONObject item = JSONObject.parseObject(loadContentFromFile(itemFile));
		JSONObject importInformation = JSONObject.parseObject(loadContentFromFile(importInformationFile));
		JSONObject exportInformation = JSONObject.parseObject(loadContentFromFile(exportInformationFile));
		JSONObject courier = JSONObject.parseObject(loadContentFromFile(courierFile));
		JSONObject deliveryInformation = JSONObject.parseObject(loadContentFromFile(deliveryInformationFile));
		JSONObject retrievalInformation = JSONObject.parseObject(loadContentFromFile(retrievalInformationFile));
		
		records.forEach((record) -> {
			String companyName = (String) record.getValue(RecordAttribute.COMPANY_NAME);
			
			//container table
			String containerCode = (String) record.getValue(RecordAttribute.CONTAINER_CODE);
			if (containerCode != null && !containerSet.contains(containerCode)) {
				JSONObject rawContainer = new JSONObject();
				rawContainer.put("type", (String) record.getValue(RecordAttribute.CONTAINER_TYPE));
				container.put(containerCode, rawContainer);
				containerSet.add(containerCode);
			}

			//ship table
			String shipName = (String) record.getValue(RecordAttribute.SHIP_NAME);
			if (shipName != null && !shipSet.contains(shipName)) {
				JSONObject rawShip = new JSONObject();
				rawShip.put("company", companyName);
				ship.put(shipName, rawShip);
				shipSet.add(shipName);
			}
			
			//item table
			JSONObject rawItem = new JSONObject();
			String itemName = (String) record.getValue(RecordAttribute.ITEM_NAME);
			rawItem.put("type", (String) record.getValue(RecordAttribute.ITEM_TYPE));
			rawItem.put("price", new BigDecimal((String) record.getValue(RecordAttribute.ITEM_PRICE)));
			if (containerCode != null) {
				rawItem.put("container", containerCode);
			}
			if (shipName != null) {
				rawItem.put("ship", shipName);
			}
			rawItem.put("log_time", (String) record.getValue(RecordAttribute.LOG_TIME));
			item.put(itemName, rawItem);
			
			//import_information table
			String importCity = (String) record.getValue(RecordAttribute.ITEM_IMPORT_CITY);
			String importTime = (String) record.getValue(RecordAttribute.ITEM_IMPORT_TIME);
			String importTax = (String) record.getValue(RecordAttribute.ITEM_IMPORT_TAX);
			
			JSONObject rawImportInformation = new JSONObject();
			rawImportInformation.put("city", importCity);
			if (importTime != null) {
				rawImportInformation.put("import_time", importTime);
			}
			rawImportInformation.put("tax", new BigDecimal(importTax));;
			importInformation.put(itemName, rawImportInformation);
			
			//export_information table
			String exportCity = (String) record.getValue(RecordAttribute.ITEM_EXPORT_CITY);
			String exportTime = (String) record.getValue(RecordAttribute.ITEM_EXPORT_TIME);
			String exportTax = (String) record.getValue(RecordAttribute.ITEM_EXPORT_TAX);
			
			JSONObject rawExportInformation = new JSONObject();
			rawExportInformation.put("city", exportCity);
			if (exportTime != null) {
				rawExportInformation.put("import_time", exportTime);
			}
			rawExportInformation.put("tax", new BigDecimal(exportTax));;
			exportInformation.put(itemName, rawExportInformation);
			
			//courier table
			String deliveryCPN = (String) record.getValue(RecordAttribute.DELIVERY_COURIER_PHONE_NUMBER);
			if (deliveryCPN != null && !courierSet.contains(deliveryCPN)) {
				JSONObject courierObject = new JSONObject();
				courierObject.put("name", (String) record.getValue(RecordAttribute.DELIVERY_COURIER));
				courierObject.put("gender", ((String) record.getValue(RecordAttribute.DELIVERY_COURIER_GENDER)).charAt(0));
				try {
					int birthYear = CalendarUtils.getCalendar((String) record.getValue(RecordAttribute.DELIVERY_FINISHED_TIME)).get(Calendar.YEAR) - (int) Double.parseDouble((String) record.getValue(RecordAttribute.DELIVERY_COURIER_AGE));
					courierObject.put("birth_year", birthYear);
				} catch (Exception e) {
					e.printStackTrace();
				}
				courierObject.put("company", companyName);
				courier.put(deliveryCPN, courierObject);
				courierSet.add(deliveryCPN);
			}
			String retrievalCPN = (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER);
			if (!courierSet.contains(retrievalCPN)) {
				JSONObject courierObject = new JSONObject();
				courierObject.put("name", (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER));
				courierObject.put("gender", ((String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_GENDER)).charAt(0));
				try {
					int birthYear = CalendarUtils.getCalendar((String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME)).get(Calendar.YEAR) - (int) Double.parseDouble((String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_AGE));
					courierObject.put("birth_year", birthYear);
				} catch (Exception e) {
					e.printStackTrace();
				}
				courierObject.put("company", companyName);
				courier.put(retrievalCPN, courierObject);
				courierSet.add(retrievalCPN);
			}
			
			//delivery_information table
			String finishTime = (String) record.getValue(RecordAttribute.DELIVERY_FINISHED_TIME);
			JSONObject dinfObject = new JSONObject();
			dinfObject.put("city", (String) record.getValue(RecordAttribute.DELIVERY_CITY));
			if (finishTime != null) {
				dinfObject.put("finish_time", finishTime);
			}
			if (deliveryCPN != null) {
				dinfObject.put("courier_phone_number", deliveryCPN);
			}
			deliveryInformation.put((String) record.getValue(RecordAttribute.ITEM_NAME), dinfObject);
			
			//retrieval_information table
			JSONObject rinfObject = new JSONObject();
			
			rinfObject.put("city", (String) record.getValue(RecordAttribute.RETRIEVAL_CITY));
			rinfObject.put("start_time", (String) record.getValue(RecordAttribute.RETRIEVAL_START_TIME));
			rinfObject.put("courier_phone_number", (String) record.getValue(RecordAttribute.RETRIEVAL_COURIER_PHONE_NUMBER));
			retrievalInformation.put((String) record.getValue(RecordAttribute.ITEM_NAME), rinfObject);
		});
		Main.debug("All records have been imported", false);
		try {
			FileOutputStream out = new FileOutputStream(containerFile);
			out.write(container.toJSONString().getBytes());
			out.close();
			out = new FileOutputStream(shipFile);
			out.write(ship.toJSONString().getBytes());
			out.close();
			out = new FileOutputStream(courierFile);
			out.write(courier.toJSONString().getBytes());
			out.close();
			out = new FileOutputStream(itemFile);
			out.write(item.toJSONString().getBytes());
			out.close();
			out = new FileOutputStream(importInformationFile);
			out.write(importInformation.toJSONString().getBytes());
			out.close();
			out = new FileOutputStream(exportInformationFile);
			out.write(exportInformation.toJSONString().getBytes());
			out.close();
			out = new FileOutputStream(retrievalInformationFile);
			out.write(retrievalInformation.toJSONString().getBytes());
			out.close();
			out = new FileOutputStream(deliveryInformationFile);
			out.write(deliveryInformation.toJSONString().getBytes());
			out.close();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeContentToFile(File file, String content) throws IOException {
		FileOutputStream stream = new FileOutputStream(file);
		stream.write(content.getBytes());
		stream.close();
	}
	
	public static String loadContentFromFile(File file) {
		byte[] fileContent = new byte[((Long) file.length()).intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(fileContent);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new String(fileContent);
		
	}

}
