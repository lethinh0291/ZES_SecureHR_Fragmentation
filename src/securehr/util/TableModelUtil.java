package securehr.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public final class TableModelUtil {
    private TableModelUtil() {
    }

    public static DefaultTableModel fromResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnLabel(i));
        }

        Vector<Vector<Object>> rows = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            rows.add(row);
        }

        return new DefaultTableModel(rows, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
