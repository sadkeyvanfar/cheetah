package gui.preference;

import model.dto.PreferencesProxyDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Saeed on 9/23/2015.
 */
class PreferenceProxyPanel extends PreferenceJPanel implements ActionListener {

    private JPanel proxySocksPanel;

    private JLabel noProxyLabel;
    private JRadioButton noProxyRadioButton;
    private JLabel systemProxyLabel;
    private JRadioButton systemProxyRadioButton;
    private JLabel manualProxyLabel;
    private JRadioButton manualProxyRadioButton;
    private ButtonGroup proxySettingGroup;

    private JLabel useProxyLabel;
    private JRadioButton useProxyRadioButton;
    private JLabel useSocksLabel;
    private JRadioButton useSocksRadioButton;

    private ButtonGroup proxySocksGroup;

    private JPanel manualProxyPanel;

    private JLabel proxyAddressLabel;
    private JLabel proxyPortLabel;
    private JLabel proxyUserNameLabel;
    private JLabel proxyPasswordLabel;

    // for socks
    private JLabel socksProxyAddressLabel;
    private JLabel socksProxyPortLabel;
    private JLabel socksProxyUserNameLabel;
    private JLabel socksProxyPasswordLabel;

    private JLabel httpLabel;
    private JLabel httpsLabel;
    private JLabel ftpLabel;
    private JLabel socksLabel;

    // http
    private JTextField httpProxyAddressTextField;
    private JSpinner httpProxyPortSpinner;
    private SpinnerNumberModel httpProxyPortSpinnerModel;
    private JTextField httpProxyUserNameTextField;
    private JPasswordField httpProxyPasswordField;

    // https
    private JTextField httpsProxyAddressTextField;
    private JSpinner httpsProxyPortSpinner;
    private SpinnerNumberModel httpsProxyPortSpinnerModel;
    private JTextField httpsProxyUserNameTextField;
    private JPasswordField httpsProxyPasswordField;

    // ftp
    private JTextField ftpProxyAddressTextField;
    private JSpinner ftpProxyPortSpinner;
    private SpinnerNumberModel ftpProxyPortSpinnerModel;
    private JTextField ftpProxyUserNameTextField;
    private JPasswordField ftpProxyPasswordField;

    // socks
    private JTextField socksProxyAddressTextField;
    private JSpinner socksProxyPortSpinner;
    private SpinnerNumberModel socksProxyPortSpinnerModel;
    private JTextField socksProxyUserNameTextField;
    private JPasswordField socksProxyPasswordField;

    private PreferencesProxyDTO preferencesProxyDTO;

    public PreferenceProxyPanel(PreferencesProxyDTO preferencesProxyDTO) {
        super("Proxy", "preferenceProxyPanel.iconPath");

        this.preferencesProxyDTO = preferencesProxyDTO;

        proxySocksPanel = new JPanel();

        noProxyLabel = new JLabel("No proxy");
        noProxyRadioButton = new JRadioButton();
        noProxyRadioButton.setActionCommand("NoProxy");
        systemProxyLabel = new JLabel("Use system proxy settings");
        systemProxyRadioButton = new JRadioButton();
        systemProxyRadioButton.setActionCommand("systemProxy");
        manualProxyLabel = new JLabel("Manual proxy configuration");
        manualProxyRadioButton = new JRadioButton();
        manualProxyRadioButton.setActionCommand("manualProxy");
        proxySettingGroup = new ButtonGroup();
        proxySettingGroup.add(noProxyRadioButton);
        proxySettingGroup.add(systemProxyRadioButton);
        proxySettingGroup.add(manualProxyRadioButton);
     //   noProxyRadioButton.setEnabled(false);  // todo for next release
      //  systemProxyRadioButton.setEnabled(false);  // todo for next release
     //   manualProxyRadioButton.setEnabled(false);  // todo for next release

        manualProxyPanel = new JPanel();

        useProxyLabel = new JLabel("Use Proxy");
        useProxyRadioButton = new JRadioButton();
        useSocksLabel =new JLabel("Use Socks");
        useSocksRadioButton = new JRadioButton();
        proxySocksGroup = new ButtonGroup();
        proxySocksGroup.add(useProxyRadioButton);
        proxySocksGroup.add(useSocksRadioButton);

        proxyAddressLabel = new JLabel("URL address");
        proxyPortLabel = new JLabel("Post");
        proxyUserNameLabel = new JLabel("User name");
        proxyPasswordLabel = new JLabel("Password");

        socksProxyAddressLabel = new JLabel("URL address");
        socksProxyPortLabel = new JLabel("Post");
        socksProxyUserNameLabel = new JLabel("User name");
        socksProxyPasswordLabel = new JLabel("Password");

        httpLabel = new JLabel("HTTP");
        httpsLabel = new JLabel("HTTPS");
        ftpLabel = new JLabel("FTP");
        socksLabel = new JLabel("SOCKS");

        // http
        httpProxyAddressTextField = new JTextField(20);
        httpProxyPortSpinnerModel = new SpinnerNumberModel(80, 1, 9999, 1);
        httpProxyPortSpinner = new JSpinner(httpProxyPortSpinnerModel);
        httpProxyUserNameTextField = new JTextField(10);
        httpProxyPasswordField = new JPasswordField(10);

        // https
        httpsProxyAddressTextField = new JTextField(20);
        httpsProxyPortSpinnerModel = new SpinnerNumberModel(8080, 1, 9999, 1);
        httpsProxyPortSpinner = new JSpinner(httpsProxyPortSpinnerModel);
        httpsProxyUserNameTextField = new JTextField(10);
        httpsProxyPasswordField = new JPasswordField(10);

        // ftp
        ftpProxyAddressTextField = new JTextField(20);
        ftpProxyPortSpinnerModel = new SpinnerNumberModel(21, 1, 9999, 1);
        ftpProxyPortSpinner = new JSpinner(ftpProxyPortSpinnerModel);
        ftpProxyUserNameTextField = new JTextField(10);
        ftpProxyPasswordField = new JPasswordField(10);


        proxySocksGroup = new ButtonGroup();

        // socks
        socksProxyAddressTextField = new JTextField(20);
        socksProxyPortSpinnerModel = new SpinnerNumberModel(13, 1, 9999, 1);
        socksProxyPortSpinner = new JSpinner(socksProxyPortSpinnerModel);
        socksProxyUserNameTextField = new JTextField(10);
        socksProxyPasswordField = new JPasswordField(10);

        layoutComponentsOfProxySocksPanel();
        layoutComponentsOfManualProxyPanel();

        noProxyRadioButton.addActionListener(this);
        systemProxyRadioButton.addActionListener(this);
        manualProxyRadioButton.addActionListener(this);

        useProxyRadioButton.addActionListener(this);
        useSocksRadioButton.addActionListener(this);

        setPreferencesProxyDTO(preferencesProxyDTO);

        setEnableStatusOfComponents();
    }

    private void layoutComponentsOfProxySocksPanel() {
        proxySocksPanel.setLayout(new GridBagLayout());
        proxySocksPanel.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();


        ///////////////// First row ////////////////////////////////////////////
        gc.gridy = 0;

        gc.weightx = 1;
        gc.weighty = 0.1;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        proxySocksPanel.add(noProxyLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        proxySocksPanel.add(noProxyRadioButton, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        proxySocksPanel.add(systemProxyLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        proxySocksPanel.add(systemProxyRadioButton, gc);

        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        proxySocksPanel.add(manualProxyLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        proxySocksPanel.add(manualProxyRadioButton, gc);

        proxySocksPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(proxySocksPanel);
    }
    private void layoutComponentsOfManualProxyPanel() {
        manualProxyPanel.setLayout(new GridBagLayout());
        manualProxyPanel.setBorder(BorderFactory.createTitledBorder("Manual proxy configuration"));
        manualProxyPanel.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();

        ///////////////// First row ////////////////////////////////////////////
        gc.gridy = 0;

        gc.weightx = 1;
        gc.weighty = 0.1;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        manualProxyPanel.add(useProxyLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        manualProxyPanel.add(useProxyRadioButton, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        manualProxyPanel.add(proxyAddressLabel, gc);
        gc.gridx = 2;
        manualProxyPanel.add(proxyPortLabel, gc);
        gc.gridx = 3;
        manualProxyPanel.add(proxyUserNameLabel, gc);
        gc.gridx = 4;
        manualProxyPanel.add(proxyPasswordLabel, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        manualProxyPanel.add(httpLabel, gc);
        gc.gridx = 1;
        gc.fill = GridBagConstraints.NONE;
        manualProxyPanel.add(httpProxyAddressTextField, gc);
        gc.gridx = 2;
        manualProxyPanel.add(httpProxyPortSpinner, gc);
        gc.gridx = 3;
        manualProxyPanel.add(httpProxyUserNameTextField, gc);
        gc.gridx = 4;
        manualProxyPanel.add(httpProxyPasswordField, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        manualProxyPanel.add(httpsLabel, gc);
        gc.gridx = 1;
        gc.fill = GridBagConstraints.NONE;
        manualProxyPanel.add(httpsProxyAddressTextField, gc);
        gc.gridx = 2;
        manualProxyPanel.add(httpsProxyPortSpinner, gc);
        gc.gridx = 3;
        manualProxyPanel.add(httpsProxyUserNameTextField, gc);
        gc.gridx = 4;
        manualProxyPanel.add(httpsProxyPasswordField, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        manualProxyPanel.add(ftpLabel, gc);
        gc.gridx = 1;
        gc.fill = GridBagConstraints.NONE;
        manualProxyPanel.add(ftpProxyAddressTextField, gc);
        gc.gridx = 2;
        manualProxyPanel.add(ftpProxyPortSpinner, gc);
        gc.gridx = 3;
        manualProxyPanel.add(ftpProxyUserNameTextField, gc);
        gc.gridx = 4;
        manualProxyPanel.add(ftpProxyPasswordField, gc);

        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        manualProxyPanel.add(useSocksLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        manualProxyPanel.add(useSocksRadioButton, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        manualProxyPanel.add(socksProxyAddressLabel, gc);
        gc.gridx = 2;
        manualProxyPanel.add(socksProxyPortLabel, gc);
        gc.gridx = 3;
        manualProxyPanel.add(socksProxyUserNameLabel, gc);
        gc.gridx = 4;
        manualProxyPanel.add(socksProxyPasswordLabel, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridy++;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        manualProxyPanel.add(socksLabel, gc);
        gc.gridx = 1;
        gc.fill = GridBagConstraints.NONE;
        manualProxyPanel.add(socksProxyAddressTextField, gc);
        gc.gridx = 2;
        manualProxyPanel.add(socksProxyPortSpinner, gc);
        gc.gridx = 3;
        manualProxyPanel.add(socksProxyUserNameTextField, gc);
        gc.gridx = 4;
        manualProxyPanel.add(socksProxyPasswordField, gc);

        manualProxyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(manualProxyPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setEnableStatusOfComponents();
    }

    private void setEnableStatusOfComponents() {
        if (manualProxyRadioButton.isSelected() && useProxyRadioButton.isSelected()) {
            setEnableStatusOfComponentsOfPanel(manualProxyPanel, true);
            setEnableStatusOfUseProxy(true);
        } else if (manualProxyRadioButton.isSelected() && useSocksRadioButton.isSelected()) {
            setEnableStatusOfComponentsOfPanel(manualProxyPanel, true);
            setEnableStatusOfUseProxy(false);
        } else {
            setEnableStatusOfComponentsOfPanel(manualProxyPanel, false);
        }
    }

    private void setEnableStatusOfComponentsOfPanel(JPanel parentPanel, boolean enableStatus) {
        Component[] components = parentPanel.getComponents();
        for (Component component : components) {
            component.setEnabled(enableStatus);
        }
    }

    private void setEnableStatusOfUseProxy(boolean enableStatus) {
        httpLabel.setEnabled(enableStatus);
        httpsLabel.setEnabled(enableStatus);
        ftpLabel.setEnabled(enableStatus);

        proxyAddressLabel.setEnabled(enableStatus);
        proxyPortLabel.setEnabled(enableStatus);
        proxyUserNameLabel.setEnabled(enableStatus);
        proxyPasswordLabel.setEnabled(enableStatus);
        httpProxyAddressTextField.setEnabled(enableStatus);
        httpProxyPortSpinner.setEnabled(enableStatus);
        httpProxyUserNameTextField.setEnabled(enableStatus);
        httpProxyPasswordField.setEnabled(enableStatus);

        httpsProxyAddressTextField.setEnabled(enableStatus);
        httpsProxyPortSpinner.setEnabled(enableStatus);
        httpsProxyUserNameTextField.setEnabled(enableStatus);
        httpsProxyPasswordField.setEnabled(enableStatus);

        ftpProxyAddressTextField.setEnabled(enableStatus);
        ftpProxyPortSpinner.setEnabled(enableStatus);
        ftpProxyUserNameTextField.setEnabled(enableStatus);
        ftpProxyPasswordField.setEnabled(enableStatus);

        socksLabel.setEnabled(!enableStatus);

        socksProxyAddressLabel.setEnabled(!enableStatus);
        socksProxyPortLabel.setEnabled(!enableStatus);
        socksProxyUserNameLabel.setEnabled(!enableStatus);
        socksProxyPasswordLabel.setEnabled(!enableStatus);
        socksProxyAddressTextField.setEnabled(!enableStatus);
        socksProxyPortSpinner.setEnabled(!enableStatus);
        socksProxyUserNameTextField.setEnabled(!enableStatus);
        socksProxyPasswordField.setEnabled(!enableStatus);
    }


    public PreferencesProxyDTO getPreferencesProxyDTO() {
        String proxySettingType = proxySettingGroup.getSelection().getActionCommand();
        switch (proxySettingType) {
            case "noProxy":
                preferencesProxyDTO.setProxySettingType(0);
                break;
            case "systemProxy":
                preferencesProxyDTO.setProxySettingType(1);
                break;
            case "manualProxy":
                preferencesProxyDTO.setProxySettingType(2);
        }

        if (useProxyRadioButton.isSelected())
            preferencesProxyDTO.setUseProxyNotSocks(true);
        else
            preferencesProxyDTO.setUseProxyNotSocks(false);

        // for http
        preferencesProxyDTO.setHttpProxyAddress(httpProxyAddressTextField.getText());
        preferencesProxyDTO.setHttpProxyPort((Integer) httpProxyPortSpinner.getValue());
        preferencesProxyDTO.setHttpProxyUserName(httpProxyUserNameTextField.getText());
        preferencesProxyDTO.setHttpProxyPassword(new String(httpProxyPasswordField.getPassword()));

        // for https
        preferencesProxyDTO.setHttpsProxyAddress(httpsProxyAddressTextField.getText());
        preferencesProxyDTO.setHttpsProxyPort((Integer) httpsProxyPortSpinner.getValue());
        preferencesProxyDTO.setHttpsProxyUserName(httpsProxyUserNameTextField.getText());
        preferencesProxyDTO.setHttpsProxyPassword(new String(httpsProxyPasswordField.getPassword()));

        // for ftp
        preferencesProxyDTO.setFtpProxyAddress(ftpProxyAddressTextField.getText());
        preferencesProxyDTO.setFtpProxyPort((Integer) ftpProxyPortSpinner.getValue());
        preferencesProxyDTO.setFtpProxyUserName(ftpProxyUserNameTextField.getText());
        preferencesProxyDTO.setFtpProxyPassword(new String(ftpProxyPasswordField.getPassword()));

        // for ftp
        preferencesProxyDTO.setSocksProxyAddress(socksProxyAddressTextField.getText());
        preferencesProxyDTO.setSocksProxyPort((Integer) socksProxyPortSpinner.getValue());
        preferencesProxyDTO.setSocksProxyUserName(socksProxyUserNameTextField.getText());
        preferencesProxyDTO.setSocksProxyPassword(new String(socksProxyPasswordField.getPassword()));

        return preferencesProxyDTO;
    }

    public void setPreferencesProxyDTO(PreferencesProxyDTO preferencesProxyDTO) {
        switch (preferencesProxyDTO.getProxySettingType()) {
            case 0:
                noProxyRadioButton.setSelected(true);
                break;
            case 1:
                systemProxyRadioButton.setSelected(true);
                break;
            case 2:
                manualProxyRadioButton.setSelected(true);
        }

        if (preferencesProxyDTO.isUseProxyNotSocks())
            useProxyRadioButton.setSelected(true);
        else
            useSocksRadioButton.setSelected(true);

        // for http
        httpProxyAddressTextField.setText(preferencesProxyDTO.getHttpProxyAddress());
        httpProxyPortSpinner.setValue(preferencesProxyDTO.getHttpProxyPort());
        httpProxyUserNameTextField.setText(preferencesProxyDTO.getHttpProxyUserName());
        httpProxyPasswordField.setText(preferencesProxyDTO.getHttpProxyPassword());

        // for https
        httpsProxyAddressTextField.setText(preferencesProxyDTO.getHttpsProxyAddress());
        httpsProxyPortSpinner.setValue(preferencesProxyDTO.getHttpsProxyPort());
        httpsProxyUserNameTextField.setText(preferencesProxyDTO.getHttpsProxyUserName());
        httpsProxyPasswordField.setText(preferencesProxyDTO.getHttpsProxyPassword());

        // for ftp
        ftpProxyAddressTextField.setText(preferencesProxyDTO.getFtpProxyAddress());
        ftpProxyPortSpinner.setValue(preferencesProxyDTO.getFtpProxyPort());
        ftpProxyUserNameTextField.setText(preferencesProxyDTO.getFtpProxyUserName());
        ftpProxyPasswordField.setText(preferencesProxyDTO.getFtpProxyPassword());

        // for ftp
        socksProxyAddressTextField.setText(preferencesProxyDTO.getSocksProxyAddress());
        socksProxyPortSpinner.setValue(preferencesProxyDTO.getSocksProxyPort());
        socksProxyUserNameTextField.setText(preferencesProxyDTO.getSocksProxyUserName());
        socksProxyPasswordField.setText(preferencesProxyDTO.getSocksProxyPassword());

        this.preferencesProxyDTO = preferencesProxyDTO;
    }
}
