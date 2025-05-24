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

package model.httpsImpl;

import concurrent.annotation.NotThreadSafe;
import enums.DownloadStatus;
import enums.ProtocolType;
import model.AbstractDownload;
import model.Download;
import utils.ConnectionUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Https implementation of Download interface.
 *
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 10/26/2015
 * @see model.Download
 */
@NotThreadSafe
public class HttpsDownload extends AbstractDownload implements Download {

    public HttpsDownload(int id, URL url, String downloadName, int partCount, File downloadPath, File downloadRangePath, ProtocolType protocolType) {
        super(id, url, downloadName, partCount, downloadPath, downloadRangePath, protocolType);
    }

    @Override
    public Void performDownload() {
        HttpsURLConnection connection = null;
        try {
            // Open connection to URL.
            connection = (HttpsURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range", "bytes=0-");

            // Connect to server.
            connection.setRequestMethod("HEAD");
            connection.connect();

            responseCode = connection.getResponseCode();

            String raw = connection.getHeaderField("Content-Disposition");
            if(raw != null && raw.indexOf('=') != -1) {
                downloadName = raw.split("=")[1]; //getting value after '='
            }

            // Make sure response code is in the 200 range.
            if (responseCode / 100 != 2) {
                error();
                status = DownloadStatus.ERROR;
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
                status = DownloadStatus.ERROR;
            }

            /* Set the size for this download if it
              hasn't been already set. */
            if (size == -1) {
                size = contentLength;
            }

            resumeCapability = responseCode == 206;
            notifyStatusChanged();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            error();
        } catch (IOException e) {
            e.printStackTrace();
            error();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        downloadInfoListener.newDownloadInfoGot(HttpsDownload.this); // todo may be download
        return null;
    }

    @Override
    public void createDownloadRanges() {
        int partSize = ConnectionUtil.getPartSizeOfDownload(size, partCount);
        int startRange = 0;
        int endRange;

        model.DownloadRange downloadRange;

        if (responseCode == 206) {
            for (int i = 0; i < partCount; i++) {
                // Ensure correct endRange calculation
                if (i < partCount - 1) {
                    endRange = startRange + partSize - 1;
                } else {
                    endRange = size - 1; // Last part should end at total file size
                }

                String partFileName = downloadName + ".00" + String.format("%02d", i + 1);
                File rangeFile = new File(downloadRangePath + File.separator + downloadName + File.separator + partFileName);

                downloadRange = new HttpsDownloadRange(i + 1, url, rangeFile, startRange, endRange);
                addDownloadRange(downloadRange);
                downloadRange.resume();

                startRange = endRange + 1;
            }
        } else {
            // Single-threaded fallback
            String partFileName = downloadName + ".001";
            File rangeFile = new File(downloadRangePath + File.separator + downloadName + File.separator + partFileName);

            downloadRange = new HttpsDownloadRange(1, url, rangeFile, startRange, size - 1);
            addDownloadRange(downloadRange);
            System.out.println("Staring 1 RANGE in the thread: " + Thread.currentThread().getName());

            downloadRange.resume();
        }

        if (downloadInfoListener != null) {
            downloadInfoListener.downloadNeedSaved(this);
        }
        startTransferRateMonitor();
    }

}
