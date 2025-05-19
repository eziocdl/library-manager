package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private Button editButton; // Botão para editar o livro
    @FXML
    private Button detailsButton; // Botão para exibir detalhes do livro
    @FXML
    private Button removeButton; // Botão para remover o livro

    private Book book;
    private BookController bookListController; // Referência ao BookController para interagir com a lista de livros

    /**
     * Define o livro a ser exibido neste card e atualiza as informações visuais.
     *
     * @param book O livro a ser exibido.
     */
    public void setBook(Book book) {
        this.book = book;
        updateCard(book);
    }

    /**
     * Define o controlador da tela principal de livros para permitir ações na lista.
     *
     * @param bookListController O controlador da tela principal de livros.
     */
    public void setBookListController(BookController bookListController) {
        this.bookListController = bookListController;
    }

    /**
     * Atualiza os elementos visuais do card com as informações do livro fornecido.
     * Carrega a capa do livro do arquivo local ou da URL, se disponível.
     *
     * @param book O livro cujas informações serão exibidas.
     */
    private void updateCard(Book book) {
        titleLabel.setText(book.getTitle());
        authorLabel.setText("Autor: " + book.getAuthor());
        availableLabel.setText("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        isbnLabel.setText("ISBN: " + book.getIsbn());
        publisherLabel.setText("Editora: " + book.getPublisher());
        yearLabel.setText("Ano: " + book.getYear());
        genreLabel.setText("Gênero: " + book.getGenre());
        loadBookCover();
    }

    /**
     * Carrega a imagem da capa do livro, primeiro tentando a URL e depois o caminho do arquivo local.
     * Se nenhum for válido, a ImageView ficará com a imagem padrão (ou vazia).
     */
    private void loadBookCover() {
        Image imageToSet = null;

        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            imageToSet = loadImageFromPathOrUrl(book.getImageUrl(), "URL");
        } else if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
            imageToSet = loadImageFromPathOrUrl(book.getCoverImagePath(), "Path");
        }

        coverImageView.setImage(imageToSet);
    }

    /**
     * Tenta carregar uma imagem de um determinado caminho (URL ou arquivo local).
     * Registra erros no console se a imagem não puder ser carregada.
     *
     * @param pathOrUrl O caminho da imagem (URL ou arquivo local).
     * @param sourceType Uma string indicando a origem do caminho ("URL" ou "Path").
     * @return A imagem carregada, ou null se ocorrer um erro.
     */
    private Image loadImageFromPathOrUrl(String pathOrUrl, String sourceType) {
        try {
            File file = new File(pathOrUrl);
            if (file.exists()) {
                return new Image(file.toURI().toString());
            } else {
                System.err.println("Arquivo de imagem não encontrado (" + sourceType + "): " + pathOrUrl);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem da " + sourceType + ": " + pathOrUrl + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Manipula o evento de clique no botão de detalhes, exibindo as informações detalhadas do livro.
     */
    @FXML
    private void handleDetails() {
        if (bookListController != null && book != null && detailsButton != null && detailsButton.getScene() != null) {
            bookListController.showBookDetails(book, detailsButton.getScene().getWindow());
        }
    }

    /**
     * Manipula o evento de clique no botão de editar, carregando a tela de edição do livro.
     */
    @FXML
    private void handleEdit() {
        if (bookListController != null && book != null && editButton != null) {
            bookListController.loadEditBookView(book, editButton);
        }
    }

    /**
     * Manipula o evento de clique no botão de remover, solicitando confirmação e removendo o livro.
     */
    @FXML
    private void handleRemove() {
        if (bookListController != null && book != null && removeButton != null && removeButton.getScene() != null) {
            bookListController.confirmRemoveBook(book, removeButton.getScene().getWindow());
        }
    }
}