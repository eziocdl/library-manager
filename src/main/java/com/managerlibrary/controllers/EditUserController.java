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
import java.io.IOException; // Adicionado para tratamento de erros de IO em imagens
import java.sql.SQLException;
import java.util.Objects; // Adicionado para Objects.requireNonNull

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
    private UserService userService; // Injetado via setUserService
    private File selectedProfileImageFile; // Arquivo de imagem de perfil selecionado
    private Stage dialogStage; // Palco (Stage) do diálogo modal

    /**
     * Construtor padrão da classe.
     * O UserService será definido via setUserService.
     * Removida a inicialização direta do UserService para permitir injeção de dependência.
     */
    public EditUserController() {
        // O UserService será injetado através do método setUserService().
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
     * **CORRIGIDO:** Renomeado de 'setService' para 'setUserService' para corresponder à chamada.
     * Define o serviço de usuários. Este é o ponto de injeção preferencial para o UserService.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) { // Renomeado de setService para setUserService
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
    }

    /**
     * Define o palco (Stage) deste diálogo modal.
     *
     * @param dialogStage O palco do diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Preenche os campos do formulário com os dados do usuário a ser editado e carrega sua foto de perfil.
     */
    private void populateFields() {
        if (currentUser == null) {
            logError("Erro: currentUser é nulo ao tentar popular campos.", null);
            return;
        }
        nameTextField.setText(currentUser.getName() != null ? currentUser.getName() : "");
        emailTextField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        cpfTextField.setText(currentUser.getCpf() != null ? currentUser.getCpf() : "");
        phoneTextField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        addressTextField.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        profileImagePathTextField.setText(currentUser.getProfileImagePath() != null ? currentUser.getProfileImagePath() : "");
        loadProfileImage();
    }

    /**
     * Carrega a imagem de perfil do usuário, exibindo uma imagem padrão em caso de erro ou se não houver imagem.
     */
    private void loadProfileImage() {
        Image imageToSet = null;

        // Limpa a imagem anterior
        profileImageView.setImage(null);

        // Tenta carregar a imagem do caminho do perfil se existir
        if (currentUser != null && currentUser.getProfileImagePath() != null && !currentUser.getProfileImagePath().isEmpty()) {
            try {
                File file = new File(currentUser.getProfileImagePath());
                if (file.exists()) {
                    imageToSet = new Image(file.toURI().toString());
                    if (!imageToSet.isError()) {
                        profileImageView.setImage(imageToSet);
                        return; // Imagem carregada com sucesso, sai do método
                    } else {
                        logError("Erro ao carregar imagem de perfil do arquivo (Image.isError()): " + currentUser.getProfileImagePath(), null);
                    }
                } else {
                    // logError("Arquivo de imagem de perfil não encontrado: " + currentUser.getProfileImagePath(), null); // Para debug
                }
            } catch (Exception e) {
                logError("Erro ao carregar imagem de perfil do arquivo: " + currentUser.getProfileImagePath(), e);
            }
        }

        // Se a imagem do perfil não foi carregada ou houve erro, tenta a imagem padrão
        try {
            // Usar o ícone de usuário mais genérico
            imageToSet = new Image(getClass().getResourceAsStream("/images/default_user_icon.png"));
            if (imageToSet.isError()) { // Verificação extra para a imagem padrão
                throw new IOException("Erro ao carregar default_user_icon.png");
            }
        } catch (IOException e) {
            logError("Erro ao carregar imagem padrão /images/default_user_icon.png", e);
            // Em último caso, tenta outra imagem padrão ou deixa nulo
            try {
                imageToSet = new Image(getClass().getResourceAsStream("/images/default_user.png")); // Outra opção de padrão
                if (imageToSet.isError()) {
                    throw new IOException("Erro ao carregar default_user.png");
                }
            } catch (IOException ex) {
                logError("Erro ao carregar imagem padrão /images/default_user.png", ex);
                imageToSet = null; // Não foi possível carregar nenhuma imagem
            }
        }
        profileImageView.setImage(imageToSet);
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
                // Tenta carregar a imagem selecionada na ImageView
                Image selectedImage = new Image(selectedProfileImageFile.toURI().toString());
                if (!selectedImage.isError()) {
                    profileImageView.setImage(selectedImage);
                } else {
                    logError("Erro ao carregar a imagem de perfil selecionada (Image.isError()): " + selectedProfileImageFile.getAbsolutePath(), null);
                    // Se a imagem selecionada tiver erro, volta para a imagem padrão
                    loadProfileImage();
                }
            } catch (Exception e) {
                logError("Erro ao carregar nova imagem de perfil: " + selectedProfileImageFile.getAbsolutePath(), e);
                // Em caso de erro, carrega a imagem padrão
                loadProfileImage();
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
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Nenhum usuário selecionado para salvar.");
            return;
        }
        if (userService == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Serviço de usuário não inicializado. Por favor, reinicie a aplicação.");
            logError("UserService é nulo em saveEditedUser", new IllegalStateException("UserService não injetado."));
            return;
        }

        // Validação básica dos campos
        String name = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String cpf = cpfTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String address = addressTextField.getText().trim();

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", "O nome do usuário é obrigatório.");
            return;
        }
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", "O email do usuário é obrigatório.");
            return;
        }
        // Validação de formato de email mais robusta
        if (!email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            showAlert(Alert.AlertType.WARNING, "Formato Inválido", "Por favor, insira um email válido.");
            return;
        }
        if (cpf.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", "O CPF do usuário é obrigatório.");
            return;
        }
        // Validação de formato de CPF (apenas números ou com máscara padrão XXX.XXX.XXX-XX)
        if (!cpf.matches("^\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}$")) {
            showAlert(Alert.AlertType.WARNING, "Formato Inválido", "Por favor, insira um CPF válido (somente números ou formato XXX.XXX.XXX-XX).");
            return;
        }
        // Você pode adicionar mais validações para telefone, endereço, etc.

        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setCpf(cpf);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        if (selectedProfileImageFile != null) {
            currentUser.setProfileImagePath(selectedProfileImageFile.getAbsolutePath());
        } else if (profileImagePathTextField.getText().trim().isEmpty()) {
            // Se o campo de texto foi limpo e nenhuma nova imagem foi selecionada,
            // significa que o usuário quer remover a imagem de perfil.
            currentUser.setProfileImagePath(null);
        }
        // Se selectedProfileImageFile for nulo e profileImagePathTextField não foi alterado,
        // mantém o valor existente de currentUser.getProfileImagePath(), que é o comportamento desejado.


        try {
            userService.updateUser(currentUser);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");
            if (userController != null) {
                userController.showUserCardsView(); // Atualiza a visualização
            }
            closeDialog();
        } catch (SQLException e) {
            logError("Erro ao atualizar o usuário no banco de dados", e);
            showAlert(Alert.AlertType.ERROR, "Erro no Banco de Dados", "Erro ao atualizar o usuário: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", e.getMessage());
        } catch (Exception e) {
            logError("Erro inesperado ao salvar edição do usuário", e);
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado ao salvar o usuário.");
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
     * @param e       A exceção ocorrida, pode ser nula.
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
     * Fecha a janela (Stage) atual.
     * Usa o `dialogStage` se estiver definido, caso contrário, usa a cena de um dos elementos FXML.
     */
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        } else {
            // Fallback: se dialogStage não foi definido (e.g., se não for um modal),
            // tenta obter o Stage de um elemento FXML.
            // Escolhemos saveEditedUser, mas qualquer um dos elementos FXML serviria.
            Stage stage = (Stage) saveEditedUser.getScene().getWindow();
            stage.close();
        }
    }
}