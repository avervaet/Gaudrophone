package gui;


import Controller.Controller;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    public enum MenuOptions {
        create_key,
        create_pedal,
        create_vertex,
        delete_component,
        select,
        search,
        play;
    }

    private final Dimension SIDEMENU_BUTTON_DIMENSIONS = new Dimension(70,70);

    protected Controller controller;
    protected JPanel soundInnerPanel;
    protected JPanel appearenceInnerPanel;
    protected JSlider octaveSlider;
    protected JSlider noteSlider;
    protected JTextField componentNameField;
    protected JButton parcourirButton;
    protected List<Component> leftMenuComponents = new ArrayList<Component>();
    protected JSlider redSlider;
    protected JSlider greenSlider;
    protected JSlider blueSlider;
    protected JTextField hotkeyField;
    protected JButton validateButton;
    protected JToggleButton btnCreateVertex;
    protected JTextField audioFileField;
    protected String audioFilePath;
    private DrawingPanel drawingPanel;
    private UIActionListener uiActionListener;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JPanel leftPanel;
    private JPanel menuPanelTop;
    private JPanel menuPanelBottom;
    private ButtonGroup menuButtonGroup;
    private JTabbedPane bottomMenuTabbedPane;
    private String[] noteLabelValues = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"};
    private JPanel configuredColorPreview;
    private MenuOptions selectedMenuOption;

    public MainWindow() {
        this.controller = null;
        selectedMenuOption = null;
        this.mainPanel = new JPanel();
        this.setTitle("Gaudrophone");
        this.setIconImage(new ImageIcon(this.getClass().getResource("/Gaudrophone_Icon_PlaceHolder.png")).getImage());
        this.setContentPane(this.mainPanel);
        mainPanel.setLayout(new BorderLayout());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1080,925));
        this.setVisible(true);

        setLookAndFeel();
        setUIComponents();
        createMenuBar();

    }

    public void setController(Controller controller){
        this.controller = controller;
        addKeyListener(new KeyHandler(controller));
        drawingPanel.setController(controller);
    }

    public void setSelectedMenuOption(MenuOptions newSelectedMenuOption)
    {
        this.selectedMenuOption = newSelectedMenuOption;
    }

    public DrawingPanel getDrawingPanel(){
        return this.drawingPanel;
    }

    public MenuOptions getSelectedMenuOption()
    {
        return this.selectedMenuOption;
    }

    private void setLookAndFeel()
    {
        try
        {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception e2)
            {

            }
        }
    }

    private void setUIComponents()
    {
        drawingPanel = new DrawingPanel(this);
        uiActionListener = new UIActionListener(this, drawingPanel);
        createLeftMenu();
        mainPanel.add(drawingPanel, BorderLayout.CENTER);
    }

    private void createLeftMenu(){

        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(175, 150));
        leftPanel.setBackground(Color.darkGray);
        leftPanel.setBorder(new LineBorder(Color.black));

            menuPanelTop = new JPanel();
            menuPanelTop.setBackground(Color.darkGray);

            menuPanelTop.setLayout(new GridLayout(4,2,5,5));

            menuButtonGroup = new ButtonGroup();
            createJToggleButton(null,"btnSelect", "/selectionner.png", menuPanelTop,menuButtonGroup, "Sélectionner");
            createJToggleButton(null, "btnDeleteComponent", "/supprimer.png", menuPanelTop, menuButtonGroup, "Supprimer");
            createJToggleButton(null, "btnCreateKey", "/touche.png", menuPanelTop, menuButtonGroup, "Créer une touche");
            createJToggleButton("Pédale", "btnCreatePedal", null, menuPanelTop, menuButtonGroup, "Créer une pédale");

            btnCreateVertex = new JToggleButton();
            btnCreateVertex.setIcon(new ImageIcon(this.getClass().getResource("/newVertex.png")));
            btnCreateVertex.setToolTipText("Ajouter un nouveau sommet à la touche sélectionnée.");
            btnCreateVertex.setName("btnCreateVertex");
            btnCreateVertex.setEnabled(false);
            btnCreateVertex.addActionListener(this.uiActionListener);
            menuPanelTop.add(btnCreateVertex);
            menuButtonGroup.add(btnCreateVertex);
            createJToggleButton("Recherche", "btnSearch", null, menuPanelTop, menuButtonGroup, "Rechercher une touche");
            JPanel playStopPanel = new JPanel();
            playStopPanel.setLayout(new GridLayout(1,2,5,5));
            playStopPanel.setPreferredSize(new Dimension(60,150));
            playStopPanel.setBackground(Color.darkGray);

            createButton(null, "btnPlay", "/play.png", playStopPanel, "Démarrer le mode 'Live'");
            createButton(null, "btnStop", "/stop.png", playStopPanel, "Sortir du mode 'Live'");
            leftPanel.add(playStopPanel);
            menuPanelBottom = new JPanel();
            menuPanelBottom.setPreferredSize(new Dimension(175,(this.getSize().height/2)));
            menuPanelBottom.setBackground(Color.darkGray);
            bottomMenuTabbedPane = createTabbedPane(new Dimension(175,(this.getSize().height/2)));
            menuPanelBottom.add(bottomMenuTabbedPane);

        leftPanel.add(menuPanelTop, BorderLayout.NORTH);
        leftPanel.add(menuPanelBottom, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);
    }

    public ButtonGroup getLeftMenuButtonGroup(){
        return this.menuButtonGroup;
    }

    private void createButton(String text, String name, String iconPath, JPanel parentPanel, String toolTip){
        JButton jButton = new JButton(text);
        jButton.setPreferredSize(SIDEMENU_BUTTON_DIMENSIONS);
        jButton.setName(name);
        jButton.addActionListener(this.uiActionListener);

        if (iconPath != null)
        {
            jButton.setIcon(new ImageIcon(this.getClass().getResource(iconPath)));
        }

        parentPanel.add(jButton);

    }

    private void createJToggleButton(String text, String name, String iconPath, JPanel parentPanel, ButtonGroup group, String toolTip){
        JToggleButton jToggleButton = new JToggleButton(text);
        jToggleButton.setPreferredSize(SIDEMENU_BUTTON_DIMENSIONS);
        jToggleButton.setName(name);
        jToggleButton.addActionListener(this.uiActionListener);

        if (iconPath != null)
        {
            jToggleButton.setIcon(new ImageIcon(this.getClass().getResource(iconPath)));
        }

        group.add(jToggleButton);
        parentPanel.add(jToggleButton);
        leftMenuComponents.add(jToggleButton);
    }

    private JTabbedPane createTabbedPane(Dimension dimension){
        JTabbedPane newJTabbedPane = new JTabbedPane();
        newJTabbedPane.setMinimumSize(dimension);
        newJTabbedPane.setMaximumSize(dimension);
        newJTabbedPane.setPreferredSize(dimension);

        newJTabbedPane.addTab("Son", null, createSoundInnerPanel(dimension));
        newJTabbedPane.addTab("Apparence", null, createAppearenceInnerPanel(dimension));
        newJTabbedPane.setSelectedIndex(0);

        return newJTabbedPane;
    }

    private JPanel createSoundInnerPanel(Dimension innerPanelsSize) {
        soundInnerPanel = new JPanel();
        soundInnerPanel.setSize(new Dimension(125,(this.getSize().height/2)));
        soundInnerPanel.setBackground(Color.GRAY);
        soundInnerPanel.setLayout(new GridLayout(12,1, 10,10));
        soundInnerPanel.setName("soundInnerPanel");

        JLabel nameLabel = createJLabel("Nom: ");
        soundInnerPanel.add(nameLabel);
        componentNameField = new JTextField();
        soundInnerPanel.add(componentNameField);


        JLabel hotkeyLabel = createJLabel("Raccourci: ");
        soundInnerPanel.add(hotkeyLabel);
        hotkeyField = new JTextField();
        hotkeyField.getDocument().addDocumentListener(new DocumentListener() {
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
                if (hotkeyField.getText().length() > 1){
                    Runnable doAssist = new Runnable() {
                        @Override
                        public void run() {
                            String character = hotkeyField.getText().substring(1);
                            hotkeyField.setText(character);
                        }
                    };
                    SwingUtilities.invokeLater(doAssist);

                }
            }
        });
        soundInnerPanel.add(hotkeyField);

        JLabel octaveLabel = createJLabel("Octave: ");
        soundInnerPanel.add(octaveLabel);
        octaveSlider = createJSlider(octaveLabel, octaveLabel.getText(),2,8, 2);
        soundInnerPanel.add(octaveSlider);

        JLabel noteLabel = createJLabel("Note: ");
        soundInnerPanel.add(noteLabel);
        noteSlider = createJSlider(noteLabel, noteLabel.getText(),0,11, 0);
        soundInnerPanel.add(noteSlider);

        soundInnerPanel.add(createJLabel("Fichier Audio:"));
        audioFileField = new JTextField();
        audioFileField.setEnabled(false);
        audioFileField.setText(null);
        soundInnerPanel.add(audioFileField);

        validateButton = new JButton("Valider");
        validateButton.setName("btnValider");
        validateButton.addActionListener(this.uiActionListener);
        soundInnerPanel.add(validateButton);

        parcourirButton = new JButton("Parcourir...");
        parcourirButton.setName("btnParcourir");
        parcourirButton.addActionListener(this.uiActionListener);
        soundInnerPanel.add(parcourirButton);

        for (Component component : soundInnerPanel.getComponents()){
            component.setVisible(false);
        }

        return soundInnerPanel;
    }


    private JPanel createAppearenceInnerPanel(Dimension innerPanelsSize) {
        appearenceInnerPanel = new JPanel();
        appearenceInnerPanel.setSize(innerPanelsSize);
        appearenceInnerPanel.setBackground(Color.GRAY);
        appearenceInnerPanel.setLayout(new GridLayout(12,1,10,10));

        JLabel colorPickerLabel = createJLabel("Couleur:");
        appearenceInnerPanel.add(colorPickerLabel);

        JLabel redLabel = createJLabel("Rouge: ");
        appearenceInnerPanel.add(redLabel);
        redSlider = createJSlider(redLabel, redLabel.getText(), 0,255,0);
        appearenceInnerPanel.add(redSlider);

        JLabel greenLabel = createJLabel("Vert: ");
        appearenceInnerPanel.add(greenLabel);
        greenSlider = createJSlider(greenLabel, greenLabel.getText(), 0,255,0);
        appearenceInnerPanel.add(greenSlider);

        JLabel blueLabel = createJLabel("Bleu: ");
        appearenceInnerPanel.add(blueLabel);
        blueSlider = createJSlider(blueLabel, blueLabel.getText(), 0,255,0);
        appearenceInnerPanel.add(blueSlider);

        configuredColorPreview = new JPanel();
        configuredColorPreview.setBackground(this.getConfiguredColor());
        appearenceInnerPanel.add(configuredColorPreview);

        for (Component component : appearenceInnerPanel.getComponents()){
            component.setVisible(false);
        }

        return appearenceInnerPanel;
    }

    public void refreshColorPreview(){
        configuredColorPreview.setBackground(this.getConfiguredColor());
    }

    public void resetCustomizationComponents(){
        octaveSlider.setValue(octaveSlider.getMinimum());
        noteSlider.setValue(noteSlider.getMinimum());
        componentNameField.setText("");
        redSlider.setValue(0);
        greenSlider.setValue(0);
        blueSlider.setValue(0);
        hotkeyField.setText("");
        audioFileField.setText("");
        validateButton.setEnabled(false);
        audioFilePath = null;
    }

    public void showCustomizationPanel(boolean isVisible){
        for (Component component : soundInnerPanel.getComponents()){
            component.setVisible(isVisible);
        }

        for (Component component : appearenceInnerPanel.getComponents()){
            component.setVisible(isVisible);
        }
    }

    public Color getConfiguredColor(){
       return new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue());
    }

    private JSlider createJSlider(JLabel associatedLabel, String associatedLabeltext, int minValue, int maxValue, int value){
        JSlider newSlider = new JSlider(minValue, maxValue, value);
        switch (associatedLabeltext){
            case "Octave: ":
                associatedLabel.setText(associatedLabeltext + Integer.toString(minValue));
                newSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        associatedLabel.setText(associatedLabeltext + newSlider.getValue());
                    }
                });
                break;
            case "Note: ":
                associatedLabel.setText(associatedLabeltext + noteLabelValues[0]);
                newSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        associatedLabel.setText(associatedLabeltext + noteLabelValues[newSlider.getValue()]);
                    }
                });
                break;
            case "Rouge: ":
                associatedLabel.setText(associatedLabeltext + 0);
                newSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        associatedLabel.setText(associatedLabeltext + redSlider.getValue());
                        refreshColorPreview();
                    }
                });
                break;
            case "Vert: ":
                associatedLabel.setText(associatedLabeltext + 0);
                newSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        associatedLabel.setText(associatedLabeltext + greenSlider.getValue());
                        refreshColorPreview();
                    }
                });
                break;
            case "Bleu: ":
                associatedLabel.setText(associatedLabeltext + 0);
                newSlider.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        associatedLabel.setText(associatedLabeltext + blueSlider.getValue());
                        refreshColorPreview();
                    }
                });
                break;
        }

        return newSlider;
    }

    private JLabel createJLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 15));

        return label;
    }

    private void createMenuBar()
    {
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fichier = new JMenu("Fichier");
            JMenu ouvrir = new JMenu("Ouvrir");
                ouvrir.add(createJMenuItem("Instrument", "menuItemLoadInstrument", null));
                ouvrir.add(createJMenuItem("Partition", "menuItemLoadPartition", null));
        fichier.add(ouvrir);
        JMenu son = new JMenu("Son");
            son.add(createJMenuItem("Guitar", "setGuitarSound", null));
            son.add(createJMenuItem("Piano", "setPianoSound", null));
            son.add(createJMenuItem("Violon", "setViolonSound", null));
            son.add(createJMenuItem("Flute", "setFluteSound", null));

        JMenu gabarit = new JMenu("Gabarits");
            gabarit.add(createJMenuItem("Défault", "setDefaultTemplate",null));
            gabarit.add(createJMenuItem("Guitar", "setGuitarTemplate",null));

        fichier.add(createJMenuItem("Sauvegarder", "menuItemSaveInstrument", null));
        fichier.add(createJMenuItem("Quitter", "menuItemQuit", "/redX.png"));

        JMenu options = new JMenu("Options");
        options.add(createJMenuItem("Afficher les détails des notes", "menuItemShowKeyCaracteristics", null));
        options.add(createJMenuItem("Activer le métronome", "menuItemActivateMetronome", null));
        options.add(createJMenuItem("Décharger la partition", "menuItemDeactivatePartition", null));
        options.add(son);

        JMenu help = new JMenu("Aide");
        help.add(createJMenuItem("À Propos", "menuItemAbout", null));

        ouvrir.add(gabarit);
        menuBar.add(fichier);
        menuBar.add(options);
        menuBar.add(help);

    }

    private JMenuItem createJMenuItem(String text, String name, String iconPath){
        JMenuItem newMenuItem;

        if (iconPath == null){
            newMenuItem = new JMenuItem(text);
        }
        else{
            newMenuItem = new JMenuItem(text, new ImageIcon(this.getClass().getResource(iconPath)));
        }

        newMenuItem.setName(name);
        newMenuItem.addActionListener(this.uiActionListener);

        return newMenuItem;
    }


}
