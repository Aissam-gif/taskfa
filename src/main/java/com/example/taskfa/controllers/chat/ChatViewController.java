package com.example.taskfa.controllers.chat;

import com.example.taskfa.model.Message;
import com.example.taskfa.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatViewController implements Initializable {
    @FXML
    public TextField message;
    @FXML
    public ScrollPane scrollMessages;
    @FXML
    private GridPane grid;
    @FXML
    private GridPane gridChat;

    Thread thread;

    private final List<Message> messages = new ArrayList<>();

    private void getMessages() {
        Message message;
        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("hh:mm");

        message = new Message();
        message.setSender(new User("Aissam","Boussoufiane"));
        message.setMessage("Bonjour tous le monde aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        message.setDate_sent(df.format(date));
        messages.add(message);

        message = new Message();
        message.setSender(new User("Achraf","Herkane"));
        message.setMessage("Salut");
        message.setDate_sent(df.format(date));
        messages.add(message);

        message = new Message();
        message.setSender(new User("Fatima","El Hadeg"));
        message.setMessage("Bonjour");
        message.setDate_sent(df.format(date));
        messages.add(message);

        message = new Message();
        message.setSender(new User("Anas","Laouissi"));
        message.setMessage("Bonjour ");
        message.setDate_sent(df.format(date));
        messages.add(message);

        message = new Message();
        message.setSender(new User("Aissam","Boussoufiane"));
        message.setMessage("Bonjour tous le monde aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        message.setDate_sent(df.format(date));
        messages.add(message);

        message = new Message();
        message.setSender(new User("Achraf","Herkane"));
        message.setMessage("Salut");
        message.setDate_sent(df.format(date));
        messages.add(message);

        message = new Message();
        message.setSender(new User("Fatima","El Hadeg"));
        message.setMessage("Bonjour");
        message.setDate_sent(df.format(date));
        messages.add(message);

        message = new Message();
        message.setSender(new User("Anas","Laouissi"));
        message.setMessage("Bonjour ");
        message.setDate_sent(df.format(date));
        messages.add(message);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        thread = new Thread("chatScrollThread") {
            public void run() {

                    scrollMessages.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
                        scrollMessages.setVvalue(Double.MAX_VALUE);
                    });

            }
        };
        thread.start();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/views/SideBarView.fxml"));
            GridPane gridPane = fxmlLoader.load();
            //SideBarController sideBarController = fxmlLoader.getController();
            gridChat.add(gridPane, 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
            Update Grid Message List With Messages
         */
        getMessages();
        try {
            fillChat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEnterMessage() throws IOException {
        String msg = message.getText();
        Message messageToSend = new Message();
        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("hh:mm");
        messageToSend.setSender(new User("SAID","SAID"));
        messageToSend.setMessage(msg);
        messageToSend.setDate_sent(df.format(date));
        messages.add(messageToSend);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/messageItem.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        ChatItemController chatItemController = fxmlLoader.getController();
        chatItemController.setData(messageToSend);
        grid.add(anchorPane, 0, messages.size());

        GridPane.setMargin(anchorPane, new Insets(10));
        message.setText("");
        scrollMessages.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            scrollMessages.setVvalue(scrollMessages.getVmax());
        });
    }

    public void fillChat() throws IOException {
        int column = 0;
        int row = 1;
        for (Message value : messages) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/views/messageItem.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            ChatItemController chatItemController = fxmlLoader.getController();
            chatItemController.setData(value);
            if (column == 1) {
                column = 0;
                row++;
            }
            grid.add(anchorPane, column++, row);
            GridPane.setMargin(anchorPane, new Insets(10));
        }
        scrollMessages.needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
            scrollMessages.setVvalue(Double.MAX_VALUE);
        });
    }



}
