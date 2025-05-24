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

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 11/4/2015
 */
public class LookAndFeel {

    public static void setLaf(String laf) {
        try {
            switch (laf) {
                case "Metal":
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    break;
                case "Nimbus":
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    break;
                case "System":
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static void changeLaf(JFrame frame, String laf, Dimension topFrameDimension) {
        switch (laf) {
            case "Metal":
                updateLAF(UIManager.getCrossPlatformLookAndFeelClassName());
                break;
            case "Nimbus":
                updateLAF("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                break;
            case "System":
                updateLAF(UIManager.getSystemLookAndFeelClassName());
                break;
            case "":
        }
        frame.setSize(topFrameDimension);
    }



    // Assuming that value is the class name of the new look-and-feel, here is the snippet to update all windows and sub-components:
    public static void updateLAF(String value) {
        if (UIManager.getLookAndFeel().getClass().getName().equals(value)) {
            return;
        }
        try {
            UIManager.setLookAndFeel(value);
            for (Frame frame : Frame.getFrames()) {
                updateLAFRecursively(frame);
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void updateLAFRecursively(Window window) {
        for (Window childWindow : window.getOwnedWindows()) {
            updateLAFRecursively(childWindow);
        }
        SwingUtilities.updateComponentTreeUI(window);
        window.pack();
    }


}
