// com/managerlibrary/controllers/NewLoanController.java
package com.managerlibrary.controllers;

import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.daos.implement.LoanDAOImpl;
import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import com.managerlibrary.util.BookStringConverter; // Importe o conversor de Book
import com.managerlibrary.util.UserStringConverter; // Importe o conversor de User
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class NewLoanController {

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private ComboBox<Book> bookComboBox;

    @FXML
    private DatePicker loanDatePicker;

    @FXML
    private DatePicker returnDatePicker;

    private UserService userService = new UserService();
    private BookService bookService = new BookService(new BookDAOImpl());
    private LoanService loanService = new LoanService(new LoanDAOImpl());
    private RootLayoutController rootLayoutController;

    public NewLoanController() throws SQLException {
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @FXML
    public void initialize() {
        try {
            loadUsers();
            loadBooks();
            loanDatePicker.setValue(LocalDate.now()); // Define a data de empréstimo inicial como hoje
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro na Inicialização", "Ocorreu um erro ao carregar dados iniciais: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUsers() throws SQLException { // Adiciona 'throws SQLException' à assinatura
        userComboBox.setItems(FXCollections.observableList(userService.getAllUsers()));
        userComboBox.setConverter(new UserStringConverter()); // Define o conversor para User
    }

    private void loadBooks() throws SQLException { // Adiciona 'throws SQLException' à assinatura
        bookComboBox.setItems(FXCollections.observableList(bookService.getAllAvailableBooks())); // Carrega apenas livros disponíveis
        bookComboBox.setConverter(new BookStringConverter()); // Define o conversor para Book
    }

    @FXML
    public void registerNewLoan() {
        User selectedUser = userComboBox.getValue();
        Book selectedBook = bookComboBox.getValue();
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (selectedUser == null || selectedBook == null || loanDate == null || returnDate == null) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, selecione um usuário, um livro e as datas de empréstimo e devolução.");
            return;
        }

        if (returnDate.isBefore(loanDate)) {
            showAlert(Alert.AlertType.ERROR, "Datas Inválidas", "A data de devolução não pode ser anterior à data de empréstimo.");
            return;
        }

        Loan newLoan = new Loan();
        newLoan.setUser(selectedUser);
        newLoan.setBook(selectedBook);
        newLoan.setLoanDate(loanDate);
        newLoan.setReturnDate(returnDate);
        newLoan.setReturned(false); // Assumindo que o método correto é setDevolvido

        try {
            loanService.addLoan(newLoan);
            bookService.markBookAsBorrowed(selectedBook.getId());
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo registrado com sucesso!");
            closeNewLoanView();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao Registrar", "Erro ao registrar o empréstimo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void closeNewLoanView() {
        if (rootLayoutController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanView.fxml"));
                Pane loanView = loader.load();
                LoanController controller = loader.getController();
                controller.setRootLayoutController(rootLayoutController);
                rootLayoutController.setCenterView(loanView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Stage stage = (Stage) userComboBox.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}