package gui.controller;

import controller.DatabaseController;
import controller.DatabaseControllerImpl;
import model.Download;
import enums.DownloadStatus;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadControllerImpl implements DownloadController {

    private final DatabaseController databaseController;
    private final List<Download> downloads;
    private final int connectionTimeout;
    private final int readTimeout;

    public DownloadControllerImpl(String databasePath, int connectionTimeout, int readTimeout) {
        String connectionUrl = "jdbc:sqlite:" + databasePath + java.io.File.separator + "cheetah.db";
        this.databaseController = new DatabaseControllerImpl("org.sqlite.JDBC", connectionUrl, connectionTimeout, "", "");
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;

        List<Download> loadedDownloads;
        try {
            databaseController.createTablesIfNotExist();
            loadedDownloads = databaseController.load();
        } catch (Exception e) {
            e.printStackTrace();
            loadedDownloads = new ArrayList<>();
        }

        this.downloads = Collections.synchronizedList(new ArrayList<>(loadedDownloads));
    }

    @Override
    public List<Download> getAllDownloads() {
        return Collections.unmodifiableList(downloads);
    }

 /*   @Override
    public void pauseDownload(Download download) {
        synchronized (downloads) {
            if (downloads.contains(download) && download.getStatus() == DownloadStatus.DOWNLOADING) {
                download.pause();
                // Optionally update DB status
                try {
                    databaseController.save(download);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
*/
    /*@Override
    public void resumeDownload(Download download) {
        synchronized (downloads) {
            if (downloads.contains(download) && download.getStatus() == DownloadStatus.PAUSED) {
                download.setConnectTimeout(this.connectionTimeout);
                download.setDownloaded(this.readTimeout);
                download.resume();
                // Optionally update DB status
                try {
                    databaseController.save(download);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @Override
    public void deleteDownload(Download download) {
        synchronized (downloads) {
            if (downloads.remove(download)) {
                try {
                    databaseController.delete(download.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Add a new download
     */
    @Override
    public void addDownload(Download download) {
        synchronized (downloads) {
            downloads.add(download);
        }
    }

    @Override
    public void saveDownload(Download download) {
        try {
            databaseController.save(download);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void openFile(String filePath) {
        try {
            Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Download selectedDownload) {
        synchronized (downloads) {
            downloads.add(selectedDownload);
        }
    }
}
