package oth.shipeditor.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import oth.shipeditor.parsing.Point2DArrayParser;
import oth.shipeditor.parsing.Point2DParser;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * @author Ontheheavens
 * @since 05.05.2023
 */
public class Hull {

    // TODO: implement reading, editing process and writing for all of these fields.

    @JsonProperty("hullName")
    String hullName;

    @JsonProperty("hullId")
    private String hullId;

    @JsonProperty("hullSize")
    private String hullSize;

    @JsonProperty("spriteName")
    private String spriteName;

    @JsonProperty("style")
    private String style;

    @JsonProperty("height")
    private double height;

    @JsonProperty("width")
    private double width;

    @Getter
    @JsonProperty("center")
    @JsonDeserialize(using = Point2DParser.class)
    Point2D.Double center;

    @Getter
    @JsonProperty("collisionRadius")
    private double collisionRadius;

    @JsonProperty("coversColor")
    private String coversColor;

    @JsonProperty("shieldCenter")
    @JsonDeserialize(using = Point2DParser.class)
    private Point2D.Double shieldCenter;

    @JsonProperty("shieldRadius")
    private double shieldRadius;

    @JsonProperty("viewOffset")
    private double viewOffset;

    @JsonProperty("builtInMods")
    private String[] builtInMods;

    @JsonProperty("builtInWeapons")
    private Map<String, String> builtInWeapons;

    @JsonProperty("builtInWings")
    private String[] builtInWings;

    @JsonProperty("weaponSlots")
    private WeaponSlot[] weaponSlots;

    @JsonProperty("engineSlots")
    private EngineSlot[] engineSlots;

    // TODO: implement undo.

    @Getter
    @JsonProperty("bounds")
    @JsonDeserialize(using = Point2DArrayParser.class)
    Point2D.Double[] bounds;

}
