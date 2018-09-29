package Domain.Playable;

import Domain.Instrument;
import Controller.PlayerTemplate;

public class Metronome extends Partition {
    public Metronome() {
        this(60);
    }

    public Metronome(int bpm) {
        super(new Instrument("metronome", PlayerTemplate.CLAVES, 9));
        endTime = (int)(1000 / ((double)bpm / 60));
        AutoNote autoNote = new AutoNote(new SimpleNote(5, 0), 0, endTime / 2);
        notes.add(autoNote);
        startPlaying();
    }
}
