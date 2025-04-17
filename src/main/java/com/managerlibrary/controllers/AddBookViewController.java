package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    @FXML
    public void saveBook(ActionEvent event) {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        String publisher = publisherField.getText();
        String year = yearField.getText();
        String genre = genreField.getText();
        String quantityStr = quantityField.getText();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || publisher.isEmpty() || year.isEmpty() || genre.isEmpty() || quantityStr.isEmpty()) {
            // TODO: Mostrar mensagem de erro
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            // TODO: Mostrar mensagem de erro para quantidade inválida
            return;
        }

        if (isEditing && bookToEdit != null) {
            // Modo de Edição
            bookToEdit.setTitle(title);
            bookToEdit.setAuthor(author);
            bookToEdit.setIsbn(isbn);
            bookToEdit.setPublisher(publisher);
            bookToEdit.setYear(year);
            bookToEdit.setGenre(genre);
            bookToEdit.setTotalCopies(quantity);
            bookToEdit.setAvailableCopies(bookToEdit.getAvailableCopies() + (quantity - bookToEdit.getTotalCopies())); // Ajusta cópias disponíveis
            bookToEdit.setImageUrl(imageUrlField.getText()); // Adicionado imageUrl

            if (selectedCoverFile != null) {
                bookToEdit.setCoverImagePath(selectedCoverFile.getAbsolutePath());
                // TODO: Lógica para salvar a imagem (copiar arquivo, etc.)
            }

            if (bookController != null) {
                bookController.updateBook(bookToEdit);
                if (dialogStage != null) {
                    dialogStage.close();
                }
            } else {
                System.err.println("BookController não injetado no AddBookViewController para edição.");
            }
        } else {
            // Modo de Adição
            Book newBook = new Book();
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setIsbn(isbn);
            newBook.setPublisher(publisher);
            newBook.setYear(year);
            newBook.setGenre(genre);
            newBook.setTotalCopies(quantity);
            newBook.setAvailableCopies(quantity);
            newBook.setImageUrl(imageUrlField.getText()); // Adicionado imageUrl

            if (selectedCoverFile != null) {
                newBook.setCoverImagePath(selectedCoverFile.getAbsolutePath());
                // TODO: Lógica para salvar a imagem (copiar arquivo, etc.)
            }

            if (bookController != null) {
                bookController.insertNewBook(newBook);
                if (dialogStage != null) {
                    dialogStage.close();
                }
            } else {
                System.err.println("BookController não injetado no AddBookViewController para adição.");
            }
        }
    }

    @FXML
    public void cancelAddBookView(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}