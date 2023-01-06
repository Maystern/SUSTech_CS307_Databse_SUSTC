import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class data_import1 {
    private PostgreSQLConnector sqlConnector;
    private HashMap<String, Boolean> shipMap;
    private HashMap<String, Boolean> containerMap;
    private HashMap<String, Boolean> retrievalCourierMap;
    private HashMap<String, Boolean> deliveryCourierMap;
    public data_import1(PostgreSQLConnector sqlConnector) {
        this.sqlConnector = sqlConnector;
        shipMap = new HashMap<>();
        containerMap = new HashMap<>();
        retrievalCourierMap = new HashMap<>();
        deliveryCourierMap = new HashMap<>();
    }
    public void work(List<DataRecord> records) {
        int cnt = 0;
        for (DataRecord record : records) {
            cnt++;
            if (cnt % 10000 == 0)
                System.out.println(cnt);
            try {
                //  向 container 表格填充数据
                String containerCode = record.getValue(DataRecord.RecordAttribute.ContainerCode);
                if (containerCode != null && containerMap.get(containerCode) == null) {
                    String containerType = record.getValue(DataRecord.RecordAttribute.ContainerType);
                    String containerSQL = String.format("INSERT INTO container (code, type) VALUES('%s', '%s')", containerCode, containerType);
                    sqlConnector.prepareStatement(containerSQL).execute();
                    containerMap.put(containerCode, true);
                }
                // 向 ship 表格填充数据
                String shipName = record.getValue(DataRecord.RecordAttribute.ShipName);
                String companyName = record.getValue(DataRecord.RecordAttribute.CompanyName);
                if (shipName != null && shipMap.get(shipName) == null) {
                    String shipSQL = String.format("INSERT INTO ship (name, company) VALUES('%s', '%s')", shipName, companyName);
                    sqlConnector.prepareStatement(shipSQL).execute();
                    shipMap.put(shipName, true);
                }
                // 向 export_information 表格填充数据
                String exportCity = record.getValue(DataRecord.RecordAttribute.ItemExportCity);
                String exportTime = record.getValue(DataRecord.RecordAttribute.ItemExportTime);
                String exportTax = record.getValue(DataRecord.RecordAttribute.ItemExportTax);
                String exportSQL = String.format("INSERT INTO export_information(city, tax) VALUES('%s', '%s') RETURNING id", exportCity, exportTax);
                if (exportTime != null) {
                    exportSQL = String.format("INSERT INTO export_information(city, time, tax) VALUES('%s', '%s', '%s') RETURNING id", exportCity, exportTime, exportTax);
                }
                PreparedStatement statement = sqlConnector.prepareStatement(exportSQL);
                statement.execute();
                ResultSet resultset = statement.getResultSet();
                resultset.next();
                long export_information_ID = resultset.getLong(1);
                // 向 delivery_courier 表格填充数据
                String delivery_courier_phone_number = record.getValue(DataRecord.RecordAttribute.DeliveryCourierPhoneNumber);
                // 向 import_information 表格填充数据
                String importCity = record.getValue(DataRecord.RecordAttribute.ItemImportCity);
                String importTime = record.getValue(DataRecord.RecordAttribute.ItemImportTime);
                String importTax = record.getValue(DataRecord.RecordAttribute.ItemImportTax);
                String importSQL = String.format("INSERT INTO import_information(city, tax) VALUES('%s', '%s') RETURNING id", importCity, importTax);
                if (importTime != null) {
                    importSQL = String.format("INSERT INTO import_information(city, time, tax) VALUES('%s', '%s', '%s') RETURNING id", importCity, importTime, importTax);
                }
                statement = sqlConnector.prepareStatement(importSQL);
                statement.execute();
                resultset = statement.getResultSet();
                resultset.next();
                long import_information_ID = resultset.getLong(1);
                // 向 delivery_courier 表格填充数据
                if (delivery_courier_phone_number != null && deliveryCourierMap.get(delivery_courier_phone_number) == null) {
                    String delivery_courier_name = record.getValue(DataRecord.RecordAttribute.DeliveryCourier);
                    String delivery_courier_gender = record.getValue(DataRecord.RecordAttribute.DeliveryCourierGender);
                    String delivery_courier_age = record.getValue(DataRecord.RecordAttribute.DeliveryCourierAge);
                    String delivery_courier_SQL =  String.format("INSERT INTO delivery_courier(phone_number, name, gender, age, company) VALUES('%s', '%s', '%s', %s, '%s')", delivery_courier_phone_number, delivery_courier_name, delivery_courier_gender, delivery_courier_age, companyName);
                    sqlConnector.prepareStatement(delivery_courier_SQL).execute();
                  deliveryCourierMap.put(delivery_courier_phone_number, true);
                }
                // 向 retrieval_courier 表格填充数据
                String retrieval_courier_phone_number = record.getValue(DataRecord.RecordAttribute.RetrievalCourierPhoneNumber);
                if (retrieval_courier_phone_number != null && retrievalCourierMap.get(retrieval_courier_phone_number) == null) {
                    String retrieval_courier_name = record.getValue(DataRecord.RecordAttribute.RetrievalCourier);
                    String retrieval_courier_gender = record.getValue(DataRecord.RecordAttribute.RetrievalCourierGender);
                    String retrieval_courier_age = record.getValue(DataRecord.RecordAttribute.RetrievalCourierAge);
                    String retrieval_courier_SQL =  String.format("INSERT INTO retrieval_courier(phone_number, name, gender, age, company) VALUES('%s', '%s', '%s', %s, '%s')", retrieval_courier_phone_number, retrieval_courier_name, retrieval_courier_gender, retrieval_courier_age, companyName);
                    sqlConnector.prepareStatement(retrieval_courier_SQL).execute();
                    retrievalCourierMap.put(retrieval_courier_phone_number, true);
                }
                //  向 retrieval_information 表格填充数据
                String retrieval_information_city = record.getValue(DataRecord.RecordAttribute.RetrievalCity);
                String retrieval_information_start_time = record.getValue(DataRecord.RecordAttribute.RetrievalStartTime);
                String retrieval_information_courier_phone_number = record.getValue(DataRecord.RecordAttribute.RetrievalCourierPhoneNumber);
                String retrieval_information_SQL = String.format("INSERT INTO retrieval_information(city, start_time, courier_phone_number) VALUES('%s', '%s', '%s') RETURNING id", retrieval_information_city, retrieval_information_start_time, retrieval_information_courier_phone_number);
                statement = sqlConnector.prepareStatement(retrieval_information_SQL);
                statement.execute();
                resultset = statement.getResultSet();
                resultset.next();
                long retrieval_information_ID = resultset.getLong(1);

                //  向 delivery_information 表格填充数据
                String delivery_information_city = record.getValue(DataRecord.RecordAttribute.DeliveryCity);
                String delivery_information_SQL = String.format("INSERT INTO delivery_information(city) VALUES('%s') RETURNING id", delivery_information_city);
                if (delivery_courier_phone_number != null) {
                    delivery_information_SQL = String.format("INSERT INTO delivery_information(city, courier_phone_number) VALUES('%s', '%s') RETURNING id", delivery_information_city, delivery_courier_phone_number);
                    String retrieval_information_finish_time = record.getValue(DataRecord.RecordAttribute.DeliveryFinishedTime);
                    if (retrieval_information_finish_time != null) {
                        delivery_information_SQL = String.format("INSERT INTO delivery_information(city, finish_time, courier_phone_number) VALUES('%s', '%s', '%s') RETURNING id", delivery_information_city, retrieval_information_finish_time, delivery_courier_phone_number);
                    }
                }
                statement = sqlConnector.prepareStatement(delivery_information_SQL);
                statement.execute();
                resultset = statement.getResultSet();
                resultset.next();
                long delivery_information_ID = resultset.getLong(1);
                // 向 item 中填充表格
                String item_name = record.getValue(DataRecord.RecordAttribute.ItemName);
                String item_type = record.getValue(DataRecord.RecordAttribute.ItemType);
                String item_price = record.getValue(DataRecord.RecordAttribute.ItemPrice);
                String item_log_time = record.getValue(DataRecord.RecordAttribute.LogTime);
                String itemSQL = String.format("INSERT INTO item(name, type, price, import_information_id, export_information_id, delivery_information_id, retrieval_information_id, log_time) VALUES('%s', '%s', %s, %d, %d, %d, %d, '%s')", item_name, item_type, item_price, import_information_ID, export_information_ID, delivery_information_ID, retrieval_information_ID,item_log_time);
                if (containerCode == null && shipName != null) {
                    itemSQL = String.format("INSERT INTO item(name, type, price, ship_name, import_information_id, export_information_id, delivery_information_id, retrieval_information_id, log_time) VALUES('%s', '%s', %s, '%s', %d, %d, %d, %d, '%s')", item_name, item_type, item_price, shipName, import_information_ID, export_information_ID, delivery_information_ID, retrieval_information_ID,item_log_time);
                } else if (containerCode != null && shipName == null) {
                    itemSQL = String.format("INSERT INTO item(name, type, price, container_code, import_information_id, export_information_id, delivery_information_id, retrieval_information_id, log_time) VALUES('%s', '%s', %s, '%s', %d, %d, %d, %d, '%s')", item_name, item_type, item_price, containerCode, import_information_ID, export_information_ID, delivery_information_ID, retrieval_information_ID,item_log_time);
                } else if (containerCode != null && shipName != null) {
                    itemSQL = String.format("INSERT INTO item(name, type, price, container_code, ship_name, import_information_id, export_information_id, delivery_information_id, retrieval_information_id, log_time) VALUES('%s', '%s', %s, '%s', '%s', %d, %d, %d, %d, '%s')", item_name, item_type, item_price, containerCode, shipName, import_information_ID, export_information_ID, delivery_information_ID, retrieval_information_ID,item_log_time);
                }
                sqlConnector.prepareStatement(itemSQL).execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
