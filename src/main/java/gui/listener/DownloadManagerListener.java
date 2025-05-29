package gui.listener;

import model.Download;

public interface DownloadManagerListener {

    // Called when a new download is added
    void downloadAdded(Download download);

    // Called when a download is removed
    void downloadRemoved(Download download);

    // Called when a download is updated (e.g., progress, status)
    void downloadUpdated(Download download);

    // Called when a download is paused
    void downloadPaused(Download download);

    void downloadCanceled(Download download);

    // Called when a download is resumed
    void downloadResumed(Download download);

    // Optional: if you support download failure/success callbacks
    void downloadCompleted(Download download);
    void downloadFailed(Download download, Throwable cause);
}
