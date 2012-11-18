// $Id$

package hunspell.merge;

import hunspell.merge.xpi.XPI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class HunspellMerge extends JApplet {

  public static HunspellMerge instance;
  public JFrame mainFrame;
  private final Vector<DictionaryFile> dictionaries = new Vector<DictionaryFile>();
  private DictionaryTableModel tableModel = new DictionaryTableModel(dictionaries);
  private JTable table = new JTable(tableModel);
  private JScrollPane scrollPane = new JScrollPane(table);
  private JRadioButton buttonOutPlain = new JRadioButton("Dictionary (*.dic, *.aff)");
  private JRadioButton buttonOutZIP = new JRadioButton("ZIP Archive (*.zip)");
  private JRadioButton buttonOutXPI = new JRadioButton("XPInstall (*.xpi)");

  private JTextField editOutputName = new JTextField();
  private JTextField editOutputDescription = new JTextField();
  private JTextField editDicFolder = new JTextField();
  private JButton buttonDownload = new JButton("Browse and download more dictionaries");
  private JButton buttonCompile = new JButton("Merge dictionaries");
  private JButton buttonDicFolder = new JButton("...");
  private JButton buttonOutDicFolder = new JButton("...");
  private Vector<DictionaryFile> selectedDictionaries = new Vector<DictionaryFile>();
  private JTextField editOutFolder = new JTextField();
  private JPanel panelSettings = new JPanel();
  private JPanel panelLog = new JPanel();
  private JTabbedPane tabbedPane = new JTabbedPane();
  private JTextPane textAreaLog = new JTextPane();
  private JScrollPane scrollLog = new JScrollPane(textAreaLog);
  private JButton buttonClearLog = new JButton("Clear log");
  private DateFormat dateFormat = new SimpleDateFormat("mm:ss");

  public HunspellMerge() {
    instance = this;

    String version = HunspellMerge.class.getPackage().getSpecificationVersion();

    mainFrame = new JFrame();

    mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    mainFrame.setTitle("Hunspell dictionary merge" + (version != null ? " v." + version : ""));
    mainFrame.setSize(500, 500);
    mainFrame.setLocationRelativeTo(null);

    createGUI();

    mainFrame.setVisible(true);
    loadDictionaries();
  }

  private void loadDictionaries() {
    FileUtil.createTempFolder();
    dictionaries.clear();

    File[] files = FileUtil.findFiles(FileUtil.dictionaryFolder, DictionaryType.getAllExts());
    if (files != null) {
      for (File file : files) {
        DictionaryFile dictionary = new DictionaryFile(file);
        if (dictionary.isValid()) {
          dictionaries.add(dictionary);
        }
      }
    }
    tableModel.fireTableDataChanged();
  }

  private void createGUI() {
    mainFrame.setLayout(new BorderLayout());
    mainFrame.add(tabbedPane, BorderLayout.CENTER);

    tabbedPane.addTab("Settings", panelSettings);
    tabbedPane.addTab("Work log", panelLog);

    createSettingsGUI();
    createLogGUI();
  }

  private void createLogGUI() {
    textAreaLog.setContentType("text/html");
    textAreaLog.setBackground(Color.white);
    textAreaLog.setEditable(false);

    panelLog.setLayout(new BorderLayout());
    panelLog.add(scrollLog, BorderLayout.CENTER);

    panelLog.add(buttonClearLog, BorderLayout.SOUTH);
    buttonClearLog.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ñlearLog();
      }
    });
  }

  private void ñlearLog() {
    textAreaLog.setText("");
  }

  private void createSettingsGUI() {
    panelSettings.setLayout(new GridBagLayout());
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
    editDicFolder.setEditable(false);
    panelDicFolder.add(editDicFolder, BorderLayout.CENTER);
    buttonDicFolder.setToolTipText("Select dictionary source folder");
    buttonDicFolder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(FileUtil.dictionaryFolder);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(panelSettings) == JFileChooser.APPROVE_OPTION) {
          FileUtil.dictionaryFolder = FileUtil.makePath(fileChooser.getSelectedFile().getPath());
          editDicFolder.setText(FileUtil.dictionaryFolder);
          loadDictionaries();
        }
      }
    }
    );
    panelDicFolder.add(buttonDicFolder, BorderLayout.EAST);
    panelSettings.add(panelDicFolder, c);

    c.weighty = 1.0;

    // Dictionary list
    scrollPane.setBorder(BorderFactory.createTitledBorder("Available dictionaries (Select one or more)"));
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updateSelectedDictionaries();
        updateNewName();
      }
    });
    panelSettings.add(scrollPane, c);

    c.weighty = 0.0;

    buttonDownload.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(new URI("https://code.google.com/p/hunspell-merge/wiki/OnlineDictionaries"));
          } catch (Exception e1) {
            Util.showError(e1);
          }
        }
      }
    });

    c.weightx = 0;
    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.NONE;
    panelSettings.add(buttonDownload, c);

    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.WEST;

    // Output folder
    JPanel panelOutput = new JPanel(new GridBagLayout());
    panelOutput.setBorder(BorderFactory.createTitledBorder("Dictionary output parameters"));
    panelSettings.add(panelOutput, c);

    // Button
    JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    panelButton.add(buttonCompile);
    panelSettings.add(panelButton, c);

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
    editOutFolder.setEditable(false);
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
        if (fileChooser.showOpenDialog(panelSettings) == JFileChooser.APPROVE_OPTION) {
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
    editOutputName.getDocument().addDocumentListener(new DocumentListener() {

      private void updateDescription() {
        editOutputDescription.setText("Merged " + editOutputName.getText() + " dictionary");
      }

      public void insertUpdate(DocumentEvent e) {
        updateDescription();
      }

      public void removeUpdate(DocumentEvent e) {
        updateDescription();
      }

      public void changedUpdate(DocumentEvent e) {
        updateDescription();
      }
    });
    panelOutput.add(editOutputName, c);

    c.gridy = 2;
    c.gridx = 0;
    c.weightx = 0.0;
    c.gridwidth = 1;
    panelOutput.add(new JLabel("XPInstall Description"), c);

    c.gridwidth = 2;
    c.gridx++;
    panelOutput.add(editOutputDescription, c);

    ButtonGroup group = new ButtonGroup();
    buttonOutPlain.setSelected(true);
    group.add(buttonOutPlain);
    group.add(buttonOutZIP);
    group.add(buttonOutXPI);

    JPanel panelOutputType = new JPanel(new GridLayout(1, 2));
    panelOutputType.add(buttonOutPlain);
    panelOutputType.add(buttonOutZIP);
    panelOutputType.add(buttonOutXPI);

    c.gridy = 3;
    c.gridwidth = 3;
    c.gridx = 0;
    panelOutput.add(panelOutputType, c);
  }

  private void log(final String text) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          textAreaLog.getDocument().insertString(textAreaLog.getDocument().getLength(), text + "\n",
              new SimpleAttributeSet());
          JScrollBar vertical = scrollLog.getVerticalScrollBar();
          vertical.setValue(vertical.getMaximum());
        } catch (BadLocationException ignored) {

        }
      }
    });
  }

  private void createDictionaries() {
    if (selectedDictionaries.size() == 0) {
      return;
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        tabbedPane.setSelectedComponent(panelLog);
        buttonCompile.setEnabled(false);
        tabbedPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Thread thread = new Thread(new Runnable() {
          public void run() {
            createDictionariesImpl();
            System.gc();
          }
        });
        thread.start();
      }
    });
  }

  private void createDictionariesImpl() {
    Date startTime = new Date();

    AffReader outAff = new AffReader();
    DicReader outDic = new DicReader();
    for (DictionaryFile dictionary : selectedDictionaries) {
      log("Loading: " + dictionary.getName() + " / " + dictionary.getSummary() + " ...");
      dictionary.readFiles();
      if (dictionary.affReader.getAliasCount() > 0) {
        log("Merging " + dictionary.affReader.getAliasCount() + " aliases ...");
      }
      log("Merging " + dictionary.affReader.getAffCount() + " affixes" +
          (dictionary.affReader.getAliasCount() > 0 ? " (" + dictionary.affReader.getAliasCount() + " aliases)" : "")
          + " ...");
      outAff.appendAff(dictionary.affReader);
      log("Merging " + dictionary.dicReader.getWordCount() + " words ...");
      outDic.appendDic(dictionary.dicReader);
      dictionary.clear();
      log("Done!\n");
    }

    log("Checking unused affixes...");
    outAff.removeUnusedAffixes(outDic);

    FileUtil.createOutputFolder();

    String tmpFolder = buttonOutPlain.isSelected() ? FileUtil.outputFolder :
        FileUtil.makePath(FileUtil.tempFolder, "output");

    if (!buttonOutPlain.isSelected()) {
      FileUtil.createFolder(tmpFolder);
      FileUtil.deleteFolder(tmpFolder, false);
    }

    String outFileName = FileUtil.validateFileName(editOutputName.getText().toLowerCase());

    try {

      log("Saving affix file: " + outFileName + ".aff");
      outAff.saveToFile(tmpFolder + outFileName + ".aff", AffixFlag.NUMBER);
      log("Saving dictionary file: " + outFileName + ".aff");
      outDic.saveToFile(tmpFolder + outFileName + ".dic", AffixFlag.NUMBER);

      if (buttonOutXPI.isSelected()) {
        String xpiName = outFileName.replace("_", "-");
        log("Create XPI: " + xpiName + ".xpi");
        XPI.createXPI(tmpFolder, xpiName, outFileName, editOutputDescription.getText());
      }

      if (buttonOutZIP.isSelected()) {
        String zipName = outFileName.replace("_", "-") + ".zip";
        log("Create ZIP: " + zipName);
        ZipUtil.zipFolder(FileUtil.outputFolder + zipName, tmpFolder);
      }

      buttonCompile.setEnabled(true);
      tabbedPane.setCursor(Cursor.getDefaultCursor());

      log("");
      log("Result " + outAff.getAffCount() + " affixes, " + outDic.getWordCount() + " words");
      log("Work time (min:sec) = " + dateFormat.format(new Date(new Date().getTime() - startTime.getTime())));
      log("Output folder: " + FileUtil.outputFolder);
      log("");

      outAff.clear();
      outDic.clear();

      Object[] options = {"OK", "Open output folder"};
      if (JOptionPane.showOptionDialog(null,
          "Dictionaries were successfully merged!", "Information",
          JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]) == 1) {
        Desktop.getDesktop().open(new File(FileUtil.outputFolder));
      }
    } catch (Exception e) {
      Util.showError(e);
    }
  }

  private void updateSelectedDictionaries() {
    selectedDictionaries.clear();
    for (int i = 0; i < dictionaries.size(); i++) {
      if (table.isRowSelected(i)) {
        selectedDictionaries.add(dictionaries.elementAt(i));
      }
    }
  }

  private void updateNewName() {

    String result = "";
    for (DictionaryFile dictionary : selectedDictionaries) {
      result += (result.equals("") ? "" : "_") + (dictionary.getLaguageID());
    }

    // Remove language duplicates
    String[] tokens = result.replace("-", "_").split("_");
    result = "";
    for (String token : tokens) {
      if (!result.contains(token)) {
        result += (result.equals("") ? "" : "_") + token;
      }
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
