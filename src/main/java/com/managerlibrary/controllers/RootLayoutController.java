package com.managerlibrary.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class RootLayoutController {

    @FXML
    private BorderPane rootLayout;

    private BookController bookController;
    private UserController userController; // Adicionada referência ao UserController

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void showBookView(ActionEvent event) {
        loadView("/views/BookView.fxml");
        // Garante que o BookController seja definido após o carregamento da view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookView.fxml"));
        try {
            loader.load();
            bookController = loader.getController();
            bookController.setRootLayoutController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showLoanView(ActionEvent event) {
        loadView("/views/LoanView.fxml");
    }

    @FXML
    public void showUserView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Pane userView = loader.load();
            userController = loader.getController();
            userController.setRootLayoutController(this);
            rootLayout.setCenter(userView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddBookClick(ActionEvent event) {
        if (bookController != null) {
            bookController.showAddBookView();
        } else {
            System.err.println("BookController não foi injetado no RootLayoutController.");
        }
    }

    @FXML
    public void handleAddUserClick(ActionEvent event) {
        if (userController != null) {
            userController.showAddUserView();
        } else {
            System.err.println("UserController não foi injetado no RootLayoutController.");
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.layout.Pane view = loader.load();
            rootLayout.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Novo método para definir a view central
    public void setCenterView(Node node) {
        rootLayout.setCenter(node);
    }
}