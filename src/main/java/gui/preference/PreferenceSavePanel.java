package gui.preference;

import model.dto.PreferencesDirectoryCategoryDTO;
import model.dto.PreferencesSaveDTO;
import utils.Utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Saeed on 9/13/2015.
 */
class PreferenceSavePanel extends PreferenceJPanel {

    private JPanel saveToPanel;
    private JPanel tempDirPanel;
    private JLabel categoryLabel;
    private JComboBox<PreferencesDirectoryCategoryDTO> categoryComboBox;
    private JLabel fileExtensionLabel;
    private JTextField fileExtensionTextField;
    private JLabel pathLabel;
    private JTextField pathTextField;
    private JLabel temporaryDirectoryLabel;
    private JTextField temporaryDirectoryTextField;
    private JLabel databasePathLabel;
    private JTextField databasePathTextField;
    private JLabel logPathLabel;
    private JTextField logPathTextField;

    private List<PreferencesDirectoryCategoryDTO> preferencesDirectoryCategoryDTOs;

    private PreferencesSaveDTO preferencesSaveDTO;

    public PreferenceSavePanel(final PreferencesSaveDTO preferencesSaveDTO) {
        super("Save To", "preferenceSavePanel.iconPath");

        this.preferencesSaveDTO = preferencesSaveDTO;

        saveToPanel = new JPanel();
        categoryLabel = new JLabel("Category");
        categoryComboBox = new JComboBox<>();
        fileExtensionLabel = new JLabel("File Extensions:");
        fileExtensionTextField = new JTextField(45);
        pathLabel = new JLabel("Path:");
        pathTextField = new JTextField(45);
        temporaryDirectoryLabel = new JLabel("Temporary Directory:");
        temporaryDirectoryTextField = new JTextField(45);
        databasePathLabel = new JLabel("Database Directory:");
        databasePathTextField = new JTextField(45);
        logPathLabel = new JLabel("log Directory:");
        logPathTextField = new JTextField(45);
        tempDirPanel = new JPanel();

        layoutComponentsOfSaveToPanel();
        layoutComponentsOfTempDirPanel();

        setPreferenceSaveDTO(preferencesSaveDTO);

        categoryComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO = (PreferencesDirectoryCategoryDTO) e.getItem();
                fileExtensionTextField.setText(concatArray(preferencesDirectoryCategoryDTO.getFileExtensions()));
                pathTextField.setText(preferencesDirectoryCategoryDTO.getPath());
            }
        });

        // Listen for changes in the text
        fileExtensionTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                ((PreferencesDirectoryCategoryDTO) categoryComboBox.getSelectedItem()).setFileExtensions(fileExtensionTextField.getText().split(" "));
            }
        });

        pathTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                ((PreferencesDirectoryCategoryDTO) categoryComboBox.getSelectedItem()).setPath(pathTextField.getText());
            }
        });

        temporaryDirectoryTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                preferencesSaveDTO.setTempDirectory(temporaryDirectoryTextField.getText());
            }
        });

        databasePathTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                preferencesSaveDTO.setDatabasePath(databasePathTextField.getText());
            }
        });

        logPathTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                preferencesSaveDTO.setLogPath(logPathTextField.getText());
            }
        });
    }

    private String concatArray(String[] strings) {
        StringBuilder result = new StringBuilder();
        for (String str : strings) {
            result.append(str).append(" ");
        }
        return result.toString();
    }

    private void layoutComponentsOfSaveToPanel() {
        saveToPanel.setLayout(new GridBagLayout());
        saveToPanel.setBorder(BorderFactory.createTitledBorder("Save To"));
        saveToPanel.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();

        Insets rightPadding = new Insets(0, 0, 0, 15);
        Insets noPadding = new Insets(0, 0, 0, 0);
        ///////////////// First row ////////////////////////////////////////////
        gc.weightx = 1;
        gc.weighty = 1;

        gc.gridy = 0;
        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START; // LINE_END
        gc.insets = rightPadding;
        saveToPanel.add(categoryLabel, gc);

        ////////////Next row ////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        saveToPanel.add(categoryComboBox, gc);

        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;
        gc.gridx = 0;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_START; // FIRST_LINE_END
        saveToPanel.add(fileExtensionLabel, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.LINE_START; // FIRST_LINE_START
        gc.insets = noPadding;
        saveToPanel.add(fileExtensionTextField, gc);

        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        saveToPanel.add(pathLabel, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = noPadding;
        saveToPanel.add(pathTextField, gc);
        saveToPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(saveToPanel);
    }

    private void layoutComponentsOfTempDirPanel() {

        tempDirPanel.setBackground(Color.WHITE);

        tempDirPanel.setBorder(new TitledBorder("Temporary Directories"));

        tempDirPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridy = 0;

        Insets rightPadding = new Insets(0, 0, 0, 15);
        Insets noPadding = new Insets(0, 0, 0, 0);

        ///////////////// First row ////////////////////////////////////////////
        gc.weightx = 1;
        gc.weighty = 1;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = rightPadding;
        tempDirPanel.add(temporaryDirectoryLabel, gc);

        gc.gridx = 0;
        gc.gridy++;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        tempDirPanel.add(temporaryDirectoryTextField, gc);

        add(tempDirPanel, BorderLayout.SOUTH);

        ///////////////// Next row ////////////////////////////////////////////

        gc.gridy++;
        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = rightPadding;
        tempDirPanel.add(databasePathLabel, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        tempDirPanel.add(databasePathTextField, gc);

        tempDirPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(tempDirPanel);

        ///////////////// Next row ////////////////////////////////////////////

        gc.gridy++;
        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.insets = rightPadding;
        tempDirPanel.add(logPathLabel, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        tempDirPanel.add(logPathTextField, gc);

        tempDirPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(tempDirPanel);
    }

    public void setPreferenceSaveDTO(PreferencesSaveDTO preferenceSaveDTO) {
        preferencesDirectoryCategoryDTOs = preferenceSaveDTO.getPreferencesDirectoryCategoryDTOs();
        // Set up combo box.
        DefaultComboBoxModel<PreferencesDirectoryCategoryDTO> directoryCategoryModel = new DefaultComboBoxModel<>();
        for(PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO : preferencesDirectoryCategoryDTOs) {
            directoryCategoryModel.addElement(preferencesDirectoryCategoryDTO);
        }
        categoryComboBox.setModel(directoryCategoryModel);
        categoryComboBox.setSelectedIndex(0);
        PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO = ((PreferencesDirectoryCategoryDTO) categoryComboBox.getSelectedItem());
        fileExtensionTextField.setText(concatArray(preferencesDirectoryCategoryDTO.getFileExtensions()));
        pathTextField.setText(preferencesDirectoryCategoryDTO.getPath());

        temporaryDirectoryTextField.setText(preferenceSaveDTO.getTempDirectory());
        databasePathTextField.setText(preferenceSaveDTO.getDatabasePath());
        logPathTextField.setText(preferenceSaveDTO.getLogPath());
    }

    public PreferencesSaveDTO getPreferenceSaveDTO() {
        preferencesSaveDTO.setPreferencesDirectoryCategoryDTOs(preferencesDirectoryCategoryDTOs);
        preferencesSaveDTO.setTempDirectory(temporaryDirectoryTextField.getText());
        preferencesSaveDTO.setDatabasePath(databasePathTextField.getText());
        preferencesSaveDTO.setLogPath(logPathTextField.getText());
        return preferencesSaveDTO;
    }

}
