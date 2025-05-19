package com.managerlibrary.controllers;

import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.entities.Book;
import com.managerlibrary.infra.DataBaseConnection;
import com.managerlibrary.services.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BookController {

    @FXML
    private FlowPane booksFlowPane;
    @FXML
    private TextField searchBookTextField;
    @FXML
    private Button searchBookButton;

    private BookService bookService;
    private RootLayoutController rootLayoutController;

    public BookController() throws SQLException {
        this.bookService = new BookService(new BookDAOImpl(DataBaseConnection.getConnection()));
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @FXML
    public void initialize() {
        System.out.println("BookController: Método initialize() chamado.");
        try {
            loadingBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAddBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Pane addBookView = loader.load();

            AddBookViewController addBookViewController = loader.getController();
            addBookViewController.setBookController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Adicionar Novo Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(booksFlowPane.getScene().getWindow());
            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            try {
                loadingBooks();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar AddBookView.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearchBook() {
        String searchTerm = searchBookTextField.getText();
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<Book> searchResults = bookService.findBooksByTitle(searchTerm);
                updateBookDisplay(searchResults);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                loadingBooks();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadingBooks() throws SQLException {
        System.out.println("BookController: Método loadingBooks() chamado.");
        ObservableList<Book> books = FXCollections.observableArrayList(bookService.findAllBooks());
        updateBookDisplay(books);
    }

    private void updateBookDisplay(List<Book> bookList) {
        ObservableList<Book> observableBookList = FXCollections.observableArrayList(bookList);
        booksFlowPane.getChildren().clear();
        for (Book book : observableBookList) {
            VBox bookCard = createBookCard(book);
            booksFlowPane.getChildren().add(bookCard);
        }
    }

    private VBox createBookCard(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookCardView.fxml"));
            VBox card = loader.load();
            BookCardController controller = loader.getController();
            controller.setBook(book);
            controller.setBookListController(this);
            return card;
        } catch (IOException e) {
            e.printStackTrace();
            // Lidar com o erro ao carregar o FXML
            return new VBox(); // Ou outra forma de indicar erro
        }
    }

    public void showBookDetails(Book book, Window owner) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Detalhes do Livro");
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.initOwner(owner);

        VBox detailsLayout = new VBox(10);
        detailsLayout.setStyle("-fx-padding: 15;");
        javafx.scene.control.Label titleLabelDetails = new javafx.scene.control.Label("Título: " + book.getTitle());
        javafx.scene.control.Label authorLabelDetails = new javafx.scene.control.Label("Autor: " + book.getAuthor());
        javafx.scene.control.Label isbnLabelDetails = new javafx.scene.control.Label("ISBN: " + book.getIsbn());
        javafx.scene.control.Label publisherLabelDetails = new javafx.scene.control.Label("Editora: " + book.getPublisher());
        javafx.scene.control.Label yearLabelDetails = new javafx.scene.control.Label("Ano: " + book.getYear());
        javafx.scene.control.Label genreLabelDetails = new javafx.scene.control.Label("Gênero: " + book.getGenre());
        javafx.scene.control.Label availabilityLabelDetails = new javafx.scene.control.Label("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        Button closeButton = new Button("Fechar");
        closeButton.setOnAction(e -> detailsStage.close());

        detailsLayout.getChildren().addAll(titleLabelDetails, authorLabelDetails, isbnLabelDetails, publisherLabelDetails, yearLabelDetails, genreLabelDetails, availabilityLabelDetails, closeButton);

        Scene detailsScene = new Scene(detailsLayout);
        detailsStage.setScene(detailsScene);
        detailsStage.showAndWait();
    }

    public void loadEditBookView(Book bookToEdit, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Pane addBookView = loader.load();

            AddBookViewController addBookViewController = loader.getController();
            addBookViewController.setBookController(this);
            addBookViewController.setBookToEdit(bookToEdit);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(sourceButton.getScene().getWindow());

            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (addBookViewController.isSaveClicked()) {
                Book editedBook = addBookViewController.getBook();
                if (editedBook != null) {
                    updateBook(editedBook);
                }
            }
            loadingBooks();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar tela de edição: " + e.getMessage());
        }
    }

    public void confirmRemoveBook(Book book, Window owner) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Remoção");
        confirmation.setHeaderText("Remover o livro: " + book.getTitle());
        confirmation.setContentText("Tem certeza que deseja remover este livro?");
        confirmation.initOwner(owner);

        Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            deleteBook(book.getId());
        }
    }

    public void insertNewBook(Book book) {
        try {
            bookService.insertBook(book);
            loadingBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Book book) {
        try {
            bookService.updateBook(book);
            loadingBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBook(int id) {
        try {
            bookService.deleteBook(id);
            loadingBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}