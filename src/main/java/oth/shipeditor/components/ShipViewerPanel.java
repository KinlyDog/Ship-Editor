package oth.shipeditor.components;

import de.javagl.viewer.Painter;
import de.javagl.viewer.Viewer;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import oth.shipeditor.components.painters.PointsPainter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @author Ontheheavens
 * @since 29.04.2023
 */
@Log4j2
public class ShipViewerPanel extends Viewer {

    @Getter
    private BufferedImage shipSprite;
    private Painter shipPaint;
    private Painter guidesPaint;
    private Painter spriteBorderPaint;
    @Getter
    private final PointsPainter pointsPainter;
    @Getter
    private final ShipViewerControls controls;

    public ShipViewerPanel() {
        this.setMinimumSize(new Dimension(240, 120));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ShipViewerPanel.this.centerViewpoint();
            }
        });
        this.setBackground(Color.GRAY);

        controls = new ShipViewerControls(this);
        this.setMouseControl(controls);

        this.pointsPainter = new PointsPainter();
    }

    public void initialize() {
        this.initSprite();
        this.drawGuides();
        this.drawBorder();
        this.drawSpriteCenter();

        this.addPainter(this.pointsPainter, 3);
        this.centerViewpoint();
    }

    private void initSprite() {
        URI spritePath;
        try {
            spritePath = Objects.requireNonNull(getClass().getClassLoader().getResource("legion_xiv.png")).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            this.shipSprite = ImageIO.read(new File(spritePath));
            this.setShipSprite(shipSprite);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setShipSprite(BufferedImage shipSprite) {
        this.removePainter(shipPaint);
        this.shipSprite = shipSprite;
        Painter spritePainter = (g, worldToScreen, w, h) -> {
            AffineTransform oldAT = g.getTransform();
            g.transform(worldToScreen);
            int width = shipSprite.getWidth();
            int height = shipSprite.getHeight();
            g.drawImage(shipSprite, 0, 0, width, height, null);
            g.setTransform(oldAT);
        };
        this.addPainter(spritePainter, 2);
        this.shipPaint = spritePainter;
    }

    public Point getSpriteCenter() {
        return new Point(shipSprite.getWidth() / 2, shipSprite.getHeight() / 2);
    }

    public Point getShipCenterAnchor() {
        return new Point(0, shipSprite.getHeight());
    }

    public void centerViewpoint() {
        AffineTransform worldToScreen = this.getWorldToScreen();
        // Get the center of the sprite in screen coordinates.
        Point2D centerScreen = worldToScreen.transform(this.getSpriteCenter(), null);
        // Calculate the delta values to center the sprite.
        double dx = (this.getWidth() / 2f) - centerScreen.getX();
        double dy = (this.getHeight() / 2f) - centerScreen.getY();
        this.translate(dx, dy);
    }

    private Point2D getAdjustedCursor() {
        return controls.getAdjustedCursor();
    }

    /**
     * Draws the guides to the viewer. The guides consist of two rectangles each 1 scaled pixel wide,
     * one along the x-axis and one along the y-axis,
     * both of which intersect at the current cursor position.
     * The size and position of the rectangles are determined based
     * on the current ship sprite and zoom level.
     ** <br>
     * Note: considerable size of implementation is necessary due to the Viewer rotating functionality
     * and 0.5 scaled pixel snapping.
     */
    private void drawGuides() {
        this.removePainter(guidesPaint);
        Painter guidesPainter = (g, worldToScreen, w, h) -> {
            Point2D mousePoint = this.getAdjustedCursor();
            AffineTransform screenToWorld = this.getScreenToWorld();
            Point2D transformedMouse = screenToWorld.transform(mousePoint, mousePoint);
            double x = transformedMouse.getX();
            double y = transformedMouse.getY();

            double spriteW = shipSprite.getWidth();
            double spriteH = shipSprite.getHeight();
            Point2D anchor = new Point(0, 0);
            double xLeft = Math.round((anchor.getX() - 0.5) * 2) / 2.0;
            double yTop = Math.round((anchor.getY() - 0.5) * 2) / 2.0;
            double xGuide = Math.round((x - 0.5) * 2) / 2.0;
            double yGuide = Math.round((y - 0.5) * 2) / 2.0;

            Rectangle2D axisX = new Rectangle2D.Double(xLeft + 0.5, yGuide, spriteW, 1);
            Rectangle2D axisY = new Rectangle2D.Double(xGuide, yTop + 0.5, 1, spriteH);

            Paint old = g.getPaint();
            Shape guideX = worldToScreen.createTransformedShape(axisX);
            Shape guideY = worldToScreen.createTransformedShape(axisY);

            g.setPaint(new Color(0x80232323, true));
            g.draw(guideX);
            g.draw(guideY);
            g.setPaint(new Color(0x40FFFFFF, true));
            g.fill(guideX);
            g.fill(guideY);
            g.setPaint(old);
        };
        this.addPainter(guidesPainter, 5);
        this.guidesPaint = guidesPainter;
    }

    private void drawBorder() {
        this.removePainter(spriteBorderPaint);
        Painter borderPainter = (g, worldToScreen, w, h) -> {
            int width = shipSprite.getWidth();
            int height = shipSprite.getHeight();
            Rectangle worldBorder = new Rectangle(0, 0, width, height);
            Shape transformed = worldToScreen.createTransformedShape(worldBorder);
            g.draw(transformed);
        };
        this.addPainter(borderPainter, 5);
        this.spriteBorderPaint = borderPainter;
    }

    private void drawSpriteCenter() {
        Painter centerPainter = (g, worldToScreen, w, h) -> {
            Point2D center = worldToScreen.transform(getSpriteCenter(), null);
            // Draw the two diagonal lines centered on the sprite center.
            int x = (int) center.getX(), y = (int) center.getY(), l = 5;
            g.drawLine(x-l, y-l, x+l, y+l);
            g.drawLine(x-l, y+l, x+l, y-l);
        };
        this.addPainter(centerPainter, 5);
    }

}
