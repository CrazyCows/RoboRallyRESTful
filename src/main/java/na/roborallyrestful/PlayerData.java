package na.roborallyrestful;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    public void writeJsonBoard(@RequestBody JsonNode jsonNode, @RequestParam String ID) {
        // Creates a new DIR. Returns an error if the DIR already exists
        String path = "Games/" + ID;
        File file = new File(path, "playerData.json");

        if(file.exists()){
            try {
                JsonNode playerData = objectMapper.readTree(file);
                playerData.to
                ArrayNode arrayNode1 = (ArrayNode) playerData;
                ArrayNode arrayNode2 = (ArrayNode) jsonNode;
                arrayNode2.addAll(arrayNode1);
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, arrayNode2);
            } catch (Exception e){
                System.out.println(e);
            }
            return;
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, "playerData.json"), jsonNode);
        } catch (IOException e) {
            throw new ErrorWritingToFileException(e);
        }
    }
    // Failsafe implemented as the user must type in an ID to load a old game file which may cause errors
    @GetMapping("/jsonPlayer")
    public JsonNode readJson(@RequestParam String ID) {
        String path = "Games/" + ID;
        try {
            File file = new File(path, "playerData.json");
            JsonNode jsonNode = objectMapper.readTree(file);
            return jsonNode;
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file", e);
        }
    }

}
