package oth.shipeditor.parsing.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import oth.shipeditor.representation.ShipTypeHints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ontheheavens
 * @since 30.06.2023
 */
@Log4j2
public class ShipTypeHintsDeserializer extends JsonDeserializer<ShipTypeHints[]> {

    @Override
    public ShipTypeHints[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<ShipTypeHints> hints = new ArrayList<>();

        if (p.currentToken() == JsonToken.START_ARRAY) {
            while (p.nextToken() != JsonToken.END_ARRAY) {
                if (p.currentToken() == JsonToken.VALUE_STRING) {
                    String enumString = p.getText();
                    ShipTypeHints hint = ShipTypeHints.valueOf(enumString);
                    hints.add(hint);
                } else {
                    // Handle other token types if needed
                    // Throw an exception or skip invalid tokens
                }
            }
        }

        return hints.toArray(new ShipTypeHints[0]);
    }

}
