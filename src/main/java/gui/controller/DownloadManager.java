package gui.controller;

import model.Download;

import java.util.List;

public interface DownloadManager {
    List<Download> getAllDownloads();
    void addDownload(Download download);
    void deleteDownload(Download download);
    void saveDownload(Download download);
    void openFile(String filePath);
    void remove(Download selectedDownload);
    void pauseDownload(Download download);
    void resumeDownload(Download download);
    void updateDownload(Download download);
    void startDownload(Download download);
}
