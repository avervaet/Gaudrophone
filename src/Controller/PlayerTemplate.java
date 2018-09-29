package Controller;

public enum PlayerTemplate {
    PIANO   (1),
    GUITAR  (25),
    FLUTE   (74),
    VIOLIN  (41),
    CLAVES  (75)
    ;

    private final int midiProgram;

    PlayerTemplate(int midiProgram) {
        this.midiProgram = midiProgram;
    }

    public int getMidiProgram() {
        return midiProgram;
    }
}
