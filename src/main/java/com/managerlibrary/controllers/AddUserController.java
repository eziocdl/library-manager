package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.sql.SQLException;

public class AddUserController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField cpfTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField addressTextField;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private UserController userController;
    private UserService userService = new UserService();

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    @FXML
    public void saveUser() {
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

        try {
            userService.addUser(user);
            userController.showUserCardsView();
            userController.setCenterView((Pane) userController.getUsersCardFlowPane().getParent()); // Cast para Pane
            clearInputFields();
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário adicionado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Erro ao adicionar o usuário: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        }
    }

    @FXML
    public void cancelAddUserView() {
        userController.setCenterView((Pane) userController.getUsersCardFlowPane().getParent()); // Cast para Pane
    }

    private void clearInputFields() {
        nameTextField.clear();
        emailTextField.clear();
        cpfTextField.clear();
        phoneTextField.clear();
        addressTextField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}