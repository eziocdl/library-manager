package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class UserCardController {

    @FXML
    private VBox userCard;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label cpfLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;

    private User currentUser;
    private UserController userListController; // Referência ao UserController para ações na lista
    private UserService userService = new UserService();
    private File selectedProfileImageFileForEdit;

    public UserCardController() throws SQLException {
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateCard(user);
    }

    public void setUserListController(UserController userListController) {
        this.userListController = userListController;
    }

    private void updateCard(User user) {
        nameLabel.setText(user.getName());
        emailLabel.setText(user.getEmail());
        cpfLabel.setText("CPF: " + formatCpf(user.getCpf()));
        phoneLabel.setText("Telefone: " + formatPhone(user.getPhone()));
        addressLabel.setText("Endereço: " + user.getAddress());

        String imagePath = user.getProfileImagePath();
        System.out.println("Caminho da imagem para " + user.getName() + ": [" + imagePath + "]");

        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            System.out.println("Arquivo existe para " + user.getName() + "? " + file.exists());
            System.out.println("Caminho absoluto do arquivo: " + file.getAbsolutePath());
            try {
                String uriString = file.toURI().toString();
                System.out.println("URI para " + user.getName() + ": [" + uriString + "]");
                profileImageView.setImage(new Image(uriString));
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem para " + user.getName() + ": " + e.getMessage());
                profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_error.png")));
            }
        } else {
            profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_icon.png")));
        }
    }

    @FXML
    private void handleEditUser() {
        System.out.println("Editar usuário: " + currentUser.getName());
        loadEditUserView(currentUser);
    }

    private void loadEditUserView(User userToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditUserView.fxml"));
            VBox editUserView = loader.load();
            EditUserController editUserController = loader.getController();
            editUserController.setUser(userToEdit);
            editUserController.setUserController(userListController); // Passa a referência para atualizar a lista

            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.initOwner(userCard.getScene().getWindow());
            stage.setTitle("Editar Usuário");
            stage.setScene(new javafx.scene.Scene(editUserView));
            stage.showAndWait();

            // Após a janela de edição ser fechada, a lista de usuários será atualizada
            userListController.showUserCardsView();

        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erro");
            errorAlert.setHeaderText("Erro ao carregar tela de edição");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }


    @FXML
    private void handleDeleteUser() {
        if (currentUser != null && userListController != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmar Remoção");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Tem certeza que deseja remover o usuário: " + currentUser.getName() + "?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    userListController.handleDeleteUser(currentUser.getId());
                }
            });
        }
    }

    // Formata o CPF para exibição
    private String formatCpf(String cpf) {
        if (cpf != null && cpf.matches("\\d{11}")) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf;
    }

    // Formata o Telefone para exibição
    private String formatPhone(String phone) {
        if (phone != null && phone.matches("\\d{10,11}")) {
            if (phone.length() == 10) {
                return "(" + phone.substring(0, 2) + ") " + phone.substring(2, 6) + "-" + phone.substring(6);
            } else if (phone.length() == 11) {
                return "(" + phone.substring(0, 2) + ") " + phone.substring(2, 7) + "-" + phone.substring(7);
            }
        }
        return phone;
    }
}