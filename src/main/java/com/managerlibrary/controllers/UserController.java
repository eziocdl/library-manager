package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserController {

    @FXML
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> addressColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> cpfColumn;

    @FXML
    private ComboBox<String> searchCriteriaComboBox;
    @FXML
    private TextField searchTextField;

    @FXML
    private TextField nameTextField; // Referências para AddUserView
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField cpfTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField addressTextField;

    private UserService userService = new UserService();
    private RootLayoutController rootLayoutController;

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        cpfColumn.setCellValueFactory(cellData -> cellData.getValue().cpfProperty());

        try {
            loadingUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAddUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddUserView.fxml"));
            Pane addUserView = loader.load();
            if (rootLayoutController != null) {
                rootLayoutController.setCenterView(addUserView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveUser() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String cpf = cpfTextField.getText();
        String phone = phoneTextField.getText();
        String address = addressTextField.getText();

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setCpf(cpf);
        user.setPhone(phone);
        user.setAddress(address);

        try {
            userService.addUser(user);
            loadingUsers();
            cancelAddUserView();
        } catch (SQLException e) {
            e.printStackTrace();
            // Lide com erros de banco de dados aqui (exiba mensagem ao usuário)
        } catch (IllegalArgumentException e) {
            // Lide com erros de validação aqui (exiba mensagem ao usuário)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Validação");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelAddUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Pane userView = loader.load();
            if (rootLayoutController != null) {
                rootLayoutController.setCenterView(userView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchUser() {
        String searchTerm = searchTextField.getText();
        String searchCriteria = searchCriteriaComboBox.getValue();

        if (searchTerm != null && !searchTerm.trim().isEmpty() && searchCriteria != null) {
            try {
                List<User> searchResults = null;
                if (searchCriteria.equals("Nome")) {
                    searchResults = userService.findUsersByName(searchTerm);
                } else if (searchCriteria.equals("CPF")) {
                    User user = userService.findUserByCPF(searchTerm);
                    if (user != null) {
                        searchResults = List.of(user);
                    } else {
                        searchResults = List.of();
                    }
                }
                updateUserDisplay(searchResults);
            } catch (SQLException e) {
                e.printStackTrace();
                // Lide com erro de busca
            }
        } else {
            try {
                loadingUsers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadingUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsers());
        usersTableView.setItems(users);
    }

    private void updateUserDisplay(List<User> userList) {
        ObservableList<User> observableUserList = FXCollections.observableArrayList(userList);
        usersTableView.setItems(observableUserList);
    }

    @FXML
    public void addUser(ActionEvent event) {
        showAddUserView();
    }

    @FXML
    public void searchUsers(ActionEvent event) {
        handleSearchUser();
    }

    @FXML
    public void updateUsers(ActionEvent event) {
        // Implemente a lógica para atualizar um usuário selecionado na TableView
    }

    @FXML
    public void deleteUsers(ActionEvent event) {
        // Implemente a lógica para deletar um usuário selecionado na TableView
    }
}