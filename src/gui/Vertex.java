package gui;

import java.awt.*;
import java.io.Serializable;

public class Vertex  implements Serializable{

    private final Color COLOR = Color.black;
    private final double RADIUS = 0.5;

    private boolean isVisible = false;
    private float x;
    private float y;

    public Vertex(float x, float y){
        this.setX(x);
        this.setY(y);
    }

    public Color getCOLOR() {return COLOR;}

    public double getRADIUS() {
        return RADIUS;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Point convertToPoint(Dimension dimensions){
        Point point = new Point();

        point.x = (int)((((double)this.getX()) / 100) * dimensions.getWidth());
        point.y = (int)((((double)this.getY()) / 100) * dimensions.getHeight());

        return point;
    }

}
