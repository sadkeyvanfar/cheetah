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

import enums.DownloadStatus;
import enums.ProtocolType;
import gui.listener.DownloadInfoListener;
import gui.listener.DownloadStatusListener;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * An abstract representation of download file.
 *
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 10/17/2015
 * @see model.DownloadRange
 */
public interface Download {

    /**
     * Gets the id of this {@code Download}.
     *
     * @return the id of this {@code Download}
     */
    int getId();

    /**
     * @param id
     */
    void setId( int id);

    /**
     * Gets the url of this {@code Download}.
     *
     * @return the url of this {@code Download}
     */
    URL getUrl();


    /**
     * @param url
     */
    void setUrl(URL url);

    /**
     * Gets the downloadName of this {@code Download}.
     *
     * @return the downloadName of this {@code Download}
     */
    String getDownloadName();

    /**
     * @param downloadName
     */
    void setDownloadName(String downloadName);

    /**
     * Gets the size of this {@code Download}.
     *
     * @return the size of this {@code Download}
     */
    int getSize(); //todo should be long

    /**
     * @param size
     */
    void setSize(int size);

    /**
     * Gets the downloadStatus of this {@code Download}.
     *
     * @return the downloadStatus of this {@code Download}
     */
    DownloadStatus getStatus();

    /**
     * @param status
     */
    void setStatus(DownloadStatus status);

    /**
     * Gets the transferRate of this {@code Download}.
     *
     * @return the transferRate of this {@code Download}
     */
    double getTransferRate();

    String getTransferRateFormatted();

    /**
     * Gets the protocolType of this {@code Download}.
     *
     * @return the protocolType of this {@code Download}
     */
    ProtocolType getProtocolType();

    /**
     * @param protocolType
     */
    void setProtocolType(ProtocolType protocolType);

    /**
     * Gets the description of this {@code Download}.
     *
     * @return the description of this {@code Download}
     */
    String getDescription();

    /**
     * @param description
     */
    void setDescription(String description);

    /**
     * Gets amount of downloaded of this {@code Download}.
     *
     * @return amount of downloaded of this {@code Download}
     */
    int getDownloaded(); //todo should be long

    /**
     * @param downloaded
     */
    void setDownloaded(int downloaded);

    /**
     * Gets the path of this {@code Download}.
     *
     * @return the path of this {@code Download}
     */
    File getDownloadPath();

    /**
     * @param downloadPath
     */
    void setDownloadPath(File downloadPath);

    /**
     * Gets the rangePath of this {@code Download}.
     *
     * @return the rangePath of this {@code Download}
     */
    File getDownloadRangePath();

    /**
     * @param downloadRangePath
     */
    void setDownloadRangePath(File downloadRangePath);

    /**
     * Gets the responseCode of this {@code Download}.
     *
     * @return the responseCode of this {@code Download}
     */
    int getResponseCode();

    /**
     * @return
     */
    boolean isResumeCapability();

    /**
     * Gets the formattedSize of this {@code Download}.
     *
     * @return the formattedSize of this {@code Download}
     */
    String getFormattedSize();

    /**
     * Gets the progress of this {@code Download}.
     *
     * @return the progress of this {@code Download}
     */
    float getProgress();

    /**
     * Gets the connectTimeout of this {@code Download}.
     *
     * @return the connectTimeout of this {@code Download}
     */
    int getConnectTimeout();

    /**
     * @param connectTimeout
     */
    void setConnectTimeout(int connectTimeout);

    /**
     * Gets the readTimeout of this {@code Download}.
     *
     * @return the readTimeout of this {@code Download}
     */
    int getReadTimeout();

    /**
     * @param readTimeout
     */
    void setReadTimeout(int readTimeout);

    /**
     * Adds an DownloadStatusListener to the set of downloadStatusListeners for this object, provided
     * that it is not the same as some DownloadStatusListener already in the set.
     * The order in which notifications will be delivered to multiple
     * DownloadDialogListeners is not specified. See the class comment.
     *
     * @param   downloadStatusListener   an DownloadStatusListener to be added.
     * @throws NullPointerException   if the parameter o is null.
     */
    void addDownloadStatusListener(DownloadStatusListener downloadStatusListener);

    /**
     * Deletes an DownloadStatusListener from the set of downloadStatusListeners of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   downloadStatusListener   the DownloadStatusListener to be deleted.
     */
    void deleteDownloadStatusListener(DownloadStatusListener downloadStatusListener);

    /**
     * Gets list of downloadRanges of this {@code Download}.
     *
     * @return list of downloadRanges of this {@code Download}
     */
    List<DownloadRange> getDownloadRangeList();

    /**
     * @param downloadRangeList
     */
    void setDownloadRangeList(List<DownloadRange> downloadRangeList);

    /**
     * @param downloadInfoListener
     */
    void setDownloadInfoListener(DownloadInfoListener downloadInfoListener);

    /**
     * @param downloadInfoListener
     */
    void removeDownloadInfo(DownloadInfoListener downloadInfoListener);

    /**
     * Pause this download.
     */
    void pause();

    /**
     * Resume this download.
     */
    void resume();

    /**
     * createDownloadRanges
     */
    void createDownloadRanges();

    /**
     * add a new downloadRange if not in downloadRangeList
     * @param downloadRange
     */
    void addDownloadRange(DownloadRange downloadRange);

    /**
     * reset data of download for redownload
     */
    void resetData();

    /**
     * startTransferRate
     */
    void startTransferRateMonitor();
}
