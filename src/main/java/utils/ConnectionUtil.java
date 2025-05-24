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

package utils;

import enums.SizeType;
import enums.TimeUnit;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/13/2015
 */
public class ConnectionUtil {

    public static void printHttpURLConnectionHeaders(HttpURLConnection httpURLConnection) {
        Map<String, List<String>> s = httpURLConnection.getHeaderFields();
        Set<Map.Entry<String, List<String>>> set = s.entrySet();
        for (Map.Entry<String, List<String>> ss : set) {
            System.out.println("key: " + ss.getKey() + "  value: " + ss.getValue());
        }
    }

    // Get file name portion of URL.
    public static String getFileName(URL url) {
        String fileName = url.getFile();
        if (fileName.lastIndexOf('?') != -1) {
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('?'));
        } else {
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        }

        return URLDecoder.decode(fileName);
    }

    public static String getRealFileName(URL url) throws IOException {
        String fileName;
        URLConnection conn = url.openConnection();
        String raw = conn.getHeaderField("Content-Disposition");
        if(raw != null && raw.indexOf('=') != -1) {
            fileName = raw.split("=")[1]; //getting value after '='
        } else {
            // fall back to random generated file name?
            String fileNameURL = url.getFile();
            if (fileNameURL.lastIndexOf('?') != -1) {
                fileName = fileNameURL.substring(fileNameURL.lastIndexOf('/') + 1, fileNameURL.lastIndexOf('?'));
            } else {
                fileName = fileNameURL.substring(fileNameURL.lastIndexOf('/') + 1);
            }
            String contentType = conn.getHeaderField("Content-type");
            if (contentType != null && !contentType.equals("") && !contentType.equals("application/octet-stream")) {
                try {
                    String type = contentType.substring(contentType.indexOf('/') + 1, contentType.indexOf(';'));
                    if (type != null && !type.equals("")) {
                        fileName = FilenameUtils.getBaseName(fileName) + '.' + type;
                    }
                } catch (Exception e) {}
            }
        }
        return fileName;
    }

    // Get file extension portion of file of URL.
    public static String getFileExtension(URL url) {
        String fileName = url.getFile();
        return FilenameUtils.getExtension(fileName);
    }

    public static String roundSizeTypeFormat(float transferRate, SizeType sizeType) {
        if (transferRate < 999) {
            String transferRateFormated = String.format("%.3f", transferRate);
            switch (sizeType) {
                case BYTE:
                    return transferRateFormated + " B";
                case KILOBYTE:
                    return transferRateFormated + " KB";
                case MEGABYTE:
                    return transferRateFormated + " MB";
                case TERABYTE:
                    return transferRateFormated + " TB";
                default:
                    return String.valueOf(transferRate);
            }
        } else {
            return roundSizeTypeFormat(transferRate / 1024, SizeType.values()[sizeType.ordinal() + 1]);
        }
    }

    public static int getPartSizeOfDownload(int downloadSize, int partCount) {
        return (int) Math.ceil((double) downloadSize / partCount); // avoids truncating
    }

    public static float calculateTransferRateInUnit(float differenceDownloaded, int longTime, TimeUnit timeUnit) {

        int unitTime;
        switch (timeUnit) {
            case SEC:
                unitTime = 1000;
                break;
            case MIN:
                unitTime = 60 * 1000;
                break;
            case HOUR:
                unitTime = 60 * 60 * 1000;
                break;
            default:
                unitTime = 1000;
        }

        return (differenceDownloaded * unitTime) / longTime;
    }
}
