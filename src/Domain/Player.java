package Domain;

import Controller.PlayerTemplate;
import Domain.Playable.Note;

import javax.sound.midi.*;
import javax.sound.midi.Instrument;
import java.io.Serializable;

/*
    Classe qui s'occupe de jouer les son
 */
public class Player implements Serializable {

    private static MidiChannel[] channels;
    private int channel;

    public Player() {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
            synth.loadInstrument(instruments[90]);
            channel = 0;
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public Player(PlayerTemplate playerTemplate) {
        this();
        setTimbre(playerTemplate);
    }

    public Player(PlayerTemplate playerTemplate, int channel) {
        this();
        setTimbre(playerTemplate, channel);
    }

    public void setTimbre(PlayerTemplate playerTemplate) {
        setTimbre(playerTemplate, channel);
    }

    public void setTimbre(PlayerTemplate playerTemplate, int channel) {
        this.channel = channel;
        channels[channel].programChange(playerTemplate.getMidiProgram());
    }

    public void play(Note note) {
        play(note, channel);
    }

    public void play(Note note, int channel) {
        channels[channel].noteOn(note.getMidiNote(), 127);
    }

    public void stop(Note note) {
        stop(note, channel);
    }

    public void stop(Note note, int channel) {
        channels[channel].noteOff(note.getMidiNote());
    }

}
