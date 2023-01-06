import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<DataRecord> records = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File("src/data.csv"));
            String titles[] = scanner.nextLine().substring(1).split(",");
            while (scanner.hasNextLine()) {
                String[] datas = scanner.nextLine().split(",");
                DataRecord record = new DataRecord();
                int i = 0;
                for (String title : titles) {
                    String tmp = title.replace(" ", "");
                    DataRecord.RecordAttribute attribute = DataRecord.RecordAttribute.valueOf(tmp);
                    if (!datas[i++].isEmpty()) {
                        record.putValue(attribute, datas[i - 1]);
                    }
                }
                records.add(record);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PostgreSQLConnector connector = new PostgreSQLConnector("localhost", 5432, "postgres", "postgres", "ljcfyh_123@99");
        try {
            connector.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        data_import1 import1 = new data_import1(connector);
        import1.work(records);
    }
}
