package Domain.Observer;

public interface IObservable {
    void registerObserver(IObserver observer);
    void notifyObservers(int data);
}
