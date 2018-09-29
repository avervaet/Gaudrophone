package Domain.Playable;

import java.applet.AudioClip;
import java.io.Serializable;

/**
 * Something an instrument can play
 */
public interface IPlayable extends Serializable {

    boolean getPlaying();
    void setPlaying(boolean newValue);
    AudioClip getSound();
    Note getNote();

}
