// com/managerlibrary/controllers/LoanController.java
package com.managerlibrary.controllers;

import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.daos.implement.LoanDAOImpl;
import com.managerlibrary.daos.implement.UserDAOImpl;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.infra.DataBaseConnection;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LoanController {

    @FXML
    private VBox loansVBox;

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private ObservableList<Loan> allLoans = FXCollections.observableArrayList();
    private RootLayoutController rootLayoutController;

    // Métodos de injeção para os serviços
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
        try {
            Connection connection = DataBaseConnection.getConnection();
            this.bookService = new BookService(new BookDAOImpl(connection));
            this.userService = new UserService(new UserDAOImpl(connection));
            this.loanService = new LoanService(new LoanDAOImpl(connection));
            loadAllLoans(); // Carrega todos os empréstimos ao inicializar o controller principal
        } catch (SQLException e) {
            showAlert("Erro ao inicializar serviços", "Não foi possível inicializar os serviços de empréstimo.");
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Qualquer inicialização adicional para o LoanController
    }

    public void loadAllLoans() {
        try {
            List<Loan> loans = loanService.getAllLoansWithDetails();
            allLoans.setAll(loans);
            displayLoans(allLoans);
        } catch (SQLException e) {
            showAlert("Erro ao carregar empréstimos", "Não foi possível carregar os empréstimos do banco de dados.");
            e.printStackTrace();
        }
    }

    private void displayLoans(ObservableList<Loan> loans) {
        loansVBox.getChildren().clear();
        for (Loan loan : loans) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanCardView.fxml"));
                VBox loanCard = loader.load();
                LoanCardController controller = loader.getController();
                controller.setLoan(loan);
                controller.setLoanController(this);
                loansVBox.getChildren().add(loanCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void showAddLoanView() {
        try {
            // Aqui está a correção - definir o location antes de usá-lo
            URL location = getClass().getResource("/com/managerlibrary/resources/views/AddLoanView.fxml");

            System.out.println("LoanController.showAddLoanView: URL do FXML: " + location);
            if (location == null) {
                System.err.println("Erro: Não foi possível encontrar o arquivo FXML em /com/managerlibrary/resources/views/AddLoanView.fxml");
                return; // Aborta se o arquivo não for encontrado
            }
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();

            // *** LOGS ADICIONADOS ***
            AddLoanViewController addLoanController = loader.getController();
            System.out.println("LoanController.showAddLoanView: Controlador AddLoanViewController obtido: " + addLoanController);
            System.out.println("LoanController.showAddLoanView: bookService em LoanController: " + bookService);
            System.out.println("LoanController.showAddLoanView: userService em LoanController: " + userService);
            if (addLoanController != null) {
                addLoanController.setBookService(bookService);
                addLoanController.setUserService(userService);
                System.out.println("LoanController.showAddLoanView: Serviços setados no AddLoanViewController.");
            } else {
                System.err.println("LoanController.showAddLoanView: Erro ao obter o controlador AddLoanViewController!");
            }
            // *** FIM DOS LOGS ADICIONADOS ***

            Stage stage = new Stage();
            stage.setTitle("Novo Empréstimo");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void filterLoansByAll(ActionEvent event) {
        displayLoans(allLoans);
    }

    @FXML
    void filterLoansByActive(ActionEvent event) {
        ObservableList<Loan> activeLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(activeLoans);
    }

    @FXML
    void filterLoansByReturned(ActionEvent event) {
        ObservableList<Loan> returnedLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() != null)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(returnedLoans);
    }

    @FXML
    void filterLoansByOverdue(ActionEvent event) {
        LocalDate today = LocalDate.now();
        ObservableList<Loan> overdueLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null && today.isAfter(loan.getReturnDate()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(overdueLoans);
    }

    @FXML
    public void markLoanAsReturned(Loan loan) {
        if (loan != null && loan.getActualReturnDate() == null) {
            loan.setActualReturnDate(LocalDate.now());
            try {
                loanService.updateLoan(loan);
                // Atualizar a exibição dos empréstimos após a devolução
                loadAllLoans();
                showAlert("Sucesso", "Empréstimo marcado como devolvido.");
            } catch (SQLException e) {
                showAlert("Erro ao marcar devolução", "Não foi possível atualizar a informação de devolução no banco de dados.");
                e.printStackTrace();
            }
        } else if (loan != null && loan.getActualReturnDate() != null) {
            showAlert("Informação", "Este empréstimo já foi devolvido em " + loan.getActualReturnDate().toString() + ".");
        } else {
            showAlert("Erro", "Não foi possível processar a devolução. Informação do empréstimo inválida.");
        }
    }

    @FXML
    public void showLoanDetails(Loan loan) {
        if (loan != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/managerlibrary/views/LoanDetailsView.fxml"));
                Parent root = loader.load();
                LoanDetailsController controller = loader.getController();
                controller.setLoan(loan);
                controller.setBookService(bookService);
                controller.setUserService(userService);
                Stage stage = new Stage();
                stage.setTitle("Detalhes do Empréstimo");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erro ao exibir detalhes", "Não foi possível carregar a tela de detalhes do empréstimo.");
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}