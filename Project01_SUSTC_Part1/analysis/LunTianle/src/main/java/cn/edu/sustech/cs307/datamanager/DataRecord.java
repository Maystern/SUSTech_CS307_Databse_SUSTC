package cn.edu.sustech.cs307.datamanager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataRecord {
	
	private Map<RecordAttribute, Object> recordMap;
	
	public DataRecord() {
		this.recordMap = Collections.synchronizedMap(new HashMap<>());
	}
	
	public Object getValue(RecordAttribute attribute) {
		return this.recordMap.get(attribute);
	}
	
	public void putValue(RecordAttribute attribute, Object value) {
		this.recordMap.put(attribute, value);
	}
	
	public static enum RecordAttribute {
		ITEM_NAME,
		ITEM_TYPE,
		ITEM_PRICE,
		RETRIEVAL_CITY,
		RETRIEVAL_START_TIME,
		RETRIEVAL_COURIER,
		RETRIEVAL_COURIER_GENDER,
		RETRIEVAL_COURIER_PHONE_NUMBER,
		RETRIEVAL_COURIER_AGE,
		DELIVERY_FINISHED_TIME,
		DELIVERY_CITY,
		DELIVERY_COURIER,
		DELIVERY_COURIER_GENDER,
		DELIVERY_COURIER_PHONE_NUMBER,
		DELIVERY_COURIER_AGE,
		ITEM_EXPORT_CITY,
		ITEM_EXPORT_TAX,
		ITEM_EXPORT_TIME,
		ITEM_IMPORT_CITY,
		ITEM_IMPORT_TAX,
		ITEM_IMPORT_TIME,
		CONTAINER_CODE,
		CONTAINER_TYPE,
		SHIP_NAME,
		COMPANY_NAME,
		LOG_TIME;
	}
	
}
