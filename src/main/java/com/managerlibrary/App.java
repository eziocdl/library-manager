package com.managerlibrary;

import com.managerlibrary.controllers.BookController;
import com.managerlibrary.controllers.LoanController;
import com.managerlibrary.controllers.RootLayoutController;
import com.managerlibrary.controllers.UserController;
import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.daos.implement.LoanDAOImpl;
import com.managerlibrary.daos.implement.UserDAOImpl;
import com.managerlibrary.infra.DataBaseConnection;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class App extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootLayoutController rootController;
    private BookController bookController;
    private UserController userController;
    private LoanController loanController;

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ManagerLibrary");

        initServices();
        initRootLayout();
        showBookView();
        primaryStage.show();
    }

    private void initServices() throws SQLException {
        java.sql.Connection connection = DataBaseConnection.getConnection(); // Obtenha a Connection

        bookService = new BookService(new BookDAOImpl(connection)); // Passe a Connection para o DAO
        userService = new UserService(new UserDAOImpl(connection)); // Passe a Connection para o DAO
        loanService = new LoanService(new LoanDAOImpl(connection)); // Passe a Connection para o DAO
    }

    public void initRootLayout() {
        try {
            FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/views/RootLayout.fxml"));
            rootLayout = rootLoader.load();
            rootController = rootLoader.getController();

            // Carregar e configurar LoanView e LoanController
            FXMLLoader loanLoader = new FXMLLoader(getClass().getResource("/views/LoanView.fxml"));
            Pane loanView = loanLoader.load();
            loanController = loanLoader.getController();
            loanController.setRootLayoutController(rootController);
            loanController.setLoanService(loanService);
            loanController.setBookService(bookService);
            loanController.setUserService(userService);

            rootController.setLoanController(loanController);
            rootController.setLoanView(loanView); // Novo método no RootLayoutController

            Scene scene = new Scene(rootLayout, 800, 600);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookView.fxml"));
            Pane bookView = loader.load();
            bookController = loader.getController();
            bookController.setRootLayoutController(rootController);
            rootController.setBookController(bookController); // ADICIONE ESTA LINHA
            rootLayout.setCenter(bookView);
            primaryStage.setTitle("Manager Library - Livros");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Pane userView = loader.load();
            userController = loader.getController();
            userController.setRootLayoutController(rootController);
            rootController.setUserController(userController); // ADICIONE ESTA LINHA
            rootLayout.setCenter(userView);
            primaryStage.setTitle("Manager Library - Usuários");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}