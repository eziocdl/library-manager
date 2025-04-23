package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.sql.SQLException;

public class EditUserController {

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

    private User currentUser;
    private UserController userController;
    private UserService userService = new UserService();

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            populateFields();
        }
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    private void populateFields() {
        nameTextField.setText(currentUser.getName());
        emailTextField.setText(currentUser.getEmail());
        cpfTextField.setText(currentUser.getCpf());
        phoneTextField.setText(currentUser.getPhone());
        addressTextField.setText(currentUser.getAddress());
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (currentUser != null) {
            currentUser.setName(nameTextField.getText());
            currentUser.setEmail(emailTextField.getText());
            currentUser.setCpf(cpfTextField.getText());
            currentUser.setPhone(phoneTextField.getText());
            currentUser.setAddress(addressTextField.getText());

            try {
                userService.updateUser(currentUser); // Assumindo que você tem um método updateUser no UserService
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");
                if (userController != null) {
                    userController.showUserCardsView(); // Atualiza a visualização
                    handleCancel(event); // Volta para a tela de gerenciamento
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar o usuário: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Lógica para voltar para a tela de gerenciamento de usuários
        if (userController != null) {
            // Recarrega a UserView (em cards ou tabela, dependendo da última visualização)
            try {
                userController.cancelAddUserView(); // Reutilizando um método existente para voltar à UserView
            } catch (Exception e) {
                e.printStackTrace();
                // Lidar com a exceção ao tentar voltar
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}