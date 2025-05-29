package gui.listener;

import model.Download;

public interface DownloadListener {
    void downloadProgressUpdated(Download download);
    void downloadStateChanged(Download download);
    void downloadCompleted(Download download);
    void downloadFailed(Download download, Exception e);
}
