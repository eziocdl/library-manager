package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.sql.SQLException;

public class AddLoanViewController {

    @FXML
    private ComboBox<Book> bookComboBox;
    @FXML
    private ComboBox<User> userComboBox;
    @FXML
    private DatePicker loanDatePicker;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private LoanController mainLoanController;
    private Stage dialogStage;

    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
        loadBooks();
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        loadUsers();
    }

    public void setMainLoanController(LoanController mainLoanController) {
        this.mainLoanController = mainLoanController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void initialize() {
        // Lógica de inicialização, se necessário
    }

    private void loadBooks() {
        try {
            ObservableList<Book> books = FXCollections.observableArrayList(bookService.findAllBooks()); // Correção: Usar findAllBooks()
            bookComboBox.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
            // Lógica para exibir erro
        }
    }

    private void loadUsers() {
        try {
            ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsers());
            userComboBox.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace();
            // Lógica para exibir erro
        }
    }

    @FXML
    private void saveLoan() {
        Book selectedBook = bookComboBox.getValue();
        User selectedUser = userComboBox.getValue();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (selectedBook != null && selectedUser != null && loanDate != null && returnDate != null) {
            Loan newLoan = new Loan();
            newLoan.setBook(selectedBook);
            newLoan.setUser(selectedUser);
            newLoan.setLoanDate(loanDate);
            newLoan.setReturnDate(returnDate);
            newLoan.setStatus("Ativo"); // Define o status inicial

            try {
                loanService.addLoan(newLoan); // Correção: Usar addLoan() e passar um objeto Loan
                if (mainLoanController != null) {
                    mainLoanController.loadAllLoans(); // Recarrega a lista na tela principal
                }
                if (dialogStage != null) {
                    dialogStage.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Lógica para exibir erro ao salvar
            }
        } else {
            // Exibir mensagem de que todos os campos são obrigatórios
        }
    }

    @FXML
    private void cancelAddLoanView() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}