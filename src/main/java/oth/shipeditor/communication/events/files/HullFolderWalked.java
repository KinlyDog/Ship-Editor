package oth.shipeditor.communication.events.files;

import oth.shipeditor.representation.Hull;
import oth.shipeditor.representation.Skin;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author Ontheheavens
 * @since 25.06.2023
 */
public record HullFolderWalked(List<Map<String, String>> csvData, Map<String, Hull> hullFiles,
                               Map<String, Skin> skinFiles, Path folder) implements FileEvent {
}
