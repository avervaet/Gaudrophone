package Domain.Playable;

import java.io.Serializable;

public class AutoNote implements Serializable {

    private IPlayable note;
    private long startTime; // in milliseconds
    private long endTime; // in ms
    private boolean played;
    private boolean stopped;

    public AutoNote(IPlayable note, long startTime) {
        this.note = note;
        this.startTime = startTime;
    }

    public AutoNote(IPlayable note, long startTime, long endTime) {
        this(note, startTime);
        this.endTime = endTime;
    }

    public IPlayable getPlayable() {
        return note;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
