package Domain.Searcher;

import Domain.InstrumentComponent.IInstrumentComponent;

import java.util.List;

public interface ISearcher {
    List<IInstrumentComponent> search(String value);
}
