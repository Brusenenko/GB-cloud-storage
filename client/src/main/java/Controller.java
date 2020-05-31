import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private String localStoragePath = "client\\local_storage/";

    @FXML
    ListView<String> localFilesList;
    @FXML
    ListView<String> cloudFilesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get(localStoragePath + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        updateLocalFilesList();
                    }
                    if (am instanceof UpdateCloudMessage) {
                        UpdateCloudMessage updateCloudMessage = (UpdateCloudMessage) am;
                        updateCloudFilesList(updateCloudMessage.getCloudFileList());
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.close();
            }
        });
        t.setDaemon(true);
        t.start();
        updateLocalFilesList();
        Network.sendMsg(new UpdateCloudMessage());
    }

    private void updateLocalFilesList() {
        updateUI(() -> {
            try {
                localFilesList.getItems().clear();
                Files.list(Paths.get(localStoragePath)).map(p -> p.getFileName().toString()).forEach(o -> localFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateCloudFilesList(ArrayList<String> filesList) {
        updateUI(() -> {
            cloudFilesList.getItems().clear();
            cloudFilesList.getItems().addAll(filesList);
        });
    }

    private static void updateUI(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    public void btnUpload(ActionEvent actionEvent) {
        try {
            Network.sendMsg(new FileMessage(Paths.get(localStoragePath + localFilesList.getSelectionModel().getSelectedItem())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnDownload(ActionEvent actionEvent) {
        Network.sendMsg(new DownloadRequest(cloudFilesList.getSelectionModel().getSelectedItem()));
    }
}
