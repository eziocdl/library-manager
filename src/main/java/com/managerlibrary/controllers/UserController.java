package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para a tela de exibição e gerenciamento de usuários.
 * Permite visualizar usuários em cards, adicionar novos usuários, buscar e excluir usuários.
 */
public class UserController {

    @FXML
    FlowPane usersCardFlowPane;

    @FXML
    private TextField searchTextField;

    private UserService userService;
    private RootLayoutController rootLayoutController;

    /**
     * Construtor padrão da classe. Inicializa o UserService.
     *
     * @throws SQLException Se ocorrer um erro ao conectar ao banco de dados.
     */
    public UserController() throws SQLException {
        this.userService = new UserService();
    }

    /**
     * Define o controlador principal da aplicação (RootLayoutController).
     *
     * @param rootLayoutController O controlador principal.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    /**
     * Obtém o controlador principal da aplicação (RootLayoutController).
     *
     * @return O controlador principal.
     */
    public RootLayoutController getRootLayoutController() {
        return rootLayoutController;
    }

    /**
     * Obtém o serviço de usuários.
     *
     * @return O serviço de usuários.
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * Método de inicialização do controlador. Exibe os cards de usuários se o FlowPane estiver presente na view.
     */
    @FXML
    public void initialize() {
        if (usersCardFlowPane != null) {
            showUserCardsView();
        }
    }

    /**
     * Cria um card visual para exibir as informações do usuário usando o FXML UserCard.fxml.
     *
     * @param user O usuário cujas informações serão exibidas no card.
     * @return O Pane (card) criado para o usuário, ou null em caso de erro.
     */
    private Pane createUserCard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserCard.fxml"));
            Pane userCard = loader.load();
            UserCardController controller = loader.getController();
            controller.setUser(user); // Passa os dados do usuário para o UserCardController
            controller.setUserListController(this); // Passa a referência do UserController para o UserCardController
            return userCard;
        } catch (IOException e) {
            logError("Erro ao carregar UserCard.fxml", e);
            return null;
        }
    }

    /**
     * Exibe os usuários em formato de cards no FlowPane, carregando os dados do banco de dados.
     */
    public void showUserCardsView() {
        if (usersCardFlowPane != null) {
            usersCardFlowPane.getChildren().clear();
            try {
                List<User> users = userService.getAllUsers();
                for (User user : users) {
                    Pane userCard = createUserCard(user);
                    if (userCard != null) {
                        usersCardFlowPane.getChildren().add(userCard);
                    }
                }
            } catch (SQLException e) {
                logError("Erro ao carregar usuários", e);
                showAlert("Erro ao Carregar", "Não foi possível carregar os usuários.");
            }
        }
    }

    /**
     * Lógica para excluir um usuário, chamada pelo UserCardController.
     * Atualiza a visualização após a exclusão.
     *
     * @param userId O ID do usuário a ser excluído.
     */
    public void handleDeleteUser(int userId) {
        try {
            userService.deleteUser(userId);
            showUserCardsView();
        } catch (SQLException e) {
            logError("Erro ao remover usuário", e);
            showAlert("Erro ao Remover", "Erro ao remover usuário: " + e.getMessage());
        }
    }

    /**
     * Exibe a tela para adicionar um novo usuário em um diálogo modal.
     * Atualiza a visualização após o fechamento do diálogo.
     */
    public void showAddUserView() {
        try {
            FXMLLoader loaderAddUser = new FXMLLoader(getClass().getResource("/views/AddUserView.fxml"));
            Pane addUserView = loaderAddUser.load();
            AddUserController addUserController = loaderAddUser.getController();
            addUserController.setUserController(this);

            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.initOwner(usersCardFlowPane.getScene().getWindow());
            stage.setTitle("Adicionar Novo Usuário");
            stage.setScene(new javafx.scene.Scene(addUserView));
            stage.showAndWait();

            // Após a janela modal ser fechada, recarregue a UserView
            showUserCardsView();

        } catch (IOException e) {
            logError("Erro ao carregar AddUserView.fxml", e);
            showAlert("Erro ao Carregar", "Erro ao carregar tela de adicionar usuário: " + e.getMessage());
        }
    }

    /**
     * Lógica para buscar usuários com base no termo de pesquisa (nome, CPF ou email).
     * Atualiza a exibição dos cards com os resultados da busca.
     */
    @FXML
    private void handleSearchUser() {
        String searchTerm = searchTextField.getText();
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<User> searchResults = userService.findUsersByNameOrCPFOrEmail(searchTerm);
                updateUserCardDisplay(searchResults);
            } catch (SQLException e) {
                logError("Erro ao buscar usuários", e);
                showAlert("Erro ao Buscar", "Erro ao buscar usuários: " + e.getMessage());
            }
        } else {
            showUserCardsView();
        }
    }

    /**
     * Atualiza a exibição dos cards de usuários com uma nova lista de usuários.
     *
     * @param userList A lista de usuários a serem exibidos.
     */
    private void updateUserCardDisplay(List<User> userList) {
        if (usersCardFlowPane != null) {
            usersCardFlowPane.getChildren().clear();
            for (User user : userList) {
                Pane userCard = createUserCard(user);
                if (userCard != null) {
                    usersCardFlowPane.getChildren().add(userCard);
                }
            }
        }
    }

    /**
     * Ação do botão para exibir a tela de adicionar usuário.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void addUser(ActionEvent event) {
        showAddUserView();
    }

    /**
     * Ação do botão para realizar a busca de usuários.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void searchUsers(ActionEvent event) {
        handleSearchUser();
    }

    /**
     * Método chamado ao cancelar a tela de adicionar usuário (atualmente não utilizado diretamente,
     * a atualização ocorre após o fechamento da janela modal).
     */
    public void cancelAddUserView() {
        showUserCardsView(); // Atualizar a visualização
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
}