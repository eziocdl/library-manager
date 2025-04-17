package com.managerlibrary.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;

public class RootLayoutController {

    @FXML
    private BorderPane rootLayout;

    private BookController bookController; // **Adicionada a declaração da variável bookController**

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void showBookView(ActionEvent event) {
        System.out.println("RootLayoutController: Método showBookView() chamado.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookView.fxml"));
            javafx.scene.layout.Pane bookView = loader.load();
            BookController bookController = loader.getController();
            bookController.setRootLayoutController(this);
            rootLayout.setCenter(bookView);
            this.bookController = bookController;
            System.out.println("RootLayoutController: BookView.fxml carregado com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("RootLayoutController: Erro ao carregar BookView.fxml: " + e.getMessage());
        }
    }

    @FXML
    public void showLoanView(ActionEvent event) {
        loadView("/views/LoanView.fxml");
    }

    @FXML
    public void showUserView(ActionEvent event) {
        loadView("/views/UserView.fxml");
    }

    @FXML
    public void handleAddBookClick(ActionEvent event) {
        if (bookController != null) {
            bookController.showAddBookView(); // **Remova o 'event' daqui**
        } else {
            System.err.println("BookController não foi injetado no RootLayoutController.");
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