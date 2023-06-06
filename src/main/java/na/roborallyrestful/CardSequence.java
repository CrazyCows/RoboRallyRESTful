package na.roborallyrestful;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@RestController
public class CardSequence {
    private ObjectMapper objectMapper = new ObjectMapper();

    CardSequence(){
        File file = new File("Games");
        if (!file.exists()){
            file.mkdir();
        }
    }

    @PostMapping("/jsonCardSequence")
    public void createCurrentCollectiveCards(@RequestBody JsonNode cardSequenceRequest, @RequestParam String ID) {
        String path = "Games/" + ID;

        try {
            // Create the directory if it doesn't exist
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "cardSequenceRequests.json");
            if (file.exists()) {
                // Read the existing JSON file
                String existingJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

                // Parse the existing JSON into a JsonNode
                JsonNode existingCardSequence = objectMapper.readTree(existingJson);

                // Get the player's name from the JSON input
                String playerName = cardSequenceRequest.fieldNames().next();

                // Get the existing player's data, if it exists
                JsonNode existingPlayerData = existingCardSequence.get(playerName);

                // Create the new player's data object
                ObjectNode playerData = objectMapper.createObjectNode();
                playerData.set("programmingCards", cardSequenceRequest.get(playerName).get("programmingCards"));

                if (existingPlayerData != null && !existingPlayerData.equals(playerData)) {
                    // Merge the programmingCards arrays of the existing and new player's data
                    ArrayNode mergedProgrammingCards = objectMapper.createArrayNode();
                    mergedProgrammingCards.addAll((ArrayNode) existingPlayerData.get("programmingCards"));
                    mergedProgrammingCards.addAll((ArrayNode) playerData.get("programmingCards"));
                    playerData.set("programmingCards", mergedProgrammingCards);
                }

                // Add or update the player's data in the existing card sequence
                ((ObjectNode) existingCardSequence).set(playerName, playerData);

                // Write the updated card sequence to the file
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, existingCardSequence);
            } else {
                // Create the new JSON object with the player's data
                ObjectNode newCardSequence = objectMapper.createObjectNode();
                String playerName = cardSequenceRequest.fieldNames().next();
                newCardSequence.set(playerName, cardSequenceRequest.get(playerName));

                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, newCardSequence);
            }
        } catch (IOException e) {
            throw new ErrorWritingToFileException(e);
        }
    }


    @PutMapping("/jsonCardSequence")
    public void updateCurrentCollectiveCards(@RequestBody JsonNode cardSequenceRequest, @RequestParam String ID) {
        String path = "Games/" + ID;
        String playerName = cardSequenceRequest.fieldNames().next();
        File file = new File(path, "cardSequenceRequests.json");

        try {
            if (file.exists()) {
                // Read the existing JSON file
                String existingJson = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

                // Parse the existing JSON into a JsonNode
                JsonNode existingData = objectMapper.readTree(existingJson);

                // Create the new player's data object
                ObjectNode playerData = objectMapper.createObjectNode();
                playerData.set("programmingCards", cardSequenceRequest.get(playerName).get("programmingCards"));

                // Remove all existing programming cards for the specific player
                ((ObjectNode) existingData).remove(playerName);

                // Add the new player's data to the existing card sequence
                ((ObjectNode) existingData).set(playerName, playerData);

                // Write the updated JSON to the file
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, existingData);
            } else {
                throw new FileNotFoundException("The file " + file.getName() + " does not exist.");
            }
        } catch (IOException e) {
            throw new ErrorWritingToFileException(e);
        }
    }

    @GetMapping("/jsonCardSequence")
    public JsonNode getCardSequences(@RequestParam String ID) {
        String path = "Games/" + ID;

        try {
            File file = new File(path, "cardSequenceRequests.json");

            if (!file.exists()) {
                throw new FileNotFoundException("The file cardSequenceRequests.json does not exist for the specified ID: " + ID);
            }

            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file", e);
        }
    }

    @DeleteMapping("/jsonCardSequence")
    public void deleteJsonPlayerData(String ID){
        String pathIn = "Games/" + ID + "/" + "cardSequenceRequests.json";
        // Create a File object representing the file to delete
        File fileToDelete = new File(pathIn);

        // Delete the file
        if (fileToDelete.exists()) {
            boolean deleted = fileToDelete.delete();
            if (!deleted) {
                throw new RuntimeException("File can't be deleted");
            }
        }
    }
}
