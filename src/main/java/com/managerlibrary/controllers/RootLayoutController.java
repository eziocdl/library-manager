package com.managerlibrary.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para o layout raiz da aplicação. Gerencia a exibição das diferentes
 * views (livros, usuários, empréstimos) na área central do BorderPane.
 */
public class RootLayoutController {

    @FXML
    private BorderPane rootLayout;

    private BookController bookController;
    private UserController userController;
    private LoanController loanController;
    private Pane loanView; // Referência para a view de empréstimos carregada
    private Stage primaryStage; // Palco principal da aplicação

    /**
     * Define o controlador de livros.
     *
     * @param bookController O controlador de livros.
     */
    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    /**
     * Define o controlador de usuários.
     *
     * @param userController O controlador de usuários.
     */
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    /**
     * Define o controlador de empréstimos e carrega a view de empréstimos.
     *
     * @param loanController O controlador de empréstimos.
     */
    public void setLoanController(LoanController loanController) {
        this.loanController = loanController;
        this.loanView = loadContent("/views/LoanView.fxml");
        if (loanView != null) {
            // Não define a view central aqui, pois pode não ser a view inicial
            // A view de empréstimos será definida quando o menu for clicado.
        }
    }

    /**
     * Define a view de empréstimos já carregada.
     *
     * @param loanView A view de empréstimos.
     */
    public void setLoanView(Pane loanView) {
        this.loanView = loanView;
    }

    /**
     * Define o palco principal da aplicação.
     *
     * @param primaryStage O palco principal.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Obtém o palco principal da aplicação.
     *
     * @return O palco principal.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Ação para o menu "Sair". Encerra a aplicação.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void handleExit(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Ação para exibir a view de livros. Carrega a view e define o controlador.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void showBookView(ActionEvent event) {
        loadView("/views/BookView.fxml", (loader) -> {
            bookController = loader.getController();
            bookController.setRootLayoutController(this);
        });
    }

    /**
     * Ação para exibir a view de empréstimos. Se a view já estiver carregada, apenas a exibe.
     * Caso contrário, carrega a view e define o controlador.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void showLoanView(ActionEvent event) {
        if (loanView != null) {
            rootLayout.setCenter(loanView);
            if (loanController != null) {
                loanController.loadAllLoans(); // Garante que os empréstimos sejam carregados ao exibir a tela
            }
        } else {
            loadView("/views/LoanView.fxml", (loader) -> {
                loanController = loader.getController();
                loanController.setRootLayoutController(this);
                loanView = (Pane) loader.getRoot(); // Garante que a referência da view seja mantida
            });
        }
    }

    /**
     * Ação para exibir a view de usuários. Carrega a view e define o controlador.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void showUserView(ActionEvent event) {
        loadView("/views/UserView.fxml", (loader) -> {
            userController = loader.getController();
            userController.setRootLayoutController(this);
        });
    }

    /**
     * Ação para o botão de adicionar livro. Chama o método no BookController para exibir a tela de adição.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void handleAddBookClick(ActionEvent event) {
        if (bookController != null) {
            bookController.showAddBookView();
        } else {
            logError("BookController não foi injetado", new IllegalStateException("BookController não foi injetado no RootLayoutController."));
        }
    }

    /**
     * Ação para o botão de adicionar usuário. Chama o método no UserController para exibir a tela de adição.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void handleAddUserClick(ActionEvent event) {
        if (userController != null) {
            userController.showAddUserView();
        } else {
            logError("UserController não foi injetado", new IllegalStateException("UserController não foi injetado no RootLayoutController."));
        }
    }

    /**
     * Método genérico para carregar views e executar uma ação no controller (opcional).
     *
     * @param fxmlPath O caminho para o arquivo FXML da view.
     * @param callback Uma interface funcional para processar o loader e o controller.
     */
    private void loadView(String fxmlPath, ControllerCallback callback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane view = loader.load();
            rootLayout.setCenter(view);
            if (callback != null) {
                callback.process(loader);
            }
        } catch (IOException e) {
            logError("Erro ao carregar view: " + fxmlPath, e);
        }
    }

    /**
     * Novo método para definir a view central diretamente.
     *
     * @param node O nó (geralmente um Pane) a ser exibido no centro.
     */
    public void setCenterView(Node node) {
        rootLayout.setCenter(node);
    }

    /**
     * Método auxiliar para carregar o conteúdo FXML e retornar o Pane carregado.
     *
     * @param fxmlPath O caminho para o arquivo FXML.
     * @return O Pane carregado ou null em caso de erro.
     */
    private Pane loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            logError("Erro ao carregar conteúdo: " + fxmlPath, e);
            return null;
        }
    }

    /**
     * Interface funcional para o callback do controller. Permite executar código após o carregamento da view.
     */
    @FunctionalInterface
    private interface ControllerCallback {
        void process(FXMLLoader loader);
    }

    /**
     * Obtém o controlador de empréstimos.
     *
     * @return O controlador de empréstimos.
     */
    public LoanController getLoanController() {
        return loanController;
    }

    /**
     * Obtém o controlador de livros.
     *
     * @return O controlador de livros.
     */
    public BookController getBookController() {
        return bookController;
    }

    /**
     * Obtém o controlador de usuários.
     *
     * @return O controlador de usuários.
     */
    public UserController getUserController() {
        return userController;
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