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
import java.util.Objects;

/**
 * Controlador para a tela de exibição e gerenciamento de usuários.
 * Permite visualizar usuários em cards, adicionar novos usuários, buscar e excluir usuários.
 */
public class UserController {

    @FXML
    FlowPane usersCardFlowPane;

    @FXML
    private TextField searchTextField;

    private UserService userService; // Agora, esta instância será injetada
    private RootLayoutController rootLayoutController;

    /**
     * **ADICIONADO:** Define o serviço de usuários.
     * Este método deve ser chamado por quem instancia o UserController (ex: RootLayoutController).
     * **Correção:** Mova a chamada de carregamento de dados para cá.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
        // **CORREÇÃO:** Chama o carregamento de dados aqui, pois o serviço está agora disponível.
        loadAllUsers();
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
     * Método de inicialização do controlador.
     * **CORREÇÃO:** Removida a chamada a loadAllUsers() daqui, pois o userService não está injetado ainda.
     */
    @FXML
    public void initialize() {
        // Lógica de inicialização do FXML, se houver.
        // O carregamento inicial dos dados (loadAllUsers) é feito em setUserService()
        // para garantir que o UserService esteja disponível.
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

            // Injetar o UserService no UserCardController
            if (userService == null) {
                logError("UserService é nulo ao criar UserCardController. Não é possível injetar.", new IllegalStateException("UserService é nulo."));
                return null;
            }
            controller.setUserService(userService); // INJEÇÃO CRÍTICA AQUI!
            controller.setRootLayoutController(rootLayoutController); // Passe o RootLayoutController para o card
            controller.setUserController(this); // Passa a referência deste controlador para callback (edição, remoção)

            controller.setUser(user); // Passa os dados do usuário para o UserCardController
            return userCard;
        } catch (IOException e) {
            logError("Erro ao carregar UserCard.fxml", e);
            return null;
        }
    }

    /**
     * **REINTRODUZIDO:** Método para carregar e exibir todos os usuários em formato de cards no FlowPane.
     * Este método delega a chamada para `loadAllUsers()`.
     * Isso é útil para manter a compatibilidade com chamadas existentes (`RootLayoutController.showUserView()`).
     */
    public void showUserCardsView() {
        loadAllUsers(); // Delega para o método principal de carregamento.
    }


    /**
     * Carrega e exibe todos os usuários em formato de cards no FlowPane.
     * Esta é a lógica principal de carregamento.
     */
    public void loadAllUsers() {
        if (usersCardFlowPane == null) {
            logError("usersCardFlowPane é nulo. Não é possível exibir os cards.", null);
            return;
        }
        usersCardFlowPane.getChildren().clear();
        if (userService == null) {
            logError("UserService é nulo ao tentar carregar todos os usuários.", new IllegalStateException("UserService não injetado."));
            showAlert("Erro de Inicialização", "O serviço de usuários não está disponível. Por favor, reinicie a aplicação.");
            return;
        }
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                usersCardFlowPane.getChildren().add(new Label("Nenhum usuário encontrado."));
            } else {
                for (User user : users) {
                    Pane userCard = createUserCard(user);
                    if (userCard != null) {
                        usersCardFlowPane.getChildren().add(userCard);
                    }
                }
            }
        } catch (SQLException e) {
            logError("Erro ao carregar usuários", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar os usuários: " + e.getMessage());
        }
    }

    /**
     * Lógica para excluir um usuário, chamada pelo UserCardController.
     * Atualiza a visualização após a exclusão.
     *
     * @param userId O ID do usuário a ser excluído.
     */
    public void handleDeleteUser(int userId) {
        if (userService == null) {
            logError("UserService é nulo ao tentar deletar usuário.", new IllegalStateException("UserService não injetado."));
            showAlert("Erro de Inicialização", "O serviço de usuários não está disponível.");
            return;
        }
        try {
            userService.deleteUser(userId);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário removido com sucesso!");
            loadAllUsers(); // Atualiza a lista após a exclusão
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

            // Injetar o UserService no AddUserController
            if (userService == null) {
                logError("UserService é nulo ao criar AddUserController. Não é possível injetar.", new IllegalStateException("UserService é nulo."));
                showAlert("Erro de Inicialização", "O serviço de usuários não está disponível para adicionar. Por favor, reinicie a aplicação.");
                return;
            }
            addUserController.setUserService(userService); // INJEÇÃO CRÍTICA AQUI!
            addUserController.setUserController(this); // Passa a referência deste controlador para callback

            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            // Definir o proprietário do diálogo
            if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
                stage.initOwner(rootLayoutController.getPrimaryStage());
            } else if (usersCardFlowPane != null && usersCardFlowPane.getScene() != null && usersCardFlowPane.getScene().getWindow() != null) {
                stage.initOwner(usersCardFlowPane.getScene().getWindow());
            } else {
                logError("Não foi possível definir o owner para o diálogo AddUserView.", null);
            }

            stage.setTitle("Adicionar Novo Usuário");
            stage.setScene(new javafx.scene.Scene(addUserView));
            stage.showAndWait();

            // Após a janela modal ser fechada, recarregue a UserView
            loadAllUsers();

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
        if (userService == null) {
            logError("UserService é nulo ao tentar buscar usuários.", new IllegalStateException("UserService não injetado."));
            showAlert("Erro de Inicialização", "O serviço de usuários não está disponível.");
            return;
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<User> searchResults = userService.findUsersByNameOrCPFOrEmail(searchTerm);
                updateUserCardDisplay(searchResults);
            } catch (SQLException e) {
                logError("Erro ao buscar usuários", e);
                showAlert("Erro ao Buscar", "Erro ao buscar usuários: " + e.getMessage());
            }
        } else {
            loadAllUsers(); // Se a busca estiver vazia, mostra todos os usuários
        }
    }

    /**
     * Atualiza a exibição dos cards de usuários com uma nova lista de usuários.
     *
     * @param userList A lista de usuários a serem exibidos.
     */
    private void updateUserCardDisplay(List<User> userList) {
        if (usersCardFlowPane == null) {
            logError("usersCardFlowPane é nulo. Não é possível atualizar a exibição.", null);
            return;
        }
        usersCardFlowPane.getChildren().clear();
        if (userList.isEmpty()) {
            usersCardFlowPane.getChildren().add(new Label("Nenhum usuário encontrado para a busca."));
            return;
        }
        for (User user : userList) {
            Pane userCard = createUserCard(user);
            if (userCard != null) {
                usersCardFlowPane.getChildren().add(userCard);
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
        loadAllUsers(); // Atualizar a visualização
    }

    /**
     * Exibe um diálogo de alerta com o tipo, título e conteúdo especificados.
     *
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
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
        // Tentar definir o owner do alerta para o palco principal, se disponível.
        // Isso ajuda a manter o alerta sobre a janela principal da aplicação.
        if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
            alert.initOwner(rootLayoutController.getPrimaryStage());
        } else if (usersCardFlowPane != null && usersCardFlowPane.getScene() != null && usersCardFlowPane.getScene().getWindow() != null) {
            alert.initOwner(usersCardFlowPane.getScene().getWindow());
        }
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
}