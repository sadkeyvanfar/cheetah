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

package model;

import concurrent.BackgroundExecutor;
import concurrent.annotation.NotThreadSafe;
import enums.*;
import enums.TimeUnit;
import gui.listener.DownloadInfoListener;
import gui.listener.DownloadRangeStatusListener;
import gui.listener.DownloadStatusListener;
import org.apache.log4j.Logger;
import utils.ConnectionUtil;
import utils.FileUtil;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Skeletal Implementation of Download interface.
 *
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 10/17/2015
 * @see model.Download
 */
@NotThreadSafe
public abstract class AbstractDownload implements Download, DownloadRangeStatusListener {

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Logger messageLogger = Logger.getLogger("message");

    private volatile boolean downloadCompleted = false;

    protected int id;
    protected URL url; // download URL
    protected String downloadName;
    protected int size; // size of download in bytes
    protected volatile DownloadStatus status; // current status of download
    protected String transferRate; // rate of transfer
    protected ProtocolType protocolType;
    protected String description;

    protected int downloaded; // number of bytes downloaded

    protected int responseCode;
    protected int partCount;

    protected File downloadPath;
    protected File downloadRangePath;

    protected boolean resumeCapability;

    protected int connectTimeout;
    protected int readTimeout;

    protected List<DownloadRange> downloadRangeList;

    protected DownloadInfoListener downloadInfoListener;

    protected Vector<DownloadStatusListener> downloadStatusListeners;

    private final static int N_CPUS = Runtime.getRuntime().availableProcessors();

    private ScheduledExecutorService scheduler;
    private final AtomicInteger previousDownloaded = new AtomicInteger(0);

    // Constructor for download.
    public AbstractDownload(int id, URL url, String downloadName, int partCount, File downloadPath, File downloadRangePath, ProtocolType protocolType) {
        this.id = id;
        this.url = url;
        this.downloadName = downloadName;
        size = -1;
        downloaded = 0;
        status = DownloadStatus.DOWNLOADING;
        this.partCount = partCount;

        this.downloadPath = downloadPath;
        this.downloadRangePath = downloadRangePath;

        this.protocolType = protocolType;

        resumeCapability = false;

        downloadRangeList = new ArrayList<>();

        downloadStatusListeners = new Vector<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDownloadName() {
        return downloadName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DownloadStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransferRate() {
        return transferRate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProtocolType getProtocolType() {
        return protocolType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProtocolType(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDownloaded() {
        return downloaded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDownloadPath() {
        return downloadPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloadPath(File downloadPath) {
        this.downloadPath = downloadPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getDownloadRangePath() {
        return downloadRangePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloadRangePath(File downloadRangePath) {
        this.downloadRangePath = downloadRangePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getResponseCode() { // todo must go to below class
        return responseCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResumeCapability() {
        return resumeCapability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormattedSize() {
        return ConnectionUtil.roundSizeTypeFormat((float) size, SizeType.BYTE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getProgress() {
        return ((float) downloaded / size); // *100
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DownloadRange> getDownloadRangeList() {
        return downloadRangeList.isEmpty() ? Collections.emptyList() : new ArrayList<>(downloadRangeList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloadRangeList(List<DownloadRange> downloadRangeList) {
        this.downloadRangeList = downloadRangeList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        Objects.requireNonNull(downloadStatusListener, "downloadStatusListener");
        if (!downloadStatusListeners.contains(downloadStatusListener)) {
            downloadStatusListeners.addElement(downloadStatusListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void deleteDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        Objects.requireNonNull(downloadStatusListener, "downloadStatusListener");
        downloadStatusListeners.removeElement(downloadStatusListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloadInfoListener(DownloadInfoListener downloadInfoListener) {
        Objects.requireNonNull(downloadInfoListener, "downloadInfoListener");
        this.downloadInfoListener = downloadInfoListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeDownloadInfo(DownloadInfoListener downloadInfoListener) {
        Objects.requireNonNull(downloadInfoListener, "downloadInfoListener");
        if (this.downloadInfoListener.equals(downloadInfoListener))
            this.downloadInfoListener = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pause() {
        messageLogger.info("Download paused: " + downloadName);
        status = DownloadStatus.DISCONNECTING;
        notifyStatusChanged();
        for (DownloadRange downloadRange : downloadRangeList)
            downloadRange.disConnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resume() {
        messageLogger.info("Download resumed: " + downloadName);
        status = DownloadStatus.DOWNLOADING;

        notifyStatusChanged();
        if (!downloadRangeList.isEmpty()) {
            for (DownloadRange downloadRange : downloadRangeList)
                downloadRange.resume();
            startTransferRateMonitor();
        } else {
            performDownload();
        }
    }

    // Mark this download as having an error.
    protected void error() {
        messageLogger.info("Download has error: " + downloadName);
        status = DownloadStatus.ERROR;
        notifyStatusChanged();

        if (downloadInfoListener != null)
            downloadInfoListener.downloadNeedSaved(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startTransferRateMonitor() {
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = BackgroundExecutor.getScheduler();
        }

        scheduler.scheduleAtFixedRate(() -> {
            if (status == DownloadStatus.COMPLETE || status == DownloadStatus.CANCELLED || status == DownloadStatus.ERROR) {
                return;
            }

            int currentDownloaded = downloaded;
            float rate = ConnectionUtil.calculateTransferRateInUnit(
                    currentDownloaded - previousDownloaded.getAndSet(currentDownloaded),
                    1000,
                    TimeUnit.SEC);

            transferRate = ConnectionUtil.roundSizeTypeFormat(rate, SizeType.BYTE) + "/sec";
            notifyStatusChanged();
        }, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    private long previousTime = 0;
    private void setTransferRate(int readed) {
        long currentTime = System.currentTimeMillis();
        long periodTime = currentTime - previousTime;

        float differenceDownloaded = ConnectionUtil.calculateTransferRateInUnit(readed, (int) periodTime, TimeUnit.SEC); // in Byte

        // calculate differenceDownloaded
        transferRate = ConnectionUtil.roundSizeTypeFormat(differenceDownloaded, SizeType.BYTE) + "/sec";
        notifyStatusChanged();
        previousTime = currentTime;


    }

    // Start or resume downloading.
    public abstract Void performDownload();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void createDownloadRanges();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDownloadRange(DownloadRange downloadRange) {
        Objects.requireNonNull(downloadRange);
        if (!downloadRangeList.contains(downloadRange)) {
            downloadRangeList.add(downloadRange);
            downloadRange.addDownloadRangeStatusListener(this);

            if (downloadInfoListener != null) {
                downloadInfoListener.newDownloadRangeEventOccured(downloadRange);
            }
        }
    }

    protected void endOfDownload() {
        messageLogger.info("End Of Download from remote: " + downloadName);
        if (downloadCompleted) {
            System.out.println("End Of Download from in the thread: " + Thread.currentThread().getName());
        }

        List<File> files = new ArrayList<>();
        for (DownloadRange downloadRange : downloadRangeList) {
            files.add(downloadRange.getDownloadRangeFile());
        }

        // files.sort(new FileComperarto());

        messageLogger.info("Joining downloaded parts together " + downloadName);
        FileUtil.joinDownloadedParts(files, downloadPath, downloadName);

        status = DownloadStatus.COMPLETE;
        notifyStatusChanged();
        if (downloadInfoListener != null)
            downloadInfoListener.downloadNeedSaved(this);
    }

    // Notify observers that this download's status has changed.
    protected void notifyStatusChanged() {
        final List<DownloadStatusListener> listenersSnapshot;
        synchronized (this) {
            listenersSnapshot = new ArrayList<>(downloadStatusListeners);
        }

        for (DownloadStatusListener listener : listenersSnapshot) {
            SwingUtilities.invokeLater(() -> listener.downloadStatusChanged(AbstractDownload.this));
        }
    }

    @Override
    public void downloadStatusChanged(DownloadRange downloadRange, int readed) {
        updateInfo(downloadRange, readed);
        notifyStatusChanged();
    }

    private synchronized void updateInfo(DownloadRange downloadRange, int readed) {
        downloaded += readed;

        switch (downloadRange.getConnectionStatus()) {
            case DISCONNECTED:
                if (isDisConnect()) {
                    status = DownloadStatus.PAUSED;
                    if (downloadInfoListener != null)
                        downloadInfoListener.downloadNeedSaved(this);
                } else {
                    status = DownloadStatus.DISCONNECTING;
                }

                logger.info("disconnect from download .... ");
                break;
            case ERROR:
                logger.info("error again");
                status = DownloadStatus.ERROR;
                if (downloadInfoListener != null)
                    downloadInfoListener.downloadNeedSaved(this);
                break;
            case WAITING_RESPONSE:
                logger.info("WAITING_RESPONSE");
                messageLogger.info("Waiting for response: " + downloadName);
                break;
            case COMPLETED:
                logger.info("COMPLETED download Range");
                messageLogger.info("All download parts has finished: " + downloadName);
                logger.info("downloaded = " + downloaded + " size = " + size + "  downloadRange = " + downloadRange.getRangeDownloaded() + " startRange= " +
                        downloadRange.getStartRange() + " end range= " + downloadRange.getEndRange());
                if (downloaded >= size && !downloadCompleted) { // when all parts downloaded
                    downloadCompleted = true;
                    System.out.println("downloaded >= size in the thread: " + Thread.currentThread().getName() + " downloaded >= size: " + (downloaded >= size));
                    endOfDownload();
                }
                break;
            default:
        }
    }

    private boolean isDisConnect() {
        boolean state = true;
        for (DownloadRange downloadRange : downloadRangeList) {
            if (downloadRange.getConnectionStatus() != ConnectionStatus.DISCONNECTED && downloadRange.getConnectionStatus() != ConnectionStatus.COMPLETED) {
                state = false;
                break;
            }
        }
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetData() {
        downloaded = 0;
        status = DownloadStatus.DOWNLOADING;
        for (DownloadRange downloadRange : downloadRangeList) {
            downloadRange.resetData();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDownload that = (AbstractDownload) o;
        return id == that.id &&
                size == that.size &&
                downloaded == that.downloaded &&
                responseCode == that.responseCode &&
                partCount == that.partCount &&
                resumeCapability == that.resumeCapability &&
                Objects.equals(logger, that.logger) &&
                Objects.equals(messageLogger, that.messageLogger) &&
                Objects.equals(url, that.url) &&
                Objects.equals(downloadName, that.downloadName) &&
                status == that.status &&
                Objects.equals(transferRate, that.transferRate) &&
                protocolType == that.protocolType &&
                Objects.equals(description, that.description) &&
                Objects.equals(downloadPath, that.downloadPath) &&
                Objects.equals(downloadRangePath, that.downloadRangePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logger, messageLogger, id, url, downloadName, size, status, transferRate, protocolType, description, downloaded, responseCode, partCount, downloadPath, downloadRangePath, resumeCapability);
    }

    @Override
    public String toString() {
        return "AbstractDownload{" +
                "id=" + id +
                ", url=" + url +
                ", downloadName='" + downloadName + '\'' +
                ", size=" + size +
                ", status=" + status +
                ", transferRate='" + transferRate + '\'' +
                ", protocolType=" + protocolType +
                ", description='" + description + '\'' +
                ", downloaded=" + downloaded +
                ", responseCode=" + responseCode +
                ", partCount=" + partCount +
                ", downloadPath=" + downloadPath +
                ", downloadRangePath=" + downloadRangePath +
                ", resumeCapability=" + resumeCapability +
                '}';
    }
}
