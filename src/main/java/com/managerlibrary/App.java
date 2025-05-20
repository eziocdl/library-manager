package com.managerlibrary;

import com.managerlibrary.controllers.RootLayoutController;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class App extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootLayoutController rootController;

    // Serviços serão inicializados aqui e passados para o RootLayoutController
    private LoanService loanService;
    private BookService bookService;
    private UserService userService;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ManagerLibrary");

        try {
            initServices();
            initRootLayout();
            // A responsabilidade de mostrar a view inicial agora é do RootLayoutController
            rootController.showBookView(); // Define a view inicial ao iniciar a aplicação
            primaryStage.show();
        } catch (SQLException e) {
            System.err.println("Erro de conexão ou inicialização dos serviços: " + e.getMessage());
            e.printStackTrace();
            // Poderia mostrar um alerta amigável ao usuário aqui
            System.exit(1); // Encerra a aplicação em caso de erro crítico
        } catch (IOException e) {
            System.err.println("Erro ao carregar layout principal: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Inicializa todas as instâncias de serviço com suas respectivas DAOs.
     * @throws SQLException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    private void initServices() throws SQLException {
        java.sql.Connection connection = DataBaseConnection.getConnection();
        Objects.requireNonNull(connection, "A conexão com o banco de dados não pode ser nula.");

        bookService = new BookService(new BookDAOImpl(connection));
        userService = new UserService(new UserDAOImpl(connection));
        loanService = new LoanService(new LoanDAOImpl(connection));
    }

    /**
     * Inicializa o layout raiz da aplicação (RootLayout.fxml) e seu controlador.
     * Injeta todos os serviços e a referência do Stage principal no RootLayoutController.
     * @throws IOException Se o arquivo FXML não puder ser carregado.
     */
    private void initRootLayout() throws IOException {
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/views/RootLayout.fxml"));
        rootLayout = rootLoader.load();
        rootController = rootLoader.getController();

        rootController.setPrimaryStage(primaryStage);

        // PASSO CRUCIAL: Chamar o método setServices para injetar todos os serviços de uma vez
        rootController.setServices(bookService, userService, loanService); // CORRIGIDO AQUI!

        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}