package lexicore_Pack;



import java.util.List;
import java.util.Stack;


public class UndoRedoManager {

    private final Stack<TextState> undoStack;
    private final Stack<TextState> redoStack;

    public UndoRedoManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }


    public void saveState(List<List<String>> currentSentences, String actionDescription) {
        undoStack.push(new TextState(currentSentences, actionDescription));
        redoStack.clear();
    }


    public TextState undo(List<List<String>> liveSentences) {
        if (!canUndo()) {
            return null;
        }
        TextState restored = undoStack.pop();
        redoStack.push(new TextState(liveSentences, restored.getActionDescription()));
        return restored;
    }


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

    public void reset() {
        undoStack.clear();
        redoStack.clear();
    }

    public String statusLine() {
        return "Undo available: " + undoCount() + " | Redo available: " + redoCount();
    }
}

