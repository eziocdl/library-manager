package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Window; // Importe Window

import java.io.File;

/**
 * Controlador para o card individual de um livro exibido na lista de livros.
 * Responsável por exibir as informações do livro e fornecer ações como detalhes,
 * edição e remoção.
 */
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
    private Button editButton;
    @FXML
    private Button detailsButton;
    @FXML
    private Button removeButton;

    private Book book;
    private BookController bookListController;

    public void setBook(Book book) {
        this.book = book;
        updateCard();
    }

    public void setBookListController(BookController bookListController) {
        this.bookListController = bookListController;
    }

    private void updateCard() {
        if (book == null) {
            titleLabel.setText("");
            authorLabel.setText("");
            availableLabel.setText("");
            isbnLabel.setText("");
            publisherLabel.setText("");
            yearLabel.setText("");
            genreLabel.setText("");
            coverImageView.setImage(null);
            return;
        }

        titleLabel.setText(book.getTitle());
        authorLabel.setText("Autor: " + book.getAuthor());
        availableLabel.setText("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        isbnLabel.setText("ISBN: " + book.getIsbn());
        publisherLabel.setText("Editora: " + book.getPublisher());
        yearLabel.setText("Ano: " + book.getYear());
        genreLabel.setText("Gênero: " + book.getGenre());
        loadBookCover();
    }

    private void loadBookCover() {
        Image imageToSet = null;

        if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
            File file = new File(book.getCoverImagePath());
            if (file.exists()) {
                try {
                    imageToSet = new Image(file.toURI().toString());
                    if (!imageToSet.isError()) {
                        coverImageView.setImage(imageToSet);
                        return;
                    } else {
                        logError("Erro ao carregar imagem do arquivo local (isError): " + book.getCoverImagePath(), null);
                    }
                } catch (Exception e) {
                    logError("Erro ao carregar imagem do arquivo local: " + book.getCoverImagePath(), e);
                }
            }
        }

        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            try {
                imageToSet = new Image(book.getImageUrl());
                if (!imageToSet.isError()) {
                    coverImageView.setImage(imageToSet);
                    return;
                } else {
                    logError("Erro ao carregar imagem da URL (isError): " + book.getImageUrl(), null);
                }
            } catch (Exception e) {
                logError("Erro ao carregar imagem da URL: " + book.getImageUrl(), e);
            }
        }

        try {
            imageToSet = new Image(getClass().getResourceAsStream("/images/default_book_icon.png"));
            coverImageView.setImage(imageToSet);
        } catch (Exception e) {
            logError("Erro ao carregar imagem padrão '/images/default_book_icon.png'", e);
            coverImageView.setImage(null);
        }
    }

    @FXML
    private void handleDetails() {
        if (bookListController != null && book != null && detailsButton != null && detailsButton.getScene() != null) {
            bookListController.showBookDetails(book, detailsButton.getScene().getWindow());
        } else {
            logError("Não foi possível mostrar detalhes do livro. Dependências ausentes ou cena indisponível.", null);
        }
    }

    @FXML
    private void handleEdit() {
        if (bookListController != null && book != null && editButton != null) {
            // CORREÇÃO: Obter o Stage (Window) do botão para passar como owner
            Window ownerWindow = editButton.getScene().getWindow();
            bookListController.loadEditBookView(book, ownerWindow); // Passa a Window
        } else {
            logError("Não foi possível carregar a tela de edição. Dependências ausentes.", null);
        }
    }

    @FXML
    private void handleRemove() {
        if (bookListController != null && book != null && removeButton != null && removeButton.getScene() != null) {
            bookListController.confirmRemoveBook(book, removeButton.getScene().getWindow());
        } else {
            logError("Não foi possível remover o livro. Dependências ausentes ou cena indisponível.", null);
        }
    }

    private void logError(String message, Exception e) {
        System.err.print(message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }
}