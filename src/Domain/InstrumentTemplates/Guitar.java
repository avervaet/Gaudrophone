package Domain.InstrumentTemplates;

import Controller.PlayerTemplate;
import Domain.Instrument;
import gui.Shape;
import gui.Vertex;

import java.util.ArrayList;

public class Guitar implements IInstrumentGenerator {

    @Override
    public Instrument create() {

        Instrument instrument = new Instrument("Guitar", PlayerTemplate.GUITAR);
        int[] cordes = { 40, 45, 50, 55, 59, 64 };

        // 0..6 | % { 0..13 }
        for (int corde = 0; corde < 6; ++corde) {
            for (int fret = 0; fret < 13; ++fret) {
                int midiNote = cordes[corde] + fret;
                ArrayList<Vertex> points = new ArrayList<>();
                // fait des rectangles width: 7, height: 14, avec du padding genre (8, 5, 8, 4)
                points.add(new Vertex(
                        4 + fret * 7 + 1,
                        100 - (8 + corde * 14 + 1)
                ));
                points.add(new Vertex(
                        4 + fret * 7 + 1,
                        100 - (8 + corde * 14 + 13)
                ));
                points.add(new Vertex(
                        4 + fret * 7 + 6,
                        100 - (8 + corde * 14 + 13)
                ));
                points.add(new Vertex(
                        4 + fret * 7 + 6,
                        100 - (8 + corde * 14 + 1)
                ));
                Shape shape = new Shape(points);

                instrument.addKey(shape, "", (char)((corde+1)*12+(fret+1)), midiNote / 12, midiNote % 12);
            }
        }

        return instrument;
    }


}
