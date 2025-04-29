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
import java.sql.SQLException;

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

    public Book getBook() { // Adicione este método
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        String publisher = publisherField.getText();
        String yearStr = yearField.getText();
        String genre = genreField.getText();
        String quantityStr = quantityField.getText();

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

        if (isEditing && bookToEdit != null) {
            bookToEdit.setTitle(title);
            bookToEdit.setAuthor(author);
            bookToEdit.setIsbn(isbn);
            bookToEdit.setPublisher(publisher);
            bookToEdit.setYear(year);
            bookToEdit.setGenre(genre);
            // Manter a lógica de ajuste de cópias disponíveis ao editar
            // Assumindo que você quer atualizar available_copies também
            int diff = quantity - bookToEdit.getTotalCopies();
            bookToEdit.setTotalCopies(quantity);
            bookToEdit.setAvailableCopies(bookToEdit.getAvailableCopies() + diff);
            bookToEdit.setImageUrl(imageUrlField.getText());
            if (selectedCoverFile != null) {
                bookToEdit.setCoverImagePath(selectedCoverFile.getAbsolutePath());
            }
            return bookToEdit;
        } else {
            Book newBook = new Book();
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setIsbn(isbn);
            newBook.setPublisher(publisher);
            newBook.setYear(year);
            newBook.setGenre(genre);
            newBook.setTotalCopies(quantity);
            newBook.setAvailableCopies(quantity);
            newBook.setImageUrl(imageUrlField.getText());
            if (selectedCoverFile != null) {
                newBook.setCoverImagePath(selectedCoverFile.getAbsolutePath());
            }
            return newBook;
        }
    }

    @FXML
    public void saveBook(ActionEvent event) {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        String publisher = publisherField.getText();
        String yearStr = yearField.getText();
        String genre = genreField.getText();
        String quantityStr = quantityField.getText();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || publisher.isEmpty() || yearStr.isEmpty() || genre.isEmpty() || quantityStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Todos os campos são obrigatórios.");
            return;
        }

        try {
            Integer.parseInt(quantityStr);
            Integer.parseInt(yearStr); // Adicionado a validação do ano aqui também
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Ano ou quantidade inválida.");
            return;
        }

        saveClicked = true; // Define saveClicked como true ao clicar em Salvar
        Stage stage = (Stage) saveBookButton.getScene().getWindow();
        stage.close();
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