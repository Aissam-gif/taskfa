package com.example.taskfa.controllers;

import com.example.taskfa.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TaskController implements Initializable {

    @FXML
    private TableView<Task> taskTableView;
    @FXML
    private TableColumn titleCol = new TableColumn("Title");
    @FXML
    private TableColumn descriptionCol = new TableColumn("Description");

    @FXML
    private Button add_task_btn;
    private final ObservableList<Task> tvObservableList = FXCollections.observableArrayList();


    public void add_task() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/popups/add_task_dialog.fxml"));
        Parent parent = fxmlLoader.load();

        DialogController dialogController = fxmlLoader.getController();
        dialogController.setObservableList(tvObservableList);

        Scene scene = new Scene(parent, 600, 500);
        Stage stage = new Stage();
        stage.initOwner(add_task_btn.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize triggered");

        //titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        //descriptionCol.setCellValueFactory(new PropertyValueFactory<>("descirption"));
        taskTableView.setItems(tvObservableList);
        titleCol.setCellValueFactory(new PropertyValueFactory<Task,String>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Task,String>("description"));

    }
}

