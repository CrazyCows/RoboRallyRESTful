package na.roborallyrestful;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GamesData {

    private ObjectMapper objectMapper = new ObjectMapper();

    GamesData(){
        File folder = new File("Games");
        if (!folder.exists()){
            folder.mkdir();
        }
    }


    // Failsafe implemented as the user must type in an ID to load a old game file which may cause errors
    @GetMapping("/jsonGames")
    public JsonNode readJson() {
        String folderPath = "Games/";
        try {
            List<String> folderNames = getFolderNames(folderPath);
            return objectMapper.valueToTree(folderNames);
        } catch (IOException e) {
            throw new RuntimeException("Error reading folder names", e);
        }
    }

    private List<String> getFolderNames(String folderPath) throws IOException {
        List<String> folderNames = new ArrayList<>();
        Path directoryPath = Paths.get(folderPath);

        if (Files.isDirectory(directoryPath)) {
            try (var stream = Files.newDirectoryStream(directoryPath)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        folderNames.add(path.getFileName().toString());
                    }
                }
            }
        }
        return folderNames;
    }

}
