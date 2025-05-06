package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddBookViewController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField publisherField;
    @FXML private TextField yearField;
    @FXML private TextField genreField;
    @FXML private TextField quantityField;
    @FXML private TextField imageUrlField;
    @FXML private Button closeButton;
    @FXML private Button saveBookButton;
    @FXML private Label coverFileNameLabel;

    private File selectedCoverFile;
    private Stage dialogStage;
    private BookController bookController;
    private Book bookToEdit;
    private boolean isEditing = false;
    private boolean saveClicked = false; // Adicione esta variável

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    public void setBookToEdit(Book book) {
        this.bookToEdit = book;
        this.isEditing = true;
        populateFields();
        saveBookButton.setText("Salvar Alterações");
    }

    @FXML
    public void initialize() {
        // Lógica de inicialização para AddBookView, se necessário
    }

    private void populateFields() {
        if (bookToEdit != null) {
            System.out.println("PopulateFields recebendo: " + bookToEdit.getTitle() + ", Editora: " + bookToEdit.getPublisher() + ", Ano: " + bookToEdit.getYear()); // ADICIONE ESTE LOG
            titleField.setText(bookToEdit.getTitle());
            authorField.setText(bookToEdit.getAuthor());
            isbnField.setText(bookToEdit.getIsbn());
            publisherField.setText(bookToEdit.getPublisher());
            yearField.setText(String.valueOf(bookToEdit.getYear()));
            genreField.setText(bookToEdit.getGenre());
            quantityField.setText(String.valueOf(bookToEdit.getTotalCopies()));
            imageUrlField.setText(bookToEdit.getImageUrl());
            // TODO: Lógica para carregar a imagem da capa, se necessário
        }
    }

    @FXML
    public void chooseCoverImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Capa do Livro");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            selectedCoverFile = selectedFile;
            coverFileNameLabel.setText(selectedFile.getName());
        }
    }

    public boolean isSaveClicked() { // Adicione este método
        return saveClicked;
    }

    public Book getBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        String publisher = publisherField.getText();
        String yearStr = yearField.getText();
        String genre = genreField.getText();
        String quantityStr = quantityField.getText();
        String imageUrl = imageUrlField.getText();

        System.out.println("getBook(): Editora lida da tela: " + publisher); // LOG NO AddBookViewController
        System.out.println("getBook(): Ano lido da tela: " + yearStr);     // LOG NO AddBookViewController

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || publisher.isEmpty() || yearStr.isEmpty() || genre.isEmpty() || quantityStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Todos os campos são obrigatórios.");
            return null;
        }

        int year;
        int quantity;

        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Ano inválido.");
            return null;
        }

        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Quantidade inválida.");
            return null;
        }

        Book book;
        if (isEditing && bookToEdit != null) {
            book = bookToEdit;
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPublisher(publisher);
            book.setYear(year);
            book.setGenre(genre);
            int diff = quantity - book.getTotalCopies();
            book.setTotalCopies(quantity);
            book.setAvailableCopies(book.getAvailableCopies() + diff);
            book.setImageUrl(imageUrl);
            if (selectedCoverFile != null) {
                book.setCoverImagePath(selectedCoverFile.getAbsolutePath());
            }
        } else {
            book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPublisher(publisher);
            book.setYear(year);
            book.setGenre(genre);
            book.setTotalCopies(quantity);
            book.setAvailableCopies(quantity);
            book.setImageUrl(imageUrl);
            if (selectedCoverFile != null) {
                book.setCoverImagePath(selectedCoverFile.getAbsolutePath());
            }
        }
        return book;
    }
    @FXML
    public void saveBook(ActionEvent event) {
        Book book = getBook();
        if (book != null) {
            if (isEditing && bookToEdit != null) {
                book.setId(bookToEdit.getId()); // Garante que o ID seja mantido para a edição
                bookController.updateBook(book);
            } else {
                bookController.insertNewBook(book);
            }
            saveClicked = true;
            Stage stage = (Stage) saveBookButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void cancelAddBookView(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
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