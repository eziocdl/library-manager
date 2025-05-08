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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AddLoanViewController {

    @FXML
    private ComboBox<String> bookSearchCriteria;
    @FXML
    private TextField bookSearchTextField;
    @FXML
    private ListView<Book> bookResultsListView;
    @FXML
    private Label selectedBookLabel;

    @FXML
    private ComboBox<String> userSearchCriteria;
    @FXML
    private TextField userSearchTextField;
    @FXML
    private ListView<User> userResultsListView;
    @FXML
    private Label selectedUserLabel;

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

    private Book selectedBook;
    private User selectedUser;

    @FXML
    public ObservableList<String> bookSearchOptions = FXCollections.observableArrayList("Título", "Autor", "ISBN");
    @FXML
    public ObservableList<String> userSearchOptions = FXCollections.observableArrayList("Nome", "CPF", "Email");

    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    public void setUserService(UserService userService) {
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
        bookSearchCriteria.setItems(bookSearchOptions);
        userSearchCriteria.setItems(userSearchOptions);

        // CellFactory para a ListView de livros
        bookResultsListView.setCellFactory(param -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                } else {
                    setText(book.getTitle() + " - " + book.getAuthor());
                }
            }
        });

        // CellFactory para a ListView de usuários
        userResultsListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getName() + " - " + user.getCpf());
                }
            }
        });
    }

    @FXML
    private void searchBook() {
        String criteria = bookSearchCriteria.getValue();
        String searchTerm = bookSearchTextField.getText();
        ObservableList<Book> results = FXCollections.observableArrayList();

        if (criteria != null && !searchTerm.isEmpty()) {
            try {
                switch (criteria.toLowerCase()) {
                    case "título":
                        results.addAll(bookService.findBooksByTitle(searchTerm));
                        break;
                    case "autor":
                        results.addAll(bookService.findBooksByAuthor(searchTerm));
                        break;
                    case "isbn":
                        Book book = bookService.findBookByISBN(searchTerm);
                        if (book != null) {
                            results.add(book);
                        }
                        break;
                    default:
                        // Critério inválido
                        break;
                }
                bookResultsListView.setItems(results);
            } catch (SQLException e) {
                // Lógica para exibir erro de busca de livros
                e.printStackTrace();
            }
        } else {
            // Lógica para informar que critério e termo são necessários
        }
    }

    @FXML
    private void selectBook(MouseEvent event) {
        selectedBook = bookResultsListView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            selectedBookLabel.setText("Livro selecionado: " + selectedBook.getTitle() + " - " + selectedBook.getAuthor());
        }
    }

    @FXML
    private void searchUser() {
        String searchTerm = userSearchTextField.getText();

        if (!searchTerm.isEmpty()) {
            try {
                List<User> results = userService.findUsersByNameOrCPFOrEmail(searchTerm);
                userResultsListView.setItems(FXCollections.observableArrayList(results));
            } catch (SQLException e) {
                // Lógica para exibir erro de busca de usuários
                e.printStackTrace();
            }
        } else {
            // Lógica para informar que o termo de busca é necessário
        }
    }

    @FXML
    private void selectUser(MouseEvent event) {
        selectedUser = userResultsListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUserLabel.setText("Usuário selecionado: " + selectedUser.getName() + " - " + selectedUser.getCpf());
        }
    }

    @FXML
    private void saveNewLoan() {
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (selectedBook != null && selectedUser != null && loanDate != null && returnDate != null) {
            Loan newLoan = new Loan();
            newLoan.setBook(selectedBook);
            newLoan.setUser(selectedUser);
            newLoan.setLoanDate(loanDate);
            newLoan.setReturnDate(returnDate);
            newLoan.setStatus("Ativo");

            try {
                loanService.addLoan(newLoan);
                if (mainLoanController != null) {
                    mainLoanController.loadAllLoans();
                }
                if (dialogStage != null) {
                    dialogStage.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Lógica para exibir erro ao salvar
            }
        } else {
            // Lógica para informar que livro, usuário e datas são obrigatórios
        }
    }

    @FXML
    private void cancelLoan() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}