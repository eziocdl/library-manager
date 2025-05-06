// com/managerlibrary/controllers/AddLoanViewController.java
package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.UserService;
import com.managerlibrary.util.BookStringConverter;
import com.managerlibrary.util.UserStringConverter;
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
        System.out.println("setBookService chamado com: " + bookService); // ADICIONE ESTA LINHA
        this.bookService = bookService;
    }

    public void setUserService(UserService userService) {
        System.out.println("setUserService chamado com: " + userService); // ADICIONE ESTA LINHA
        this.userService = userService;
    }

    public void setMainLoanController(LoanController mainLoanController) {
        this.mainLoanController = mainLoanController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void initialize() {
        // Carrega os livros e usuários APENAS se os serviços já foram injetados
        if (bookService != null) {
            loadBooks();
            bookComboBox.setConverter(new BookStringConverter()); // Aplica o conversor para Livro
        }
        if (userService != null) {
            loadUsers();
            userComboBox.setConverter(new UserStringConverter()); // Aplica o conversor para Usuário
        }
    }

    private void loadBooks() {
        try {
            ObservableList<Book> books = FXCollections.observableArrayList(bookService.findAllBooks());
            bookComboBox.setItems(books);
        } catch (SQLException e) {
            System.err.println("Erro ao carregar livros: " + e.getMessage()); // ADICIONEI ESTA LINHA
            e.printStackTrace();
            // Lógica para exibir erro
        }
    }

    private void loadUsers() {
        try {
            ObservableList<User> users = FXCollections.observableArrayList(userService.getAllUsers());
            userComboBox.setItems(users);
        } catch (SQLException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage()); // ADICIONEI ESTA LINHA
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
                loanService.addLoan(newLoan);
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