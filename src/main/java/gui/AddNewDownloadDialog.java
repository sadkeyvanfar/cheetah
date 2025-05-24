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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import concurrent.BackgroundTask;
import gui.listener.AddNewDownloadListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/10/2015
 */
class AddNewDownloadDialog extends JDialog {

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Logger messageLogger = Logger.getLogger("message");

    private final static ExecutorService backgroundExec = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).build());

    // Add download text field.
    private JTextField newTextField;
    private JButton newButton;

    private JPanel useAuthorizationPanel;
    private JLabel useAuthorizationLabel;
    private JCheckBox useAuthorizationCheckBox;
    private JLabel userIDLabel;
    private JTextField userIDTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;

    private AddNewDownloadListener addNewDownloadListener;

    public AddNewDownloadDialog(JFrame parent) {
        super(parent, "Add New download", false);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

        newButton = new JButton(bundle.getString("addNewDownloadDialog.newButton.label"));
        newButton.setToolTipText(bundle.getString("addNewDownloadDialog.newButton.toolTip"));
        newTextField = new JTextField(44);

        useAuthorizationPanel = new JPanel();
        useAuthorizationLabel = new JLabel(bundle.getString("addNewDownloadDialog.useAuthorizationLabel.label"));
        useAuthorizationCheckBox = new JCheckBox();
        userIDLabel = new JLabel(bundle.getString("addNewDownloadDialog.userIDLabel.label"));
        userIDTextField = new JTextField(8);
        userIDTextField.setEnabled(false);
        passwordLabel = new JLabel(bundle.getString("addNewDownloadDialog.passwordLabel.label"));
        passwordField = new JPasswordField(8);
        passwordField.setEnabled(false);

        // Set up menmomics
        newButton.setMnemonic(KeyEvent.VK_O);

        useAuthorizationLabel.setDisplayedMnemonic(KeyEvent.VK_N);
        useAuthorizationLabel.setLabelFor(useAuthorizationCheckBox);

        Border innerBorder = BorderFactory.createTitledBorder(bundle.getString("addNewDownloadDialog.useAuthorizationPanel.label"));
        Border outerBorder = BorderFactory.createEmptyBorder(5,5,5,5);
        useAuthorizationPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        layoutControls();

        // can use ActionListener for class and use that
        newButton.addActionListener(actionEvent -> addAction());

        useAuthorizationCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getItem();
                if (checkBox.isSelected()) {
                    userIDTextField.setEnabled(true);
                    passwordField.setEnabled(true);
                } else {
                    userIDTextField.setEnabled(false);
                    passwordField.setEnabled(false);
                }
            }
        });
        pack();

       // setSize(740, 150);
        setResizable(true);
        setLocationRelativeTo(parent);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
            }
        });
    }

    private void layoutControls() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Insets rightPadding = new Insets(0, 0, 0, 15);
        Insets noPadding = new Insets(0, 0, 0, 0);

        ///////////////// First row ////////////////////////////////////////////
        gc.weightx = 1;
        gc.weighty = 1;

        gc.gridy = 0;
        gc.gridx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.insets = rightPadding;
        panel.add(newButton, gc);

        gc.gridwidth = 3;
        gc.gridx = 1;
        gc.gridy = 0;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        panel.add(newTextField, gc);
        ////////////Next row ////////////////////////////
        gc.gridy++;

        gc.gridwidth = 1;
        gc.gridx = 0;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_END;
        panel.add(useAuthorizationLabel, gc);

        gc.gridwidth = 2;
        gc.gridx = 1;
        gc.insets = noPadding;
        gc.anchor = GridBagConstraints.LINE_START;
        panel.add(useAuthorizationCheckBox, gc);

        useAuthorizationPanel.add(userIDLabel); // todo may use GridBagLayout
        useAuthorizationPanel.add(userIDTextField);
        useAuthorizationPanel.add(passwordLabel);
        useAuthorizationPanel.add(passwordField);


        gc.gridwidth = 2;
        gc.gridx = 2;
        gc.insets = rightPadding;
        gc.anchor = GridBagConstraints.LINE_END;
        panel.add(useAuthorizationPanel, gc);

        pack();

        add(panel);
    }

    public void setAddNewDownloadListener(AddNewDownloadListener addNewDownloadListener) {
        Objects.requireNonNull(addNewDownloadListener, "addNewDownloadListener");
        this.addNewDownloadListener = addNewDownloadListener;
    }

    // Add a new download.
    private void addAction() {
        class WindowListener extends WindowAdapter {
            BackgroundTask<?> task;

            @Override
            public void windowClosing(WindowEvent event) {
                if (task != null)
                    task.cancel(true);
            }
        }
        final WindowListener listener = new WindowListener();
        listener.task = new BackgroundTask<Void>() {
            public Void compute() {
                doSomeWork();
                onCompletion(false, "", null);
                return null;
            }
            public void onCompletion(boolean cancelled, String s,
                                     Throwable exception) {
                removeWindowListener(listener);
                newButton.setEnabled(true);
            }

            private void doSomeWork() {
                messageLogger.info("New Download URL added: " + newTextField.getText());
                URL verifiedUrl = verifyUrl(newTextField.getText());
                if (verifiedUrl != null) {
                    messageLogger.info("URL verified.");
                    newButton.setEnabled(false);
                    if (addNewDownloadListener != null) {
                        addNewDownloadListener.newDownloadEventOccured(verifiedUrl);
                    }
                    newTextField.setText(""); // reset new text field
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(AddNewDownloadDialog.this,
                            "Invalid download URL", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        addWindowListener(listener);
        backgroundExec.execute(listener.task);
    }

    // can be in DownloadManagerUI
    // Verify download URL.
    private URL verifyUrl(String url) {
        // Only allow HTTP URLs.
        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
            messageLogger.info("Error: Scheme of URL must be http or https.");
            return null;
        }

        // Verify format of URL.
        URL verifiedUrl;
        try {
            verifiedUrl = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("URL isn't correct");
            messageLogger.info("Error: URL isn't correct.");
            return null;
        }

        // Make sure URL specifies a file.
        if (verifiedUrl.getFile().length() < 2)
            return null;

        return verifiedUrl;
    }

    public void onPaste(){
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        if (t == null)
            return;
        try {
        String textURL = (String) t.getTransferData(DataFlavor.stringFlavor);
            if (verifyUrl(textURL) != null) {
                newTextField.setText(textURL);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void shutdownThreads() {
        backgroundExec.shutdown();
    }
}
