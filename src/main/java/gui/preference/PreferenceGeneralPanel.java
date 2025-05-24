package gui.preference;

import model.dto.PreferencesGeneralDTO;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Saeed on 9/13/2015.
 */
public class PreferenceGeneralPanel extends PreferenceJPanel {

    private JPanel generalPanel;
    private JLabel lunchOnStartupLabel;
    private JCheckBox lunchOnStartupCheckBox;

    private PreferencesGeneralDTO preferencesGeneralDTO;

    public PreferenceGeneralPanel(PreferencesGeneralDTO preferencesGeneralDTO) {
        super("General", "preferenceGeneralPanel.iconPath");

        this.preferencesGeneralDTO = preferencesGeneralDTO;

        generalPanel = new JPanel();
        lunchOnStartupLabel = new JLabel("Launch Cheetah on startup");
        lunchOnStartupCheckBox = new JCheckBox();
        lunchOnStartupCheckBox.setEnabled(false);

        layoutComponentsOfGeneralPanel();
    }

    private void layoutComponentsOfGeneralPanel() {
        generalPanel.setLayout(new GridBagLayout());
        generalPanel.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();


        ///////////////// First row ////////////////////////////////////////////
        gc.gridy = 0;

        gc.weightx = 1;
        gc.weighty = 0.1;

        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = new Insets(0, 0, 0, 5);
        generalPanel.add(lunchOnStartupLabel, gc);

        gc.gridx = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        gc.anchor = GridBagConstraints.LINE_START;
        generalPanel.add(lunchOnStartupCheckBox, gc);
        ///////////////// Next row ////////////////////////////////////////////

        generalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(generalPanel);
    }

}
