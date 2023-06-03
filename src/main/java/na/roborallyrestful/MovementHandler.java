package na.roborallyrestful;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class MovementHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    MovementHandler(){
        File file = new File("Games");
        if (!file.exists()){
            file.mkdir();
        }
    }

    // Failsafe has been implemented here as the user can make a mistake
    @PostMapping("/jsonMoves")
    public void writeJsonBoard(@RequestBody JsonNode jsonNode, @RequestParam String ID, @RequestParam String playerName) {
        // Creates a new DIR. Returns an error if the DIR already exists
        String path = "Games/" + ID;
        Path path1 = Paths.get(path, playerName + ".json");
        /*
        if(Files.exists(path1)){
            throw new DirectoryExistsException();
        }
         */
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, playerName + ".json"), jsonNode);
        } catch (IOException e) {
            throw new ErrorWritingToFileException(e);
        }
    }
    // Failsafe implemented as the user must type in an ID to load a old game file which may cause errors
    @GetMapping("/jsonMoves")
    public JsonNode readJson(@RequestParam String ID, @RequestParam String playerName) {
        String path = "Games/" + ID;
        try {
            File file = new File(path, playerName + ".json");
            JsonNode jsonNode = objectMapper.readTree(file);
            return jsonNode;
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file", e);
        }
    }
}