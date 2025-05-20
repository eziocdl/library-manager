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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Controlador para o card individual de um usuário exibido na lista de usuários.
 * Responsável por exibir os detalhes do usuário e fornecer ações como editar e remover.
 */
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

    private User currentUser; // O usuário associado a este card
    private UserController userController; // RENOMEADO para clareza e consistência (era userListController)
    private UserService userService; // Instância do UserService injetada
    private RootLayoutController rootLayoutController; // NOVO: Referência ao RootLayoutController
    private File selectedProfileImageFileForEdit; // Arquivo de imagem de perfil selecionado para edição

    /**
     * Construtor padrão da classe.
     */
    public UserCardController() {
        // O UserService, UserController e RootLayoutController serão injetados via setters.
    }

    /**
     * Define o serviço de usuários.
     * Este método deve ser chamado por quem instancia o UserCardController (ex: UserController).
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
    }

    /**
     * Define o controlador da tela principal de usuários para permitir ações na lista.
     * **RENOMEADO:** De setUserListController para setUserController para maior clareza.
     *
     * @param userController O controlador da tela principal de usuários.
     */
    public void setUserController(UserController userController) {
        this.userController = Objects.requireNonNull(userController, "UserController não pode ser nulo.");
    }

    /**
     * **NOVO MÉTODO:** Define o controlador do layout raiz da aplicação.
     * Usado para acessar o Stage principal para diálogos modais, por exemplo.
     *
     * @param rootLayoutController O controlador do layout raiz.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = Objects.requireNonNull(rootLayoutController, "RootLayoutController não pode ser nulo.");
    }

    /**
     * Define o usuário a ser exibido neste card e atualiza as informações visuais.
     *
     * @param user O usuário a ser exibido.
     */
    public void setUser(User user) {
        this.currentUser = user;
        updateCard(user);
    }

    /**
     * Atualiza os elementos visuais do card com as informações do usuário fornecido.
     * Formata o CPF e o telefone para melhor exibição e carrega a imagem de perfil.
     *
     * @param user O usuário cujas informações serão exibidas no card.
     */
    private void updateCard(User user) {
        nameLabel.setText(user.getName());
        emailLabel.setText(user.getEmail());
        cpfLabel.setText("CPF: " + formatCpf(user.getCpf()));
        phoneLabel.setText("Telefone: " + formatPhone(user.getPhone()));
        addressLabel.setText("Endereço: " + user.getAddress());
        loadProfileImage(user.getProfileImagePath(), user.getName());
    }

    /**
     * Carrega a imagem de perfil do usuário, exibindo uma imagem padrão em caso de erro ou se não houver imagem.
     *
     * @param imagePath O caminho da imagem de perfil do usuário.
     * @param userName  O nome do usuário (usado para logs).
     */
    private void loadProfileImage(String imagePath, String userName) {
        profileImageView.setImage(null); // Limpa a imagem anterior
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            // Removidos os System.out.println para um ambiente de produção
            try {
                String uriString = file.toURI().toString();
                profileImageView.setImage(new Image(uriString));
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem para " + userName + ": " + e.getMessage());
                profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_error.png")));
            }
        } else {
            profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user_icon.png")));
        }
    }

    /**
     * Manipula o evento de clique no botão de editar usuário. Carrega a tela de edição
     * para o usuário associado a este card.
     */
    @FXML
    private void handleEditUser() {
        System.out.println("Editar usuário: " + currentUser.getName());
        loadEditUserView(currentUser);
    }

    /**
     * Carrega a tela de edição de usuário em um diálogo modal, preenchendo os campos
     * com os dados do usuário a ser editado.
     *
     * @param userToEdit O usuário a ser editado.
     */
    private void loadEditUserView(User userToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditUserView.fxml"));
            VBox editUserView = loader.load();
            EditUserController editUserController = loader.getController();

            // 1. Injetar o usuário a ser editado
            editUserController.setUser(userToEdit);

            // 2. Injetar o UserService (EditUserController provavelmente precisará dele para salvar as alterações)
            if (userService != null) {
                editUserController.setUserService(userService);
            } else {
                logError("UserService é nulo ao tentar passar para EditUserController.", new IllegalStateException("UserService é nulo."));
                showAlert("Erro de Inicialização", "O serviço de usuários não está disponível para edição.");
                return; // Impede a abertura da janela se o serviço for nulo
            }

            // 3. Injetar a referência de volta para o UserController pai (para atualizar a lista)
            if (userController != null) {
                editUserController.setUserController(userController);
            } else {
                logError("UserController é nulo ao tentar passar para EditUserController.", new IllegalStateException("UserController é nulo."));
            }

            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            // **CORREÇÃO CRUCIAL:** Usar RootLayoutController para obter o primaryStage como owner
            if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
                stage.initOwner(rootLayoutController.getPrimaryStage());
            } else if (userCard.getScene() != null && userCard.getScene().getWindow() != null) { // Fallback
                stage.initOwner(userCard.getScene().getWindow());
            } else {
                logError("Não foi possível definir o owner para o diálogo EditUserView.", null);
            }


            stage.setTitle("Editar Usuário");
            stage.setScene(new javafx.scene.Scene(editUserView));
            stage.showAndWait();

            // Após a janela de edição ser fechada, a lista de usuários será atualizada
            // Chama loadAllUsers() no UserController, que já tem a lógica de recarregar.
            if (userController != null) {
                userController.loadAllUsers();
            } else {
                logError("UserController é nulo. Não foi possível atualizar a lista de usuários após edição.", null);
            }


        } catch (IOException e) {
            logError("Erro ao carregar tela de edição", e);
            showAlert("Erro ao Carregar", "Erro ao carregar tela de edição: " + e.getMessage());
        }
    }

    /**
     * Manipula o evento de clique no botão de remover usuário. Exibe um diálogo de
     * confirmação antes de chamar o método de exclusão no UserController.
     */
    @FXML
    private void handleDeleteUser() {
        if (currentUser != null && userController != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmar Remoção");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Tem certeza que deseja remover o usuário: " + currentUser.getName() + "?");
            // Definir o owner do alerta
            if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
                confirmation.initOwner(rootLayoutController.getPrimaryStage());
            } else if (userCard.getScene() != null && userCard.getScene().getWindow() != null) { // Fallback
                confirmation.initOwner(userCard.getScene().getWindow());
            }

            confirmation.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    userController.handleDeleteUser(currentUser.getId());
                }
            });
        }
    }

    /**
     * Formata o CPF para exibição no padrão "XXX.XXX.XXX-XX".
     *
     * @param cpf O CPF a ser formatado.
     * @return O CPF formatado ou o CPF original se não corresponder ao padrão.
     */
    private String formatCpf(String cpf) {
        if (cpf != null && cpf.matches("\\d{11}")) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf;
    }

    /**
     * Formata o Telefone para exibição nos padrões "(XX) XXXX-XXXX" ou "(XX) XXXXX-XXXX".
     *
     * @param phone O telefone a ser formatado.
     * @return O telefone formatado ou o telefone original se não corresponder ao padrão.
     */
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

    /**
     * Exibe um diálogo de alerta com a mensagem especificada.
     *
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Tentar definir o owner do alerta para o palco principal, se disponível.
        if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
            alert.initOwner(rootLayoutController.getPrimaryStage());
        } else if (userCard.getScene() != null && userCard.getScene().getWindow() != null) { // Fallback
            alert.initOwner(userCard.getScene().getWindow());
        }
        alert.showAndWait();
    }

    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida.
     */
    private void logError(String message, Exception e) {
        System.err.print("ERRO: " + message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }
}