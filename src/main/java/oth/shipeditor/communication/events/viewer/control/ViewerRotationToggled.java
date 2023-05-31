package oth.shipeditor.communication.events.viewer.control;

import oth.shipeditor.communication.events.viewer.ViewerEvent;

/**
 * @author Ontheheavens
 * @since 28.05.2023
 */
public record ViewerRotationToggled(boolean isSelected, boolean isEnabled) implements ViewerEvent {

}
