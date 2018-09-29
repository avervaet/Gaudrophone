package Domain.InstrumentTemplates;

import Controller.PlayerTemplate;
import Domain.Instrument;

public class Default implements IInstrumentGenerator {
    @Override
    public Instrument create() {
        Instrument instrument = new Instrument("Default", PlayerTemplate.PIANO);
        return instrument;
    }
}
