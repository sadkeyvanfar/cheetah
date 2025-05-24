package gui;

import enums.DownloadStatus;
import gui.listener.DownloadStatusListener;
import model.Download;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for managing downloads in a JTable.
 *
 * Author: Saeed Kayvanfar
 */
public class DownloadsTableModel extends AbstractTableModel implements DownloadStatusListener {

    private static final String[] COLUMN_NAMES = {
            "Name", "Size", "Progress", "Transfer Rate", "Status", "Description"
    };

    private static final Class<?>[] COLUMN_CLASSES = {
            String.class, String.class, JProgressBar.class, String.class, String.class, String.class
    };

    private final List<Download> downloadList = new ArrayList<>();

    public DownloadsTableModel() {}

    public List<Download> getDownloadList() {
        return downloadList;
    }

    public void setDownloads(List<Download> downloads) {
        // Unregister listeners from existing downloads
        for (Download download : downloadList) {
            download.deleteDownloadStatusListener(this);
        }

        downloadList.clear();
        downloadList.addAll(downloads);

        // Register listeners to new downloads
        for (Download download : downloadList) {
            download.addDownloadStatusListener(this);
        }

        fireTableDataChanged();
    }

    public void addDownload(Download download) {
        if (!downloadList.contains(download)) {
            download.addDownloadStatusListener(this);
            downloadList.add(download);
            int newRow = downloadList.size() - 1;
            fireTableRowsInserted(newRow, newRow);
        }
    }

    public Download getDownload(int row) {
        return downloadList.get(row);
    }

    public void clearDownload(Download download) {
        int row = downloadList.indexOf(download);
        if (row != -1) {
            downloadList.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

    public void clearDownloads(List<Download> downloads) {
        for (Download download : downloads) {
            clearDownload(download);
        }
    }

    public List<Download> getDownloadsByStatus(DownloadStatus status) {
        List<Download> selected = new ArrayList<>();
        for (Download download : downloadList) {
            if (download.getStatus() == status) {
                selected.add(download);
            }
        }
        return selected;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount() {
        return downloadList.size();
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return COLUMN_CLASSES[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Download download = downloadList.get(row);
        switch (col) {
            case 0: return download.getDownloadName();
            case 1:
                String size = String.valueOf(download.getFormattedSize());
                return "-1".equals(size) ? "" : size;
            case 2: return download.getProgress();
            case 3: return download.getTransferRate();
            case 4: return download.getStatus().getDesc();
            case 5: return download.getDescription();
            default: return null;
        }
    }

    @Override
    public void downloadStatusChanged(Download download) {
        int index = downloadList.indexOf(download);
        if (index != -1) {
            fireTableRowsUpdated(index, index);
        }
    }
}
