package gui;


import javafx.util.Pair;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;


public class Shape implements Serializable {
    private Color color = Color.black;
    private boolean isHightLighted;
    private List<Vertex> vertexList = new ArrayList<Vertex>();

    public Shape(){

    }

    public Shape(List<Vertex> vertexList){
        this.vertexList = vertexList;
    }

    public int getVertexCount(){
        return this.vertexList.size();
    }

    public List<Vertex> getVertexList() {
        return vertexList;
    }

    public void addVertex(Vertex newVertex){
        this.vertexList.add(newVertex);
    }

    public void removeVertex(int index){
        this.vertexList.remove(index);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isHighLighted() {
        return highLighted;
    }

    public void setHighLighted(boolean highLighted) {
        this.highLighted = highLighted;
    }

    private boolean highLighted;

    //https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
    public boolean isInBoundaries(Vertex point){
        Vertex[] vertices = new Vertex[this.getVertexList().size()];
        vertices = this.getVertexList().toArray(vertices);
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
            if ((vertices[i].getY() > point.getY()) != (vertices[j].getY() > point.getY()) &&
                    (point.getX() < (vertices[j].getX() - vertices[i].getX()) * (point.getY() - vertices[i].getY()) / (vertices[j].getY()-vertices[i].getY()) + vertices[i].getX())) {
                result = !result;
            }
        }
        if (!result){
            result = wasAVertexClicked(point);
        }

        return result;
    }

    public boolean wasAVertexClicked(Vertex point){
        boolean result = false;
        for (Vertex vertex : this.getVertexList()){
            float dx = Math.abs(point.getX()-vertex.getX());
            float dy = Math.abs(point.getY()-vertex.getY());

            if (dx < vertex.getRADIUS() && dy < vertex.getRADIUS()){
                result = true;
            }
        }
        return result;
    }

    public Vertex getVertexAt(Vertex point){
        Vertex vertexToReturn = null;
        for (Vertex vertex : this.getVertexList()){
            float dx = Math.abs(point.getX()-vertex.getX());
            float dy = Math.abs(point.getY()-vertex.getY());

            if (dx < vertex.getRADIUS() && dy < vertex.getRADIUS()){
                vertexToReturn = vertex;
            }
        }
        return vertexToReturn;
    }

    //https://www.developpez.net/forums/d537417/general-developpement/algorithme-mathematiques/mathematiques/distance-d-point-segment/
    private double distanceToSegment(Vertex vectorStart, Vertex vectorEnd, Vertex point) {

        float vsX = vectorStart.getX();
        float vsY = vectorStart.getY();
        float veX = vectorEnd.getX();
        float veY = vectorEnd.getY();
        float pX = point.getX();
        float pY = point.getY();

        if (vsX==veX && vsY==veY) return distance(vectorStart,point);

        float sx=veX-vsX;
        float sy=veY-vsY;

        float ux=pX-vsX;
        float uy=pY-vsY;

        float dp=sx*ux+sy*uy;
        if (dp<0) return distance(vectorStart,point);

        float sn2 = sx*sx+sy*sy;
        if (dp>sn2) return distance(vectorEnd,point);

        double ah2 = dp*dp / sn2;
        float un2=ux*ux+uy*uy;
        return Math.sqrt(un2-ah2);
    }

    private double distance(Vertex p1, Vertex p2) {
        float d2 = (p2.getX()-p1.getX())*(p2.getX()-p1.getX())+(p2.getY()-p1.getY())*(p2.getY()-p1.getY());
        return Math.sqrt(d2);
    }

    public void insertVertex(Vertex point){
        int lowestDistanceIndex = 0;
        double lowestDistance = 0;
        double distance = 0;
        if (!this.isInBoundaries(point)){
            for (int i = 0; i < vertexList.size(); i++){
                if (i == vertexList.size() - 1){
                    distance = distanceToSegment(vertexList.get(i), vertexList.get(0), point);
                } else {
                    distance = distanceToSegment(vertexList.get(i), vertexList.get(i+1), point);
                }

                if (i == 0){
                    lowestDistance = distance;
                } else {
                    if (distance < lowestDistance){
                        lowestDistanceIndex = i;
                        lowestDistance = distance;
                    }
                }
            }
            vertexList.add(lowestDistanceIndex + 1, point);
        }
    }

    public void move(float x, float y){
        for (Vertex vertex : vertexList){
            vertex.setX(vertex.getX()+x);
            vertex.setY(vertex.getY()+y);
        }
    }

}
