package gui;

import Controller.Controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    Controller controller;

    public KeyHandler(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Does nothing :D
    }

    @Override
    public void keyPressed(KeyEvent e) {
        controller.componentPressed(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        controller.componentReleased(e.getKeyChar());
    }
}
