package com.managerlibrary.controllers; // Certifique-se de que o pacote está correto

import com.managerlibrary.entities.Book;
import com.managerlibrary.services.BookService; // <--- Importação necessária
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox; // Ou o tipo de layout raiz do seu BookCardView.fxml

import java.util.Objects; // <--- Importação necessária

/**
 * Controlador para o card individual de um livro exibido na tela principal.
 * Permite visualizar informações resumidas e interagir com o livro (detalhes, editar, remover).
 */
public class BookCardController {

    @FXML
    private VBox bookCardVBox; // O elemento raiz do seu BookCardView.fxml
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label availableCopiesLabel;

    private Book book; // O livro associado a este card
    private BookController bookListController; // Referência ao BookController principal
    private RootLayoutController rootLayoutController; // Referência ao RootLayoutController

    private BookService bookService; // <--- NOVO CAMPO PARA O BOOKSERVICE

    /**
     * Define o livro para este card e atualiza a interface.
     * @param book O objeto Book a ser exibido no card.
     */
    public void setBook(Book book) {
        this.book = book;
        updateCardUI();
    }

    /**
     * Define o BookController principal para este card.
     * Permite que as ações do card (ex: "Ver Detalhes", "Editar", "Remover")
     * acionem métodos no controlador principal.
     * @param bookListController O BookController principal.
     */
    public void setBookListController(BookController bookListController) {
        this.bookListController = Objects.requireNonNull(bookListController, "BookListController não pode ser nulo.");
    }

    /**
     * Define o RootLayoutController.
     * Útil para exibir alertas com base no palco principal ou para navegação.
     * @param rootLayoutController O RootLayoutController.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = Objects.requireNonNull(rootLayoutController, "RootLayoutController não pode ser nulo.");
    }

    /**
     * Define o serviço de livros.
     * Este método deve ser chamado para injetar a dependência, permitindo
     * que o card realize operações de serviço (ex: deletar/editar).
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) { // <--- NOVO MÉTODO SETTER
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo em BookCardController.");
    }

    /**
     * Método de inicialização do controlador. Chamado automaticamente pelo FXMLLoader.
     */
    @FXML
    private void initialize() {
        // Inicializações que não dependem do objeto 'book' podem vir aqui.
        // Os dados do livro são setados por setBook().
    }

    /**
     * Atualiza os Labels do card com as informações do livro.
     */
    private void updateCardUI() {
        if (book != null) {
            titleLabel.setText(book.getTitle());
            authorLabel.setText("Autor: " + book.getAuthor());
            isbnLabel.setText("ISBN: " + book.getIsbn());
            availableCopiesLabel.setText("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        } else {
            // Limpa ou exibe uma mensagem padrão se o livro for nulo
            titleLabel.setText("Livro Desconhecido");
            authorLabel.setText("Autor: N/A");
            isbnLabel.setText("ISBN: N/A");
            availableCopiesLabel.setText("Disponíveis: N/A");
        }
    }

    /**
     * Manipula o clique no card (ou um botão "Ver Detalhes").
     * Abre a tela de detalhes do livro.
     */
    @FXML
    private void handleViewDetails() {
        if (bookListController != null && book != null) {
            // Passa a janela proprietária (Stage) do card para o diálogo de detalhes
            bookListController.showBookDetails(book, bookCardVBox.getScene().getWindow());
        } else {
            logError("Erro: BookListController ou Book não definidos para ver detalhes.", null);
        }
    }

    /**
     * Manipula o clique no botão de editar.
     * Abre a tela de edição do livro.
     */
    @FXML
    private void handleEditBook() {
        if (bookListController != null && book != null) {
            bookListController.loadEditBookView(book, bookCardVBox.getScene().getWindow());
        } else {
            logError("Erro: BookListController ou Book não definidos para editar.", null);
        }
    }

    /**
     * Manipula o clique no botão de remover.
     * Chama o método de confirmação de remoção no BookController principal.
     */
    @FXML
    private void handleRemoveBook() {
        if (bookListController != null && book != null) {
            bookListController.confirmRemoveBook(book, bookCardVBox.getScene().getWindow());
        } else {
            logError("Erro: BookListController ou Book não definidos para remover.", null);
        }
    }

    // Método de log de erro, como em outras classes.
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