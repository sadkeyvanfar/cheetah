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

package model.httpImpl;

import concurrent.annotation.NotThreadSafe;
import enums.DownloadStatus;
import enums.ProtocolType;
import model.*;
import utils.ConnectionUtil;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Http implementation of Download interface.
 *
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 10/17/2015
 * @see model.Download
 */
@NotThreadSafe
public class HttpDownload extends AbstractDownload implements Download {

    public HttpDownload(int id, URL url, String downloadName, int partCount, File downloadPath, File downloadRangePath, ProtocolType protocolType) {
        super(id, url, downloadName, partCount, downloadPath, downloadRangePath, protocolType);
    }

    @Override
    public Void performDownload() {
        HttpURLConnection connection = null;
        try {
            // Open connection to URL.
            connection = (HttpURLConnection) url.openConnection();
            if (connectTimeout != 0) // When not set
                connection.setConnectTimeout(connectTimeout);
            if (readTimeout != 0) // When not set
                connection.setReadTimeout(readTimeout);

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
                //        error();
                status = DownloadStatus.ERROR;
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                //         error();
                status = DownloadStatus.ERROR;
            }

            /* Set the size for this download if it
              hasn't been already set. */
            if (size == -1) {
                size = contentLength;
            }

            resumeCapability = responseCode == 206;

            connection.disconnect();
            notifyStatusChanged();

            //         createDownloadRanges(connection, partCount);
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

        downloadInfoListener.newDownloadInfoGot(HttpDownload.this);
        return null;
    }

    @Override
    public void createDownloadRanges() {
        int partSize= ConnectionUtil.getPartSizeOfDownload(size, partCount);
        int startRange = 0;
        int endRange = partSize - 1;

        model.DownloadRange downloadRange;

        // if connection is able to part download
        if (responseCode == 206) {
            for (int i = 0;  i < partCount; i++) {
                String partFileName = downloadName + ".00" + (i + 1);
                downloadRange = new HttpDownloadRange(i + 1, url, new File(downloadRangePath + File.separator + downloadName + File.separator + partFileName), startRange, endRange);

                addDownloadRange(downloadRange);

                downloadRange.setConnectTimeout(connectTimeout);
                downloadRange.setReadTimeout(readTimeout);

                startRange = endRange + 1;
                if (i != partCount - 2) {
                    endRange = startRange + partSize - 1;
                } else {
                    endRange = 0;
                }
            }
        } else {
            String partFileName = downloadName + ".00" + 1;
            downloadRange = new HttpDownloadRange(1, url, new File(downloadRangePath + File.separator + downloadName + File.separator + partFileName), startRange, size);
            addDownloadRange(downloadRange);
            downloadRange.setConnectTimeout(connectTimeout);
            downloadRange.setReadTimeout(readTimeout);
        }
        if (downloadInfoListener != null)
            downloadInfoListener.downloadNeedSaved(this);
    }
}
