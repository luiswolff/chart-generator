/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ------------------------
 * FastScatterPlotDemo.java
 * ------------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: FastScatterPlotDemo.java,v 1.13 2004/04/26 19:11:54 taqua Exp $
 *
 * Changes (from 29-Oct-2002)
 * --------------------------
 * 29-Oct-2002 : Added standard header and Javadocs (DG);
 * 12-Nov-2003 : Enabled zooming (DG);
 *
 */

package org.jfree.chart.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A demo of the fast scatter plot.
 *
 */
public class FastScatterPlotDemo extends ApplicationFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -2100215028047859073L;

    /**
     * Creates a new fast scatter plot demo.
     *
     * @param title  the frame title.
     * @param source 
     */
    public FastScatterPlotDemo(final String title, String source) {

        super(title);
        ValueAxis timeAxis = new DateAxis("X");
        timeAxis.setLowerMargin(0.02); // reduce the default margins
        timeAxis.setUpperMargin(0.02);
        NumberAxis yAxis = new NumberAxis("y");
        yAxis.setAutoRangeIncludesZero(false);

        XYPlot plot = new XYPlot(dataset(source), timeAxis, yAxis, null);

        XYURLGenerator urlGenerator = null;
        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setURLGenerator(urlGenerator);
        plot.setRenderer(renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        // force aliasing of the rendered content..
        //chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final ChartPanel panel = new ChartPanel(chart, true);
        /*panel.setPreferredSize(new java.awt.Dimension(500, 270));
        //      panel.setHorizontalZoom(true);
        //    panel.setVerticalZoom(true);
        panel.setMinimumDrawHeight(10);
        panel.setMaximumDrawHeight(2000);
        panel.setMinimumDrawWidth(20);
        panel.setMaximumDrawWidth(2000);
        */
        setContentPane(panel);

    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************

    private TimePeriodValuesCollection dataset(String source) {
        Map<String, TimePeriodValues> data = new TreeMap<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try (BufferedReader in = new BufferedReader(new FileReader(source))) {
            in.lines().skip(1L).map(l -> l.split("\t")).forEach(l -> {
                String name = l[0];
                Date date;
                try {
                    date = df.parse(l[1]);
                } catch (ParseException e) {
                    return;
                }
                double value = Double.parseDouble(l[2]);

                TimePeriodValues values = data.computeIfAbsent(name, TimePeriodValues::new);
                values.add(new Minute(date), value);
            });
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        data.values().forEach(dataset::addSeries);
        return dataset;
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final FastScatterPlotDemo demo = new FastScatterPlotDemo("Fast Scatter Plot Demo", args[0]);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
