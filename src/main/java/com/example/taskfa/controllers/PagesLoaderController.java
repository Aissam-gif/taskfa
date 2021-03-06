package com.example.taskfa.controllers;

import com.example.taskfa.controllers.chat.ChatViewController;
import com.example.taskfa.controllers.sideBar.SideBarController;
import com.example.taskfa.controllers.tasks.admin.TaskViewController;
import com.example.taskfa.controllers.vcs.VcsViewController;
import com.example.taskfa.model.ScreenLoader;
import com.example.taskfa.model.User;
import com.example.taskfa.modelDao.UserDAO;
import com.example.taskfa.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PagesLoaderController implements Initializable {
    @FXML
    private BorderPane mainPane;
    @FXML
    private Button closeWindow,reduceWindow;

    private SideBarController sideBarController;
    private OverViewController overViewController;

    private int projectIdpassed;

    User user = UserSession.getCurrentUser();

    public void goToVcs() throws IOException {
        VcsViewController vcsViewController;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(user.getMenu().showVcsView()));
        Pane overview = fxmlLoader.load();
        vcsViewController = fxmlLoader.getController();
        vcsViewController.loadFXML(projectIdpassed);
        mainPane.setCenter(overview);

    }

    public void goToChat() throws IOException {
        ChatViewController chatViewController;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(user.getMenu().showChat()));
        Pane overview = fxmlLoader.load();
        chatViewController = fxmlLoader.getController();
        chatViewController.loadFXML(projectIdpassed);
        mainPane.setCenter(overview);
    }

    public void goToTasks() throws IOException {
        TaskViewController taskViewControllerAdmin;
        com.example.taskfa.controllers.tasks.user.TaskViewController taskViewControllerUser;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(user.getMenu().showTask()));
        Pane task = fxmlLoader.load();
        if (user.isAdmin()){
            taskViewControllerAdmin = fxmlLoader.getController();
            taskViewControllerAdmin.loadFXML(projectIdpassed);
        } else {
            taskViewControllerUser = fxmlLoader.getController();
            taskViewControllerUser.loadFXML(projectIdpassed);
        }
        mainPane.setCenter(task);
    }
    
    public void goToHome() throws IOException {
        GridPane homeSideBar;
        FXMLLoader fxmlLoader = new FXMLLoader();
        ScreenLoader screen = new ScreenLoader();
        Pane view = screen.getPage("projectView");
        mainPane.setCenter(view);
    }

    public void gotoOverview() throws IOException, SQLException, ClassNotFoundException {
        OverViewController overviewController;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(user.getMenu().showOverView()));
        Pane overview = fxmlLoader.load();
        overviewController = fxmlLoader.getController();
        overviewController.loadFXML(projectIdpassed);
        mainPane.setCenter(overview);
    }

    public void goToResources() throws IOException {
        RessourceController ressourceController;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(user.getMenu().showResources()));
        Pane overview = fxmlLoader.load();
        ressourceController = fxmlLoader.getController();
        ressourceController.loadFXML(projectIdpassed);
        mainPane.setCenter(overview);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        user = UserSession.getCurrentUser();
        try {
            FXMLLoader fxmlLoader1 = new FXMLLoader();
            FXMLLoader fxmlLoader2 = new FXMLLoader();
            fxmlLoader1.setLocation(getClass().getResource(user.getMenu().showSideBar()));
            GridPane sideBar = fxmlLoader1.load();
            sideBarController = fxmlLoader1.getController();
            fxmlLoader2.setLocation(getClass().getResource(user.getMenu().showOverView()));
            GridPane center = fxmlLoader2.load();
            overViewController = fxmlLoader2.getController();
            mainPane.setLeft(sideBar);
            BorderPane.setMargin(mainPane.getLeft(), new Insets(-50,0,0,0));
            mainPane.setCenter(center);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setProjectId(int projectId) throws SQLException, ClassNotFoundException {
        projectIdpassed = projectId;
        try {
            user.setAdmin(UserDAO.isAdminInProject(user.getIdUser(), projectId));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        sideBarController.loadFXML(projectIdpassed);
        overViewController.loadFXML(projectIdpassed);
        System.out.println("In loader controller : " + projectId);
    }

    public void closeWindow() {
        // get a handle to the stage
        Stage stage = (Stage) closeWindow.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    public void reduceWindow() {
        Stage stage = (Stage) reduceWindow.getScene().getWindow();
        stage.setIconified(true);
    }

}
