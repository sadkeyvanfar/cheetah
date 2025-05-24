/*
 * Cheetah - A Free Fast Downloader
 *
 * Copyright © 2017 Saeed Kayvanfar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gui.preference;

import gui.listener.OptionsCategoryPanelListener;
import model.dto.PreferencesDTO;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> on 6/8/17.
 */
public class OptionsPanels extends JPanel implements OptionsCategoryPanelListener {

    private PreferenceGeneralPanel preferenceGeneralPanel;
    private PreferenceSavePanel preferenceSavePanel;
    private PreferenceDownloadPanel preferenceDownloadPanel;
    private PreferenceConnectionPanel preferenceConnectionPanel;
    private PreferenceProxyPanel preferenceProxyPanel;
    private PreferenceSitesLoginPanel preferenceSitesLoginPanel;
    private PreferenceInterfacePanel preferenceInterfacePanel;

    private CardLayout cardLayout;

    public OptionsPanels(JFrame parent, PreferencesDTO preferencesDTO) {
        preferenceGeneralPanel = new PreferenceGeneralPanel(preferencesDTO.getPreferencesGeneralDTO());
        preferenceSavePanel = new PreferenceSavePanel(preferencesDTO.getPreferencesSaveDTO());
        preferenceDownloadPanel = new PreferenceDownloadPanel();
        preferenceConnectionPanel = new PreferenceConnectionPanel(preferencesDTO.getPreferencesConnectionDTO());
        preferenceProxyPanel = new PreferenceProxyPanel(preferencesDTO.getPreferencesProxyDTO());
        preferenceSitesLoginPanel = new PreferenceSitesLoginPanel();
        preferenceInterfacePanel = new PreferenceInterfacePanel(preferencesDTO.getPreferencesInterfaceDTO(), parent);

        cardLayout = new CardLayout();
        setLayout(cardLayout);
        add(preferenceGeneralPanel, "General");
        add(preferenceSavePanel, "Save To");
        add(preferenceDownloadPanel, "download");
        add(preferenceConnectionPanel, "Connection");
        add(preferenceProxyPanel, "Proxy");
        add(preferenceSitesLoginPanel, "Site Login");
        add(preferenceInterfacePanel, "Interface");

        cardLayout.show(this, "General");
    }

    public PreferencesDTO getPreferencesDTO() {
        PreferencesDTO preferenceDTO = new PreferencesDTO();
        preferenceDTO.setPreferencesSaveDTO(preferenceSavePanel.getPreferenceSaveDTO());
        preferenceDTO.setPreferencesConnectionDTO(preferenceConnectionPanel.getPreferencesConnectionDTO());
        preferenceDTO.setPreferencesProxyDTO(preferenceProxyPanel.getPreferencesProxyDTO());
        preferenceDTO.setPreferencesInterfaceDTO(preferenceInterfacePanel.getPreferencesInterfaceDTO());
        return preferenceDTO;
    }

    public void setPreferencesDTO(PreferencesDTO preferencesDTO) {
        preferenceSavePanel.setPreferenceSaveDTO(preferencesDTO.getPreferencesSaveDTO());
        preferenceConnectionPanel.setPreferencesConnectionDTO(preferencesDTO.getPreferencesConnectionDTO());
        preferenceInterfacePanel.setPreferencesInterfaceDTO(preferencesDTO.getPreferencesInterfaceDTO());
        preferenceProxyPanel.setPreferencesProxyDTO(preferencesDTO.getPreferencesProxyDTO());
    }

    public void setPanelBackgroundColor(Color color) {
        this.setBackground(color);
        preferenceGeneralPanel.setBackground(color);
        preferenceSavePanel.setBackground(color);
        preferenceDownloadPanel.setBackground(color);
        preferenceConnectionPanel.setBackground(color);
        preferenceProxyPanel.setBackground(color);
        preferenceSitesLoginPanel.setBackground(color);
        preferenceInterfacePanel.setBackground(color);
    }

    @Override
    public void nodeSelectedEventOccured(String nodeName) {
        cardLayout.show(this, nodeName);
    }
}
