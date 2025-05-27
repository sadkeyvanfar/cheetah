package gui.controller;

import controller.DatabaseController;
import controller.DatabaseControllerImpl;
import model.Download;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadManagerImpl implements DownloadManager {

    private final DatabaseController databaseController;
    private final List<Download> downloads;
    private final int connectionTimeout;
    private final int readTimeout;

    public DownloadManagerImpl(String databasePath, int connectionTimeout, int readTimeout) {
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
    public void remove(Download download) {
        synchronized (downloads) {
            downloads.remove(download);
        }
    }

    @Override
    public void pauseDownload(Download download) {
        synchronized (downloads) {
            if (downloads.contains(download)) {
                download.pause(); // assume model.Download has pause()
                saveDownload(download);
            }
        }
    }

    @Override
    public void resumeDownload(Download download) {
        synchronized (downloads) {
            if (downloads.contains(download)) {
                download.resume(); // assumes resume() method exists
              //  saveDownload(download); todo
            }
        }
    }

    @Override
    public void startDownload(Download download) {
        synchronized (downloads) {
            if (!downloads.contains(download)) {
                downloads.add(download);
            }
            download.resume();
            saveDownload(download);
        }
    }

    @Override
    public void updateDownload(Download download) {
        // For progress, state, etc.
        saveDownload(download);
    }
}
