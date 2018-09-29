package Domain.Searcher;

import Domain.Instrument;
import Domain.InstrumentComponent.IInstrumentComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class HotkeySearcher implements ISearcher {
    private Instrument instrument;
    public HotkeySearcher(Instrument instrument){
        this.instrument = instrument;
    }

    @Override
    public List<IInstrumentComponent> search(String value) {
        // Sets cannot contain duplicates, this prevents adding a key twice
        Collection<IInstrumentComponent> ret = new HashSet<>();

        for (String searchTerm : value.split(" "))
            for (IInstrumentComponent component : instrument.getTouches())
                if ((char)component.getHotKey() == searchTerm.charAt(0))
                    ret.add(component);

        return new ArrayList<>(ret);
    }
}
