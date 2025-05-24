/*
 * Cheetah - A Free Fast Downloader
 *
 * Copyright © 2015 Saeed Kayvanfar
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

package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/10/2015
 */
class StatusPanel extends JPanel implements ActionListener {

    private final ResourceBundle defaultPreferencesBundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    private JLabel cheetahLabel;
    private JLabel selectedDownloadNameLabel;

    public StatusPanel() {
        setLayout(new BorderLayout());

        cheetahLabel = new JLabel(defaultPreferencesBundle.getString("aboutPanel.program.name"));
        cheetahLabel.setFont(new Font("Dialog", Font.PLAIN, 18));
        selectedDownloadNameLabel = new JLabel();

        add(cheetahLabel, BorderLayout.EAST);
        add(selectedDownloadNameLabel, BorderLayout.WEST);
    }

    @Override
    public Insets getInsets() {
        return new Insets(0, 25, 0, 25);
    }

    public void setStatus(String selectedDownloadName) {
        selectedDownloadNameLabel.setText(selectedDownloadName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle action events if needed
    }

}
