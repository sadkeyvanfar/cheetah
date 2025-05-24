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

import comparator.FileNameComparator;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/15/2015
 */
public class FileUtil {

    public static void joinDownloadedParts(List<File> files, File path, String fileName) {
        File outputFile = new File(path + File.separator + outputFile(new File(path + File.separator + fileName)));

        FileOutputStream fos;
        FileInputStream fis;
        byte[] fileBytes;
        int bytesRead;
        try {
            fos = new FileOutputStream(outputFile,true);
            for (File file : files) {
                fis = new FileInputStream(file);
                fileBytes = new byte[(int) file.length()];
                bytesRead = fis.read(fileBytes, 0,(int)  file.length());
                assert(bytesRead == fileBytes.length);
                assert(bytesRead == (int) file.length());
                fos.write(fileBytes);
                fos.flush();
                fileBytes = null;
                fis.close();
                fis = null;
            }
            fos.close();
            fos = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Specify real name for file to download that was not repeated in download folder
     * @param file full file name with extension like index.html
     * @return
     */
    public static String outputFile(File file) {
        if (!file.exists()) {
            return file.getName();
        } else {
            String filenameAndExtension = file.getName();
            String fileName = FilenameUtils.getBaseName(filenameAndExtension);
            String extension = FilenameUtils.getExtension(filenameAndExtension);
            if (fileName.lastIndexOf('~') != -1) {
                String firstPartFileName = fileName.substring(0, fileName.lastIndexOf('~'));
                int lastPartFileName = Integer.parseInt(fileName.substring(fileName.lastIndexOf('~') + 1)) + 1;
                return outputFile(new File(file.getParent() + File.separator + firstPartFileName + "~" + lastPartFileName + "." + extension));
            } else {
                return outputFile(new File(file.getParent() + File.separator + fileName + "~1" + "." + extension));
            }

        }
    }



    public static File outputFile(List<File> files, FileNameComparator fileNameComparator) {
        List<File> outputFiles = new ArrayList<>();
        for (File file : files) {
            File outputFile = new File(file.getParentFile() + File.separator + outputFile(file));
            outputFiles.add(outputFile);
        }

        return Collections.max(outputFiles, fileNameComparator);
    }

    // Get file name portion of URL.
    public static String getFileName(File file) {
        String fileName = file.toString();
        return fileName.substring(fileName.lastIndexOf(File.separator) + 1);
    }

    /**
     * Specify real name for file to download that was not repeated in download manger
     * @param fileName full file name with extension like index.html
     * @param allFileNames
     * @return
     */
    public static String countableFileName(String fileName, List<String> allFileNames) {
        if (!allFileNames.contains(fileName)) {
            return fileName;
        } else {
            String baseNameName = FilenameUtils.getBaseName(fileName);
            String extension = FilenameUtils.getExtension(fileName);
            if (baseNameName.lastIndexOf('~') != -1) {
                String firstPartFileName = baseNameName.substring(0, baseNameName.lastIndexOf('~'));
                int lastPartFileName = Integer.parseInt(baseNameName.substring(baseNameName.lastIndexOf('~') + 1)) + 1;
                return countableFileName(firstPartFileName + "~" + lastPartFileName + "." + extension, allFileNames);
            } else {
                return countableFileName(baseNameName + "~1" + "." + extension, allFileNames);
            }

        }
    }
}
