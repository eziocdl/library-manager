package com.managerlibrary.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.managerlibrary.entities.Book;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;

public class AddBookViewController {

    private BookController bookController;
    private Stage dialogStage;
    private Book bookToEdit;
    private boolean saveClicked = false;
    private File selectedCoverFile;

    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField isbnField;
    @FXML
    private TextField publisherField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField imageUrlField;
    @FXML
    private Label coverFileNameLabel;
    @FXML
    private ImageView coverImageView;

    @FXML
    public void initialize() {
        // Código de inicialização, se necessário
    }

    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBookToEdit(Book book) {
        this.bookToEdit = book;
        if (book != null) {
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            isbnField.setText(book.getIsbn());
            publisherField.setText(book.getPublisher());
            yearField.setText(String.valueOf(book.getYear()));
            genreField.setText(book.getGenre());
            quantityField.setText(String.valueOf(book.getTotalCopies()));
            imageUrlField.setText(book.getImageUrl());
            // Carregar a imagem se houver um caminho
            if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
                File file = new File(book.getCoverImagePath());
                if (file.exists()) {
                    coverImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
                    coverFileNameLabel.setText(file.getName());
                    selectedCoverFile = file;
                } else if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                    coverImageView.setImage(new javafx.scene.image.Image(book.getImageUrl()));
                    coverFileNameLabel.setText("URL");
                    selectedCoverFile = null; // Se carregou da URL, não há arquivo selecionado
                } else {
                    coverImageView.setImage(null);
                    coverFileNameLabel.setText("Nenhuma imagem");
                    selectedCoverFile = null;
                }
            } else if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                coverImageView.setImage(new javafx.scene.image.Image(book.getImageUrl()));
                coverFileNameLabel.setText("URL");
                selectedCoverFile = null;
            } else {
                coverImageView.setImage(null);
                coverFileNameLabel.setText("Nenhuma imagem");
                selectedCoverFile = null;
            }
        }
    }

    public Book getBook() {
        Book book = new Book();
        book.setTitle(titleField.getText());
        book.setAuthor(authorField.getText());
        book.setIsbn(isbnField.getText());
        book.setPublisher(publisherField.getText());
        try {
            book.setYear(Integer.parseInt(yearField.getText()));
            book.setTotalCopies(Integer.parseInt(quantityField.getText()));
            book.setAvailableCopies(book.getTotalCopies()); // Inicialmente, todos estão disponíveis
        } catch (NumberFormatException e) {
            // Lidar com erro de formato nos campos numéricos
            return null;
        }
        book.setGenre(genreField.getText());
        book.setImageUrl(imageUrlField.getText());
        if (selectedCoverFile != null) {
            book.setCoverImagePath(selectedCoverFile.getAbsolutePath());
        } else if (bookToEdit != null) {
            book.setCoverImagePath(bookToEdit.getCoverImagePath()); // Mantém o caminho anterior se não escolher novo arquivo
        }
        return book;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void saveBook() {
        if (isInputValid()) {
            saveClicked = true;
            Book book = getBook();
            if (book != null) {
                if (bookToEdit == null) {
                    bookController.insertNewBook(book);
                } else {
                    book.setId(bookToEdit.getId()); // Mantém o ID para a edição
                    bookController.updateBook(book);
                }
                dialogStage.close();
            } else {
                // Exibir mensagem de erro de validação
            }
        }
    }

    @FXML
    private void cancelAddBookView() {
        dialogStage.close();
    }

    @FXML
    private void chooseCoverImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Capa do Livro");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showOpenDialog(dialogStage);
        if (file != null) {
            selectedCoverFile = file;
            coverFileNameLabel.setText(file.getName());
            coverImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
            imageUrlField.setText(""); // Limpa a URL se escolher um arquivo
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (titleField.getText() == null || titleField.getText().isEmpty()) {
            errorMessage += "Título inválido!\n";
        }
        if (authorField.getText() == null || authorField.getText().isEmpty()) {
            errorMessage += "Autor inválido!\n";
        }
        if (isbnField.getText() == null || isbnField.getText().isEmpty()) {
            errorMessage += "ISBN inválido!\n";
        }
        if (publisherField.getText() == null || publisherField.getText().isEmpty()) {
            errorMessage += "Editora inválida!\n";
        }
        if (yearField.getText() == null || yearField.getText().isEmpty()) {
            errorMessage += "Ano inválido!\n";
        } else {
            try {
                Integer.parseInt(yearField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Ano deve ser um número!\n";
            }
        }
        if (genreField.getText() == null || genreField.getText().isEmpty()) {
            errorMessage += "Gênero inválido!\n";
        }
        if (quantityField.getText() == null || quantityField.getText().isEmpty()) {
            errorMessage += "Quantidade inválida!\n";
        } else {
            try {
                Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Quantidade deve ser um número!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Exibir mensagem de erro
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Campos Inválidos");
            alert.setHeaderText("Por favor, corrija os campos inválidos");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}