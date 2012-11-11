// $Id$

package hunspell.merge;

import hunspell.merge.xpi.XPI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

public class HunspellMerge extends JApplet {

  public static HunspellMerge instance;
  public JFrame mainFrame;
  private final Vector<DictionaryFile> dictionaries = new Vector<DictionaryFile>();
  private DictionaryTableModel tableModel = new DictionaryTableModel(dictionaries);
  private JTable table = new JTable(tableModel);
  private JScrollPane scrollPane = new JScrollPane(table);

  private JTextField editOutputName = new JTextField();
  private JTextField editDicFolder = new JTextField();
  private JButton buttonCompile = new JButton("Merge dictionaries");
  private JButton buttonDicFolder = new JButton("...");
  private JButton buttonOutDicFolder = new JButton("...");
  private Vector<DictionaryFile> selectedDictionaries = new Vector<DictionaryFile>();
  private JTextField editOutFolder = new JTextField();

  public HunspellMerge() {
    instance = this;
    mainFrame = new JFrame();

    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    mainFrame.setTitle("Hunspell dictionary merge");
    mainFrame.setSize(500, 400);
    mainFrame.setLocationRelativeTo(null);

    createGUI();

    mainFrame.setVisible(true);
    loadDictionaries();
  }

  private void loadDictionaries() {
    FileUtil.createFolder(FileUtil.tempFolder);
    dictionaries.clear();

    File[] files = FileUtil.findFiles(FileUtil.dictionaryFolder, DictionaryType.getAllExts());
    if (files != null) {
      for (File file : files) {
        DictionaryFile dictionary = new DictionaryFile(file);
        if (dictionary.isValid())
          dictionaries.add(dictionary);
      }
    }
    tableModel.fireTableDataChanged();
  }

  private void createGUI() {
    mainFrame.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.weightx = 1.0;
    c.weighty = 0.0;
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = GridBagConstraints.REMAINDER;

    // Source folder
    JPanel panelDicFolder = new JPanel(new BorderLayout(2, 2));
    panelDicFolder.setBorder(BorderFactory.createTitledBorder("Dictionary source folder"));
    editDicFolder.setText(FileUtil.dictionaryFolder);
    panelDicFolder.add(editDicFolder, BorderLayout.CENTER);
    buttonDicFolder.setToolTipText("Select dictionary source folder");
    buttonDicFolder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(FileUtil.dictionaryFolder);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
          FileUtil.dictionaryFolder = FileUtil.addDelimeter(fileChooser.getSelectedFile().getPath());
          editDicFolder.setText(FileUtil.dictionaryFolder);
          loadDictionaries();
        }
      }
    }
    );
    panelDicFolder.add(buttonDicFolder, BorderLayout.EAST);
    mainFrame.add(panelDicFolder, c);

    c.weighty = 1.0;

    // Dictionary list
    scrollPane.setBorder(BorderFactory.createTitledBorder("Available dictionaries (Select one or more)"));
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updateSelectedDictionaries();
        updateNewName();
      }
    });
    mainFrame.add(scrollPane, c);

    c.weighty = 0.0;

    // Output folder
    JPanel panelOutDicFolder = new JPanel(new BorderLayout(2, 2));
    panelOutDicFolder.setBorder(BorderFactory.createTitledBorder("Dictionary output folder"));
    editOutFolder.setText(FileUtil.outputFolder);
    panelOutDicFolder.add(editOutFolder, BorderLayout.CENTER);
    buttonOutDicFolder.setToolTipText("Select dictionary output folder");
    buttonOutDicFolder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(FileUtil.outputFolder);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
          FileUtil.outputFolder = FileUtil.addDelimeter(fileChooser.getSelectedFile().getPath());
          editOutFolder.setText(FileUtil.outputFolder);
        }
      }
    }
    );
    panelOutDicFolder.add(buttonOutDicFolder, BorderLayout.EAST);
    mainFrame.add(panelOutDicFolder, c);

    JPanel panelOutName = new JPanel(new BorderLayout(2, 2));
    panelOutName.setBorder(BorderFactory.createTitledBorder("Output dictionary name (without extension)"));
    panelOutName.add(editOutputName, BorderLayout.CENTER);
    mainFrame.add(panelOutName, c);

    // Action button
    buttonCompile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createDictionaries();
      }
    }
    );

    JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    panelButton.add(buttonCompile);
    mainFrame.add(panelButton, c);
  }

  private void createDictionaries() {
    if (selectedDictionaries.size() == 0)
      return;

    FileUtil.createFolder(FileUtil.outputFolder);

    AffReader outAff = new AffReader();
    DicReader outDic = new DicReader();

    for (DictionaryFile dictionary : selectedDictionaries) {
      dictionary.readFiles();
      outAff.appendAff(dictionary.affReader);
      outDic.appendDic(dictionary.dicReader);
    }

    outAff.removeUnusedAffixes(outDic);
    outAff.saveToFile(FileUtil.outputFolder + editOutputName.getText() + ".aff", AffixFlag.NUMBER);
    outDic.saveToFile(FileUtil.outputFolder + editOutputName.getText() + ".dic", AffixFlag.NUMBER);

    XPI.createXPI(FileUtil.outputFolder, editOutputName.getText(), "");
  }

  private void updateSelectedDictionaries() {
    selectedDictionaries.clear();
    for (int i = 0; i < dictionaries.size(); i++) {
      if (table.isRowSelected(i))
        selectedDictionaries.add(dictionaries.elementAt(i));
    }
  }

  private void updateNewName() {

    String result = "";
    for (int i = 0; i < selectedDictionaries.size(); i++) {
      result += (selectedDictionaries.elementAt(i).getNameNoExt()) + ((i < selectedDictionaries.size() - 1) ? "_" : "");
    }

    editOutputName.setText(result);
  }

  @Override
  public void start() {
    super.start();
  }

  // Called when application starts
  public static void main(String[] args) {
    new HunspellMerge().start();
  }
}
