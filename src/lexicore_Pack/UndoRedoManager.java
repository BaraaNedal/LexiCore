package lexicore_Pack;

import java.util.List;
import java.util.Stack;

/**
 * Undo / Redo Local Buffer.
 * <p>
 * Mimics a mobile text editor's rollback behaviour using two separate
 * {@link Stack} structures (LIFO):
 * <ul>
 *   <li>{@code undoStack} — states to go back to</li>
 *   <li>{@code redoStack} — states that were undone, available to re-apply</li>
 * </ul>
 * Any operation that mutates the document (e.g. a word replacement) should
 * call {@link #saveState} with the state BEFORE the mutation is applied.
 * Calling {@link #undo} then restores that state and calling {@link #redo}
 * re-applies whatever was undone — standard editor semantics: a fresh
 * mutation always clears the redo stack, since the "future" it pointed to
 * no longer exists once the user branches off in a new direction.
 */
public class UndoRedoManager {

    private final Stack<TextState> undoStack;
    private final Stack<TextState> redoStack;

    public UndoRedoManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    /**
     * Call this BEFORE applying a mutation, passing the document's state
     * as it is right now (i.e. the state to return to on undo).
     * Starting a new action invalidates any previously undone future, so
     * the redo stack is cleared.
     *
     * @param currentSentences  current sentence/token structure, pre-mutation
     * @param actionDescription short label describing the upcoming action
     */
    public void saveState(List<List<String>> currentSentences, String actionDescription) {
        undoStack.push(new TextState(currentSentences, actionDescription));
        redoStack.clear();
    }

    /**
     * Rolls back to the previous state.
     *
     * @param liveSentences the document's state right now, so it can be
     *                      pushed onto the redo stack before we go back
     * @return the restored {@link TextState}, or {@code null} if there is
     *         nothing left to undo
     */
    public TextState undo(List<List<String>> liveSentences) {
        if (!canUndo()) {
            return null;
        }
        TextState restored = undoStack.pop();
        redoStack.push(new TextState(liveSentences, restored.getActionDescription()));
        return restored;
    }

    /**
     * Re-applies the most recently undone state.
     *
     * @param liveSentences the document's state right now, so it can be
     *                      pushed back onto the undo stack before we move forward
     * @return the re-applied {@link TextState}, or {@code null} if there is
     *         nothing left to redo
     */
    public TextState redo(List<List<String>> liveSentences) {
        if (!canRedo()) {
            return null;
        }
        TextState reApplied = redoStack.pop();
        undoStack.push(new TextState(liveSentences, reApplied.getActionDescription()));
        return reApplied;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public int undoCount() {
        return undoStack.size();
    }

    public int redoCount() {
        return redoStack.size();
    }

    /** Clears both stacks — useful when a brand-new document is loaded. */
    public void reset() {
        undoStack.clear();
        redoStack.clear();
    }

    /** One-line status, handy for the console menu's feedback line. */
    public String statusLine() {
        return "Undo available: " + undoCount() + " | Redo available: " + redoCount();
    }
}
