package scatterplot;

import style.EmailColors;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import style.GlasbeyColors;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScatterPlotFactory {
    /** Produce a JPanel which shows the clustering of emailers in the company */
    public ChartPanel getEmailClusteringPlot(String csvPath) {
        Object[] parsedCsv = parseEmailCsv(csvPath);
        ArrayList<DataValue> dataValues = (ArrayList<DataValue>) parsedCsv[0];
        Map<XYDataItem, DataValue> coordinateNameMap = (Map<XYDataItem, DataValue>) parsedCsv[1];

        XYDataset dataset = createEmailTsneDataset(dataValues);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "TSNE Clustering of Emailers based on Email Involvement",
                "", "", dataset,
                PlotOrientation.HORIZONTAL, true, true, true
        );
        chart.setAntiAlias(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        // Set background to a lighter color
        plot.setBackgroundPaint(new Color(229, 235, 247));
        // Hide x and y axis
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);

        // The whole thing below this is to make a tooltip
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        XYToolTipGenerator xyToolTipGenerator = new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                XYDataItem dataItem = new XYDataItem(dataset.getX(series, item), dataset.getY(series, item));
                String name = coordinateNameMap.get(dataItem).getHeader();
                String department = (String) coordinateNameMap.get(dataItem).getInfo().get("Department");
                return String.format("<html><p><b>%s</b></p>", name) +
                        String.format("<p>%s</p>", department) +
                        "</html>";
            }
        };
        renderer.setDefaultToolTipGenerator(xyToolTipGenerator);
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, EmailColors.deptColors[i]);
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setInitialDelay(0);

        return chartPanel;
    }
    private ArrayList<String[]> parseCsv(String csvPath) {
        // Read the CSV
        ArrayList<String[]> stringList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(csvPath);
            CSVReader csvReader = new CSVReader(fileReader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                stringList.add(line);
            }
            fileReader.close();
            csvReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("IO Exception. Scatterplot may be incomplete.");
        } catch (CsvValidationException e) {
            System.out.println("CSV Validation failed.");
        }
        // Remove header
        if (stringList.size() > 0) {
            stringList.remove(0);
        }
        return stringList;
    }
    private Object[] parseEmailCsv(String csvPath) {
        ArrayList<String[]> stringList = parseCsv(csvPath);

        // Transform CSV lines into DataValue objects
        ArrayList<DataValue> parsingList = new ArrayList<>();
        Map<XYDataItem, DataValue> coordinateNameMap = new HashMap<>();
        for (String[] line:stringList) {
            Map<String, Object> info = Map.of(
                    "Department", line[3],
                    "DeptId", Integer.parseInt(line[2])
            );

            double x = Double.parseDouble(line[4]);
            double y = Double.parseDouble(line[5]);
            DataValue dataValue = new DataValue(x, y, line[1], info);
            parsingList.add(dataValue);
            coordinateNameMap.put(new XYDataItem(x, y), dataValue);
        }

        return new Object[] {parsingList, coordinateNameMap};
    }
    private XYDataset createEmailTsneDataset(ArrayList<DataValue> dataValues) {
        // Creates the dataset required by JFreeChart for Email TSNEs
        XYSeriesCollection dataset = new XYSeriesCollection();

        ArrayList<Integer> seenDeptIds = new ArrayList<>();
        Map<Integer, XYSeries> xySeries = new HashMap<>();
        for (DataValue dataValue:dataValues) {
            Integer deptId = (int) dataValue.getInfo().get("DeptId");
            String dept = (String) dataValue.getInfo().get("Department");
            if (!seenDeptIds.contains(deptId)) {
                seenDeptIds.add(deptId);
                xySeries.put(deptId, new XYSeries(dept));
            }
            xySeries.get(deptId).add(new XYDataItem(dataValue.getX(), dataValue.getY()));
        }
        for (XYSeries xy:xySeries.values()) {
            dataset.addSeries(xy);
        }
        return dataset;
    }

    /** Produce a JPanel which shows the clustering of articles by words they contain */
    public ChartPanel getArticleClusteringPlot(String csvPath) {
        Map<XYDataItem, ArticleData> parsedCsv = parseArticleCsv(csvPath);

        XYDataset dataset = createArticleTsneDataset(parsedCsv);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "TSNE Clustering of News Articles after TF-IDF Vectorization",
                "", "", dataset,
                PlotOrientation.HORIZONTAL, true, true, true
        );
        chart.setAntiAlias(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        // Set background to lighter color
        plot.setBackgroundPaint(new Color(229, 235, 247));
        // Hide x and y axis
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);

        // The whole thing below this is to make a tooltip
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        XYToolTipGenerator xyToolTipGenerator = new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                XYDataItem dataItem = new XYDataItem(dataset.getX(series, item), dataset.getY(series, item));
                ArticleData articleData = parsedCsv.get(dataItem);
                String title = articleData.getTitle();
                String publication = articleData.getPublication();
                String date = articleData.getDate();
                String filename = articleData.getFilename();
                return String.format("<html><p><b>%s</b></p>", title) +
                        String.format("<p>%s</p>", publication) +
                        String.format("<p>%s</p>", date) +
                        String.format("<p>filename: %s</p>", filename) +
                        "</html>";
            }
        };
        renderer.setDefaultToolTipGenerator(xyToolTipGenerator);
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, GlasbeyColors.colors[i]);
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setInitialDelay(0);

        return chartPanel;
    }

    /**
     * Parse the article_tsne.csv file.
     *
     * @return A {@code Map<XYDataItem, ArticleData>} object where the XYDataItem is the position
     * of the article on the TSNE plot.
     */
    private Map<XYDataItem, ArticleData> parseArticleCsv(String csvPath) {
        ArrayList<String[]> stringList = parseCsv(csvPath);

        Map<XYDataItem, ArticleData> dataMap = new HashMap<>();
        for (String[] line:stringList) {
            String filename = line[0];
            String publication = line[1];
            int publicationId = Integer.parseInt(line[2]);
            String title = line[3];
            String date = line[4];
            double x = Double.parseDouble(line[5]);
            double y = Double.parseDouble(line[6]);

            XYDataItem xyDataItem = new XYDataItem(x, y);
            ArticleData articleData = new ArticleData(title, publication, publicationId, date, filename);

            dataMap.put(xyDataItem, articleData);
        }
        return dataMap;
    }

    /** Creates a dataset, grouped by publication, for visualization */
    private XYDataset createArticleTsneDataset(Map<XYDataItem, ArticleData> parsedCsv) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        ArrayList<Integer> seenPubIds = new ArrayList<>();
        Map<Integer, XYSeries> xySeries = new HashMap<>();
        for (Map.Entry<XYDataItem, ArticleData> entry : parsedCsv.entrySet()) {
            int pubId = entry.getValue().getPublicationId();
            String pub = entry.getValue().getPublication();
            if (!seenPubIds.contains(pubId)) {
                seenPubIds.add(pubId);
                xySeries.put(pubId, new XYSeries(pub));
            }
            xySeries.get(pubId).add(entry.getKey());
        }
        for (XYSeries xy : xySeries.values()) {
            dataset.addSeries(xy);
        }
        return dataset;
    }
}
