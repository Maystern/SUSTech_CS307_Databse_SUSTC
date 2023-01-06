import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

public class DataRecord {
    private Map<RecordAttribute, String> recordMap;
    public DataRecord() {
        recordMap = new HashMap<>();
    }
    public String getValue(RecordAttribute attribute) {
        return recordMap.get(attribute);
    }
    public void putValue(RecordAttribute attribute, String value) {
        recordMap.put(attribute, value);
    }
    public static enum RecordAttribute {
        ItemName,
        ItemType,
        ItemPrice,
        RetrievalCity,
        RetrievalStartTime,
        RetrievalCourier,
        RetrievalCourierGender,
        RetrievalCourierPhoneNumber,
        RetrievalCourierAge,
        DeliveryFinishedTime,
        DeliveryCity,
        DeliveryCourier,
        DeliveryCourierGender,
        DeliveryCourierPhoneNumber,
        DeliveryCourierAge,
        ItemExportCity,
        ItemExportTax,
        ItemExportTime,
        ItemImportCity,
        ItemImportTax,
        ItemImportTime,
        ContainerCode,
        ContainerType,
        ShipName,
        CompanyName,
        LogTime;
    }
}
