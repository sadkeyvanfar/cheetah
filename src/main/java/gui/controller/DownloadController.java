package gui.controller;

import model.Download;

import java.util.List;

public interface DownloadController {
    List<Download> getAllDownloads();
/*    void pauseDownload(Download download);
    void resumeDownload(Download download);*/
    void addDownload(Download download);
    void deleteDownload(Download download);
    void saveDownload(Download download);
    void openFile(String filePath);
    void remove(Download selectedDownload);
}
