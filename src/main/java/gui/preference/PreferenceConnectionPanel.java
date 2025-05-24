package gui.preference;

import enums.ConnectionType;
import model.dto.PreferencesConnectionDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Saeed on 9/23/2015.
 */
class PreferenceConnectionPanel extends PreferenceJPanel {

    private JPanel connectionTypePanel;
    private JPanel connectionPanel;
    private JPanel errorsPanel;
    private JLabel connectionTypeLabel;
    private JComboBox<String> connectionTypeComboBox;
    private JLabel maxConnectionNumberLabel;
    private JComboBox<Integer> maxConnectionNumberComboBox;

    private JLabel timeBetweenAttemptsLabel;
    private JSpinner timeBetweenAttemptsSpinner;
    private SpinnerNumberModel timeBetweenAttemptsSpinnerModel;
    private JLabel maxNumberAttemptsLabel;
    private JSpinner maxNumberAttemptsSpinner;
    private SpinnerNumberModel maxNumberAttemptsSpinnerModel;
    private JLabel connectionTimeOutLabel;
    private JSpinner connectionTimeOutSpinner;
    private SpinnerNumberModel connectionTimeOutSpinnerModel;
    private JLabel readTimeOutLabel;
    private JSpinner readTimeOutSpinner;
    private SpinnerNumberModel readTimeOutSpinnerModel;

    private PreferencesConnectionDTO preferencesConnectionDTO;

    public PreferenceConnectionPanel(PreferencesConnectionDTO preferencesConnectionDTO) {
        super("Connection", "preferenceConnectionPanel.iconPath");

        this.preferencesConnectionDTO = preferencesConnectionDTO;

        connectionTypePanel = new JPanel();
        connectionPanel = new JPanel();
        errorsPanel = new JPanel();
        connectionTypeLabel = new JLabel("Connection Type:");
        connectionTypeComboBox = new JComboBox<>(new String[] {ConnectionType.NotSet.getDesc(), ConnectionType.LOW.getDesc(),
                ConnectionType.MEDIUM.getDesc(), ConnectionType.HIGH.getDesc()});
        connectionTypeComboBox.setEditable(false);
        connectionTypeComboBox.setEnabled(false);  // todo for next release
        maxConnectionNumberLabel = new JLabel("Max Connections Number:");
        maxConnectionNumberComboBox = new JComboBox<>(new Integer[] {1, 2, 4, 8, 16, 24, 32});
        maxConnectionNumberComboBox.setEditable(false);

        maxConnectionNumberLabel.setDisplayedMnemonic(KeyEvent.VK_N);
        maxConnectionNumberLabel.setLabelFor(maxConnectionNumberComboBox);

        timeBetweenAttemptsLabel = new JLabel("Pause Time Between attempts(in sec):");
        timeBetweenAttemptsSpinnerModel = new SpinnerNumberModel(5, 1, 9999, 1);
        timeBetweenAttemptsSpinner = new JSpinner(timeBetweenAttemptsSpinnerModel);
        maxNumberAttemptsLabel = new JLabel("Maximum number of attempts:");
        maxNumberAttemptsSpinnerModel = new SpinnerNumberModel(20, 1, 9999, 1);
        maxNumberAttemptsSpinner = new JSpinner(maxNumberAttemptsSpinnerModel);
        connectionTimeOutLabel = new JLabel("Connection Timeout(in sec):");
        connectionTimeOutSpinnerModel = new SpinnerNumberModel(20, 1, 9999, 1);
        connectionTimeOutSpinner = new JSpinner(connectionTimeOutSpinnerModel);
        readTimeOutLabel = new JLabel("Read Timeout(in sec):");
        readTimeOutSpinnerModel = new SpinnerNumberModel(30, 1, 9999, 1);
        readTimeOutSpinner = new JSpinner(readTimeOutSpinnerModel);

        timeBetweenAttemptsSpinner.setEnabled(false);
        maxNumberAttemptsSpinner.setEnabled(false);

        layoutComponentsOfConnectionPanel();
        layoutComponentsOfErrorsPanel();

        setPreferencesConnectionDTO(preferencesConnectionDTO);
    }

    private void layoutComponentsOfConnectionPanel() {
        connectionPanel.setLayout(new GridBagLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connections"));
        connectionPanel.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();


        ///////////////// First row ////////////////////////////////////////////
        gc.gridy = 0;

        gc.weightx = 1;
        gc.weighty = 0.1;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        connectionPanel.add(connectionTypeLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        connectionPanel.add(connectionTypeComboBox, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        connectionPanel.add(maxConnectionNumberLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        connectionPanel.add(maxConnectionNumberComboBox, gc);

        connectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(connectionPanel);
    }

    private void layoutComponentsOfErrorsPanel() {
        errorsPanel.setLayout(new GridBagLayout());
        errorsPanel.setBorder(BorderFactory.createTitledBorder("Errors"));
        errorsPanel.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();

        ///////////////// First row ////////////////////////////////////////////
        gc.gridy = 0;

        gc.weightx = 1;
        gc.weighty = 0.1;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        errorsPanel.add(timeBetweenAttemptsLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        errorsPanel.add(timeBetweenAttemptsSpinner, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        errorsPanel.add(maxNumberAttemptsLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        errorsPanel.add(maxNumberAttemptsSpinner, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        errorsPanel.add(connectionTimeOutLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        errorsPanel.add(connectionTimeOutSpinner, gc);

        errorsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        errorsPanel.add(readTimeOutLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        errorsPanel.add(readTimeOutSpinner, gc);

        errorsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(errorsPanel);
    }

    public PreferencesConnectionDTO getPreferencesConnectionDTO() {
        preferencesConnectionDTO.setConnectionType(ConnectionType.valueOfByDesc((String) connectionTypeComboBox.getSelectedItem()));
        preferencesConnectionDTO.setMaxConnectionNumber((int) maxConnectionNumberComboBox.getSelectedItem());
        preferencesConnectionDTO.setTimeBetweenAttempts((int) timeBetweenAttemptsSpinner.getValue());
        preferencesConnectionDTO.setMaxNumberAttempts((int) maxNumberAttemptsSpinner.getValue());
        preferencesConnectionDTO.setConnectionTimeOut((int) connectionTimeOutSpinner.getValue());
        preferencesConnectionDTO.setReadTimeOut((int) readTimeOutSpinner.getValue());
        return preferencesConnectionDTO;
    }

    public void setPreferencesConnectionDTO(PreferencesConnectionDTO preferencesConnectionDTO) {
        connectionTypeComboBox.setSelectedItem(preferencesConnectionDTO.getConnectionType().getDesc());
        maxConnectionNumberComboBox.setSelectedItem(preferencesConnectionDTO.getMaxConnectionNumber());
        timeBetweenAttemptsSpinner.setValue(preferencesConnectionDTO.getTimeBetweenAttempts());
        maxNumberAttemptsSpinner.setValue(preferencesConnectionDTO.getMaxNumberAttempts());
        connectionTimeOutSpinner.setValue(preferencesConnectionDTO.getConnectionTimeOut());
        readTimeOutSpinner.setValue(preferencesConnectionDTO.getReadTimeOut());
    }
}
