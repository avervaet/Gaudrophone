package Domain.Playable;

import java.io.Serializable;

public class Note implements Serializable {
    private int octave;
    private int semitone;

    public static String[] letters = {
        "C", "C#", "D", "D#", "E", "F",
        "F#", "G", "G#", "A", "A#", "B"
    };

    /**
     * Represents a MIDI note
     * ref: http://www.electronics.dit.ie/staff/tscarff/Music_technology/midi/midi_note_numbers_for_octaves.htm
     * @param octave The octave offset (0 to 10)
     * @param semitone The semitone offset from 0(C), 1(C#), 2(D) ... 11(B)
     */
    public Note(int octave, int semitone) {
        this.octave = octave;
        this.semitone = semitone;
    }

    /**
     * Computed field from octave and semitone
     * @return a numeral value representing the note (0 to 127)
     */
    public int getMidiNote() {
        return this.octave * 12 + this.semitone;
    }

    public String getNoteName() {
        return letters[getMidiNote() % 12];
    }

    public String getFullNoteName() { return getNoteName() + this.octave;}

    public int getOctave() { return octave; }

    public int getSemitone() { return semitone; }
}
