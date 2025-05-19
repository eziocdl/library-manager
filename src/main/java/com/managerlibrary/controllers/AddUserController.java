package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;

/**
 * Controlador para a tela de adicionar um novo usuário. Permite a entrada de dados do usuário
 * e salva as informações no banco de dados através do UserService.
 */
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
    private TextField profileImagePathTextField;
    @FXML
    private Button chooseProfileImageButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private UserController userController;
    private UserService userService;
    private File selectedProfileImageFile; // Arquivo de imagem de perfil selecionado

    /**
     * Define o controlador da tela principal de usuários que interage com este diálogo.
     * Garante que o UserService seja inicializado.
     *
     * @param userController O controlador da tela principal de usuários.
     */
    public void setUserController(UserController userController) {
        this.userController = userController;
        this.userService = userController.getUserService();
    }

    /**
     * Método de inicialização do controlador. Garante que o UserService esteja inicializado.
     */
    @FXML
    public void initialize() {
        if (userService == null && userController != null) {
            this.userService = userController.getUserService();
        }
    }

    /**
     * Abre um diálogo para o usuário escolher um arquivo de imagem para a foto de perfil.
     * Atualiza o campo de texto com o caminho do arquivo selecionado.
     */
    @FXML
    public void chooseProfileImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Foto de Perfil");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        Stage stage = (Stage) chooseProfileImageButton.getScene().getWindow();
        selectedProfileImageFile = fileChooser.showOpenDialog(stage);

        if (selectedProfileImageFile != null) {
            profileImagePathTextField.setText(selectedProfileImageFile.getAbsolutePath());
        }
    }

    /**
     * Salva as informações do novo usuário no banco de dados. Valida os campos de entrada
     * e exibe mensagens de sucesso ou erro. Atualiza a tela principal de usuários após a adição.
     */
    @FXML
    public void saveUser() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String cpf = cpfTextField.getText();
        String phone = phoneTextField.getText();
        String address = addressTextField.getText();
        String profileImagePath = (selectedProfileImageFile != null) ? selectedProfileImageFile.getAbsolutePath() : null;

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setCpf(cpf);
        user.setPhone(phone);
        user.setAddress(address);
        user.setProfileImagePath(profileImagePath);

        try {
            if (userService != null) {
                userService.addUser(user);
                userController.showUserCardsView(); // Atualiza a lista de usuários na tela principal
                clearInputFields(); // Limpa os campos após o sucesso
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário adicionado com sucesso!");
                closeDialog(); // Fecha o diálogo após salvar
            } else {
                showAlert(Alert.AlertType.ERROR, "Erro", "Serviço de usuário não inicializado.");
            }
        } catch (SQLException e) {
            logError("Erro ao salvar usuário", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Erro ao adicionar o usuário: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        } catch (Exception e) {
            logError("Erro inesperado ao salvar usuário", e);
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado ao salvar o usuário.");
        }
    }

    /**
     * Fecha o diálogo modal de adicionar usuário ao clicar no botão "Cancelar".
     *
     * @param event O evento de clique do botão.
     */
    @FXML
    public void cancelAddUser(ActionEvent event) {
        closeDialog();
    }

    /**
     * Limpa os campos de entrada do formulário.
     */
    private void clearInputFields() {
        nameTextField.clear();
        emailTextField.clear();
        cpfTextField.clear();
        phoneTextField.clear();
        addressTextField.clear();
        profileImagePathTextField.clear();
        selectedProfileImageFile = null;
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
     * Fecha o diálogo modal atual.
     */
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}