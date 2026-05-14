package org.motorph.listeners;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.util.function.Consumer;

public class TextFieldHandler implements DocumentListener {
    private final Consumer<String> action;

    public TextFieldHandler(Consumer<String> action) {
        this.action = action;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            action.accept(e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            action.accept(e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
