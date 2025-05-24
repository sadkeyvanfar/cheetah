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

import concurrent.DownloadExecutor;
import concurrent.MyThreadFactory;
import concurrent.TimingThreadPool;
import concurrent.annotation.NotThreadSafe;
import enums.ConnectionStatus;
import gui.listener.DownloadRangeStatusListener;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * Skeletal Implementation of DownloadRange interface.
 *
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 10/17/2015
 * @see model.DownloadRange
 */
@NotThreadSafe
public abstract class AbstractDownloadRange implements DownloadRange, Callable<Void> {

    // Max size of download buffer.
    protected static final int MAX_BUFFER_SIZE = 1024; // 1024 - 4096

    protected int id;
    protected URL url; // download URL
    protected int number;
    protected int rangeSize; // size of download in bytes
    protected int rangeDownloaded; // number of bytes downloaded
    protected volatile ConnectionStatus connectionStatus; // current status of download

    protected int startRange;
    protected int endRange;

    protected File downloadRangeFile;

    protected int connectTimeout;
    protected int readTimeout;

    // to quick stop thread
    protected boolean stop = false;

    protected Thread thread = null;

    private Vector<DownloadRangeStatusListener> downloadRangeStatusListeners;

    public AbstractDownloadRange(int number, URL url, File downloadRangeFile, int startRange, int endRange) {
        //     this.id = id;
        this.number = number;
        this.url = url;
        rangeSize = -1;
        rangeDownloaded = 0; //todo for cancel act
        connectionStatus = ConnectionStatus.CONNECTING;

        this.startRange = startRange;
        this.endRange = endRange;

        this.downloadRangeFile = downloadRangeFile;

        downloadRangeStatusListeners = new Vector<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getRangeSize() {
        return rangeSize;
    }

    @Override
    public void setRangeSize(int rangeSize) {
        this.rangeSize = rangeSize;
    }

    @Override
    public int getRangeDownloaded() {
        return rangeDownloaded;
    }

    @Override
    public void setRangeDownloaded(int rangeDownloaded) {
        this.rangeDownloaded = rangeDownloaded;
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    @Override
    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    @Override
    public int getStartRange() {
        return startRange;
    }

    @Override
    public void setStartRange(int startRange) {
        this.startRange = startRange;
    }

    @Override
    public int getEndRange() {
        return endRange;
    }

    @Override
    public void setEndRange(int endRange) {
        this.endRange = endRange;
    }

    @Override
    public File getDownloadRangeFile() {
        return downloadRangeFile;
    }

    @Override
    public void setDownloadRangeFile(File downloadRangeFile) {
        this.downloadRangeFile = downloadRangeFile;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public synchronized void addDownloadRangeStatusListener(
            DownloadRangeStatusListener downloadRangeStatusListener) { // todo may be go to DownloadRange
        Objects.requireNonNull(downloadRangeStatusListener, "downloadRangeStatusListener");
        if (!downloadRangeStatusListeners.contains(downloadRangeStatusListener)) {
            downloadRangeStatusListeners.addElement(downloadRangeStatusListener);
        }
    }

    @Override
    public synchronized void deleteDownloadRangeStatusListener(DownloadRangeStatusListener downloadRangeStatusListener) {
        Objects.requireNonNull(downloadRangeStatusListener, "downloadRangeStatusListener");
        downloadRangeStatusListeners.removeElement(downloadRangeStatusListener);
    }

    // Pause this download.
    @Override
    public void disConnect() {
        if (connectionStatus == ConnectionStatus.CONNECTING || connectionStatus == ConnectionStatus.SEND_GET || connectionStatus == ConnectionStatus.RECEIVING_DATA)
            connectionStatus = ConnectionStatus.DISCONNECTING;
        stop = true;
        stateChanged(0);
    }

    @Override
    public void resume() {
        if (connectionStatus != ConnectionStatus.COMPLETED) {
            stop = false;
            download();
        }
    }

    // Mark this download as having an error.
    protected void error() {
        connectionStatus = ConnectionStatus.ERROR;
        stateChanged(0);
    }

    protected void download() {
        DownloadExecutor.getExecutor().submit(this);
    }

    @Override
    public abstract Void call();

    // Notify observers that this download's status has changed.
    protected void stateChanged(int readed) {
        for (DownloadRangeStatusListener downloadRangeStatusListener : downloadRangeStatusListeners)
            downloadRangeStatusListener.downloadStatusChanged(this, readed);
    }

    @Override
    public void resetData() {
        rangeSize = -1;
        rangeDownloaded = 0;
        connectionStatus = ConnectionStatus.CONNECTING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AbstractDownloadRange that = (AbstractDownloadRange) o;
        return id == that.id &&
                number == that.number &&
                rangeSize == that.rangeSize &&
                rangeDownloaded == that.rangeDownloaded &&
                startRange == that.startRange &&
                endRange == that.endRange &&
                Objects.equals(url, that.url) &&
                connectionStatus == that.connectionStatus &&
                Objects.equals(downloadRangeFile, that.downloadRangeFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, number, rangeSize, rangeDownloaded, connectionStatus, startRange, endRange, downloadRangeFile);
    }

    @Override
    public String toString() {
        return "AbstractDownloadRange{" +
                "id=" + id +
                ", url=" + url +
                ", number=" + number +
                ", rangeSize=" + rangeSize +
                ", rangeDownloaded=" + rangeDownloaded +
                ", connectionStatus=" + connectionStatus +
                ", startRange=" + startRange +
                ", endRange=" + endRange +
                ", downloadRangeFile=" + downloadRangeFile +
                '}';
    }
}
