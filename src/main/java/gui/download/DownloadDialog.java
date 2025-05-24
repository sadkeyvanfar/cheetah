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

import gui.listener.DownloadInfoListener;
import gui.listener.DownloadPropertiesPanelListener;
import model.Download;
import model.DownloadRange;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/14/2015
 */
public class DownloadDialog extends JDialog implements DownloadPropertiesPanelListener {

    private DownloadInfoPanel downloadInfoPanel;
    private DownloadPropertiesPanel downloadPropertiesPanel;

    private Download download;

    private DownloadInfoListener downloadInfoListener;

    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    public void setDownloadInfoListener(DownloadInfoListener downloadInfoListener) {
        this.downloadInfoListener = downloadInfoListener;
    }

    public void removeDownloadInfoListener(DownloadInfoListener downloadInfoListener) {
        if (this.downloadInfoListener.equals(downloadInfoListener))
            this.downloadInfoListener = null;
    }


    public Download getDownload() {
        return download;
    }

    public DownloadDialog(JFrame parent, Download download) {
        super(parent, "Download Dialog", false);

        this.download = download;

        setLayout(new BorderLayout());

        downloadInfoPanel = new DownloadInfoPanel(download);
        downloadPropertiesPanel = new DownloadPropertiesPanel(download);
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(bundle.getString("downloadDialog.tabbedPane.downloadInfoPanel"), downloadInfoPanel);
        tabbedPane.addTab(bundle.getString("downloadDialog.tabbedPane.downloadPropertiesPanel"), downloadPropertiesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        downloadPropertiesPanel.setDownloadPropertiesPanelListener(this);

        pack();
        setLocationRelativeTo(parent);
    }

    public void addDownloadRange(DownloadRange downloadRange) {
        downloadInfoPanel.addDownloadRange(downloadRange);
    }

    public void setDownloadRanges(List<DownloadRange> downloadRanges) {
        downloadInfoPanel.setDownloadRanges(downloadRanges);
    }

    @Override
    public void okButtonClicked(String downloadDescription) {
        download.setDescription(downloadDescription);
        if (downloadInfoListener != null)
            downloadInfoListener.downloadNeedSaved(download);
    }
}
