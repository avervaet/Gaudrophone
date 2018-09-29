package gui;

import Controller.PlayerTemplate;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UIActionListener implements ActionListener {

    private MainWindow mainWindow;
    private DrawingPanel drawingPanel;

    public UIActionListener(MainWindow mainWindow, DrawingPanel drawingPanel){
        this.mainWindow = mainWindow;
        this.drawingPanel = drawingPanel;
    }

    public void actionPerformed(ActionEvent e) { ;
        Object obj = e.getSource();
        if (obj instanceof JMenuItem) {
            menuItemActionPerformed(obj);
        } else if (obj instanceof  JToggleButton){
            leftMenuActionPerformed(obj);
        } else if (obj instanceof  JButton){
            buttonActionPerformed(obj);
        }
    }

    private void buttonActionPerformed(Object obj){
        JButton button = (JButton) obj;

        switch (button.getName()){
            case "btnStop":
                stopAction();
                break;
            case "btnPlay":
                playAction();
                break;
            case "btnValider":
                drawingPanel.validateNewComponent();
                break;
            case "btnParcourir":
                parcourirAction();
                break;
        }
    }

    private void menuItemActionPerformed(Object obj)
    {
        JMenuItem menuItem = (JMenuItem) obj;

        switch (menuItem.getName()) {
            case "menuItemQuit":
                System.exit(0);
                break;
            case "setGuitarSound":
                mainWindow.controller.changeInstrumentTimbre(PlayerTemplate.GUITAR);
                break;
            case "setViolonSound":
                mainWindow.controller.changeInstrumentTimbre(PlayerTemplate.VIOLIN);
                break;
            case "setFluteSound":
                mainWindow.controller.changeInstrumentTimbre(PlayerTemplate.FLUTE);
                break;
            case "setPianoSound":
                mainWindow.controller.changeInstrumentTimbre(PlayerTemplate.PIANO);
                break;
            case "menuItemSaveInstrument":
                saveInstrumentAction();
                break;
            case "menuItemLoadInstrument":
                loadInstrumentAction();
                break;
            case "menuItemLoadPartition":
                loadPartitionAction();
                break;
            case "menuItemShowKeyCaracteristics":
                showKeyCaracteristicsAction();
                break;
            case "menuItemDeactivatePartition":
                drawingPanel.instrumentChanged();
                break;
            case "menuItemActivateMetronome":
                drawingPanel.setIsMetronomeActive(!drawingPanel.isMetronomeActive());
                break;
            case "setDefaultTemplate":
                mainWindow.controller.loadGabaritDefault();
                break;
            case "setGuitarTemplate":
                mainWindow.controller.loadGabaritGuitar();
                break;
        }
    }

    private void playAction(){
        mainWindow.setSelectedMenuOption(MainWindow.MenuOptions.play);
        mainWindow.getLeftMenuButtonGroup().clearSelection();
        drawingPanel.menuOptionChanged();
        drawingPanel.requestFocusInWindow();
        for (Component component : mainWindow.leftMenuComponents){
            component.setEnabled(false);
        }
    }

    private void stopAction(){
        if (mainWindow.getSelectedMenuOption() == MainWindow.MenuOptions.play){
            mainWindow.setSelectedMenuOption(null);
            mainWindow.controller.releaseAll();
            drawingPanel.stopPlaying();
            for (Component component : mainWindow.leftMenuComponents){
                component.setEnabled(true);
            }
        }
    }

    private void leftMenuActionPerformed(Object obj){
        JToggleButton jToggleButton = (JToggleButton) obj;

        switch (jToggleButton.getName()){

            case "btnCreateKey":
                this.mainWindow.setSelectedMenuOption(MainWindow.MenuOptions.create_key);
                break;
            case "btnDeleteComponent":
                this.mainWindow.setSelectedMenuOption(MainWindow.MenuOptions.delete_component);
                break;
            case "btnCreatePedal":
                this.mainWindow.setSelectedMenuOption(MainWindow.MenuOptions.create_pedal);
                break;
            case "btnSelect":
                this.mainWindow.setSelectedMenuOption(MainWindow.MenuOptions.select);
                break;
            case "btnCreateVertex":
                this.mainWindow.setSelectedMenuOption(MainWindow.MenuOptions.create_vertex);
                break;
            case "btnSearch":
                this.mainWindow.setSelectedMenuOption(MainWindow.MenuOptions.search);
                break;

        }
        drawingPanel.menuOptionChanged();
    }

    private void parcourirAction(){
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("fichiers wav", "wav");
        fc.setFileFilter(filter);
        fc.setDialogTitle("Choisissez un fichier audio.");
        int returnVal = fc.showOpenDialog(mainWindow.soundInnerPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            mainWindow.audioFileField.setText(file.getPath());
            mainWindow.audioFilePath = file.getPath();
        } else {
            mainWindow.audioFileField.setText(null);
            mainWindow.audioFilePath = null;
        }
    }

    private void saveInstrumentAction ()
    {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("fichier de sauvegarde", "ser");
        fc.setFileFilter(filter);
        fc.setDialogTitle("Choisissez un fichier de sauvegarde");
        int returnValue = fc.showSaveDialog(mainWindow.getDrawingPanel());

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fname = file.getAbsolutePath();

            if(!fname.endsWith(".ser") ) {
                file = new File(fname + ".ser");
            }
            mainWindow.controller.saveInstrument(file.getPath());
        }
    }

    private void loadInstrumentAction ()
    {
        //Todo, va peut-être être envoyé au controller pour la désérialization.
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("fichier de sauvegarde", "ser");
        fc.setFileFilter(filter);
        fc.setDialogTitle("Choisissez un fichier de sauvegarde");
        int returnValue = fc.showOpenDialog(mainWindow.getDrawingPanel());

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            mainWindow.controller.loadInstrument(file.getPath());
            drawingPanel.instrumentChanged();
            drawingPanel.repaint();
        }
    }

    private void loadPartitionAction ()
    {
        final JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("fichiers txt", "txt");
        fc.setFileFilter(filter);
        fc.setDialogTitle("Choisissez une partition.");
        int returnVal = fc.showOpenDialog(drawingPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            drawingPanel.loadPartition(file);
        }

    }

    private void showKeyCaracteristicsAction ()
    {
        drawingPanel.setShowComponentsDetails(!drawingPanel.getShowComponentsDetails());
        drawingPanel.repaint();
    }

    private void aboutAction ()
    {
        //Todo
    }
}
