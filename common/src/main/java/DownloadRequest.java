import lombok.Getter;

@Getter
class DownloadRequest extends AbstractMessage {
    private String filename;

    DownloadRequest(String filename) {
        this.filename = filename;
    }
}
