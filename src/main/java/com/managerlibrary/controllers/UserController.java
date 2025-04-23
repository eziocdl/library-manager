package com.managerlibrary.controllers;

import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
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
    private ComboBox<String> searchCriteriaComboBox;
    @FXML
    private TextField searchTextField;

    @FXML
    private Label nameDetailLabel; // Elementos para exibir detalhes na UserView
    @FXML
    private Label cpfDetailLabel;
    @FXML
    private Label phoneDetailLabel;
    @FXML
    private Label emailDetailLabel;
    @FXML
    private Label addressDetailLabel;

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

    public void setCenterView(Pane centerView) {
        if (rootLayoutController != null) {
            rootLayoutController.setCenterView(centerView);
        }
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

        Label userIcon = new Label();
        userIcon.getStyleClass().addAll("items-center", "justify-center", "w-12", "h-12", "rounded-full", "bg-blue-100", "text-blue-700", "font-semibold");
        userIcon.setText("\uf007");
        userIcon.setFont(Font.font("Font Awesome 5 Free", FontWeight.NORMAL, 16));

        StackPane iconContainer = new StackPane(userIcon);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefWidth(48);
        iconContainer.setPrefHeight(48);

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

        Button detailsButton = new Button("Detalhes");
        detailsButton.getStyleClass().addAll("px-4", "py-2", "border", "border-gray-300", "rounded-md", "text-gray-900", "font-semibold", "hover:bg-gray-100", "focus:outline-none", "focus:ring-2", "focus:ring-blue-600");
        detailsButton.setOnAction(event -> handleUserDetails(user));

        Button editButton = new Button("Editar");
        editButton.getStyleClass().addAll("px-4", "py-2", "border", "border-yellow-300", "rounded-md", "text-yellow-700", "font-semibold", "hover:bg-yellow-100", "focus:outline-none", "focus:ring-2", "focus:ring-yellow-600");
        editButton.setOnAction(event -> handleEditUser(user));

        Button removeButton = new Button("Remover");
        removeButton.getStyleClass().addAll("px-4", "py-2", "border", "border-red-300", "rounded-md", "text-red-700", "font-semibold", "hover:bg-red-100", "focus:outline-none", "focus:ring-2", "focus:ring-red-600");
        removeButton.setOnAction(event -> handleDeleteUser(user));

        HBox actionsBox = new HBox(8);
        actionsBox.getChildren().addAll(detailsButton, editButton, removeButton);
        actionsBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(actionsBox, new Insets(8, 0, 0, 0));

        card.getChildren().addAll(userDetails, actionsBox);
        return card;
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

    public void handleUserDetails(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Parent root = loader.load();

            // Obtenha o próprio UserController
            UserController controller = loader.getController();

            // Preencha os campos de detalhes
            controller.displayUserDetails(user);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Detalhes do Usuário");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar UserView.fxml para detalhes: " + e.getMessage());
        }
    }

    public void displayUserDetails(User user) {
        if (nameDetailLabel != null) {
            nameDetailLabel.setText("Nome: " + user.getName());
        }
        if (cpfDetailLabel != null) {
            cpfDetailLabel.setText("CPF: " + formatCpf(user.getCpf()));
        }
        if (phoneDetailLabel != null) {
            phoneDetailLabel.setText("Telefone: " + formatPhone(user.getPhone()));
        }
        if (emailDetailLabel != null) {
            emailDetailLabel.setText("Email: " + user.getEmail());
        }
        if (addressDetailLabel != null) { // Use addressDetailLabel aqui
            addressDetailLabel.setText("Endereço: " + user.getAddress());
        }
    }

    public void handleEditUser(User user) {
        System.out.println("Editar usuário: " + user.getName());
        loadEditUserView(user);
    }

    public void loadEditUserView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditUserView.fxml"));
            Pane editUserView = loader.load();
            EditUserController editUserController = loader.getController();
            editUserController.setUser(user);
            editUserController.setUserController(this);
            editUserController.setService(this.getUserService());
            if (rootLayoutController != null) {
                rootLayoutController.setCenterView(editUserView);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddUserView.fxml"));
            Parent root = loader.load();

            AddUserController addUserController = loader.getController();
            addUserController.setUserController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Adicionar Novo Usuário");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar AddUserView.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchUser() {
        String searchTerm = searchTextField.getText();
        String searchCriteria = searchCriteriaComboBox.getValue();

        if (searchTerm != null && !searchTerm.trim().isEmpty() && searchCriteria != null) {
            try {
                List<User> searchResults = Collections.emptyList();
                if (searchCriteria.equals("Nome")) {
                    searchResults = userService.findUsersByName(searchTerm);
                } else if (searchCriteria.equals("CPF")) {
                    User user = userService.findUserByCPF(searchTerm);
                    if (user != null) {
                        searchResults = List.of(user);
                    }
                }
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
        User selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            loadEditUserView(selectedUser);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Selecione um usuário para editar.");
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteUsers(ActionEvent event) {
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
    }

    @FXML
    public void switchToCardsView(ActionEvent event) {
        showUserCardsView();
    }

    @FXML
    public void switchToTableView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Pane userView = loader.load();
            UserController controller = loader.getController();
            controller.setRootLayoutController(this.rootLayoutController);
            rootLayoutController.setCenterView(userView);
            controller.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelAddUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Pane userView = loader.load();
            UserController controller = loader.getController();
            controller.setRootLayoutController(this.rootLayoutController);
            rootLayoutController.setCenterView(userView);
            showUserCardsView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}