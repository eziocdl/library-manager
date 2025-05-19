package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class BookCardController {

    @FXML
    private ImageView coverImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label availableLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Button editButton; // Adicionado o @FXML para o botão Editar

    private Book book;
    private BookController bookListController; // Referência ao BookController para ações na lista

    public void setBook(Book book) {
        this.book = book;
        updateCard(book);
    }

    public void setBookListController(BookController bookListController) {
        this.bookListController = bookListController;
    }

    private void updateCard(Book book) {
        titleLabel.setText(book.getTitle());
        authorLabel.setText("Autor: " + book.getAuthor());
        availableLabel.setText("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        isbnLabel.setText("ISBN: " + book.getIsbn());
        publisherLabel.setText("Editora: " + book.getPublisher());
        yearLabel.setText("Ano: " + book.getYear());
        genreLabel.setText("Gênero: " + book.getGenre());

        Image imageToSet = null;

        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            try {
                File file = new File(book.getImageUrl());
                if (file.exists()) {
                    imageToSet = new Image(file.toURI().toString());
                } else {
                    System.err.println("Arquivo de imagem não encontrado (URL): " + book.getImageUrl());
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem da URL: " + book.getImageUrl() + " - " + e.getMessage());
            }
        } else if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
            try {
                File file = new File(book.getCoverImagePath());
                if (file.exists()) {
                    imageToSet = new Image(file.toURI().toString());
                } else {
                    System.err.println("Arquivo de imagem não encontrado (Path): " + book.getCoverImagePath());
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem do Path: " + book.getCoverImagePath() + " - " + e.getMessage());
            }
        }

        coverImageView.setImage(imageToSet); // Define a imagem, que pode ser null se não houver um caminho válido
    }

    @FXML
    private void handleDetails() {
        if (bookListController != null) {
            bookListController.showBookDetails(book, coverImageView.getScene().getWindow());
        }
    }

    @FXML
    private void handleEdit() {
        if (bookListController != null && editButton != null) {
            bookListController.loadEditBookView(book, editButton);
        }
    }

    @FXML
    private void handleRemove() {
        if (bookListController != null) {
            bookListController.confirmRemoveBook(book, coverImageView.getScene().getWindow());
        }
    }
}