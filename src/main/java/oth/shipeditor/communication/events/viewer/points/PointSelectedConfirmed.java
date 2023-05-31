package oth.shipeditor.communication.events.viewer.points;

import oth.shipeditor.components.entities.WorldPoint;

/**
 * @author Ontheheavens
 * @since 29.05.2023
 */
public record PointSelectedConfirmed(WorldPoint point) implements PointEvent {

}
