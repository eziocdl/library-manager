package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserController {

    @FXML
    FlowPane usersCardFlowPane;

    @FXML
    private TextField searchTextField;

    private UserService userService = new UserService();
    private RootLayoutController rootLayoutController;

    public UserController() throws SQLException {
    }



    // Método para definir o RootLayoutController (você já deve ter este)
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
        // ... (sua lógica de inicialização existente) ...
    }

    // Adicione este método para obter o RootLayoutController
    public RootLayoutController getRootLayoutController() {
        return rootLayoutController;
    }

    // Getter para o UserService
    public UserService getUserService() {
        return userService;
    }

    // Inicialização do Controller
    @FXML
    public void initialize() {
        // Exibição dos cards, se o FlowPane estiver presente na view
        if (usersCardFlowPane != null) {
            showUserCardsView();
        }
    }

    // Cria um card visual para exibir as informações do usuário USANDO O UserCard.fxml
    private Pane createUserCard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserCard.fxml"));
            Pane userCard = loader.load();
            UserCardController controller = loader.getController();
            controller.setUser(user); // Passa os dados do usuário para o UserCardController
            controller.setUserListController(this); // Passa a referência do UserController para o UserCardController
            return userCard;
        } catch (IOException e) {
            e.printStackTrace();
            // Tratar erro ao carregar o FXML do card
            return null;
        }
    }

    // Exibe os usuários em formato de cards no FlowPane USANDO O UserCard.fxml
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
                e.printStackTrace();
            }
        }
    }

    // Lógica para excluir um usuário (chamado pelo UserCardController)
    public void handleDeleteUser(int userId) {
        try {
            userService.deleteUser(userId);
            showUserCardsView();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erro");
            errorAlert.setHeaderText("Erro ao remover usuário");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }

    // Exibe a tela para adicionar um novo usuário (modal)
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
            e.printStackTrace();
            System.err.println("Erro ao carregar views: " + e.getMessage());
        }
    }

    // Lógica para buscar usuários
    @FXML
    private void handleSearchUser() {
        String searchTerm = searchTextField.getText();
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<User> searchResults = userService.findUsersByNameOrCPFOrEmail(searchTerm);
                updateUserCardDisplay(searchResults);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showUserCardsView();
        }
    }

    // Atualiza a exibição dos cards com uma nova lista de usuários USANDO O UserCard.fxml
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

    // Ações dos botões na interface
    @FXML
    public void addUser(ActionEvent event) {
        showAddUserView();
    }

    @FXML
    public void searchUsers(ActionEvent event) {
        handleSearchUser();
    }

    // Método chamado ao cancelar a tela de adicionar usuário
    public void cancelAddUserView() {
        showUserCardsView(); // Atualizar a visualização
    }
}