package oth.shipeditor.components;

import oth.shipeditor.Window;
import oth.shipeditor.communication.EventBus;
import oth.shipeditor.communication.events.viewer.ViewerRepaintQueued;
import oth.shipeditor.communication.events.viewer.points.PointSelectQueued;
import oth.shipeditor.communication.events.viewer.points.PointSelectedConfirmed;
import oth.shipeditor.components.entities.BoundPoint;
import oth.shipeditor.components.entities.WorldPoint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * @author Ontheheavens
 * @since 28.05.2023
 */
public class BoundList extends JList<BoundPoint> {

    public BoundList(DefaultListModel<BoundPoint> model) {
        super(model);
        this.addListSelectionListener(e -> {
            int index = this.getSelectedIndex();
            if (index != -1) {
                BoundPoint point = this.getModel().getElementAt(index);
                point.setSelected(true);
                EventBus.publish(new PointSelectQueued(point));
                EventBus.publish(new ViewerRepaintQueued());
            }
        });
        this.setCellRenderer(new BoundPointCellRenderer());
        int margin = 3;
        this.setBorder(new EmptyBorder(margin, margin, margin, margin));
        this.initPointListeners();
    }

    private void initPointListeners() {
        EventBus.subscribe(event -> {
            if (event instanceof PointSelectedConfirmed checked && checked.point() instanceof BoundPoint) {
                BoundList.this.setSelectedValue(checked.point(), true);
            }
        });
    }

    static class BoundPointCellRenderer extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object point, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, point, index, isSelected, cellHasFocus);
            BoundPoint checked = (BoundPoint) point;
            Point2D position = checked.getPosition();
            String displayText = "Bound #" + index + ": (X:" + position.getX() + ",Y:" + position.getY() + ")";
            setText(displayText);
            return this;
        }
    }

    // TODO: revisit later, still a clusterfuck.
    public Point2D getCoordinatesForDisplay(WorldPoint input,
                                            ViewerStatusPanel.CoordsDisplayMode mode) {
        Point2D position = input.getPosition();
        Point2D result = position;
        ShipViewerPanel viewerPanel = Window.getFrame().getShipView();
        switch (mode) {
            case WORLD -> {
            }
            case SCREEN -> {
                Point2D viewerLoc = viewerPanel.getLocation();
                result = new Point2D.Double(
                        position.getX() - viewerLoc.getX(),
                        position.getY() - viewerLoc.getY()
                );
                double roundedX = Math.round(result.getX() * 2) / 2.0;
                double roundedY = Math.round(result.getY() * 2) / 2.0;
                result =  new Point2D.Double(roundedX, roundedY);
            }
            case SPRITE_CENTER -> {
                Point2D center = viewerPanel.getSelectedLayer().getSpriteCenter();
                result = new Point2D.Double(
                        position.getX() - center.getX(),
                        position.getY() - center.getY()
                );
            }
            case SHIPCENTER_ANCHOR -> {
                Point2D center = viewerPanel.getShipCenterAnchor();
                result = new Point2D.Double(
                        position.getX() - center.getX(),
                        -position.getY() + center.getY()
                );
            }
            case SHIP_CENTER -> {
                Point2D center = viewerPanel.getSelectedLayer().getTranslatedCenter();
                result = new Point2D.Double(
                        position.getX() - center.getX(),
                        position.getY() - center.getY()
                );
            }
        }
        return result;
    }


}
