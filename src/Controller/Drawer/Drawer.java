package Controller.Drawer;

import Controller.Controller;
import Domain.InstrumentComponent.IInstrumentComponent;
import Domain.InstrumentComponent.Key;
import gui.DrawingPanel;
import gui.MainWindow;
import gui.Vertex;
import java.awt.*;
import java.util.List;

public class Drawer {
    private Controller controller;
    private Dimension initialDimension;
    private DrawingPanel drawingPanel;
    private int[] polyXs;
    private int[] polyYs;
    private int radius;
    private Point point;
    private Color highlightColor = new Color(92,229,255);

    public Drawer(Controller controller, Dimension initialDimension){
        this.controller = controller;
        this.initialDimension = initialDimension;
        this.drawingPanel = controller.getMainWindow().getDrawingPanel();
    }

    public void draw(Graphics g){
        drawComponents(g);
        drawNewComponentVertex(g);
        drawSelectedInstrumentVertex(g);
        drawInstrumentName(g);
    }

    private void drawComponents(Graphics g){
        String detailsString = "";
        Polygon componentPolygon;
        int centerX;
        int centerY;

        if (controller.getComponents().size() > 0){
            for (IInstrumentComponent instrumentComponent : controller.getComponents()){
                componentPolygon = createPolygon(instrumentComponent);

                g.setColor(instrumentComponent.getShape().getColor());
                g.fillPolygon(componentPolygon);

                if (drawingPanel.getShowComponentsDetails()){
                    centerX = ((int)componentPolygon.getBounds().getCenterX()) - (componentPolygon.getBounds().width/10);
                    centerY = ((int)componentPolygon.getBounds().getCenterY()) - (componentPolygon.getBounds().height/10);

                    detailsString = createDetailsString(instrumentComponent);

                    g.setColor(highlightColor);
                    g.setFont(new Font ("Serif ", Font.BOLD, 12));
                    for (String line : detailsString.split("\n")){
                        g.drawString(line,centerX,centerY += g.getFontMetrics().getHeight());
                    }
                }

                if (instrumentComponent.getShape().isHighLighted()){
                    Graphics2D borders = (Graphics2D) g;
                    borders.setColor(highlightColor);
                    borders.setStroke(new BasicStroke(3));
                    borders.drawPolygon(componentPolygon);
                }
            }
        }
    }

    private Polygon createPolygon(IInstrumentComponent component){
        Polygon componentPolygon = new Polygon();
        componentPolygon.npoints = component.getShape().getVertexList().size();
        componentPolygon.xpoints = getPolyXs(component.getShape().getVertexList());
        componentPolygon.ypoints = getPolyYs(component.getShape().getVertexList());
        return componentPolygon;
    }

    private String createDetailsString(IInstrumentComponent component){
        String detailsString = "";
        detailsString += "Nom: " + component.getName() + "\n";
        detailsString += "Raccourci: " + (char)component.getHotKey() + "\n";
        if(component instanceof Key) {
            if (((Key) component).getSoundClipPath() == null){
                detailsString += "Octave: " + ((Key) component).getNote().getOctave() + "\n";
                detailsString += "Note: " + ((Key) component).getNote().getNoteName() + "\n";
            } else {
                detailsString += "Audio: " +((Key) component).getSoundClipPath() + "\n";
            }
        }
        return detailsString;
    }

    private void drawSelectedInstrumentVertex(Graphics g){
        if (controller.getMainWindow().getSelectedMenuOption() != MainWindow.MenuOptions.delete_component) {
            if (drawingPanel.getSelectedInstrumentComponent() != null){
                for (Vertex vertex : drawingPanel.getSelectedInstrumentComponent().getShape().getVertexList()){
                    drawVertex(g, vertex);
                }
            }
        } else {
            for(IInstrumentComponent component : controller.getInstrument().getTouches()){
                for (Vertex vertex : component.getShape().getVertexList()){
                    drawVertex(g, vertex);
                }
            }
        }
    }

    private void drawNewComponentVertex(Graphics g){
        MainWindow.MenuOptions option = controller.getMainWindow().getSelectedMenuOption();
        if ((option == MainWindow.MenuOptions.create_key || option == MainWindow.MenuOptions.create_pedal) && !drawingPanel.getNewComponentVertexList().isEmpty()){
            for (Vertex vertex: drawingPanel.getNewComponentVertexList()){
                drawVertex(g, vertex);
            }
        }
    }

    private void drawVertex(Graphics g, Vertex vertex){
        radius = (int)((vertex.getRADIUS() / 100) * initialDimension.getWidth());
        point = vertex.convertToPoint(initialDimension);
        g.setColor(vertex.getCOLOR());
        g.fillOval((int)point.getX()-radius,(int)point.getY()-radius, radius*2, radius*2);
        Graphics2D border = (Graphics2D) g;
        border.setStroke(new BasicStroke(3));
        border.setColor(highlightColor);
        border.drawOval((int)point.getX()-radius,(int)point.getY()-radius, radius*2, radius*2);
    }

    private int[] getPolyXs(List<Vertex> vertexList){
        polyXs = new int[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++){
            polyXs[i] = vertexList.get(i).convertToPoint(initialDimension).x;
        }

        return polyXs;
    }

    private int[] getPolyYs(List<Vertex> vertexList){
        polyYs = new int[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++){
            polyYs[i] = vertexList.get(i).convertToPoint(initialDimension).y;
        }

        return polyYs;
    }

    private void drawInstrumentName(Graphics g){
        String instrumentName = controller.getInstrument().getName();
        g.setColor(Color.black);
        g.setFont(new Font ("Serif ", Font.BOLD, 20));
        int textWidth = g.getFontMetrics().stringWidth(instrumentName);
        g.drawString(instrumentName, (int)(g.getClipBounds().getWidth()-textWidth)/2, 25);
    }


}
