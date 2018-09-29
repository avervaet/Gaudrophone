package Domain.InstrumentComponent;

import Domain.Instrument;
import Domain.Playable.Partition;
import gui.Shape;
import gui.Vertex;

public class Pedal extends Partition implements IInstrumentComponent {

	private String name;
	private int hotkey;
	private Shape shape;


	public Pedal(Shape shape, String name, char hotkey, Instrument instrument) {
		super(instrument);
		this.name = name;
		this.hotkey = (int)hotkey;
		this.shape = shape;
	}

	public void togglePlay() {
		if (playing) stopPlaying();
		else startPlaying();
	}

	public void pressPedal() {
		if (isRecording())
			stopRecording();
		else if (isRecorded())
			togglePlay();
		else
			startRecording();
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

}
