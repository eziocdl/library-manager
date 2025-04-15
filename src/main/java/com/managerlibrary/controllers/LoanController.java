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
    // Tela AddLoanView
    @FXML
    private ComboBox<Book> bookComboBox;
    @FXML
    private ComboBox<User> userComboBox;
    @FXML
    private DatePicker loanDatePicker;
    @FXML
    private DatePicker returnDatePicker;

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private RootLayoutController rootLayoutController;
    private ObservableList<Loan> allLoans = FXCollections.observableArrayList();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Adicione isto

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
            allLoans.addAll(loanService.getAllLoansWithDetails()); // Precisa implementar este método no LoanService
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com o erro
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
                controller.setLoanController(this); // Passa referência para o LoanController
                controller.displayLoanDetails();
                loansVBox.getChildren().add(loanCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void showAddLoanView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddLoanView.fxml"));
            Pane addLoanView = loader.load();
            LoanController addLoanController = loader.getController();
            addLoanController.setRootLayoutController(this.rootLayoutController);
            addLoanController.initializeAddLoanView();
            rootLayoutController.setCenterView(addLoanView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeAddLoanView() {
        try {
            bookComboBox.setItems(FXCollections.observableArrayList(bookService.findAllBooks()));
            userComboBox.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
            loanDatePicker.setValue(LocalDate.now());
            setDatePickerFormat(loanDatePicker); // Adicionar formatação
            returnDatePicker.setValue(LocalDate.now().plusDays(7)); // Definir um valor inicial para a data de devolução
            setDatePickerFormat(returnDatePicker); // Adicionar formatação
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com o erro
        }
    }

    private void setDatePickerFormat(DatePicker datePicker) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
            }
        });
    }

    @FXML
    public void saveLoan(ActionEvent event) {
        Book selectedBook = bookComboBox.getValue();
        User selectedUser = userComboBox.getValue();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (selectedBook == null || selectedUser == null || loanDate == null || returnDate == null) {
            // Mostrar erro
            return;
        }

        Loan loan = new Loan();
        loan.setBookId(selectedBook.getId());
        loan.setUserId(selectedUser.getId());
        loan.setLoanDate(loanDate);
        loan.setReturnDate(returnDate);
        loan.setStatus("Ativo");
        loan.setFine(0.0);

        try {
            loanService.addLoan(loan);
            bookService.decrementAvailableCopies(selectedBook.getId());
            loadAllLoans();
            cancelAddLoanView(event);
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com o erro
        }
    }

    @FXML
    public void cancelAddLoanView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanView.fxml"));
            Pane loanView = loader.load();
            LoanController controller = loader.getController();
            controller.setRootLayoutController(this.rootLayoutController);
            rootLayoutController.setCenterView(loanView);
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
            Book book = bookService.findBookById(loan.getBookId());
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