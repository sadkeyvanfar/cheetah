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

package gui.download;

import gui.listener.DownloadAskDialogListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 10/3/2015
 */
public class DownloadAskDialog extends JDialog implements ActionListener {

    private JLabel urlLabel;
    private JTextField urlTextField;
    private JLabel pathLabel;
    private JTextField pathTextField;
    private JButton pathButton;
    private JFileChooser pathFileChooser;
    private JLabel sizeLabel;
    private JTextField sizeTextField;
    private JLabel resumeCapabilityLabel;
    private JLabel resumeCapabilityResult;
    private JButton startDownloadButton;
    private JButton cancelDownloadButton;
    private JButton laterDownloadButton;

    private DownloadAskDialogListener downloadAskDialogListener;

    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    private JFrame parent;

    public DownloadAskDialog(JFrame parent) {
        super(parent, "Download File", false);

        this.parent = parent;

        urlLabel = new JLabel(bundle.getString("downloadAskDialog.urlLabel.label"));
        urlTextField = new JTextField(50);
        urlTextField.setEditable(false);
        pathLabel = new JLabel(bundle.getString("downloadAskDialog.pathLabel.label"));
        pathTextField = new JTextField(35);
        pathButton = new JButton(bundle.getString("downloadAskDialog.pathButton.label"));
        pathButton.setToolTipText(bundle.getString("downloadAskDialog.pathButton.toolTip"));
        pathFileChooser = new JFileChooser();
        sizeLabel = new JLabel(bundle.getString("downloadAskDialog.sizeLabel.label"));
        sizeTextField = new JTextField(10);
        sizeTextField.setEditable(false);
        resumeCapabilityLabel = new JLabel(bundle.getString("downloadAskDialog.resumeCapabilityLabel.label"));
        resumeCapabilityResult = new JLabel();

        startDownloadButton = new JButton(bundle.getString("downloadAskDialog.startDownloadButton.label"));
        startDownloadButton.setToolTipText(bundle.getString("downloadAskDialog.startDownloadButton.toolTip"));
        cancelDownloadButton = new JButton(bundle.getString("downloadAskDialog.cancelDownloadButton.label"));
        cancelDownloadButton.setToolTipText(bundle.getString("downloadAskDialog.cancelDownloadButton.toolTip"));
        laterDownloadButton = new JButton(bundle.getString("downloadAskDialog.laterDownloadButton.label"));
        laterDownloadButton.setToolTipText(bundle.getString("downloadAskDialog.laterDownloadButton.toolTip"));

        startDownloadButton.addActionListener(this);
        cancelDownloadButton.addActionListener(this);
        laterDownloadButton.addActionListener(this);
        pathButton.addActionListener(this);

        layoutControls();
        pack();
        setResizable(true);
        setLocationRelativeTo(parent);
    }

    private void layoutControls() {
        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        Insets rightPadding = new Insets(0, 0, 0, 15);
        Insets noPadding = new Insets(0, 0, 0, 0);

        panel.setBorder(new EmptyBorder(10, 10, 10, 80));
        ///////////////// First row ////////////////////////////////////////////
        gc.weightx = 1;
        gc.weighty = 0.25;

        gc.gridy = 0;
        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = rightPadding;
        panel.add(urlLabel, gc);

        gc.gridwidth = 3;
        gc.gridx = 1;
        gc.gridy = 0;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        panel.add(urlTextField, gc);

        ////////////Next row ////////////////////////////
        gc.gridy++;

        gc.gridwidth = 1;
        gc.gridx = 0;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_END;
        panel.add(pathLabel, gc);

        gc.gridwidth = 2;
        gc.gridx = 1;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(pathTextField, gc);

        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = 1;
        gc.gridx = 3;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        panel.add(pathButton, gc);

        ///////////////// Next row ////////////////////////////////////////////
        ////////////Next row ////////////////////////////
        gc.gridy++;

        gc.gridwidth = 1;
        gc.gridx = 0;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_END;
        panel.add(sizeLabel, gc);

        gc.gridwidth = 3;
        gc.gridx = 1;
        //    gc.gridy = 1;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        panel.add(sizeTextField, gc);

        ////////////Next row ////////////////////////////
        gc.gridy++;

        gc.gridwidth = 1;
        gc.gridx = 0;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_END;
        panel.add(resumeCapabilityLabel, gc);

        gc.gridwidth = 3;
        gc.gridx = 1;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        panel.add(resumeCapabilityResult, gc);
        ///////////////// Next row ////////////////////////////////////////////
        gc.gridwidth = 1;
        gc.gridy++;
        gc.gridx = 1;
        gc.insets = noPadding;
        panel.add(startDownloadButton, gc);

        gc.gridx = 2;
        gc.insets = noPadding;
        panel.add(cancelDownloadButton, gc);

        gc.gridx = 3;
        gc.insets = noPadding;
        panel.add(laterDownloadButton, gc);

        add(panel);
    }

    public void setDownloadAskDialogListener(DownloadAskDialogListener downloadAskDialogListener) {
        this.downloadAskDialogListener = downloadAskDialogListener;
    }

    public void removeDownloadAskDialogListener(DownloadAskDialogListener downloadAskDialogListener) {
        if (downloadAskDialogListener.equals(downloadAskDialogListener))
            downloadAskDialogListener = null;
    }

    public void setInfo(String url, String path) {
        urlTextField.setText(url);
  //      downloadNameField.setText(downloadNameFile);
        pathTextField.setText(path);
    }

    public void setInfo(String url, String path, String size, boolean resumeCapability) {
        urlTextField.setText(url);
        pathTextField.setText(path);
        sizeTextField.setText(size);
        if (resumeCapability) {
            resumeCapabilityResult.setForeground(Color.GREEN);
            resumeCapabilityResult.setText(bundle.getString("downloadAskDialog.resumeCapabilityResult.label.yes"));
        } else {
            resumeCapabilityResult.setForeground(Color.RED);
            resumeCapabilityResult.setText(bundle.getString("downloadAskDialog.resumeCapabilityResult.label.no"));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!sizeTextField.getText().equals("")) {
            JButton clicked= (JButton) e.getSource();
            if (downloadAskDialogListener != null) {
                if (clicked == startDownloadButton) {
                    downloadAskDialogListener.startDownloadEventOccured(pathTextField.getText());
                    dispose();
                } else if (clicked == cancelDownloadButton) {
                    downloadAskDialogListener.cancelDownloadEventOccured();
                    dispose();
                } else if (clicked == laterDownloadButton) {
                    downloadAskDialogListener.laterDownloadEventOccured();
                    dispose();
                }
            }
            if (clicked == pathButton) {
                pathFileChooser.setSelectedFile(new File(pathTextField.getText()));
                if(pathFileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                    pathTextField.setText(pathFileChooser.getSelectedFile().toString());
                }
            }
        }
    }
}
