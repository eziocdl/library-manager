package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
    private TextField profileImagePathTextField;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button chooseProfileImageButton;

    @FXML
    private Button saveEditedUser; // Corrigido o fx:id

    @FXML
    private Button cancelEditUser;   // Corrigido o fx:id

    private User currentUser;
    private UserController userController;
    private UserService userService;
    private File selectedProfileImageFile;

    // Adicione este construtor
    public EditUserController() throws SQLException {
        this.userService = new UserService();
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            populateFields();
        }
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public void setService(UserService userService) {
        this.userService = userService;
    }

    private void populateFields() {
        nameTextField.setText(currentUser.getName());
        emailTextField.setText(currentUser.getEmail());
        cpfTextField.setText(currentUser.getCpf());
        phoneTextField.setText(currentUser.getPhone());
        addressTextField.setText(currentUser.getAddress());
        profileImagePathTextField.setText(currentUser.getProfileImagePath());
        if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            try {
                File file = new File(currentUser.getProfileImagePath());
                if (file.exists()) {
                    profileImageView.setImage(new Image(file.toURI().toString()));
                } else {
                    profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
                }
            } catch (Exception e) {
                profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_error.png")));
            }
        } else {
            profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_icon.png")));
        }
    }

    @FXML
    private void chooseProfileImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Nova Foto de Perfil");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        Stage stage = (Stage) chooseProfileImageButton.getScene().getWindow();
        selectedProfileImageFile = fileChooser.showOpenDialog(stage);

        if (selectedProfileImageFile != null) {
            profileImagePathTextField.setText(selectedProfileImageFile.getAbsolutePath());
            try {
                profileImageView.setImage(new Image(selectedProfileImageFile.toURI().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveEditedUser(ActionEvent event) {
        if (currentUser != null) {
            currentUser.setName(nameTextField.getText());
            currentUser.setEmail(emailTextField.getText());
            currentUser.setCpf(cpfTextField.getText());
            currentUser.setPhone(phoneTextField.getText());
            currentUser.setAddress(addressTextField.getText());
            if (selectedProfileImageFile != null) {
                currentUser.setProfileImagePath(selectedProfileImageFile.getAbsolutePath());
            }

            try {
                userService.updateUser(currentUser);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");
                if (userController != null) {
                    userController.showUserCardsView(); // Atualiza a visualização
                    // REMOVA A LINHA QUE FECHA A JANELA DE EDIÇÃO
                    // Stage stage = (Stage) saveEditedUser.getScene().getWindow();
                    // stage.close();
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
    private void cancelEditUser(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}