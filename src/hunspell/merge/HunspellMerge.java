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

  private JRadioButton buttonOutPlain = new JRadioButton("Uncompressed (*.dic, *.aff)");
  private JRadioButton buttonOutXPI = new JRadioButton("XPI FireFox extension (*.xpi)");

  private JTextField editOutputName = new JTextField();
  private JTextField editOutputDescription = new JTextField();
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
    FileUtil.createTempFolder();
    loadDictionaries();
  }

  private void loadDictionaries() {
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
          FileUtil.dictionaryFolder = FileUtil.makePath(fileChooser.getSelectedFile().getPath());
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
    JPanel panelOutput = new JPanel(new GridBagLayout());
    panelOutput.setBorder(BorderFactory.createTitledBorder("Dictionary output parameters"));
    mainFrame.add(panelOutput, c);

    // Button
    JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    panelButton.add(buttonCompile);
    mainFrame.add(panelButton, c);

    // Action button
    buttonCompile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createDictionaries();
      }
    }
    );

    // Output panel
    c = new GridBagConstraints();
    c.weightx = 0.0;
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = 1;
    c.gridx = 0;
    c.insets = new Insets(2, 2, 2, 2);

    panelOutput.add(new JLabel("Folder"), c);

    editOutFolder.setText(FileUtil.outputFolder);
    c.weightx = 1.0;
    c.gridx++;
    panelOutput.add(editOutFolder, c);

    c.weightx = 0.0;
    c.gridx++;
    c.gridwidth = GridBagConstraints.REMAINDER;

    buttonOutDicFolder.setToolTipText("Select dictionary output folder");
    buttonOutDicFolder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(FileUtil.outputFolder);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
          FileUtil.outputFolder = FileUtil.makePath(fileChooser.getSelectedFile().getPath());
          editOutFolder.setText(FileUtil.outputFolder);
        }
      }
    }
    );
    panelOutput.add(buttonOutDicFolder, c);

    c.gridy = 1;
    c.gridx = 0;
    c.weightx = 0.0;
    c.gridwidth = 1;
    panelOutput.add(new JLabel("Language ID"), c);

    c.gridwidth = 2;
    c.gridx++;
    editOutputName.setToolTipText("<html>Examples:<br>en_US_ru_RU<br>en_ru<br>en_ru_de</html>");
    panelOutput.add(editOutputName, c);

    c.gridy = 2;
    c.gridx = 0;
    c.weightx = 0.0;
    c.gridwidth = 1;
    panelOutput.add(new JLabel("Description (for FireFox XPI)"), c);

    editOutputDescription.setText("Merged dictionary");
    c.gridwidth = 2;
    c.gridx++;
    panelOutput.add(editOutputDescription, c);

    ButtonGroup group = new ButtonGroup();
    buttonOutPlain.setSelected(true);
    group.add(buttonOutPlain);
    group.add(buttonOutXPI);

    JPanel panelOutputType = new JPanel(new GridLayout(1, 2));
    panelOutputType.add(buttonOutPlain);
    panelOutputType.add(buttonOutXPI);

    c.gridy = 3;
    c.gridwidth = 3;
    c.gridx = 0;
    panelOutput.add(panelOutputType, c);
  }

  private void createDictionaries() {
    if (selectedDictionaries.size() == 0)
      return;

    FileUtil.createTempFolder();

    AffReader outAff = new AffReader();
    DicReader outDic = new DicReader();
    for (DictionaryFile dictionary : selectedDictionaries) {
      dictionary.readFiles();
      outAff.appendAff(dictionary.affReader);
      outDic.appendDic(dictionary.dicReader);
    }
    outAff.removeUnusedAffixes(outDic);

    FileUtil.createTempFolder();
    FileUtil.createOutputFolder();

    String outFolder = !buttonOutXPI.isSelected() ? FileUtil.outputFolder :
        FileUtil.makePath(FileUtil.tempFolder, "dictionaries");
    String outFileName = FileUtil.validateFileName(editOutputName.getText().toLowerCase());

    try {
      outAff.saveToFile(outFolder + outFileName + ".aff", AffixFlag.NUMBER);
      outDic.saveToFile(outFolder + outFileName + ".dic", AffixFlag.NUMBER);

      if (buttonOutXPI.isSelected())
        XPI.createXPI(outFileName.replace("_", "-"), editOutputDescription.getText());

      JOptionPane.showMessageDialog(this,
          "Dictionary was successfully created.\n\nOutput folder:\n" + FileUtil.outputFolder);
    } catch (Exception e) {
      Util.showError(e);
    }

    FileUtil.deleteFolder(FileUtil.tempFolder, true);
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
    for (DictionaryFile dictionary : selectedDictionaries) {
      result += (result.equals("") ? "" : "_") + (dictionary.getNameNoExt());
    }

    // Remove language duplicates
    String[] tokens = result.replace("-", "_").split("_");
    result = "";
    for (String token : tokens) {
      if (!result.contains(token))
        result += (result.equals("") ? "" : "_") + token;
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
