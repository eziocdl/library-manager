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

/**
 * Controlador para a tela de edição de um usuário existente. Permite modificar
 * os dados do usuário e salvar as alterações no banco de dados.
 */
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
    private Button saveEditedUser;
    @FXML
    private Button cancelEditUser;

    private User currentUser; // O usuário atualmente sendo editado
    private UserController userController; // Controlador da tela principal de usuários
    private UserService userService;
    private File selectedProfileImageFile; // Arquivo de imagem de perfil selecionado

    /**
     * Construtor padrão da classe. Inicializa o UserService.
     *
     * @throws SQLException Se ocorrer um erro ao conectar ao banco de dados.
     */
    public EditUserController() throws SQLException {
        this.userService = new UserService();
    }

    /**
     * Define o usuário a ser editado e preenche os campos da tela com seus dados.
     *
     * @param user O usuário a ser editado.
     */
    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            populateFields();
        }
    }

    /**
     * Define o controlador da tela principal de usuários para permitir a atualização da visualização.
     *
     * @param userController O controlador da tela principal de usuários.
     */
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    /**
     * Define o serviço de usuários.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Preenche os campos do formulário com os dados do usuário a ser editado e carrega sua foto de perfil.
     */
    private void populateFields() {
        nameTextField.setText(currentUser.getName());
        emailTextField.setText(currentUser.getEmail());
        cpfTextField.setText(currentUser.getCpf());
        phoneTextField.setText(currentUser.getPhone());
        addressTextField.setText(currentUser.getAddress());
        profileImagePathTextField.setText(currentUser.getProfileImagePath());
        loadProfileImage();
    }

    /**
     * Carrega a imagem de perfil do usuário, exibindo uma imagem padrão em caso de erro ou se não houver imagem.
     */
    private void loadProfileImage() {
        if (currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            try {
                File file = new File(currentUser.getProfileImagePath());
                if (file.exists()) {
                    profileImageView.setImage(new Image(file.toURI().toString()));
                } else {
                    profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
                }
            } catch (Exception e) {
                logError("Erro ao carregar imagem de perfil", e);
                profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_error.png")));
            }
        } else {
            profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_icon.png")));
        }
    }

    /**
     * Abre um diálogo para o usuário escolher uma nova foto de perfil.
     * Atualiza o campo de texto e a ImageView com a imagem selecionada.
     *
     * @param event O evento de clique no botão.
     */
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
                logError("Erro ao carregar nova imagem de perfil", e);
            }
        }
    }

    /**
     * Salva as alterações feitas no usuário no banco de dados e atualiza a visualização na tela principal.
     * Exibe mensagens de sucesso ou erro.
     *
     * @param event O evento de clique no botão "Salvar".
     */
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
                }
                closeDialog();
            } catch (SQLException e) {
                logError("Erro ao atualizar o usuário", e);
                showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar o usuário: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
            }
        }
    }

    /**
     * Fecha a tela de edição de usuário ao clicar no botão "Cancelar".
     *
     * @param event O evento de clique no botão.
     */
    @FXML
    private void cancelEditUser(ActionEvent event) {
        closeDialog();
    }

    /**
     * Exibe um diálogo de alerta com o tipo, título e conteúdo especificados.
     *
     * @param alertType O tipo do alerta (INFORMATION, ERROR, WARNING, etc.).
     * @param title     O título do alerta.
     * @param content   O conteúdo da mensagem do alerta.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida.
     */
    private void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Fecha a janela (Stage) atual.
     */
    private void closeDialog() {
        Stage stage = (Stage) cancelEditUser.getScene().getWindow();
        stage.close();
    }
}