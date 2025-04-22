package hacp.histofact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dimension {
    private double width;
    private double length;
    private double height;

    public Dimension(double width, double length, double height) {
        this.width = width;
        this.length = length;
        this.height = height;
    }
    public Dimension() {}
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format("%.1f × %.1f × %.1f cm", length, width, height);
    }
}