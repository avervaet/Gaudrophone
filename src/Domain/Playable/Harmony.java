package Domain.Playable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Harmony {
    private List<Note> notes = new ArrayList<Note>();
    private  double rythm;

    public Harmony(){
        this.rythm = 1;
    }

    public void addNote(Note note){
        this.notes.add(note);
    }

    public void setRythm(double rythm){
        this.rythm = rythm;
    }

    public double getRythm(){
        return this.rythm;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public int getHeight(){
        return this.notes.size();
    }
    public void print(){
        for (Note note : this.notes){
            System.out.println(note.getNoteName());
        }
    }

}
