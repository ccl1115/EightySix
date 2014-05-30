package com.utree.eightysix.app.intro;

import static com.utree.eightysix.widget.lockpattern.LockPatternView.Cell;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class PatternHelper {

    private static final List<Cell> DEFAULT_PATTERN = new ArrayList<Cell>();

    static {
        DEFAULT_PATTERN.add(Cell.of(0, 0));
        DEFAULT_PATTERN.add(Cell.of(0, 1));
        DEFAULT_PATTERN.add(Cell.of(0, 2));
        DEFAULT_PATTERN.add(Cell.of(1, 2));
        DEFAULT_PATTERN.add(Cell.of(2, 2));
    }

    public boolean check(List<Cell> cells) {
        return compare(DEFAULT_PATTERN, cells);
    }

    private boolean compare(List<Cell> target, List<Cell> src) {
        if (target.size() != src.size()) return false;

        for (int i = 0; i < target.size(); i++) {
            if (!target.get(i).equals(src.get(i))) {
                return false;
            }
        }
        return true;
    }

    public int getRetries() {
        return 3;
    }
}
