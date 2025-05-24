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

import utils.DesktopUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/25/2015
 */
class AboutPanel extends JPanel {

    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    private final JLabel programNameLabel;
    private final JLabel productionMessageLabel;
    private final JLabel websiteLabel;
    private final JTextArea copyrightTextArea;

    public AboutPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        programNameLabel = new JLabel(bundle.getString("aboutPanel.program.name"));
        productionMessageLabel = new JLabel(bundle.getString("aboutPanel.program.productionMessage"));
        websiteLabel = new JLabel(bundle.getString("aboutPanel.program.website"));
        copyrightTextArea = new JTextArea(bundle.getString("aboutPanel.program.copyright"), 15, 40);

        programNameLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        productionMessageLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        websiteLabel.setFont(new Font("Dialog", Font.BOLD, 13));
        copyrightTextArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        copyrightTextArea.setEditable(false);

        productionMessageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    URI uri = new URI(bundle.getString("aboutPanel.program.author.mail"));
                    DesktopUtil.openDefaultMailClient(uri);
                } catch (URISyntaxException | IOException e1) {
                    e1.printStackTrace();
                }

            }
        });

        add(programNameLabel);
        add(new JSeparator());
        add(websiteLabel);
        add(productionMessageLabel);
        add(new JScrollPane(copyrightTextArea));
    }

    @Override
    public Insets getInsets() {
        return new Insets(20,20,20,20);
    }

}
