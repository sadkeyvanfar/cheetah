package gui.preference;

import enums.ProxyType;
import gui.listener.PreferencesListener;
import model.dto.OptionsCategoryDto;
import model.dto.PreferencesDTO;
import model.dto.PreferencesInterfaceDTO;
import model.dto.PreferencesProxyDTO;
import service.ProxyService;
import service.impl.ProxyServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Saeed on 9/13/2015.
 */
public class PreferenceDialog extends JDialog {

    private JPanel confirmationPanel;
    private JButton okButton;
    private JButton cancelButton;
    private JButton defaultButton;

    private OptionsCategoryPanel optionsCategoryPanel;
    private OptionsPanels preferencePanels;

    private PreferencesListener preferencesListener;

    JFrame parent;

    public PreferenceDialog(JFrame parent, PreferencesDTO preferencesDTO) {

        super(parent, "Preferences", true);
        this.parent = parent;
        setLayout(new BorderLayout());

        OptionsCategoryDto optionsCategoryDto = new OptionsCategoryDto("Category", Arrays.asList("General", "Save To",
                "download", "Connection", "Proxy", "Site Login", "Interface")); // Sounds, File Types, dial Up / VPN
        optionsCategoryPanel = new OptionsCategoryPanel(optionsCategoryDto);
        preferencePanels = new OptionsPanels(parent, preferencesDTO);
        optionsCategoryPanel.setOptionsCategoryPanelListener(preferencePanels);

        JScrollPane scrollPane = new JScrollPane(preferencePanels);
        scrollPane.setMinimumSize(new Dimension(150, 400));

        confirmationPanel = new JPanel();
        confirmationPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        defaultButton = new JButton("Defaults");

        confirmationPanel.add(okButton);
        confirmationPanel.add(cancelButton);
        confirmationPanel.add(defaultButton);

        add(optionsCategoryPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(confirmationPanel, BorderLayout.SOUTH);

        setPanelBackgroundColor(Color.WHITE);

        setMinimumSize(new Dimension(350,250));
        setLocationRelativeTo(parent);
        pack();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButtonClicked();
                PreferenceDialog.this.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesListener.preferenceReset();
                okButtonClicked();
                PreferenceDialog.this.setVisible(false);
            }
        });

        defaultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                preferencesListener.preferenceDefaults();
            }
        });
    }

    private void setPanelBackgroundColor(Color color) {
        optionsCategoryPanel.setBackground(color);
        preferencePanels.setPanelBackgroundColor(color);
        confirmationPanel.setBackground(color);
    }

    private void okButtonClicked() {
        PreferencesDTO preferenceDTO = preferencePanels.getPreferencesDTO();
        if (preferencesListener != null) {
            preferencesListener.preferencesSet(preferenceDTO);
        }
        setPreferenceEffects(preferenceDTO);
    }

    public void setPreferencesDTO(PreferencesDTO preferencesDTO) {
        preferencePanels.setPreferencesDTO(preferencesDTO);
    }

    private void setPreferenceEffects(PreferencesDTO preferencesDTO) {
       PreferencesInterfaceDTO preferencesInterfaceDTO = preferencesDTO.getPreferencesInterfaceDTO();
        utils.LookAndFeel.changeLaf(parent, preferencesInterfaceDTO.getLookAndFeelName(), new Dimension(900, 580));
        setLocale(new Locale(preferencesInterfaceDTO.getLocalName()));
        ProxyService proxyService = new ProxyServiceImpl();
        PreferencesProxyDTO preferencesProxyDTO = preferencesDTO.getPreferencesProxyDTO();
        if (preferencesProxyDTO.getProxySettingType() == 0) {
            proxyService.setNoProxy();
        } else if (preferencesProxyDTO.getProxySettingType() == 1) {
            proxyService.setUseSystemProxy();
        } else if (preferencesProxyDTO.getProxySettingType() == 2 && preferencesProxyDTO.isUseProxyNotSocks()) {
            proxyService.setProxy(ProxyType.HTTP_PROXY, preferencesProxyDTO.getHttpProxyAddress(), preferencesProxyDTO.getHttpProxyPort(),
                    preferencesProxyDTO.getHttpProxyUserName(), preferencesProxyDTO.getHttpProxyPassword());
        } else if (preferencesProxyDTO.getProxySettingType() == 2 && !preferencesProxyDTO.isUseProxyNotSocks()) {
            proxyService.setProxy(ProxyType.SOCKS_PROXY, preferencesProxyDTO.getSocksProxyAddress(), preferencesProxyDTO.getSocksProxyPort(),
                    preferencesProxyDTO.getSocksProxyUserName(), preferencesProxyDTO.getSocksProxyPassword());
        }
    }

    public void setPreferencesListener(PreferencesListener preferencesListener) {
        this.preferencesListener = preferencesListener;
    }
}
