package Domain.Playable;

import java.applet.AudioClip;

public class SimpleNote implements IPlayable {
    private boolean playing;
    private Note note;

    public SimpleNote(int octave, int semitone) {
        this.note = new Note(octave, semitone);
    }

    public SimpleNote(Note note) {
        this.note = note;
    }

    @Override
    public boolean getPlaying() {
        return playing;
    }

    @Override
    public void setPlaying(boolean newValue) {
        this.playing = newValue;
    }

    @Override
    public AudioClip getSound() {
        return null;
    }

    @Override
    public Note getNote() {
        return note;
    }
}
