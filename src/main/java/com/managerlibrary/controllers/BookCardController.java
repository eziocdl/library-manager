package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.services.BookService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File; // Importar File
import java.util.Objects;

public class BookCardController {

    @FXML
    private VBox bookCardVBox;
    @FXML
    private ImageView coverImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label availableCopiesLabel;
    @FXML
    private Button editButton;

    private Book book;
    private BookController bookListController;
    private RootLayoutController rootLayoutController;
    private BookService bookService;

    public void setBook(Book book) {
        this.book = book;
        updateCardUI();
    }

    public void setBookListController(BookController bookListController) {
        this.bookListController = Objects.requireNonNull(bookListController, "BookListController não pode ser nulo.");
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = Objects.requireNonNull(rootLayoutController, "RootLayoutController não pode ser nulo.");
    }

    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo em BookCardController.");
    }

    @FXML
    private void initialize() {
        // Nenhuma inicialização específica aqui.
    }

    private void updateCardUI() {
        if (book != null) {
            titleLabel.setText(book.getTitle());
            authorLabel.setText("Autor: " + book.getAuthor());
            isbnLabel.setText("ISBN: " + book.getIsbn());
            publisherLabel.setText("Editora: " + book.getPublisher());
            yearLabel.setText("Ano: " + book.getYear());
            genreLabel.setText("Gênero: " + book.getGenre());
            availableCopiesLabel.setText("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());

            // --- Lógica de carregamento da imagem ---
            String imagePath = book.getCoverImagePath(); // Use o caminho do arquivo
            String imageUrl = book.getImageUrl(); // Ou a URL (se houver)

            // Priorize o caminho do arquivo local, se existir
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    try {
                        Image image = new Image(imageFile.toURI().toString());
                        coverImageView.setImage(image);
                        if (image.isError()) {
                            logError("Erro ao carregar a imagem do arquivo para o livro " + book.getTitle() + ". Caminho: " + imagePath + ". Erro: " + image.exceptionProperty().get().getMessage(), null);
                            coverImageView.setImage(null);
                        } else if (image.getWidth() == 0 && image.getHeight() == 0) {
                            logError("Imagem do arquivo carregada para " + book.getTitle() + " mas com dimensões zero. Caminho: " + imagePath, null);
                        }
                    } catch (IllegalArgumentException e) {
                        logError("Caminho de arquivo da imagem inválido para o livro " + book.getTitle() + ": " + imagePath, e);
                        coverImageView.setImage(null);
                    } catch (Exception e) {
                        logError("Erro inesperado ao carregar imagem do arquivo para o livro " + book.getTitle() + ": " + imagePath, e);
                        coverImageView.setImage(null);
                    }
                } else {
                    // Se o arquivo não existe, tente carregar da URL se houver uma
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        System.out.println("Debug: Arquivo não encontrado. Tentando carregar imagem da URL para '" + book.getTitle() + "': " + imageUrl);
                        loadImageFromUrl(imageUrl);
                    } else {
                        logError("Arquivo de imagem não encontrado e URL de imagem ausente para o livro: " + book.getTitle(), null);
                        coverImageView.setImage(null);
                    }
                }
            } else if (imageUrl != null && !imageUrl.isEmpty()) { // Se não há coverImagePath, tente a imageUrl
                System.out.println("Debug: coverImagePath vazio. Tentando carregar imagem da URL para '" + book.getTitle() + "': " + imageUrl);
                loadImageFromUrl(imageUrl);
            } else {
                coverImageView.setImage(null);
                System.out.println("Debug: Imagem URL e CoverImagePath para " + book.getTitle() + " são nulos ou vazios. Não carregada.");
            }
            // --- Fim da lógica de carregamento da imagem ---

        } else {
            titleLabel.setText("Livro Desconhecido");
            authorLabel.setText("Autor: N/A");
            isbnLabel.setText("ISBN: N/A");
            publisherLabel.setText("Editora: N/A");
            yearLabel.setText("Ano: N/A");
            genreLabel.setText("Gênero: N/A");
            availableCopiesLabel.setText("Disponíveis: N/A");
            if (coverImageView != null) {
                coverImageView.setImage(null);
            }
        }
    }

    // Novo método para carregar imagem de URL, para evitar duplicação de código
    private void loadImageFromUrl(String imageUrl) {
        try {
            Image image = new Image(imageUrl);
            coverImageView.setImage(image);
            if (image.isError()) {
                logError("Erro ao carregar a imagem da URL para o livro '" + book.getTitle() + "'. URL: " + imageUrl + ". Erro: " + image.exceptionProperty().get().getMessage(), null);
                coverImageView.setImage(null);
            } else if (image.getWidth() == 0 && image.getHeight() == 0) {
                logError("Imagem da URL carregada para " + book.getTitle() + " mas com dimensões zero. URL: " + imageUrl, null);
            }
        } catch (IllegalArgumentException e) {
            logError("URL da imagem inválida para o livro " + book.getTitle() + ": " + imageUrl, e);
            coverImageView.setImage(null);
        } catch (Exception e) {
            logError("Erro inesperado ao carregar imagem da URL para o livro " + book.getTitle() + ": " + imageUrl, e);
            coverImageView.setImage(null);
        }
    }


    @FXML
    private void handleViewDetails() {
        System.out.println("Clicou em Detalhes para: " + (book != null ? book.getTitle() : "N/A"));
        if (bookListController != null && book != null && bookCardVBox != null) {
            bookListController.showBookDetails(book, bookCardVBox.getScene().getWindow());
        } else {
            logError("Erro: BookListController, Book ou bookCardVBox não definidos para ver detalhes. (Book: " + (book == null ? "null" : book.getTitle()) + ", BookListController: " + (bookListController == null ? "null" : "ok") + ", bookCardVBox: " + (bookCardVBox == null ? "null" : "ok") + ")", null);
        }
    }

    @FXML
    private void handleEditBook() {
        System.out.println("Clicou em Editar para: " + (book != null ? book.getTitle() : "N/A"));
        if (bookListController != null && book != null && bookCardVBox != null) {
            bookListController.loadEditBookView(book, bookCardVBox.getScene().getWindow());
        } else {
            logError("Erro: BookListController, Book ou bookCardVBox não definidos para editar. (Book: " + (book == null ? "null" : book.getTitle()) + ", BookListController: " + (bookListController == null ? "null" : "ok") + ", bookCardVBox: " + (bookCardVBox == null ? "null" : "ok") + ")", null);
        }
    }

    @FXML
    private void handleRemoveBook() {
        System.out.println("Clicou em Remover para: " + (book != null ? book.getTitle() : "N/A"));
        if (bookListController != null && book != null && bookCardVBox != null) {
            bookListController.confirmRemoveBook(book, bookCardVBox.getScene().getWindow());
        } else {
            logError("Erro: BookListController, Book ou bookCardVBox não definidos para remover. (Book: " + (book == null ? "null" : book.getTitle()) + ", BookListController: " + (bookListController == null ? "null" : "ok") + ", bookCardVBox: " + (bookCardVBox == null ? "null" : "ok") + ")", null);
        }
    }

    private void logError(String message, Exception e) {
        System.err.print("ERRO no BookCardController: " + message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }
}