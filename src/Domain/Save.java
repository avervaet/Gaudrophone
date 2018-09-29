package Domain;

import java.io.*;

public class Save {

    // Saves to a temporary file
    public void write(Instrument instrument) {
        try {
            File file = File.createTempFile("instrument", ".ser");
            write(instrument, file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Save to a specified file
    public void write(Instrument instrument, String filename) {
        try {

            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            System.out.println(instrument instanceof Serializable);
            out.writeObject(instrument);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    // Load from file
    public Instrument read(String filename) {
        Instrument instrument = null;
        try {
            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            instrument = (Instrument) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }

        return instrument;
    }

}
