package com.managerlibrary.controllers;

import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.entities.Book;
import com.managerlibrary.services.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BookController {

    @FXML
    private FlowPane booksFlowPane;

    @FXML
    private Label coverFileNameLabel; // Adicionado para exibir o nome do arquivo

    @FXML
    private TextField searchBookTextField; // Referência ao TextField de busca (certifique-se de ter este fx:id no seu FXML)

    private BookService bookService;
    private RootLayoutController rootLayoutController;
    private File selectedCoverFile; // Para armazenar o arquivo selecionado

    public BookController() throws SQLException {
        this.bookService = new BookService(new BookDAOImpl());
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @FXML
    public void initialize() {
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
            if (rootLayoutController != null) {
                rootLayoutController.setCenterView(addBookView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void chooseCoverImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Capa do Livro");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null); // Use null para o Stage padrão
        if (selectedFile != null) {
            selectedCoverFile = selectedFile;
            coverFileNameLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    public void saveBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Pane addBookView = loader.load();

            TextField titleTextField = (TextField) addBookView.lookup("#titleTextField");
            TextField authorTextField = (TextField) addBookView.lookup("#authorTextField");
            TextField isbnTextField = (TextField) addBookView.lookup("#isbnTextField");
            TextField publisherTextField = (TextField) addBookView.lookup("#publisherTextField");
            TextField yearTextField = (TextField) addBookView.lookup("#yearTextField");
            TextField genreTextField = (TextField) addBookView.lookup("#genreTextField");
            TextField totalCopiesTextField = (TextField) addBookView.lookup("#totalCopiesTextField");

            String title = titleTextField.getText();
            String author = authorTextField.getText();
            String isbn = isbnTextField.getText();
            String publisher = publisherTextField.getText();
            String year = yearTextField.getText();
            String genre = genreTextField.getText();
            String totalCopiesStr = totalCopiesTextField.getText();

            int totalCopies = 0;
            try {
                totalCopies = Integer.parseInt(totalCopiesStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }

            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPublisher(publisher);
            book.setYear(year);
            book.setGenre(genre);
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(totalCopies);

            // Lógica para salvar a imagem (apenas o caminho por enquanto)
            if (selectedCoverFile != null) {
                book.setCoverImagePath(selectedCoverFile.getAbsolutePath());
                System.out.println("Caminho da capa: " + selectedCoverFile.getAbsolutePath());
                // No futuro, você pode copiar o arquivo para uma pasta específica
                // ou salvar os dados binários no banco de dados.
            }

            bookService.insertBook(book);
            loadingBooks();
            cancelAddBookView();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelAddBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookView.fxml"));
            Pane bookView = loader.load();
            if (rootLayoutController != null) {
                rootLayoutController.setCenterView(bookView);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchBook() { // Adicione este método
        String searchTerm = searchBookTextField.getText();
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<Book> searchResults = bookService.findBooksByTitle(searchTerm); // Busca por título como exemplo
                updateBookDisplay(searchResults);
            } catch (SQLException e) {
                e.printStackTrace();
                // Lidar com o erro de busca
            }
        } else {
            try {
                loadingBooks(); // Se o campo de busca estiver vazio, mostrar todos os livros
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadingBooks() throws SQLException {
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
        ImageView coverImageView = new ImageView(new Image("@/images/book_cover_placeholder.png"));
        coverImageView.setFitWidth(100);
        coverImageView.setFitHeight(150);
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold;");
        Label authorLabel = new Label("Autor: " + book.getAuthor());
        Label availableLabel = new Label("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        Button detailsButton = new Button("Detalhes");

        card.getChildren().addAll(coverImageView, titleLabel, authorLabel, availableLabel, detailsButton);
        return card;
    }
}