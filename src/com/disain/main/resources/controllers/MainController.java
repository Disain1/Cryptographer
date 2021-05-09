package com.disain.main.resources.controllers;

import com.disain.main.utils.CryptoUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MainController {
    @FXML
    private ListView<File> fileListView;

    @FXML
    private TextField keyEdit;

    @FXML
    private Button startCryptoButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private MenuItem openMenuItem;

    private Key secretKey;
    private File inputFile;
    private File outputFile;

    public void onStart() throws IOException, NoSuchAlgorithmException {
        String username = System.getenv().get("USERPROFILE").split("\\\\")[2];
        Key key = CryptoUtils.generateKey();

        Path path = Paths.get("C:\\Cryptographer");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            CryptoUtils.saveKeyFile("C:\\Cryptographer\\" + username + ".key", key);
        }

        try {
            setSecretKey(CryptoUtils.readKeyFile("C:\\Cryptographer\\" + username + ".key"));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Внимание");
            alert.setHeaderText("Возникла ошибка");
            alert.setContentText("Неудалось найти файл ключа (" + username + ".key), перезапустите программу!");
            alert.showAndWait();

            CryptoUtils.saveKeyFile("C:\\Cryptographer\\" + username + ".key", key);
            System.exit(0);
        }
    }

    @FXML
    void startFileCrypto(ActionEvent event) {
        Thread thread = new Thread(() -> {
            try {
                Platform.runLater(() -> {
                    progressBar.setVisible(true);
                    openMenuItem.setDisable(true);
                    startCryptoButton.setDisable(true);
                });

                int fileListSize = fileListView.getItems().size();
                long startTime = System.currentTimeMillis();

                for (File file : fileListView.getItems()) {
                    Platform.runLater(() -> {
                        fileListView.getSelectionModel().select(file);
                        fileListView.scrollTo(file);
                    });

                    String[] filePath = file.getAbsolutePath().split("\\.");
                    String extension = filePath[filePath.length-1];
                    inputFile = file;

                    if (extension.equals("crypto")) {
                        outputFile = new File(file.getAbsolutePath().replace(".crypto", ""));
                        CryptoUtils.decrypt(secretKey, inputFile, outputFile);
                    } else {
                        outputFile = new File(file.getAbsolutePath() + ".crypto");
                        CryptoUtils.encrypt(secretKey, inputFile, outputFile);
                    }

                    progressBar.setProgress(
                            progressBar.getProgress() + Math.round(1.0 / fileListSize * 100) / 100.0
                    );

                    Files.delete(inputFile.toPath());
                }

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Внимание");
                    alert.setHeaderText("Процесс завершён");
                    alert.setContentText(
                            String.format("Кол-во файлов: %s\nВремя: %d сек.",
                                    fileListSize, (System.currentTimeMillis() - startTime) / 1000)
                    );
                    alert.showAndWait();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Внимание");
                    alert.setHeaderText("Возникла ошибка");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            } finally {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    openMenuItem.setDisable(false);
                    progressBar.setProgress(0.0);
                    fileListView.getItems().clear();
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    void onDragDropped(DragEvent event) {
        fileListView.getItems().addAll(
                event.getDragboard().getFiles()
        );
        startCryptoButton.setDisable(false);
    }

    @FXML
    void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    void openKeyFolder(ActionEvent event) throws IOException {
        Desktop.getDesktop().open(new File("C:\\Cryptographer"));
    }

    @FXML
    void showSettingsScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/SettingsScene.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Cryptographer - Настройки");
        stage.setResizable(false);

        stage.initOwner(fileListView.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    @FXML
    void showKeyStoreScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/KeyStoreScene.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Cryptographer - Хранилище ключей");
        stage.setResizable(false);

        stage.initOwner(fileListView.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    @FXML
    void showAboutScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/AboutScene.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Cryptographer - О программе");
        stage.setResizable(false);

        stage.initOwner(fileListView.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    @FXML
    void openFileDialog(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть документ");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );

        MenuItem menuItem = (MenuItem) event.getTarget();
        Window window = menuItem.getParentPopup().getScene().getWindow();
        List<File> file = fileChooser.showOpenMultipleDialog(window);

        if (file != null) {
            fileListView.getItems().addAll(file);
            startCryptoButton.setDisable(false);
        }
    }

    @FXML
    void removeFile(ActionEvent event) {
        int index = fileListView.getFocusModel().getFocusedIndex();

        if (index != -1)
            fileListView.getItems().remove(index);

        if (fileListView.getItems().isEmpty())
            startCryptoButton.setDisable(true);
    }

    @FXML
    void clearFileList(ActionEvent event) {
        fileListView.getItems().clear();

        if (fileListView.getItems().isEmpty())
            startCryptoButton.setDisable(true);
    }

    public void setSecretKey(Key key) {
        this.secretKey = key;
        keyEdit.setText(CryptoUtils.toString(secretKey));
    }

    public void initialize() throws Exception {
        onStart();
    }
}
