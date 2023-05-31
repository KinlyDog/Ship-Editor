package oth.shipeditor.representation;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import oth.shipeditor.communication.BusEventListener;
import oth.shipeditor.communication.EventBus;
import oth.shipeditor.communication.events.BusEvent;
import oth.shipeditor.communication.events.files.HullFileOpened;
import oth.shipeditor.communication.events.files.SpriteOpened;
import oth.shipeditor.communication.events.viewer.layers.ShipLayerCreated;
import oth.shipeditor.communication.events.viewer.layers.ShipLayerUpdated;
import oth.shipeditor.representation.data.Hull;
import oth.shipeditor.representation.data.ShipData;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ontheheavens
 * @since 27.05.2023
 */
@Log4j2
public class LayerManager {

    @Getter
    private final List<ShipLayer> layers = new ArrayList<>();

    @Getter @Setter
    private ShipLayer activeLayer;

    public void initListeners() {
        this.initOpenSpriteListener();
        this.initOpenHullListener();
    }

    private void initOpenSpriteListener() {
        EventBus.subscribe(new BusEventListener() {
            @Override
            public void handleEvent(BusEvent event) {
                if (event instanceof SpriteOpened checked) {
                    BufferedImage sprite = checked.sprite();
                    if (activeLayer != null) {
                        activeLayer.setShipSprite(sprite);
                        EventBus.publish(new ShipLayerUpdated(activeLayer));
                    } else {
                        ShipLayer newLayer = new ShipLayer(sprite);
                        activeLayer = newLayer;
                        layers.add(newLayer);
                        EventBus.publish(new ShipLayerCreated(newLayer));
                    }
                }
            }

            @Override
            public String toString() {
                return "OpenSpriteListener" + this.hashCode();
            }
        });
    }

    // TODO: implement layer tab and multiple layer support

    private void initOpenHullListener() {
        EventBus.subscribe(event -> {
            if (event instanceof HullFileOpened checked) {
                Hull hull = checked.hull();
                if (activeLayer != null) {
                    ShipData data = activeLayer.getShipData();
                    if (data != null ) {
                        data.setHull(hull);
                    } else {
                        data = new ShipData(hull);
                        activeLayer.setShipData(data);
                    }
                    EventBus.publish(new ShipLayerUpdated(activeLayer));
                } else {
                    ShipLayer newLayer = new ShipLayer(new ShipData(hull));
                    activeLayer = newLayer;
                    layers.add(newLayer);
                    EventBus.publish(new ShipLayerCreated(newLayer));
                }
            }
        });
    }

}
