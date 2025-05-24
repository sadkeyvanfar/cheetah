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

import model.Download;
import model.DownloadRange;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/14/2015
 */
class DownloadInfoPanel extends JPanel {

    // Table showing download ranges.
    private JTable downloadRangeTable;

    // download Range table's data model.
    private DownloadRangesTableModel downloadRangesTableModel;

    public DownloadInfoPanel(Download download) {

        setLayout(new BorderLayout());

        // Set up Downloads table.
        downloadRangesTableModel = new DownloadRangesTableModel();

        downloadRangeTable = new JTable(downloadRangesTableModel);
        downloadRangeTable.setShowGrid(false);

        setColumnWidths();

        add(new JScrollPane(downloadRangeTable), BorderLayout.CENTER);
    }

    public void addDownloadRange(DownloadRange downloadRange) {

        downloadRangesTableModel.addDownloadRange(downloadRange);
    }

    public void setDownloadRanges(java.util.List<DownloadRange> downloadRanges) {
        downloadRangesTableModel.setDownloadRanges(downloadRanges);
    }

    private void setColumnWidths(){
        downloadRangeTable.getColumnModel().getColumn(0).setPreferredWidth(56);
        downloadRangeTable.getColumnModel().getColumn(0).setMaxWidth(70);
        downloadRangeTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        downloadRangeTable.getColumnModel().getColumn(1).setMaxWidth(150);

        downloadRangeTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }
}
