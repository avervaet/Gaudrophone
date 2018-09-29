package Domain.Playable;

import Domain.Instrument;
import Domain.Observer.IObservable;
import Domain.Observer.IObserver;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Partition implements Serializable, IObservable {

    ArrayList<AutoNote> notes;
    private ArrayList<AutoNote> buff; // for incomplete autonotes
    private Instant startTime; // start of register or play
    long endTime; // end of register or play in ms elapsed since startTime
    protected Instrument instrument;
    private transient Thread thread;
    private boolean recording = false;
    private boolean loop;
    private boolean recorded;
    protected boolean playing;
    protected ArrayList<IObserver> observers;

    public Partition(Instrument instrument) {
        this(instrument, true);
    }

    public Partition(Instrument instrument, boolean loop) {
        this.startTime = Instant.now();
        this.instrument = instrument;
        this.notes = new ArrayList<>();
        this.buff = new ArrayList<>();
        this.loop = loop;
        this.observers = new ArrayList<>();
    }

    private long getElapsed() {
        Instant now = Instant.now();
        Duration offset = Duration.between(startTime, now);
        return offset.toMillis();
    }

    private void resetNotes() {
        this.startTime = Instant.now();
        for (AutoNote note : notes) {
            note.setPlayed(false);
            note.setStopped(false);
        }
    }

    public void addAutoNote(AutoNote note) {
        notes.add(note);
    }

    public void addNoteStart(IPlayable note, Instant startTime) {
        long l_startTime = Duration.between(this.startTime, startTime).toMillis();
        buff.add(new AutoNote(note, l_startTime));
    }

    public void addNoteStop(IPlayable note, Instant endTime) {
        long l_endTime = Duration.between(this.startTime, endTime).toMillis();
        for (Iterator<AutoNote> it = buff.iterator(); it.hasNext(); ) {
            AutoNote autoNote = it.next();
            IPlayable playable = autoNote.getPlayable();
            if (playable.getNote().getMidiNote() == note.getNote().getMidiNote()) {
                if (playable.getSound() == note.getSound()) {
                    autoNote.setEndTime(l_endTime);
                    notes.add(autoNote);
                    it.remove();
                }
            }
        }
    }

    public void startPlaying() {
        if (recording || playing)
            return; // bail out

        this.playing = true;

        this.startTime = Instant.now();
        notes.sort(Comparator.comparingLong(AutoNote::getStartTime));

        Runnable runnable = () -> {
            int i = 0;
            while (true) {
                for (AutoNote autoNote : notes) {
                    if (!autoNote.isPlayed()) {
                        if (autoNote.getStartTime() <= getElapsed()) {
                            instrument.play(autoNote.getPlayable());
                            autoNote.setPlayed(true);
                            notifyObservers(i++);
                        }
                    } else if (!autoNote.isStopped()) {
                        if (autoNote.getEndTime() <= getElapsed()) {
                            instrument.stop(autoNote.getPlayable());
                            autoNote.setStopped(true);
                        }
                    }
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
                if (endTime <= getElapsed()) {
                    resetNotes();
                    if (!loop)
                        break;
                }
            }
        };

        thread = new Thread(runnable);
        thread.start();
    }

    public void stopPlaying() {
        this.playing = false;
        thread.interrupt();
        resetNotes();
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    // TODO: make start() and stop() a setter for this.recording;
    protected void startRecording() {
        this.recording = true;
        this.notes = new ArrayList<>();
        this.startTime = Instant.now();
    }

    protected void stopRecording() {
        this.recording = false;
        setRecorded(true);
        this.endTime = getElapsed();
        // clear buff
        for (Iterator<AutoNote> it = buff.iterator(); it.hasNext(); ) {
            AutoNote autoNote = it.next();
            autoNote.setEndTime(getElapsed());
            notes.add(autoNote);
            it.remove();
        }
    }

    public boolean isRecording() {
        return this.recording;
    }

    protected boolean isRecorded() {
        return recorded;
    }

    private void setRecorded(boolean recorded) {
        this.recorded = recorded;
    }

    public boolean getPlaying(){ return this.playing; }

    @Override
    public void registerObserver(IObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void notifyObservers(int data) {
        for (IObserver observer : this.observers)
            observer.sendmsg(data);
    }
}
