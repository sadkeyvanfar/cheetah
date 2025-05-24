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
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/10/2015
 */

// This class renders a JProgressBar in a table cell.
class ProgressRenderer extends JProgressBar implements TableCellRenderer {

    private final NumberFormat percentFormat;

    // Constructor for ProgressRenderer.
    public ProgressRenderer(int min, int max) {
        super(min, max);
        percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(2);
    }

    /* Returns this JProgressBar as the renderer
       for the given table cell. */
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column)
    {
        setValue((int) ((Float) value * 100) + 1);
        setString(percentFormat.format(((Float) value).floatValue()));
        return this;
    }
}
