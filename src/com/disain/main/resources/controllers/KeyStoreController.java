package com.disain.main.resources.controllers;

import com.disain.main.Main;
import com.disain.main.utils.CryptoUtils;
import com.disain.main.utils.KeyStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.security.Key;

public class KeyStoreController {
    @FXML
    private TableView<KeyStore> keyStoreTable;

    @FXML
    private TableColumn<KeyStore, String> nameColumn;

    @FXML
    private TableColumn<KeyStore, String> keyColumn;

    @FXML
    void onTableClick(MouseEvent event) throws IOException, ClassNotFoundException {
        if (event.getClickCount() == 2) {
            String keyPath = "C:\\Cryptographer\\" + keyStoreTable.getFocusModel().getFocusedItem().getName();
            Main.mainController.setSecretKey(CryptoUtils.readKeyFile(keyPath));
            keyStoreTable.getScene().getWindow().hide();
        }
    }

    @FXML
    void addKey(ActionEvent event) throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть документ");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Файлы ключа", "*.key")
        );

        MenuItem menuItem = (MenuItem) event.getTarget();
        Window window = menuItem.getParentPopup().getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);

        try {
            if (file != null) {
                Files.copy(file.toPath(), new File("C:\\Cryptographer\\" + file.getName()).toPath());
                Key key = CryptoUtils.readKeyFile(file.toString());
                keyStoreTable.getItems().add(
                        new KeyStore(file.getName(), CryptoUtils.toString(key))
                );
            }
        } catch (FileAlreadyExistsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Внимание");
            alert.setHeaderText("Возникла ошибка");
            alert.setContentText("Такой ключ уже существует!");
            alert.showAndWait();
        }
    }

    @FXML
    void deleteKey(ActionEvent event) throws IOException {
        KeyStore focusedItem = keyStoreTable.getFocusModel().getFocusedItem();
        String username = System.getenv().get("USERPROFILE").split("\\\\")[2];
        if (focusedItem.getName().equals(username + ".key")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Внимание");
            alert.setHeaderText("Возникла ошибка");
            alert.setContentText("Невозможно удалить основной ключ!");
            alert.showAndWait();
        } else {
            keyStoreTable.getItems().remove(focusedItem);
            Files.delete(new File("C:\\Cryptographer\\" + focusedItem.getName()).toPath());
        }
    }

    public void initialize() throws IOException, ClassNotFoundException {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));

        File keyPath = new File("C:\\Cryptographer");
        for (File file : keyPath.listFiles()) {
            Key key = CryptoUtils.readKeyFile(file.toString());
            keyStoreTable.getItems().add(
                    new KeyStore(file.getName(), CryptoUtils.toString(key))
            );
        }
    }
}
