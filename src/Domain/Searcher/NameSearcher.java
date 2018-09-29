package Domain.Searcher;

import Domain.Instrument;
import Domain.InstrumentComponent.IInstrumentComponent;
import Domain.InstrumentComponent.Key;
import Domain.Playable.Note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class NameSearcher implements ISearcher {
    private Instrument instrument;
    public NameSearcher(Instrument instrument){
        this.instrument = instrument;
    }

    @Override
    public List<IInstrumentComponent> search(String value){
        Collection<IInstrumentComponent> ret = new HashSet<>();
        String[] searchTerms = value.split(" ");

        for (IInstrumentComponent touche : instrument.getTouches())
            for (String term : searchTerms)
                if (touche.getName().contains(term))
                    ret.add(touche);
                else if (touche instanceof Key) {
                    Key key = (Key) touche;
                    Note note = key.getNote();
                    String notename = String.format("%s%d", note.getNoteName(), note.getOctave());
                    if (notename.contains(term))
                        ret.add(touche);
                }

        return new ArrayList<>(ret);
    }
}
