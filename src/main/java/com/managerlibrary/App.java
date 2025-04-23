package com.managerlibrary;

import com.managerlibrary.controllers.BookController;
import com.managerlibrary.controllers.LoanController;
import com.managerlibrary.controllers.RootLayoutController;
import com.managerlibrary.controllers.UserController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootLayoutController rootController;
    private BookController bookController; // Adicionada referência ao BookController no App
    private UserController userController; // Adicionada referência ao UserController no App

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ManagerLibrary");

        initRootLayout();
        showBookView(); // Garante que a BookView e o BookController sejam inicializados cedo
        primaryStage.show();
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RootLayout.fxml"));
            rootLayout = loader.load();
            rootController = loader.getController();
            Scene scene = new Scene(rootLayout, 800, 600);
            primaryStage.setScene(scene);
            // primaryStage.show(); // Movido para o final do start()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookView.fxml"));
            Pane bookView = loader.load();
            bookController = loader.getController(); // Inicializa a referência do BookController no App
            bookController.setRootLayoutController(rootController);

            // Injeta o BookController no RootLayoutController
            rootController.setBookController(bookController);

            rootLayout.setCenter(bookView);
            primaryStage.setTitle("Manager Library - Livros");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoanView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanView.fxml"));
            Pane loanView = loader.load();
            LoanController loanController = loader.getController();
            loanController.setRootLayoutController(rootController);
            rootLayout.setCenter(loanView);
            primaryStage.setTitle("Manager Library - Empréstimos");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UserView.fxml"));
            Pane userView = loader.load();
            userController = loader.getController(); // Inicializa a referência do UserController no App
            userController.setRootLayoutController(rootController);
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