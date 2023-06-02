package na.roborallyrestful;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Failed attempt to send the actual error message to the Client
// Someone should really come back and fix this if time allows

/*
@ControllerAdvice
public class customExceptions {

    @ExceptionHandler(DirectoryExistsException.class)
    public ResponseEntity<ErrorResponse> handleDirectoryExistsException(DirectoryExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    public static class ErrorResponse {
        private int status;
        private String reason;
        // Takes the ReponseStatus and saves it such the client can read it
        public ErrorResponse(int status, String reason) {
            this.status = status;
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        // getters and setters
    }
}
*/


// Primitive error messages
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "File already exists")
class DirectoryExistsException extends RuntimeException {
    public DirectoryExistsException() {
        super("File already exists");
    }
}

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Error writing to file")
class ErrorWritingToFileException extends RuntimeException {
    public ErrorWritingToFileException(Throwable cause) {
        super("File already exists", cause);
    }
}

