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

package gui;

import javax.swing.*;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/10/2015
 */
public class Application {

    private final static String NAME_OF_CUI = "Cheetah";

    public static void main( String[] args ) {
        String path = System.getProperty("user.home") + "/Downloads/Cheetah Downloaded Files/Logs";

        System.setProperty("log.file.path", path);

        // enable anti-aliased text:
        System.setProperty("awt.useSystemAAFontSettings","lcd"); // on or lcd
        System.setProperty("swing.aatext", "true");
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                System.setProperty("swing.crossplatformlaf", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                break;
            }
        }

        SwingUtilities.invokeLater(() -> new DownloadManagerGUI(NAME_OF_CUI));

    }
}
