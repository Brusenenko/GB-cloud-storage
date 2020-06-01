import lombok.Getter;

@Getter
class DeleteRequest extends AbstractMessage {
    private String filename;

    DeleteRequest(String filename) {
        this.filename = filename;
    }
}
