package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image; // Adicionado para ImageView
import javafx.scene.image.ImageView; // Adicionado para ImageView
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException; // Adicionado para lidar com erros de imagem
import java.sql.SQLException;
import java.util.Objects; // Adicionado para Objects.requireNonNull

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
    @FXML
    private ImageView profileImageView; // Adicionado para a ImageView da foto de perfil

    private UserController userController; // Referência ao controlador pai
    private UserService userService; // Instância do serviço, injetada
    private File selectedProfileImageFile; // Arquivo de imagem de perfil selecionado
    private Stage dialogStage; // Palco (Stage) do diálogo modal

    /**
     * Construtor padrão.
     * O UserService será injetado via setUserService().
     */
    public AddUserController() {
        // Nenhuma inicialização de UserService aqui, pois ele será injetado.
    }

    /**
     * Define o controlador da tela principal de usuários que interage com este diálogo.
     *
     * @param userController O controlador da tela principal de usuários.
     */
    public void setUserController(UserController userController) {
        this.userController = userController;
        // REMOVIDO: A inicialização do userService a partir do userController.
        // Agora, userService será injetado diretamente via setUserService().
    }

    /**
     * **ADICIONADO:** Define o serviço de usuários.
     * Este método é chamado para injetar a dependência do UserService.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
    }

    /**
     * Define o palco (Stage) deste diálogo modal.
     * Útil para fechar a janela após a ação.
     *
     * @param dialogStage O palco do diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    /**
     * Método de inicialização do controlador. Chamado após o FXML ser carregado.
     */
    @FXML
    private void initialize() {
        // Define uma imagem padrão ao iniciar, se o profileImageView não estiver nulo.
        try {
            if (profileImageView != null) {
                Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_user_icon.png")));
                if (!defaultImage.isError()) {
                    profileImageView.setImage(defaultImage);
                } else {
                    logError("Erro ao carregar imagem padrão /images/default_user_icon.png (Image.isError() true)", null);
                }
            }
        } catch (NullPointerException e) {
            logError("Erro: Recurso /images/default_user_icon.png não encontrado.", e);
        } catch (Exception e) {
            logError("Erro inesperado ao configurar imagem padrão na inicialização", e);
        }
    }

    /**
     * Abre um diálogo para o usuário escolher um arquivo de imagem para a foto de perfil.
     * Atualiza o campo de texto e a ImageView com o caminho do arquivo selecionado.
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
            try {
                Image selectedImage = new Image(selectedProfileImageFile.toURI().toString());
                if (!selectedImage.isError()) {
                    profileImageView.setImage(selectedImage);
                } else {
                    logError("Erro ao carregar a imagem selecionada (Image.isError()): " + selectedProfileImageFile.getAbsolutePath(), null);
                    // Opcional: Voltar para a imagem padrão se a selecionada tiver erro
                    profileImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_user_icon.png"))));
                }
            } catch (Exception e) {
                logError("Erro ao exibir nova imagem de perfil: " + selectedProfileImageFile.getAbsolutePath(), e);
                // Em caso de erro, volta para a imagem padrão
                profileImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_user_icon.png"))));
            }
        }
    }

    /**
     * Salva as informações do novo usuário no banco de dados. Valida os campos de entrada
     * e exibe mensagens de sucesso ou erro. Atualiza a tela principal de usuários após a adição.
     */
    @FXML
    public void saveUser() {
        // Validação inicial do serviço
        if (userService == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Serviço de usuário não inicializado. Por favor, reinicie a aplicação.");
            logError("UserService é nulo em saveUser", new IllegalStateException("UserService não injetado."));
            return;
        }

        // Validação de entrada dos campos (exemplo simples, pode ser mais robusto com um método isInputValid)
        String name = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String cpf = cpfTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String address = addressTextField.getText().trim();
        String profileImagePath = (selectedProfileImageFile != null) ? selectedProfileImageFile.getAbsolutePath() : null;

        if (name.isEmpty() || email.isEmpty() || cpf.isEmpty()) { // Exemplo mínimo de validação
            showAlert(Alert.AlertType.WARNING, "Campos Obrigatórios", "Nome, Email e CPF são campos obrigatórios.");
            return;
        }
        // Adicionar mais validações aqui (formato de email, CPF, telefone, etc.)
        if (!email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            showAlert(Alert.AlertType.WARNING, "Formato Inválido", "Por favor, insira um email válido.");
            return;
        }
        // Remove caracteres não numéricos do CPF para validação
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        if (!cleanCpf.matches("\\d{11}")) {
            showAlert(Alert.AlertType.WARNING, "Formato Inválido", "Por favor, insira 11 dígitos para o CPF (somente números).");
            return;
        }

        // Remove caracteres não numéricos do telefone para validação
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (!cleanPhone.matches("\\d{10,11}")) {
            showAlert(Alert.AlertType.WARNING, "Formato Inválido", "Por favor, insira 10 ou 11 dígitos para o telefone (somente números, com DDD).");
            return;
        }


        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setCpf(cleanCpf); // Salva o CPF limpo
        user.setPhone(cleanPhone); // Salva o telefone limpo
        user.setAddress(address);
        user.setProfileImagePath(profileImagePath);



        try {
            userService.addUser(user);
            // Verifica se o userController foi definido antes de chamar showUserCardsView
            if (userController != null) {
                userController.showUserCardsView(); // Atualiza a lista de usuários na tela principal
            }
            clearInputFields(); // Limpa os campos após o sucesso
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário adicionado com sucesso!");
            closeDialog(); // Fecha o diálogo após salvar
        } catch (SQLException e) {
            logError("Erro ao salvar usuário no banco de dados", e);
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
        // Volta para a imagem padrão
        try {
            if (profileImageView != null) {
                profileImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_user_icon.png"))));
            }
        } catch (Exception e) {
            logError("Erro ao resetar imagem padrão após limpar campos", e);
        }
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
        System.err.print(message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }

    /**
     * Fecha o diálogo modal atual.
     * Usa o `dialogStage` se estiver definido, caso contrário, tenta obter o Stage de um dos elementos FXML.
     */
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            // Fallback: se dialogStage não foi definido, tenta obter o Stage de um elemento FXML.
            // Escolhemos saveButton, mas qualquer um dos elementos FXML seria suficiente.
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        }
    }
}