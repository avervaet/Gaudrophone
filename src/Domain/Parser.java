package Domain;

import Domain.Playable.*;
import org.w3c.dom.html.HTMLAreaElement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.*;

public class Parser {

    private int rythm;
    private String line;
    private ArrayList<String> headers;
    private String comments;
    private ArrayList<String> commentsBuff;
    private String[] time = { "_", ",", "." };
    private Double[] timeValue = { 1.0, 0.5, 0.25 };
    private BufferedReader buff;
    private ArrayList<Harmony> partition;

    public Parser(String path){
        this.comments = "";
        this.commentsBuff = new ArrayList<String>();
        this.headers = new ArrayList<String>();
        partition = new ArrayList<Harmony>();
        try {
            InputStream flux = new FileInputStream(path);
            InputStreamReader lecture = new InputStreamReader(flux);
            this.buff = new BufferedReader(lecture);
            readLine();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Partition parse(Instrument instrument){
        setRythm(buff);
        while ( this.line != null){
            while (!isUsefull() && this.line != null){
                readLine();
            }
            ArrayList<Harmony> paragraph  = readParagraph(buff);
            this.partition.addAll(paragraph);
            if (this.commentsBuff.size() > 0) {
                int n = paragraph.size();
                this.comments += String.format("%-" + n * 4 + "s", this.commentsBuff.get(this.commentsBuff.size() - 1));
                this.commentsBuff.remove(this.commentsBuff.size() - 1);
                this.headers.addAll(this.commentsBuff);
                this.commentsBuff.clear();
            }
        }

        return createPartition(instrument);
    }

    public ArrayList<String> getMusicSheet() {
        ArrayList<String> out = new ArrayList<>();
        out.addAll(this.headers);
        out.add("");

        out.add(this.comments);

        int maximum = 0;
        for (Harmony harmony : this.partition){
            if (harmony.getHeight() > maximum){
                maximum = harmony.getHeight();
            }
        }
        for (int i = 0; i < maximum; i++){
            out.add("");
            for (Harmony harmony : partition) {
                String note = "";
                if (i < harmony.getNotes().size()){
                    note = harmony.getNotes().get(i).getFullNoteName();
                }
                out.set(out.size() - 1, out.get(out.size() - 1) + String.format("%1$-4s", note));
            }
        }

        return out;
    }

    private Partition createPartition(Instrument instrument) {
        Partition part = new Partition(instrument, false);
        double nbTimes = 0;
        int msPerTime = (int) (1000 / ( (double) this.rythm / 60 ));

        for (Harmony harmony : this.partition) {
            long startTime = (long) (nbTimes * msPerTime);
            long endTime = (long) ( (nbTimes + harmony.getRythm()) * msPerTime );

            for (Note note : harmony.getNotes()) {
                AutoNote autoNote = new AutoNote(new SimpleNote(note), startTime, endTime);
                part.addAutoNote(autoNote);
            }

            nbTimes += harmony.getRythm();
        }

        part.setEndTime((int)(nbTimes * msPerTime));

        return part;
    }

    private boolean isComment(){
        if (this.line.startsWith("//")){
                this.commentsBuff.add(this.line);
            return true;
        }
        return false;
    }

    private boolean isEmpty() {
        if (this.line == null){
            return true;
        }
        String currentLine = this.line;
        currentLine = currentLine.replace("|", "");
        currentLine = currentLine.replace(" ", "");
        return currentLine.equals("");
    }

    private boolean isUsefull() {
        if (isEmpty()) {
            return false;
        } else {
            return !isComment();
        }
    }

    private boolean setRythm(BufferedReader buff){
        while ( this.line != null ){
            if (isUsefull()){
                try {
                    this.rythm = Integer.parseInt(this.line);
                    readLine();
                } catch (NumberFormatException e) {
                    this.rythm = 60;
                }
                return true;
            }
            readLine();
        }
        return false;
    }

    private ArrayList<Harmony> readParagraph(BufferedReader buff){
        ArrayList<Harmony> paragraph = new ArrayList<Harmony>();
        List<String> times = Arrays.asList(this.time);
        while (!isEmpty()){
            while (isComment()){
                readLine();
            }
            String currentLine = this.line;
            currentLine = currentLine.replace("|", "");
            currentLine = currentLine.toUpperCase();
            String[] notes = currentLine.split(" ");
            int noteCreated = 0;
            for (int i=0; i < notes.length; i++){
                notes[i] = notes[i].replace(" ","");
                if(!notes[i].equals("")){
                    //isRythm
                    if (Character.isDigit(notes[i].charAt(0)) || times.contains(notes[i]) ){
                        paragraph.get(noteCreated).setRythm(createTime(notes[i]));
                        noteCreated +=1;
                    }
                    //isNote
                    else{
                        Note note = createNote(notes[i]);
                        if (noteCreated >= paragraph.size()){
                            Harmony harmony = new Harmony();
                            harmony.addNote(note);
                            paragraph.add(harmony);
                        }
                        else {
                            paragraph.get(noteCreated).addNote(note);
                        }
                        noteCreated +=1;
                    }
                }
            }
            readLine();
        }
        return paragraph;
    }

    private Note createNote(String stringNote){
        List<String> letters = Arrays.asList(Note.letters);
        int semitone = 0;
        int octave = 4;
        for (char letter : stringNote.toCharArray()){
            if (letters.contains(Character.toString(letter))){
                semitone = letters.indexOf(Character.toString(letter));
            }
            if (Character.toUpperCase(letter) == 'X') {
                semitone = 0;
                octave = 0;
            }
            if (letter == '#') {
                semitone = (semitone + 1) % 12;
            }
            if (Character.isDigit(letter)){
                octave = Integer.parseInt(Character.toString(letter));
            }
        }
        Note note = new Note(octave, semitone);
        return note;
    }

    private Double createTime(String timeString){
        List<String> times = Arrays.asList(this.time);
        List<Double> timesValues = Arrays.asList(this.timeValue);
        if (times.contains(timeString)){
            return timesValues.get(times.indexOf(timeString)) ;
        }
        else{
            return Double.parseDouble(timeString);
        }
    }

    public void readLine(){
        try{
            this.line = buff.readLine();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}