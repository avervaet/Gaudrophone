package Domain;

import Domain.InstrumentComponent.IInstrumentComponent;
import Domain.InstrumentComponent.Key;
import Domain.InstrumentComponent.Pedal;
import Controller.PlayerTemplate;
import Domain.Playable.IPlayable;
import Domain.Playable.Partition;
import gui.Shape;
import gui.Vertex;

import java.applet.AudioClip;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;


public class Instrument implements Serializable {

	private ArrayList<IInstrumentComponent> touches;
	private String name = "";
	private Player player;
	private Partition partition;

	private Instrument(String name) {
		this.name = name;
		this.touches = new ArrayList<>();
		this.partition = new Partition(this);
	}
	
	public Instrument(String name, PlayerTemplate playerTemplate) {
		this(name);
		this.player = new Player(playerTemplate);
	}

	public Instrument(String name, PlayerTemplate playerTemplate, int channel) {
		this(name);
		this.player = new Player(playerTemplate, channel);
	}
	
	public void addKey(Shape shape, String name, char hotkey, String location) {
		touches.add(new Key(shape, name, hotkey, location));
	}

	public void addKey(Shape shape, String name, char hotkey, int octave, int semitone) {
		touches.add(new Key(shape, name, hotkey, octave, semitone));
	}

	public void pressed(char hotkey) {
		for (IInstrumentComponent touche : touches) {
			if (touche.getHotKey() == (int) hotkey) {
				pressed(touche);
				touche.getShape().setHighLighted(true);
			}
		}
	}

	public void pressed(IInstrumentComponent component) {
		if (component instanceof IPlayable)
			play((IPlayable) component);
		else if (component instanceof Pedal) {
			((Pedal) component).pressPedal();
		}
		component.getShape().setHighLighted(true);
	}

	public void released(char hotkey) {
		for (IInstrumentComponent touche : touches) {
			if (touche.getHotKey() == (int) hotkey) {
				released(touche);
				touche.getShape().setHighLighted(false);
			}
		}
	}

	public void released(IInstrumentComponent component) {
		if (component instanceof IPlayable)
			stop((IPlayable)component);
		component.getShape().setHighLighted(false);
	}

	public void releaseAll(){
		for (IInstrumentComponent component : touches){
			if (component instanceof IPlayable)
				stop((IPlayable) component);
			else if (component instanceof Pedal) {
				if (((Pedal) component).getPlaying()){
					((Pedal) component).togglePlay();
				}
			}
			component.getShape().setHighLighted(false);
		}
	}

	public void play(IPlayable touche) {
		AudioClip sound = touche.getSound();

		if (!touche.getPlaying()) {
			if (sound == null)
				player.play(touche.getNote());
			else
				sound.play();

			if (partition.isRecording())
				partition.addNoteStart(touche, Instant.now());

			for (IInstrumentComponent uneTouche : touches) {
				if (uneTouche instanceof Pedal) {
					Pedal pedal = (Pedal) uneTouche;
					if (pedal.isRecording())
						pedal.addNoteStart(touche, Instant.now());
				}
			}
			touche.setPlaying(true);
		}
	}

	public void stop(IPlayable touche) {
		if (partition.isRecording())
			partition.addNoteStop(touche, Instant.now());

		for (IInstrumentComponent uneTouche : touches) {
			if (uneTouche instanceof Pedal) {
				Pedal pedal = (Pedal) uneTouche;
				if (pedal.isRecording())
					pedal.addNoteStop(touche, Instant.now());
			}
		}

		touche.setPlaying(false);

		AudioClip sound = touche.getSound();

		if (sound == null) {
			player.stop(touche.getNote());
		}
	}

	public void changeComponentValues(IInstrumentComponent component, String name, int octave, int semiton, char hotkey, String audioClipPath){
		if (component instanceof Key){
			((Key) component).setSound(audioClipPath);
			((Key) component).setNote(octave, semiton);
		}
		component.setHotKey(hotkey);
		component.setName(name);
	}

	public void addPedal(Shape shape, String name, char hotkey) {
		touches.add(new Pedal(shape, name, hotkey, this));
	}

	public void setTimbre(PlayerTemplate timbre) {
		this.player.setTimbre(timbre);
	}

	public void setName(String name){ this.name = name; }

	public String getName() {return this.name;}

	public ArrayList<IInstrumentComponent> getTouches() {
		return touches;
	}

	public IInstrumentComponent componentClicked(Vertex vertex){
		if (!touches.isEmpty()){
			for (IInstrumentComponent component : this.touches){
				if (component.getShape().isInBoundaries(vertex)){
					return component;
				}
			}
		}

		return null;
	}

	public Vertex vertexClicked(Vertex atVertex){
		Vertex vertexToReturn = null;
		if (!touches.isEmpty()){
			for (IInstrumentComponent component : this.touches){
				vertexToReturn = component.getShape().getVertexAt(atVertex);
				if (vertexToReturn != null){
					return vertexToReturn;
				}
			}
		}
		return vertexToReturn;
	}


	public void deleteComponentAt(Vertex point){
		IInstrumentComponent componentToDelete = this.componentClicked(point);

		if (componentToDelete != null){
			Vertex vertexToDelete = componentToDelete.getShape().getVertexAt(point);

			if (vertexToDelete != null){
				if (componentToDelete.getShape().getVertexList().size() >= 4){
					componentToDelete.getShape().getVertexList().remove(vertexToDelete);
				}
			} else {
				this.touches.remove(componentToDelete);
			}
		}
	}

	public void unHighLightAll(){
		for (IInstrumentComponent component: this.getTouches()){
			component.getShape().setHighLighted(false);
		}
	}
}
