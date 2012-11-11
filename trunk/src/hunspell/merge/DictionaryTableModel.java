// $Id$

package hunspell.merge;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class DictionaryTableModel extends AbstractTableModel {

  private static final int COLUMN_NAME = 0;
  private static final int COLUMN_TYPE = 1;
  private static final int COLUMN_ENCODING = 2;

  private String[] columnNames;
  private Vector<DictionaryFile> data;

  public DictionaryTableModel(Vector<DictionaryFile> data) {
    this.data = data;
    columnNames = new String[] {
        "Name",
        "Type",
        "Encoding"};
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public String getColumnName(int column) {
    return columnNames[column];
  }

  public int getRowCount() {
    return data.size();
  }

  public Object getValueAt(int row, int col) {
    DictionaryFile file = data.elementAt(row);
    switch (col) {
      case COLUMN_NAME:
        return file.getName();
      case COLUMN_TYPE:
        return file.getType().getName();
      case COLUMN_ENCODING:
        return file.getCharset();
    }
    return null;
  }
}
