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

@RestController
public class PlayerData {
    private ObjectMapper objectMapper = new ObjectMapper();

    PlayerData(){
        File file = new File("Games");
        if (!file.exists()){
            file.mkdir();
        }
    }

    // Failsafe has been implemented here as the user can make a mistake
    @PostMapping("/jsonPlayer")
    public void getAndCombinePlayerData(@RequestBody JsonNode jsonNode, @RequestParam String ID) {
        // Creates a new DIR. Returns an error if the DIR already exists
        String path = "Games/" + ID;
        File file = new File(path, "collectivePLayerData.json");

        if(file.exists()){
            try {
                // Read old JSON file as a JsonNode
                JsonNode playerData = objectMapper.readTree(file);

                // Create a new ArrayNode
                ArrayNode merged = objectMapper.createArrayNode();

                if (playerData.isArray()) {
                    // Iterate through array elements
                    for (JsonNode element : playerData) {
                        // If the "name" field is "John", print the "age" field
                        if ("Player 2".equals(element.get("name").asText())) {
                            System.out.println("John's age is: " + element.get("color").asText());
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
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, "collectivePLayerData.json"), jsonNode);
        } catch (IOException e) {
            throw new ErrorWritingToFileException(e);
        }
    }


    @GetMapping("/jsonPlayer")
    public JsonNode getPlayerData(@RequestParam String ID) {
        String path = "Games/" + ID;
        try {
            File file = new File(path, "collectivePLayerData.json");
            JsonNode jsonNode = objectMapper.readTree(file);
            return jsonNode;
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file", e);
        }
    }

    @DeleteMapping("/jsonPlayer")
    public void deleteJsonPlayerData(String ID){
        String pathIn = "Games/" + ID + "/" + "collectivePlayerData.json";
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
