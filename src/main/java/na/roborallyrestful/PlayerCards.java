package na.roborallyrestful;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * EXACTLY the same as player data. Only split up to get a new URL tag
 * Should probably have made a helper instead of duplicating code. But since it is only used twice there is no point..
 */


@RestController
public class PlayerCards {
    private ObjectMapper objectMapper = new ObjectMapper();

    PlayerCards(){
        File file = new File("Games");
        if (!file.exists()){
            file.mkdir();
        }
    }
    @PostMapping("/jsonCards")
    public void createCurrentCollectiveCards(@RequestBody JsonNode jsonNode, @RequestParam String ID) {

        String path = "Games/" + ID;
        String playerName = jsonNode.get("name").asText();
        File file = new File(path, "cardSequenceRequest.json");
        // Creates a new DIR. Returns an error if the DIR already exists
        if(file.exists()){
            try {
                // Read old JSON file as a JsonNode
                JsonNode playerData = objectMapper.readTree(file);
                // Create a new ArrayNode
                ArrayNode merged = objectMapper.createArrayNode();

                // This function does nothing, read TODO
                if (playerData.isArray()) {
                    // Iterate through array elements
                    for (JsonNode element : playerData) {
                        if (playerName.equals(element.get("name").asText())) {
                            // TODO: Implement what happens if a player tries to register twice...
                            // TODO: Players joining is handled and protected by the GUI. As such this function should not be needed. If issues, implement!
                            break;
                        }
                    }
                }

                // If playerData is an array, add its elements to the merged array
                if (playerData.isArray()) {
                    for (JsonNode element : playerData) {
                        merged.add(element);
                    }
                }

                // Add new JSON data to the merged array
                merged.add(jsonNode);

                // Write the merged array back to the file
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, merged);
            } catch (Exception e){
                System.out.println(e);
            }
            return;
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, "cardSequenceRequest.json"), jsonNode);
        } catch (IOException e) {
            throw new ErrorWritingToFileException(e);
        }
    }


    @PutMapping("/jsonCards")
    public void updateCurrentCollectiveCards(@RequestBody JsonNode newPlayerData,@RequestParam String ID) {
        String path = "Games/" + ID;
        String playerName = newPlayerData.get("name").asText();
        File file = new File(path, "cardSequenceRequest.json");

        if(file.exists()){
            try {
                // Read old JSON file as a JsonNode
                JsonNode oldPlayerData = objectMapper.readTree(file);
                // Create a new ArrayNode
                ArrayNode updatedPlayerData = objectMapper.createArrayNode();

                if (oldPlayerData.isArray()) {
                    // Iterate through array elements
                    for (JsonNode element : oldPlayerData) {
                        if (playerName.equals(element.get("name").asText())) {
                            // If the player is found, update their data with the new data
                            updatedPlayerData.add(newPlayerData);
                        } else {
                            // If the player isn't the one we're looking for, copy their data over without changes
                            updatedPlayerData.add(element);
                        }
                    }
                }

                // Write the updated player data back to the file
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, updatedPlayerData);
            } catch (Exception e){
                System.out.println(e);
            }
            return;
        }

        // If the file doesn't exist, no operation will be performed.
        System.out.println("File does not exist.");
    }

    @GetMapping("/jsonCards")
    public JsonNode getPlayerData(@RequestParam String ID) {
        String path = "Games/" + ID;

        try {
            File file = new File(path, "cardSequenceRequest.json");
            JsonNode jsonNode = objectMapper.readTree(file);
            return jsonNode;
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file", e);
        }
    }

    @DeleteMapping("/jsonCards")
    public void deleteJsonPlayerData(String ID){
        String pathIn = "Games/" + ID + "/" + "cardSequenceRequest.json";
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