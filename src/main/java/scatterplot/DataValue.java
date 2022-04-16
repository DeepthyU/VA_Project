package scatterplot;

import java.util.Map;

public class DataValue {
    private String header;
    private Map<String, Object> info;
    private double x;
    private double y;

    public DataValue(double x, double y, String header) {
        this.x = x;
        this.y = y;
        this.header = header;

    }
    public DataValue(double x, double y, String header, Map<String, Object> info) {
        this.x = x;
        this.y = y;
        this.header = header;
        this.info = info;
    }
    public String getHeader() {
        return this.header;
    }
    public Map<String, Object> getInfo() {
        return this.info;
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
