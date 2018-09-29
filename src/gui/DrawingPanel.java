package gui;

import Controller.Controller;
import Controller.Drawer.Drawer;
import Domain.InstrumentComponent.*;
import Domain.Observer.IObserver;
import Domain.Playable.Partition;
import Domain.Searcher.SearchOption;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DrawingPanel extends JLayeredPane implements MouseListener, IObserver {
    private Controller controller;
    private MainWindow mainWindow;
    private Shape newShape;
    private IInstrumentComponent selectedComponent;
    private MainWindow.MenuOptions selectedOption;
    private Vertex componentTranslateFrom;
    private Vertex vertexToMove;
    private boolean showComponentsDetails = false;
    private JPanel searchPanel;
    private JScrollPane partitionPanel;
    private JPanel scrollPanePanel = new JPanel();
    private JTextPane partitionTxtPane = new JTextPane();
    private ButtonGroup rbtnGroup;
    private JRadioButton rbtnRaccourci;
    private JRadioButton rbtnNom;
    private JTextField txtRecherche;
    private int metronomeBps;
    private JSlider jSliderMetronome;
    private boolean isMetronomeActive;
    private JPanel metronomeSubPanel;
    private JLabel lblMetronome;
    private Partition playingPartition = null;
    private IInstrumentComponent playingComponent = null;
    private List<IInstrumentComponent> searchResult = new ArrayList<>();
    private MainWindow.MenuOptions createKeyMode = MainWindow.MenuOptions.create_key;
    private MainWindow.MenuOptions createPedalMode = MainWindow.MenuOptions.create_pedal;
    private MainWindow.MenuOptions selectMode = MainWindow.MenuOptions.select;
    private MainWindow.MenuOptions playMode = MainWindow.MenuOptions.play;
    private MainWindow.MenuOptions createVertexMode = MainWindow.MenuOptions.create_vertex;
    private MainWindow.MenuOptions deleteMode = MainWindow.MenuOptions.delete_component;
    private MainWindow.MenuOptions searchMode = MainWindow.MenuOptions.search;

    protected List<Vertex> newComponentVertexList = new ArrayList<Vertex>();

    public DrawingPanel(MainWindow mainWindow){
        this.setOpaque(true);
        this.setBackground(Color.LIGHT_GRAY);
        this.mainWindow = mainWindow;
        addMouseListener(this);
        createSearchSubPanel();
        createPartitionSubPanel();
        createMetronomeSubPanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (mainWindow != null && mainWindow.controller != null)
        {
            if (mainWindow.controller.getInstrument()!= null){
                this.repaint();
                Drawer mainDrawer = new Drawer(mainWindow.controller, this.getSize());
                mainDrawer.draw(g);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Vertex clickPosition = convertPixelsToVertex(e.getPoint());
        selectedOption = mainWindow.getSelectedMenuOption();
        IInstrumentComponent componentAtPos = controller.componentClicked(clickPosition);

        if (selectedOption == playMode){
            if (componentAtPos != null){
                playingComponent = componentAtPos;
                controller.componentPressed(playingComponent);
            }
        } else if (selectedOption == selectMode) {
            if (selectedComponent == componentAtPos){
                vertexToMove = controller.vertexClicked(clickPosition);
            } else {
                if (selectedComponent != null){
                    selectedComponent.getShape().setHighLighted(false);
                    mainWindow.btnCreateVertex.setEnabled(false);
                }
                selectedComponent = componentAtPos;
                mainWindow.btnCreateVertex.setEnabled(true);
                selectedComponentChanged();

                if (selectedComponent != null){
                    selectedComponent.getShape().setHighLighted(true);
                }
            }
            componentTranslateFrom = clickPosition;
        }

        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Vertex releasePosition = convertPixelsToVertex(e.getPoint());

        if (selectedOption == playMode){
            if (playingComponent != null){
                controller.componentReleased(playingComponent);
                playingComponent = null;
            }
        } else if (selectedOption == createKeyMode || selectedOption == createPedalMode){
            if (newComponentVertexList.isEmpty()){
                newShape = new Shape();
                newShape.addVertex(releasePosition);
                newComponentVertexList.add(releasePosition);
            }
            else{
                if (newShape.getVertexList().size() >= 3){
                    if (!newShape.isInBoundaries(releasePosition)){
                        newShape.insertVertex(releasePosition);
                        newComponentVertexList.add(releasePosition);
                    }
                } else {
                    newShape.addVertex(releasePosition);
                    newComponentVertexList.add(releasePosition);
                }

                if (newShape.getVertexList().size() >= 3){
                    mainWindow.validateButton.setEnabled(true);
                }

            }
        } else if (selectedOption == selectMode){
            if (selectedComponent != null){
                if (vertexToMove == null) {
                    if (componentTranslateFrom != null){
                        if (wasComponentMoved(componentTranslateFrom, releasePosition)){
                            float toX = releasePosition.getX() - componentTranslateFrom.getX();
                            float toY = releasePosition.getY() - componentTranslateFrom.getY();
                            selectedComponent.getShape().move(toX, toY);
                            componentTranslateFrom = null;
                        }
                    }
                }
                else {
                    if (wasComponentMoved(vertexToMove, releasePosition)){
                        vertexToMove.setX(releasePosition.getX());
                        vertexToMove.setY(releasePosition.getY());
                        vertexToMove = null;
                    }
                }
            }
        } else if (selectedOption == deleteMode){
            controller.deleteComponentAt(releasePosition);
        } else if (selectedOption == createVertexMode){
            if (!selectedComponent.getShape().isInBoundaries(releasePosition)){
                selectedComponent.getShape().insertVertex(releasePosition);
            }
        }

        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    private void createSearchSubPanel(){
        searchPanel = new JPanel();
        searchPanel.setBounds(0,0,(mainWindow.getWidth()/100)*20, (mainWindow.getHeight()/100)*5);
        searchPanel.setBorder(new LineBorder(Color.black,2));
        searchPanel.setBackground(new Color(0,0,0,75));
        searchPanel.setOpaque(true);

        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel rechercheLabel = new JLabel("Recherche: ");
        txtRecherche = new JTextField("");
        txtRecherche.setPreferredSize(new Dimension((searchPanel.getWidth()/100)*40, searchPanel.getHeight() - 15));
        JLabel nomLabel = new JLabel("Nom/Note:");
        rbtnNom = new JRadioButton();
        rbtnNom.setSelected(true);
        JLabel raccourciLabel = new JLabel("Raccourci:");
        rbtnRaccourci = new JRadioButton();
        rbtnGroup = new ButtonGroup();
        rbtnGroup.add(rbtnNom);
        rbtnGroup.add(rbtnRaccourci);

        searchPanel.add(rechercheLabel);
        searchPanel.add(txtRecherche);
        searchPanel.add(nomLabel);
        searchPanel.add(rbtnNom);
        searchPanel.add(raccourciLabel);
        searchPanel.add(rbtnRaccourci);

        txtRecherche.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SearchOption searchOption;
                if (rbtnNom.isSelected()){
                    searchOption = SearchOption.NAME;
                } else {
                    searchOption = SearchOption.HOTKEY;
                }
                Runnable doAssist = new Runnable() {
                    @Override
                    public void run() {
                        unHighLightSearchResult(searchResult);
                        if (txtRecherche.getText().length() > 0){
                            searchResult = controller.search(txtRecherche.getText(), searchOption);
                            highlightSearchResult(searchResult);
                        }
                    }
                };
                SwingUtilities.invokeLater(doAssist);
            }
        });

        searchPanel.setVisible(false);
        this.add(searchPanel,0);
    }

    private void createMetronomeSubPanel(){
        metronomeSubPanel = new JPanel();
        metronomeSubPanel.setBounds(0,partitionPanel.getHeight(),(mainWindow.getWidth()/100)*10, (mainWindow.getHeight()/100)*5);
        metronomeSubPanel.setBorder(new LineBorder(Color.black,2));
        metronomeSubPanel.setBackground(new Color(0,0,0,75));
        metronomeSubPanel.setLayout(new GridLayout(2,1));
        metronomeSubPanel.setOpaque(true);

        lblMetronome = new JLabel("Métronome(Bps 0-120): ");
        jSliderMetronome = new JSlider(0,120,0);

        jSliderMetronome.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                metronomeBps = jSliderMetronome.getValue();
                lblMetronome.setText("Métronome(Bps 0-120): " + Integer.toString(metronomeBps));
            }
        });
        metronomeSubPanel.add(lblMetronome);
        metronomeSubPanel.add(jSliderMetronome);
        metronomeSubPanel.setVisible(false);
        this.add(metronomeSubPanel, 0);
    }


    private void createPartitionSubPanel(){
        partitionPanel = new JScrollPane(scrollPanePanel);
        partitionPanel.setBounds(0,0,(mainWindow.getWidth()/100)*40, (mainWindow.getHeight()/100)*15);
        partitionPanel.setBorder(new LineBorder(Color.black,2));
        partitionPanel.getViewport().setBackground(new Color(0,0,0,75));
        partitionPanel.setOpaque(false);
        partitionPanel.setVisible(true);
        partitionTxtPane = new JTextPane();
        partitionTxtPane.setFont(new Font("Courier New", Font.PLAIN, 17));;
        scrollPanePanel.add(partitionTxtPane);
        partitionPanel.setVisible(false);
        this.add(partitionPanel,1);
    }

    private void highlightSearchResult(List<IInstrumentComponent> searchResult){
        for (IInstrumentComponent component : searchResult){
            component.getShape().setHighLighted(true);
        }
    }

    protected void loadPartition(File file){
        playingPartition = controller.parsePartition(file);
        partitionPanel.setVisible(true);
        partitionInitialDisplay();
        playingPartition.registerObserver(this);
    }

    private void partitionInitialDisplay(){
        ArrayList<String> lines = controller.getMusicSheet();
        String msgToDisplay = "";

        for (int i = 0; i < lines.size();i++){
            msgToDisplay += lines.get(i) + "\n";
        }
        partitionTxtPane.setText(msgToDisplay);
        partitionTxtPane.setEditable(false);
    }

    //not working and out of time.
    private void highLightPartitionAt(int index, String text){
        partitionTxtPane.setText("");
        String lines[] = text.split("\\r?\\n");
        String prefix = "";
        String suffix = "";
        String textToHighLight = "";

        for (int i = 0; i < lines.length; i++){
            if (lines[i].substring(0,2) != "//"){
                String notes[] = lines[i].split("\\s+");

                if (index < notes.length){
                    textToHighLight = notes[index] + " ";

                    for (int j = 0; j < index ; j++){
                        prefix += notes[j] + " ";
                    }

                    for (int j = index + 1; j < notes.length ; j++){
                        suffix += notes[j] + " ";
                    }

                    appendToPane(partitionTxtPane, prefix, Color.black);
                    appendToPane(partitionTxtPane, textToHighLight, Color.yellow);
                    appendToPane(partitionTxtPane, suffix + "\n", Color.BLACK);
                    prefix = "";
                    suffix = "";
                    textToHighLight = "";
                }
            } else {
                appendToPane(partitionTxtPane, lines[i] + "\n", Color.BLACK);
            }
        }
    }

    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }



    private void unHighLightSearchResult(List<IInstrumentComponent> searchResult){
        for (IInstrumentComponent component : searchResult){
            component.getShape().setHighLighted(false);
        }
    }

    private boolean wasComponentMoved(Vertex mouseStartingPoint, Vertex mouseReleasePoint){
        float fromX = mouseStartingPoint.getX();
        float toX = mouseReleasePoint.getX();
        float fromY = mouseStartingPoint.getY();
        float toY = mouseReleasePoint.getY();
        double distance = Math.hypot(fromX-toX, fromY-toY);
        return distance > 0.5;
    }

    private void selectedComponentChanged(){
        mainWindow.showCustomizationPanel(selectedComponent != null);
        if (selectedComponent != null){
            char hotkey = (char)selectedComponent.getHotKey();
            if (selectedComponent instanceof Key){
                mainWindow.audioFileField.setText(((Key) selectedComponent).getSoundClipPath());
                mainWindow.audioFilePath = ((Key) selectedComponent).getSoundClipPath();
                if (((Key) selectedComponent).getSoundClipPath() == null){
                    mainWindow.octaveSlider.setValue(((Key) selectedComponent).getNote().getOctave());
                    mainWindow.noteSlider.setValue(((Key) selectedComponent).getNote().getSemitone());
                }
                mainWindow.parcourirButton.setEnabled(true);
            } else {
                mainWindow.parcourirButton.setEnabled(false);
                mainWindow.noteSlider.setEnabled(false);
                mainWindow.octaveSlider.setEnabled(false);
                mainWindow.audioFileField.setText("");
                mainWindow.octaveSlider.setValue(0);
                mainWindow.noteSlider.setValue(0);
            }
            mainWindow.redSlider.setValue(selectedComponent.getShape().getColor().getRed());
            mainWindow.greenSlider.setValue(selectedComponent.getShape().getColor().getGreen());
            mainWindow.blueSlider.setValue(selectedComponent.getShape().getColor().getBlue());
            mainWindow.validateButton.setEnabled(true);
            mainWindow.hotkeyField.setText(String.valueOf(hotkey));
            mainWindow.componentNameField.setText(selectedComponent.getName());
        }
    }

    public void resetSelectedComponent(){
        if (selectedComponent != null){
            selectedComponent.getShape().setHighLighted(false);
            selectedComponent = null;
        }
        mainWindow.resetCustomizationComponents();
    }

    public void setController(Controller controller){
        this.controller = controller;
        addKeyListener(new KeyHandler(controller));
    }

    private Vertex convertPixelsToVertex(Point point){
        Vertex newVertex;
        double vertexX;
        double vertexY;

        vertexX = (point.getX() / this.getWidth()) * 100;
        vertexY = (point.getY() / this.getHeight()) * 100;

        newVertex = new Vertex((float)vertexX, (float)vertexY);

        return newVertex;
    }

    public void validateNewComponent(){
        MainWindow.MenuOptions selectedOption = mainWindow.getSelectedMenuOption();
        String name = mainWindow.componentNameField.getText();
        int octave = mainWindow.octaveSlider.getValue();
        int note = mainWindow.noteSlider.getValue();
        char hotkey;
        Color color = mainWindow.getConfiguredColor();
        if (mainWindow.hotkeyField.getText().length() != 0){
            hotkey =  mainWindow.hotkeyField.getText().charAt(0);
        } else {
            hotkey = '#';
        }

        if (selectedOption == createKeyMode){
            newShape.setColor(color);
            if (mainWindow.audioFilePath == null){
                controller.addKey(newShape, name, hotkey, octave, note);
            } else {
                controller.addKey(newShape, name, hotkey, mainWindow.audioFilePath);
            }

            newShape = null;
            newComponentVertexList.clear();
            mainWindow.resetCustomizationComponents();
        } else if (selectedOption == createPedalMode) {
            newShape.setColor(color);
            controller.addPedal(newShape, name, hotkey);
        } else if (selectedOption == selectMode) {
            controller.changeComponentValues(selectedComponent,name,octave,note,hotkey, mainWindow.audioFilePath);
            selectedComponent.getShape().setColor(color);
        }

        this.repaint();
    }

    public void menuOptionChanged(){
        selectedOption = mainWindow.getSelectedMenuOption();
        boolean isCreatePedal = selectedOption != createPedalMode;
        if (selectedOption == createKeyMode){
            mainWindow.validateButton.setEnabled(false);
            mainWindow.showCustomizationPanel(true);
        } else if (selectedOption == createPedalMode){
            mainWindow.showCustomizationPanel(true);
        } else if (selectedOption == playMode){
            if (isMetronomeActive()){
                controller.startMetronome(metronomeBps);
            }

            if (playingPartition != null){
                playingPartition.startPlaying();
            }

        } else {
            mainWindow.validateButton.setEnabled(true);
            mainWindow.showCustomizationPanel(false);
        }

        if (selectedOption != createVertexMode){
            mainWindow.btnCreateVertex.setEnabled(false);
            if (selectedComponent != null){
                selectedComponent.getShape().setHighLighted(false);
                resetSelectedComponent();
            }
        }

        mainWindow.parcourirButton.setEnabled(isCreatePedal);
        mainWindow.noteSlider.setEnabled(isCreatePedal);
        mainWindow.octaveSlider.setEnabled(isCreatePedal);
        searchPanel.setVisible(selectedOption == searchMode);
        searchResult.clear();
        rbtnNom.setSelected(true);
        txtRecherche.setText("");
        controller.unHightLightAll();
        newComponentVertexList.clear();
        componentTranslateFrom = null;
        vertexToMove = null;
        newShape = null;
        mainWindow.resetCustomizationComponents();

        this.repaint();

    }

    public void instrumentChanged(){
        this.playingPartition = null;
        partitionTxtPane.setText("");
        partitionPanel.setVisible(false);
    }

    public List<Vertex> getNewComponentVertexList() {
        return newComponentVertexList;
    }

    public void setShowComponentsDetails(boolean showCaracs){
        this.showComponentsDetails = showCaracs;
    }

    public boolean getShowComponentsDetails(){ return this.showComponentsDetails; }

    public IInstrumentComponent getSelectedInstrumentComponent(){
        return this.selectedComponent;
    }


    public void setIsMetronomeActive(boolean isActive){
        this.isMetronomeActive = isActive;
        this.metronomeSubPanel.setVisible(isActive);
    }

    public boolean isMetronomeActive() { return isMetronomeActive; }

    protected void stopPlaying(){
        if (isMetronomeActive()){
            controller.stopMetronome();
        }
        menuOptionChanged();
        if (playingPartition != null){
            playingPartition.stopPlaying();
        }
    }


    @Override
    public void sendmsg(int data){
        //highLightPartitionAt(data, partitionTxtPane.getText());
    }
}
