package com.managerlibrary.controllers;

import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.entities.Book;
import com.managerlibrary.services.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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

    @FXML private FlowPane booksFlowPane;
    @FXML private TextField searchBookTextField;
    @FXML private Button searchBookButton;

    private BookService bookService;
    private RootLayoutController rootLayoutController;

    public BookController() throws SQLException {
        this.bookService = new BookService(new BookDAOImpl());
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
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-pref-width: 150;");
        card.setUserData(book); // Associate the Book object with the card

        ImageView coverImageView = new ImageView();
        coverImageView.setFitWidth(100);
        coverImageView.setFitHeight(150);
        // TODO: Adicionar aqui a lógica para carregar a imagem da capa, se necessário

        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label authorLabel = new Label("Autor: " + book.getAuthor());

        Label availableLabel = new Label("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());

        Button detailsButton = new Button("Detalhes");
        detailsButton.setOnAction(event -> {
            Stage detailsStage = new Stage();
            detailsStage.setTitle("Detalhes do Livro");
            detailsStage.initModality(Modality.APPLICATION_MODAL);
            Window mainWindow = detailsButton.getScene().getWindow();
            detailsStage.initOwner(mainWindow);

            VBox detailsLayout = new VBox(10);
            detailsLayout.setStyle("-fx-padding: 15;");
            Label titleLabelDetails = new Label("Título: " + book.getTitle());
            Label authorLabelDetails = new Label("Autor: " + book.getAuthor());
            Label isbnLabelDetails = new Label("ISBN: " + book.getIsbn());
            Label publisherLabelDetails = new Label("Editora: " + book.getPublisher());
            Label yearLabelDetails = new Label("Ano: " + book.getYear());
            Label genreLabelDetails = new Label("Gênero: " + book.getGenre());
            Label availabilityLabelDetails = new Label("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
            Button closeButton = new Button("Fechar");
            closeButton.setOnAction(e -> detailsStage.close());

            detailsLayout.getChildren().addAll(titleLabelDetails, authorLabelDetails, isbnLabelDetails, publisherLabelDetails, yearLabelDetails, genreLabelDetails, availabilityLabelDetails, closeButton);

            Scene detailsScene = new Scene(detailsLayout);
            detailsStage.setScene(detailsScene);
            detailsStage.showAndWait();
        });
        detailsButton.getStyleClass().add("book-card-action-button"); // Aplica a classe CSS

        Button editButton = new Button("Editar");
        editButton.setOnAction(event -> {
            handleEditBook(book); // Call handleEditBook with the book object
        });
        editButton.getStyleClass().add("book-card-action-button"); // Aplica a classe CSS

        Button removeButton = new Button("Remover");
        removeButton.setOnAction(event -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmar Remoção");
            confirmation.setHeaderText("Remover o livro: " + book.getTitle());
            confirmation.setContentText("Tem certeza que deseja remover este livro?");

            // Obtenha a janela principal a partir do botão
            Window mainWindow = removeButton.getScene().getWindow();
            confirmation.initOwner(mainWindow); // Define a janela principal como proprietária

            Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                try {
                    bookService.deleteBook(book.getId()); // Assuming your Book entity has an getId() method
                    loadingBooks(); // Reload the list after deletion
                    // TODO: Show success message
                } catch (SQLException e) {
                    e.printStackTrace();
                    // TODO: Show error message
                }
            }
        });
        removeButton.getStyleClass().add("book-card-action-button"); // Aplica a classe CSS

        HBox actionButtons = new HBox(5); // Reduzi o espaçamento para tentar otimizar o espaço
        actionButtons.getChildren().addAll(detailsButton, editButton, removeButton);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(coverImageView, titleLabel, authorLabel, availableLabel, actionButtons);
        VBox.setVgrow(actionButtons, javafx.scene.layout.Priority.ALWAYS);
        card.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        return card;
    }

    public void insertNewBook(Book book) {
        try {
            bookService.insertBook(book);
            loadingBooks();
            // TODO: Mostrar mensagem de sucesso para o usuário
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Mostrar mensagem de erro para o usuário
        }
    }

    public void updateBook(Book book) {
        try {
            bookService.updateBook(book);
            loadingBooks();
            // TODO: Mostrar mensagem de sucesso de atualização
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Mostrar mensagem de erro de atualização
        }
    }

    public void deleteBook(int id) {
        try {
            bookService.deleteBook(id);
            loadingBooks();
            // TODO: Show success message
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Show error message
        }
    }

    @FXML
    private void handleEditBook(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Pane addBookView = loader.load();

            AddBookViewController addBookViewController = loader.getController();
            addBookViewController.setBookController(this);
            addBookViewController.setBookToEdit(book); // Pass the book to edit

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(booksFlowPane.getScene().getWindow());
            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            try {
                loadingBooks(); // Reload the list after editing
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar AddBookView.fxml para edição: " + e.getMessage());
        }
    }
}