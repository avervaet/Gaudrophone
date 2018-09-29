package Domain.InstrumentTemplates;

import Domain.Instrument;

// Patron stratégie pour la création d'instrument
public interface IInstrumentGenerator {

    Instrument create();

}
