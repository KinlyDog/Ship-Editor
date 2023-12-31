package oth.shipeditor.undo;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import oth.shipeditor.undo.edits.ListeningEdit;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Ontheheavens
 * @since 15.06.2023
 */
@Log4j2
public final class UndoOverseer {

    private static final UndoOverseer seer = new UndoOverseer();

    private UndoOverseer() {
        undoAction.setEnabled(false);
        redoAction.setEnabled(false);
    }

    /**
     * Isn't meant to have protective checks; the input vehicles need to be disabled if there is no edits in stack.
     */
    private final Action undoAction = new AbstractAction("Undo") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Deque<Edit> undo = seer.getUndoStack();
            Edit head = undo.pop();
            head.undo();
            Deque<Edit> redo = seer.getRedoStack();
            redo.push(head);
            updateActionState();
        }
    };

    private final Action redoAction = new AbstractAction("Redo") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Deque<Edit> redo = seer.getRedoStack();
            Edit head = redo.pop();
            head.redo();
            Deque<Edit> undo = seer.getUndoStack();
            undo.push(head);
            updateActionState();
        }
    };

    @Getter
    private final Deque<Edit> undoStack = new ArrayDeque<>();

    @Getter
    private final Deque<Edit> redoStack = new ArrayDeque<>();


    private void updateActionState() {
        Deque<Edit> undo = seer.getUndoStack();
        String undoName = "Undo";
        if (undo.isEmpty()) {
            undoAction.setEnabled(false);
            undoAction.putValue(Action.NAME, undoName);
        } else {
            undoAction.setEnabled(true);
            Edit nextUndoable = UndoOverseer.getNextUndoable();
            undoName = undoName + " " + nextUndoable.getName();
            undoAction.putValue(Action.NAME, undoName);
        }

        Deque<Edit> redo = seer.getRedoStack();
        String redoName = "Redo";
        if (redo.isEmpty()) {
            redoAction.setEnabled(false);
            redoAction.putValue(Action.NAME, redoName);
        } else {
            redoAction.setEnabled(true);
            Edit nextUndoable = UndoOverseer.getNextRedoable();
            redoName = redoName + " " + nextUndoable.getName();
            redoAction.putValue(Action.NAME, redoName);
        }
    }

    public static Action getUndoAction() {
        return seer.undoAction;
    }

    public static Action getRedoAction() {
        return seer.redoAction;
    }

    static Edit getNextUndoable() {
        Deque<Edit> stack = seer.getUndoStack();
        return stack.peek();
    }

    private static Edit getNextRedoable() {
        Deque<Edit> stack = seer.getRedoStack();
        return stack.peek();
    }

    static void post(Edit edit) {
        Deque<Edit> stack = seer.getUndoStack();
        stack.addFirst(edit);
        UndoOverseer.clearRedoStack();
        seer.updateActionState();
    }

    private static void clearRedoStack() {
        UndoOverseer.clearEditListeners(seer.redoStack);
        seer.redoStack.clear();
    }

    // TODO: introduce trimming mechanism.

    private static void clearEditListeners(Iterable<Edit> stack) {
        stack.forEach(edit -> {
            if (edit instanceof ListeningEdit checked) {
                checked.unregisterListeners();
            }
        });
    }

}
