// $Id$

package hunspell.merge;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class DictionaryTableModel extends AbstractTableModel {

  public static final int COLUMN_NAME = 0;
  public static final int COLUMN_TYPE = 1;
  public static final int COLUMN_WORDS = 2;
  public static final int COLUMN_ENCODING = 3;

  private String[] columnNames;
  private Vector<DictionaryFile> data;

  public DictionaryTableModel(Vector<DictionaryFile> data) {
    this.data = data;
    columnNames = new String[] {
        "Name",
        "Type",
        "Words",
        "Language (Encoding)"};
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

  private String prepare(String text) {
    return " " + text + " ";
  }

  public Object getValueAt(int row, int col) {
    DictionaryFile file = data.elementAt(row);
    switch (col) {
      case COLUMN_NAME:
        return prepare(file.getName());
      case COLUMN_TYPE:
        return prepare(file.getType().getName());
      case COLUMN_WORDS:
        return prepare(file.getWordsCount());
      case COLUMN_ENCODING:
        return prepare(file.getSummary());
    }
    return null;
  }
}
