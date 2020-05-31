import lombok.Getter;

import java.util.ArrayList;

@Getter
public class UpdateCloudMessage extends AbstractMessage {
    private ArrayList<String> cloudFileList;

    public UpdateCloudMessage() {
    }

    public UpdateCloudMessage(ArrayList<String> serverFileList) {
        this.cloudFileList = serverFileList;
    }
}
