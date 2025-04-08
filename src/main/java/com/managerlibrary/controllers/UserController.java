package com.managerlibrary.controllers;


import com.managerlibrary.entities.User;
import com.managerlibrary.services.UserService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.awt.*;
import java.sql.SQLException;

public class UserController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField phoneTextField;

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

    private UserService userService = new UserService();

    @FXML
    public void initialize() throws SQLException {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        loadingUsers();

    }

    @FXML
    public void addUser() throws SQLException {
        String name = nameTextField.getText();
        String address = addressTextField.getText();
        String phone = phoneTextField.getText();

        User user = new User();
        user.setName(name);
        user.setAddress(address);
        user.setPhone(phone);
        userService.addUser(user);
        loadingUsers();
    }

    @FXML
    public void searchUsers() {

    }

    @FXML
    public void updateUsers() {
    }

    @FXML
    public void deleteUsers() {

    }


    private void loadingUsers() throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsers());
        usersTableView.setItems(users);
    }
}
