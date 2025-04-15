package com.managerlibrary;

import com.managerlibrary.controllers.BookController;
import com.managerlibrary.controllers.RootLayoutController;
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
    private RootLayoutController rootController; // Adicione esta linha

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ManagerLibrary");

        initRootLayout();

    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RootLayout.fxml"));
            rootLayout = loader.load();

            // *** CRUCIAL: Obtenha e armazene o Controller ***
            rootController = loader.getController();

            Scene scene = new Scene(rootLayout, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookView.fxml"));
            Pane bookView = loader.load();

            // Obtenha o controlador do BookView
            BookController bookController = loader.getController();

            // Use a instância armazenada do RootLayoutController
            bookController.setRootLayoutController(rootController);

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