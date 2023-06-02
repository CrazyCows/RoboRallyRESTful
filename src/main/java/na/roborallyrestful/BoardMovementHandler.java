package na.roborallyrestful;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@RestController
public class BoardMovementHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    BoardMovementHandler(){
        File file = new File("Games");
        if (!file.exists()){
            file.mkdir();
        }
    }

    // Failsafe has been implemented here as the user can make a mistake
    @PostMapping("/jsonMoves")
    public void writeJsonBoard(@RequestBody JsonNode jsonNode, @RequestParam String ID) {
        // Creates a new DIR. Returns an error if the DIR already exists
        String path = "Games/" + ID;
        Path path1 = Paths.get(path, "moves.json");
        if(Files.exists(path1)){
            throw new DirectoryExistsException();
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, "moves.json"), jsonNode);
        } catch (IOException e) {
            throw new ErrorWritingToFileException(e);
        }
    }

    // The user has no direct input to updating the JSON files.
    // The computer wont make mistakes and as such no fail safe implemented
    @PutMapping("/jsonMoves")
    public void updateJsonBoard(@RequestBody JsonNode jsonNode, @RequestParam String ID){
        String path = "Games/" + ID;
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, "moves.json"), jsonNode);
        } catch (IOException e){
            throw new ErrorWritingToFileException(e);
        }
    }

    // Failsafe implemented as the user must type in an ID to load a old game file which may cause errors
    @GetMapping("/jsonMoves")
    public JsonNode readJson(@RequestParam String ID) {
        String path = "Games/" + ID;
        try {
            File file = new File(path, "moves.json");
            JsonNode jsonNode = objectMapper.readTree(file);
            return jsonNode;
        } catch (IOException e) {
            throw new RuntimeException("Error reading from file", e);
        }
    }

    // Deletes the whole folder, both board and moves
    @DeleteMapping("/jsonMoves")
    public void deleteJsonBoard(String ID){
        String pathIn = "Games/" + ID;
        // Found on the interwebs... Checks all files in the holder and deletes them
        try {
            Path path = Paths.get(pathIn);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException("Folder can't be deleted", e);
        }
    }

}
