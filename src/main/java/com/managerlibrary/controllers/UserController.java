package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    @FXML
    FlowPane usersCardFlowPane;

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
    private ComboBox<String> searchCriteriaComboBox; // Removido do FXML, manter aqui se usar em outra parte
    @FXML
    private TextField searchTextField;

    private UserService userService = new UserService();
    private RootLayoutController rootLayoutController;

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    public RootLayoutController getRootLayoutController() {
        return rootLayoutController;
    }

    public FlowPane getUsersCardFlowPane() {
        return usersCardFlowPane;
    }

    public UserService getUserService() {
        return userService;
    }

    @FXML
    public void initialize() {
        if (usersTableView != null) {
            idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
            phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
            emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
            cpfColumn.setCellValueFactory(cellData -> cellData.getValue().cpfProperty());

            try {
                loadingUsersForTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (usersCardFlowPane != null) {
            showUserCardsView();
        }
    }

    private VBox createUserCard(User user) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("gap-4", "p-5", "border", "border-gray-200", "rounded-lg", "shadow-sm", "max-w-md");
        card.setUserData(user);

        ImageView userIconImageView = new ImageView();
        userIconImageView.setFitWidth(32);
        userIconImageView.setFitHeight(32);

        StackPane iconContainer = new StackPane(userIconImageView);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefWidth(48);
        iconContainer.setPrefHeight(48);
        iconContainer.setStyle("-fx-background-color: #e0f2f7; -fx-background-radius: 50%;");

        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(user.getName());
        nameLabel.getStyleClass().add("font-bold");
        Label emailLabel = new Label(user.getEmail());
        emailLabel.getStyleClass().addAll("text-sm", "text-gray-500");

        Label cpfLabel = new Label("CPF: " + formatCpf(user.getCpf()));
        cpfLabel.getStyleClass().addAll("text-sm", "text-gray-900");

        Label phoneLabel = new Label("Telefone: " + formatPhone(user.getPhone()));
        phoneLabel.getStyleClass().addAll("text-sm", "text-gray-900");

        Label addressLabel = new Label("Endereço: " + user.getAddress());
        addressLabel.getStyleClass().addAll("text-sm", "text-gray-900");

        infoBox.getChildren().addAll(nameLabel, emailLabel, cpfLabel, phoneLabel, addressLabel);

        HBox userDetails = new HBox(10);
        userDetails.getChildren().addAll(iconContainer, infoBox);

        Button editButton = new Button("Editar");
        editButton.getStyleClass().addAll("px-4", "py-2", "border", "border-yellow-300", "rounded-md", "text-yellow-700", "font-semibold", "hover:bg-yellow-100", "focus:outline-none", "focus:ring-2", "focus:ring-yellow-600");
        editButton.setOnAction(event -> handleEditUser(user, card));

        Button removeButton = new Button("Remover");
        removeButton.getStyleClass().addAll("px-4", "py-2", "border", "border-red-300", "rounded-md", "text-red-700", "font-semibold", "hover:bg-red-100", "focus:outline-none", "focus:ring-2", "focus:ring-red-600");
        removeButton.setOnAction(event -> handleDeleteUser(user));

        HBox actionsBox = new HBox(8);
        actionsBox.getChildren().addAll(editButton, removeButton);
        actionsBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(actionsBox, new Insets(8, 0, 0, 0));

        card.getChildren().addAll(userDetails, actionsBox);
        return card;
    }

    public void handleEditUser(User user, VBox card) {
        System.out.println("Editar usuário: " + user.getName());

        VBox infoBox = (VBox) ((HBox) card.getChildren().get(0)).getChildren().get(1);
        infoBox.getChildren().clear();

        TextField nameTextField = new TextField(user.getName());
        TextField emailTextField = new TextField(user.getEmail());
        TextField cpfTextField = new TextField(user.getCpf());
        TextField phoneTextField = new TextField(user.getPhone());
        TextField addressTextField = new TextField(user.getAddress());

        infoBox.getChildren().addAll(nameTextField, emailTextField, cpfTextField, phoneTextField, addressTextField);

        Button saveButton = new Button("Salvar");
        saveButton.getStyleClass().addAll("px-4", "py-2", "border", "border-green-300", "rounded-md", "text-green-700", "font-semibold", "hover:bg-green-100", "focus:outline-none", "focus:ring-2", "focus:ring-green-600");
        saveButton.setOnAction(event -> handleSave(user, card, nameTextField, emailTextField, cpfTextField, phoneTextField, addressTextField));

        HBox actionsBox = (HBox) card.getChildren().get(1);
        actionsBox.getChildren().clear();
        actionsBox.getChildren().add(saveButton);
    }

    private void handleSave(User user, VBox card, TextField nameTextField, TextField emailTextField, TextField cpfTextField, TextField phoneTextField, TextField addressTextField) {
        user.setName(nameTextField.getText());
        user.setEmail(emailTextField.getText());
        user.setCpf(cpfTextField.getText());
        user.setPhone(phoneTextField.getText());
        user.setAddress(addressTextField.getText());

        try {
            userService.updateUser(user);
            showUserCardsView();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erro");
            errorAlert.setHeaderText("Erro ao atualizar usuário");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private String formatCpf(String cpf) {
        if (cpf != null && cpf.matches("\\d{11}")) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf;
    }

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

    public void showUserCardsView() {
        if (usersCardFlowPane != null) {
            usersCardFlowPane.getChildren().clear();
            try {
                List<User> users = userService.getAllUsers();
                for (User user : users) {
                    usersCardFlowPane.getChildren().add(createUserCard(user));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleDeleteUser(User user) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Remoção");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Tem certeza que deseja remover o usuário: " + user.getName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.deleteUser(user.getId());
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
        });
    }

    public void showAddUserView() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/AddUserView.fxml"));
            javafx.scene.Parent root = loader.load();
            AddUserController addUserController = loader.getController();
            addUserController.setUserController(this);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Adicionar Novo Usuário");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar AddUserView.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchUser() {
        String searchTerm = searchTextField.getText();
        // A busca agora será feita com base no texto digitado, sem critério de ComboBox
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<User> searchResults = userService.findUsersByNameOrCPFOrEmail(searchTerm); // Adapte seu serviço para buscar por múltiplos campos
                updateUserCardDisplay(searchResults);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            showUserCardsView();
        }
    }

    private void loadingUsersForTable() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsers());
        usersTableView.setItems(users);
    }

    private void updateUserTableDisplay(List<User> userList) {
        ObservableList<User> observableUserList = FXCollections.observableArrayList(userList);
        usersTableView.setItems(observableUserList);
    }

    private void updateUserCardDisplay(List<User> userList) {
        if (usersCardFlowPane != null) {
            usersCardFlowPane.getChildren().clear();
            for (User user : userList) {
                usersCardFlowPane.getChildren().add(createUserCard(user));
            }
        }
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
        if (usersTableView != null) {
            User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Lógica para editar usuário na TableView, se necessário
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Selecione um usuário para editar.");
                alert.showAndWait();
            }
        } else if (usersCardFlowPane != null) {
            // A lógica de edição nos cards já está implementada no handleEditUser
        }
    }

    @FXML
    public void deleteUsers(ActionEvent event) {
        if (usersTableView != null) {
            User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                handleDeleteUser(selectedUser);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Selecione um usuário para remover.");
                alert.showAndWait();
            }
        } else if (usersCardFlowPane != null) {
            // A lógica de remoção já está implementada no botão de cada card
        }
    }

    @FXML
    public void switchToCardsView(ActionEvent event) {
        showUserCardsView();
    }

    @FXML
    public void switchToTableView(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            javafx.scene.layout.Pane userView = loader.load();
            UserController controller = loader.getController();
            controller.setRootLayoutController(this.rootLayoutController);
            rootLayoutController.setCenterView(userView);
            controller.initialize();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelAddUserView() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            javafx.scene.layout.Pane userView = loader.load();
            UserController controller = loader.getController();
            controller.setRootLayoutController(this.rootLayoutController);
            rootLayoutController.setCenterView(userView);
            showUserCardsView();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}