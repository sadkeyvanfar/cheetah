package gui.listener;

import gui.model.DownloadAction;
import model.Download;

public interface DownloadTablePanelListener {
    void onMenuAction(Download download, DownloadAction action);
    void downloadSelected(Download download);
    void downloadDoubleClicked(Download download);
}
