package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.SQLException;

public class UserController {

    @FXML
    private FlowPane usersFlowPane;

    private UserService userService = new UserService();
    private RootLayoutController rootLayoutController;

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @FXML
    public void initialize() {
        try {
            loadingUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAddUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddUserView.fxml"));
            Pane addUserView = loader.load();
            if (rootLayoutController != null) {
                rootLayoutController.setCenterView(addUserView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddUserView.fxml"));
            Pane addUserView = loader.load();

            TextField nameTextField = (TextField) addUserView.lookup("#nameTextField");
            TextField emailTextField = (TextField) addUserView.lookup("#emailTextField");
            TextField cpfTextField = (TextField) addUserView.lookup("#cpfTextField");
            TextField phoneTextField = (TextField) addUserView.lookup("#phoneTextField");
            TextField addressTextField = (TextField) addUserView.lookup("#addressTextField");

            String name = nameTextField.getText();
            String email = emailTextField.getText();
            String cpf = cpfTextField.getText();
            String phone = phoneTextField.getText();
            String address = addressTextField.getText();

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setCpf(cpf);
            user.setPhone(phone);
            user.setAddress(address);

            userService.addUser(user); // Assumindo que este método existe no seu UserService
            loadingUsers();
            cancelAddUserView();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelAddUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Pane userView = loader.load();
            if (rootLayoutController != null) {
                rootLayoutController.setCenterView(userView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadingUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsers()); // Assumindo este método no seu UserService
        usersFlowPane.getChildren().clear();
        for (User user : users) {
            VBox userCard = createUserCard(user);
            usersFlowPane.getChildren().add(userCard);
        }
    }

    private VBox createUserCard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserCardView.fxml"));
            VBox card = loader.load();
            Label nameLabel = (Label) card.lookup("#nameLabel");
            Label emailLabel = (Label) card.lookup("#emailLabel");
            Label cpfLabel = (Label) card.lookup("#cpfLabel");
            Label phoneLabel = (Label) card.lookup("#phoneLabel");
            Label addressLabel = (Label) card.lookup("#addressLabel");
            Button detailsButton = (Button) card.lookup("#detailsButton");

            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            cpfLabel.setText(user.getCpf());
            phoneLabel.setText(user.getPhone());
            addressLabel.setText(user.getAddress());
            detailsButton.setOnAction(event -> showUserDetails(user));

            return card;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    public void showUserDetails(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button detailsButton = (Button) event.getSource();
            VBox userCard = (VBox) detailsButton.getParent();
            Label nameLabel = (Label) userCard.lookup("#nameLabel");
            System.out.println("Detalhes do usuário: " + nameLabel.getText());
            // Implemente a lógica para exibir os detalhes completos do usuário
        }
    }

    public void showUserDetails(User user) {
        System.out.println("Detalhes do usuário: " + user.getName());
        // Outra forma de acessar os detalhes, se necessário
    }
}