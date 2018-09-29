package Domain.InstrumentComponent;

import Domain.Playable.IPlayable;
import Domain.Playable.Note;
import gui.Shape;
import org.w3c.dom.html.HTMLAppletElement;

import javax.sound.sampled.AudioInputStream;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/*
	Une touche "typique".
	Avec un son synthé ou custom
 */
public class Key implements IInstrumentComponent, IPlayable {

	private String name;
	private int hotkey;
	private Shape shape;
	private transient AudioClip sound;
	private String soundClipPath;
	private Note note;
	private boolean isPlaying = false;


	/*
		Une touche avec un son venant du disque
	 */
	public Key(Shape shape, String name, char hotkey, String location) {
		this.name = name;
		this.hotkey = (int)hotkey;

		setSound(location);
		this.soundClipPath = location;
		this.shape = shape;
	}

	/*
		Une touche à synthétiser
	 */
	public Key(Shape shape, String name, char hotkey, int octave, int semitone) {
		this.name = name;
		this.hotkey = (int)hotkey;
		this.note = new Note(octave, semitone);
		this.shape = shape;
	}


	public String getSoundClipPath() { return soundClipPath; }

	public void setSoundClipPath(String path) { this.soundClipPath = path;}

	public void setNote(int octave, int semiton){
		this.note = new Note(octave, semiton);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Shape getShape() {
		return this.shape;
	}

	@Override
	public int getHotKey() {
		return this.hotkey;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setHotKey(char hotkey) {
		this.hotkey = hotkey;
	}

	@Override
	public boolean getPlaying() {
		return this.isPlaying;
	}

	@Override
	public void setPlaying(boolean newValue) {
		this.isPlaying = newValue;
	}

	@Override
	public AudioClip getSound() {
		return this.sound;
	}

	public void setSound(String path){
		AudioClip audioClip = null;
		if (path != null){
			URL url = null;
			try {
				url = new URL("file:///"+path);
				String nullFragment = null;
				URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
			} catch (MalformedURLException e) {
				System.out.println("URL " + "url is a malformed URL");
			} catch (URISyntaxException e) {
				System.out.println("URI " + "url is a malformed URL");
			}
			audioClip = Applet.newAudioClip(url);
			this.soundClipPath = path;
		}
		else {
			this.soundClipPath = null;
		}

		this.sound = audioClip;
	}

	@Override
	public Note getNote() {
		return this.note;
	}

}
