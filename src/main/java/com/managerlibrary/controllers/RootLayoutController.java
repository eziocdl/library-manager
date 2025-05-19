package com.managerlibrary.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class RootLayoutController {

    @FXML
    private BorderPane rootLayout;

    private BookController bookController;
    private UserController userController;
    private LoanController loanController;
    private Pane loanView; // Referência para a view de empréstimos carregada
    private Stage primaryStage; // Adicione esta linha

    // Injeção do BookController
    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    // Injeção do UserController
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    // Injeção do LoanController
    public void setLoanController(LoanController loanController) {
        this.loanController = loanController;
    }

    // Método para definir a view de empréstimos carregada
    public void setLoanView(Pane loanView) {
        this.loanView = loanView;
    }

    // Método para definir o primaryStage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Método para obter o primaryStage
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    // Ação para o menu "Sair"
    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    // Ação para exibir a view de livros
    @FXML
    public void showBookView(ActionEvent event) {
        loadView("/views/BookView.fxml", (loader) -> {
            bookController = loader.getController();
            bookController.setRootLayoutController(this);
        });
    }

    // Ação para exibir a view de empréstimos
    @FXML
    public void showLoanView(ActionEvent event) {
        if (loanView != null) {
            rootLayout.setCenter(loanView);
            if (loanController != null) {
                loanController.loadAllLoans(); // Garante que os empréstimos sejam carregados ao exibir a tela
            }
        }
    }

    // Ação para exibir a view de usuários
    @FXML
    public void showUserView(ActionEvent event) {
        loadView("/views/UserView.fxml", (loader) -> {
            userController = loader.getController();
            userController.setRootLayoutController(this);
        });
    }

    // Ação para o botão de adicionar livro
    @FXML
    public void handleAddBookClick(ActionEvent event) {
        if (bookController != null) {
            bookController.showAddBookView();
        } else {
            System.err.println("BookController não foi injetado no RootLayoutController.");
        }
    }

    // Ação para o botão de adicionar usuário
    @FXML
    public void handleAddUserClick(ActionEvent event) {
        if (userController != null) {
            userController.showAddUserView();
        } else {
            System.err.println("UserController não foi injetado no RootLayoutController.");
        }
    }

    // Método genérico para carregar views e executar uma ação no controller (opcional)
    private void loadView(String fxmlPath, ControllerCallback callback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane view = loader.load();
            rootLayout.setCenter(view);
            if (callback != null) {
                callback.process(loader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Novo método para definir a view central diretamente
    public void setCenterView(Node node) {
        rootLayout.setCenter(node);
    }

    // Método auxiliar para carregar o conteúdo FXML
    private Pane loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Interface funcional para o callback do controller
    @FunctionalInterface
    private interface ControllerCallback {
        void process(FXMLLoader loader);
    }

    // Getters para os serviços (se a primeira abordagem fosse usada)
    public LoanController getLoanController() {
        return loanController;
    }

    public BookController getBookController() {
        return bookController;
    }

    public UserController getUserController() {
        return userController;
    }
}