package oth.shipeditor.undo.edits;

import lombok.AllArgsConstructor;
import oth.shipeditor.communication.events.Events;
import oth.shipeditor.components.viewer.entities.BaseWorldPoint;
import oth.shipeditor.components.viewer.entities.BoundPoint;
import oth.shipeditor.components.viewer.painters.AbstractPointPainter;
import oth.shipeditor.components.viewer.painters.BoundPointsPainter;
import oth.shipeditor.undo.AbstractEdit;

/**
 * @author Ontheheavens
 * @since 17.06.2023
 */
@AllArgsConstructor
public class PointRemovalEdit extends AbstractEdit {
    private final AbstractPointPainter painter;
    private final BaseWorldPoint removed;
    private final int indexOfRemoved;

    @Override
    public void undo() {
        if (painter instanceof BoundPointsPainter checked) {
            checked.insertPoint((BoundPoint) removed, indexOfRemoved);
        } else {
            painter.addPoint(removed);
        }
        Events.repaintView();
    }

    @Override
    public void redo() {
        painter.removePoint(removed);
        Events.repaintView();
    }

    @Override
    public String getName() {
        return "Remove Point";
    }

}
