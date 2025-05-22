package com.managerlibrary.controllers;

import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Controlador para o layout raiz da aplicação. Gerencia a exibição das diferentes
 * views (livros, usuários, empréstimos) na área central do BorderPane.
 * Este controlador é responsável por carregar as views e seus controladores,
 * injetando as dependências (serviços) necessárias.
 */
public class RootLayoutController {

    @FXML
    private BorderPane rootLayout;

    private Stage primaryStage; // Palco principal da aplicação

    // Instâncias dos serviços (serão injetadas pela classe App)
    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    // Cache para os Panes das views e seus respectivos controladores
    private Pane bookViewCache;
    private BookController bookControllerCache;

    private Pane userViewCache;
    private UserController userControllerCache;

    private Pane loanViewCache;
    private LoanController loanControllerCache;

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
     * **NOVO MÉTODO:** Injeta todos os serviços neste controlador.
     * Chamado pela classe App após a inicialização.
     */
    public void setServices(BookService bookService, UserService userService, LoanService loanService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
        this.loanService = Objects.requireNonNull(loanService, "LoanService não pode ser nulo.");
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
     * Ação para exibir a view de livros.
     * Carrega a view e seu controlador se não estiverem em cache, injeta os serviços,
     * e os exibe no centro do layout.
     *
     * @param event O evento de ação (pode ser nulo se chamado programaticamente).
     */
    @FXML
    public void showBookView(ActionEvent event) {
        showBookView(); // Chama a versão sem ActionEvent
    }

    // Versão sem ActionEvent para ser chamada programaticamente (ex: da App na inicialização)
    public void showBookView() {
        if (bookViewCache == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookView.fxml"));
                bookViewCache = loader.load(); // Carrega o FXML
                bookControllerCache = loader.getController(); // Obtém o controlador AUTOMATICAMENTE

                // *** INJEÇÃO DE DEPENDÊNCIA NO BookController ***
                // Garante que o RootLayoutController seja injetado antes dos serviços
                bookControllerCache.setRootLayoutController(this); // Passa a si mesmo para o BookController
                bookControllerCache.setBookService(this.bookService);

                logInfo("BookView e BookController carregados e serviços injetados.");
            } catch (IOException e) {
                logError("Erro ao carregar BookView.fxml ou injetar serviços: " + e.getMessage(), e);
                return; // Impede a continuação se a view não carregou
            }
        }
        setCenterView(bookViewCache);
        bookControllerCache.loadAllBooks(); // Chama o método para carregar os dados
    }

    /**
     * Ação para exibir a view de empréstimos.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void showLoanView(ActionEvent event) {
        if (loanViewCache == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanView.fxml"));
                loanViewCache = loader.load();
                loanControllerCache = loader.getController();

                // *** INJEÇÃO DE DEPENDÊNCIA NO LoanController ***
                // Garante que o RootLayoutController seja injetado antes dos serviços
                loanControllerCache.setRootLayoutController(this);
                loanControllerCache.setLoanService(this.loanService);
                loanControllerCache.setBookService(this.bookService); // LoanController precisa de BookService
                loanControllerCache.setUserService(this.userService); // LoanController precisa de UserService

                logInfo("LoanView e LoanController carregados e serviços injetados.");
            } catch (IOException e) {
                logError("Erro ao carregar LoanView.fxml ou injetar serviços: " + e.getMessage(), e);
                return;
            }
        }
        setCenterView(loanViewCache);
        loanControllerCache.loadAllLoans(); // Chama o método para carregar os dados
    }

    /**
     * Ação para exibir a view de usuários.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void showUserView(ActionEvent event) {
        if (userViewCache == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
                userViewCache = loader.load();
                userControllerCache = loader.getController();

                // *** INJEÇÃO DE DEPENDÊNCIA NO UserController ***
                // CORREÇÃO: Garante que o RootLayoutController seja injetado PRIMEIRO
                // antes que o setUserService chame loadAllUsers e este tente usar o RootLayoutController
                userControllerCache.setRootLayoutController(this);
                userControllerCache.setUserService(this.userService); // setUserService agora será chamado DEPOIS que rootLayoutController é definido

                logInfo("UserView e UserController carregados e serviços injetados.");
            } catch (IOException e) {
                logError("Erro ao carregar UserView.fxml ou injetar serviços: " + e.getMessage(), e);
                return;
            }
        }
        setCenterView(userViewCache);
        // Mesmo que loadAllUsers seja chamado em setUserService, chamamos aqui também
        // para garantir que a lista seja sempre recarregada ao exibir a view,
        // caso ela já estivesse em cache e precisasse de atualização.
        userControllerCache.loadAllUsers();
    }

    /**
     * Ação para o botão de adicionar livro. Chama o método no BookController para exibir a tela de adição.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void handleAddBookClick(ActionEvent event) {
        if (bookControllerCache != null) {
            bookControllerCache.showAddBookView();
        } else {
            logError("BookController não foi carregado. Não é possível adicionar livro.", null);
        }
    }

    /**
     * Ação para o botão de adicionar usuário. Chama o método no UserController para exibir a tela de adição.
     *
     * @param event O evento de ação.
     */
    @FXML
    public void handleAddUserClick(ActionEvent event) {
        if (userControllerCache != null) {
            userControllerCache.showAddUserView();
        } else {
            logError("UserController não foi carregado. Não é possível adicionar usuário.", null);
        }
    }

    /**
     * Define o nó (geralmente um Pane) a ser exibido no centro do BorderPane.
     *
     * @param node O nó a ser exibido.
     */
    public void setCenterView(Node node) {
        if (rootLayout != null && node != null) {
            rootLayout.setCenter(node);
        } else {
            logError("rootLayout ou o nó a ser definido no centro é nulo.", null);
        }
    }

    /**
     * Obtém o controlador de empréstimos (para outros controladores, se necessário).
     *
     * @return O controlador de empréstimos.
     */
    public LoanController getLoanController() {
        return loanControllerCache;
    }

    /**
     * Obtém o controlador de livros (para outros controladores, se necessário).
     *
     * @return O controlador de livros.
     */
    public BookController getBookController() {
        return bookControllerCache;
    }

    /**
     * Obtém o controlador de usuários (para outros controladores, se necessário).
     *
     * @return O controlador de usuários.
     */
    public UserController getUserController() {
        return userControllerCache;
    }

    private void logError(String message, Exception e) {
        System.err.print("ERRO: " + message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }

    private void logInfo(String message) {
        System.out.println("INFO: " + message);
    }
}