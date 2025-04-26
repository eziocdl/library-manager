// com/managerlibrary/controllers/LoanController.java
package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.util.StringConverter;

public class LoanController {

    @FXML
    private VBox loansVBox;
    @FXML
    private TextField searchTextField;

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private RootLayoutController rootLayoutController;
    private ObservableList<Loan> allLoans = FXCollections.observableArrayList();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LoanController() {
        try {
            this.loanService = new LoanService(new com.managerlibrary.daos.implement.LoanDAOImpl());
            this.bookService = new BookService(new com.managerlibrary.daos.implement.BookDAOImpl());
            this.userService = new UserService(new com.managerlibrary.daos.implement.UserDAOImpl());
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar Services: " + e.getMessage());
            // Lidar com o erro de inicialização
        }
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @FXML
    public void initialize() {
        loadAllLoans();
        displayLoans(allLoans);
    }

    private void loadAllLoans() {
        try {
            allLoans.clear();
            allLoans.addAll(loanService.getAllLoansWithDetails());
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com o erro
        }
    }

    private void displayLoans(ObservableList<Loan> loans) {
        if (loansVBox != null) { // Adicionada verificação de null
            loansVBox.getChildren().clear();
            for (Loan loan : loans) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanCardView.fxml"));
                    VBox loanCard = loader.load();
                    LoanCardController controller = loader.getController();
                    controller.setLoan(loan);
                    controller.setLoanController(this);
                    controller.displayLoanDetails();
                    loansVBox.getChildren().add(loanCard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("Erro: loansVBox não foi inicializado (é null).");
            // Lidar com o erro onde o VBox não está injetado
        }
    }

    @FXML
    public void showAddLoanView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/NewLoanView.fxml"));
            Pane addLoanView = loader.load();
            NewLoanController addLoanController = loader.getController();
            addLoanController.setRootLayoutController(this.rootLayoutController);
            rootLayoutController.setCenterView(addLoanView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void filterLoansByAll() {
        displayLoans(allLoans);
    }

    @FXML
    public void filterLoansByActive() {
        ObservableList<Loan> activeLoans = allLoans.filtered(loan -> "Ativo".equalsIgnoreCase(loan.getStatus()) && loan.getActualReturnDate() == null);
        displayLoans(activeLoans);
    }

    @FXML
    public void filterLoansByReturned() {
        ObservableList<Loan> returnedLoans = allLoans.filtered(loan -> loan.getActualReturnDate() != null);
        displayLoans(returnedLoans);
    }

    @FXML
    public void filterLoansByOverdue() {
        ObservableList<Loan> overdueLoans = allLoans.filtered(loan -> "Ativo".equalsIgnoreCase(loan.getStatus()) && loan.getActualReturnDate() == null && LocalDate.now().isAfter(loan.getReturnDate()));
        displayLoans(overdueLoans);
    }

    public void markLoanAsReturned(Loan loan) {
        try {
            LocalDate now = LocalDate.now();
            loanService.markAsReturned(loan.getId(), now);
            Book book = loan.getBook();
            if (book != null) {
                bookService.incrementAvailableCopies(book.getId());
            }
            loadAllLoans();
            displayLoans(allLoans);
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com erro
        }
    }

    public void showLoanDetails(Loan loan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanDetailsView.fxml"));
            Pane loanDetailsView = loader.load();
            LoanDetailsController controller = loader.getController();
            controller.setLoan(loan);
            controller.displayLoanDetails();
            rootLayoutController.setCenterView(loanDetailsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}