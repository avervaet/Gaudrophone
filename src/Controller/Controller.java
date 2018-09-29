package Controller;

import Domain.InstrumentComponent.IInstrumentComponent;
import Domain.InstrumentComponent.Key;
import Domain.InstrumentTemplates.Default;
import Domain.InstrumentTemplates.Guitar;
import Domain.InstrumentTemplates.IInstrumentGenerator;
import Domain.Instrument;
import Domain.Parser;
import Domain.Playable.Metronome;
import Domain.Playable.Partition;
import Domain.Save;
import Domain.Searcher.HotkeySearcher;
import Domain.Searcher.ISearcher;
import Domain.Searcher.NameSearcher;
import Domain.Searcher.SearchOption;
import gui.MainWindow;
import gui.Shape;
import gui.Vertex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


/*
    Basically an Event Handler
 */
public class Controller {
    private Instrument instrument;
    private MainWindow mainWindow;
    private IInstrumentGenerator generator;
    private Metronome metronome;
    private ISearcher searcher;
    private Parser parser;

    public Controller(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        mainWindow.setController(this);
        generator = new Default();
        instrument = generator.create();
        mainWindow.getDrawingPanel().repaint();
    }

    public void changeComponentValues(IInstrumentComponent component, String name, int octave, int semiton, char hotkey, String audioClipPath){
        instrument.changeComponentValues(component, name, octave, semiton, hotkey, audioClipPath);
    }

    public Partition parsePartition(File file){
        parser = new Parser(file.getPath());
        return parser.parse(this.instrument);
    }

    public ArrayList<String> getMusicSheet(){
        return parser.getMusicSheet();
    }


    public void startMetronome(int bps){
        this.metronome = new Metronome(bps);
    }

    public void stopMetronome(){
        this.metronome.stopPlaying();
        this.metronome = null;
    }


    public MainWindow getMainWindow() {
        return mainWindow;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void componentPressed(char hotkey) {
        instrument.pressed(hotkey);
        mainWindow.getDrawingPanel().repaint();
    }

    public void componentPressed(IInstrumentComponent component){
        instrument.pressed(component);
        mainWindow.getDrawingPanel().repaint();
    }

    public void componentReleased(char hotkey) {
        instrument.released(hotkey);
        mainWindow.getDrawingPanel().repaint();
    }

    public void componentReleased(IInstrumentComponent component){
        instrument.released(component);
        mainWindow.getDrawingPanel().repaint();
    }

    public void releaseAll(){
        instrument.releaseAll();
    }

    public void addKey(Shape shape, String name, char hotkey, int octave, int semitone){
        instrument.addKey(shape, name, hotkey, octave,semitone);
    }

    public void addKey(Shape shape, String name, char hotkey, String location) {
        instrument.addKey(shape, name, hotkey, location);
    }

    public void addPedal(Shape shape, String name, char hotkey){
        instrument.addPedal(shape, name, hotkey);
    }

    public void deleteComponentAt(Vertex vertex){
        if (instrument != null){
            instrument.deleteComponentAt(vertex);
        }
    }

    public List<IInstrumentComponent> getComponents(){
        return instrument.getTouches();
    }

    public IInstrumentComponent componentClicked(Vertex vertex){
        if (instrument != null){
            IInstrumentComponent componentClicked = instrument.componentClicked(vertex);
            return componentClicked;
        }
        return null;
    }

    public Vertex vertexClicked(Vertex atVertex){
        if (instrument != null){
            Vertex vertexClicked = instrument.vertexClicked(atVertex);
            return vertexClicked;
        }
        return null;
    }

    public void changeInstrumentTimbre(PlayerTemplate timbre) {
        instrument.setTimbre(timbre);
    }

    public void saveInstrument(String path){
        File saveFile = new File(path);
        this.instrument.setName(saveFile.getName().substring(0, saveFile.getName().length()-4));
        mainWindow.getDrawingPanel().resetSelectedComponent();
        List<String> soundClipPaths = new ArrayList<>();
        for (IInstrumentComponent component : instrument.getTouches()){
            if (component instanceof Key){
                if (((Key) component).getSound() != null){
                    soundClipPaths.add(((Key) component).getSoundClipPath());
                    File from = new File(((Key) component).getSoundClipPath());
                    File to = new File(saveFile.getParent() + "\\" + from.getName());
                    try {
                        Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e){

                    }
                    ((Key) component).setSoundClipPath(from.getName());
                }
            }
        }

        unHightLightAll();

        Save saver = new Save();
        saver.write(this.instrument, path);
        List<IInstrumentComponent> components = instrument.getTouches();
        int soundClipPathsindex = 0;
        for (int i = 0 ; i < components.size();i++){
            if (components.get(i) instanceof Key){
                if (((Key)components.get(i)).getSound() != null){
                    ((Key)components.get(i)).setSoundClipPath(soundClipPaths.get(soundClipPathsindex));
                    soundClipPathsindex++;
                }
            }
        }
        mainWindow.getDrawingPanel().repaint();
    }

    public void loadInstrument(String path){
        mainWindow.getDrawingPanel().resetSelectedComponent();
        Save saver = new Save();
        this.instrument = saver.read(path);
        for (IInstrumentComponent component : instrument.getTouches()){
            if (component instanceof Key){
                if (((Key) component).getSound() == null && ((Key) component).getSoundClipPath() != null){
                    File saveFile = new File(path);
                    String pathWithBackSlash = saveFile.getParent() + "\\" + ((Key) component).getSoundClipPath();
                    String soundClipLoc = pathWithBackSlash.replace("\\", "/");
                    ((Key) component).setSound(soundClipLoc);
                }
            }
        }
    }

    public void loadGabaritGuitar(){
        mainWindow.getDrawingPanel().resetSelectedComponent();
        generator = new Guitar();
        instrument = generator.create();
        mainWindow.getDrawingPanel().instrumentChanged();
        mainWindow.getDrawingPanel().repaint();
    }

    public void loadGabaritDefault(){
        mainWindow.getDrawingPanel().resetSelectedComponent();
        generator = new Default();
        instrument = generator.create();
        mainWindow.getDrawingPanel().instrumentChanged();
        mainWindow.getDrawingPanel().repaint();
    }

    public List<IInstrumentComponent> search(String value, SearchOption searchOption){
        List<IInstrumentComponent> result = new ArrayList<>();
        if (searchOption == SearchOption.NAME){
            searcher = new NameSearcher(instrument);
            result = searcher.search(value);
        } else if (searchOption == SearchOption.HOTKEY){
            searcher = new HotkeySearcher(instrument);
            result = searcher.search(value);
        }
        return result;
    }

    public void unHightLightAll(){
        instrument.unHighLightAll();
    }
}
