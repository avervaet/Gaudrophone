package Domain.InstrumentComponent;

import gui.Shape;

import java.io.Serializable;

/**
 * A componnent of the instrument.
 */
public interface IInstrumentComponent extends Serializable {

	String getName();
	Shape getShape(); // TODO: make it a shape
	int getHotKey(); // in the form (int)'a'
	void setName(String name);
	void setHotKey(char hotkey);
}
