package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.services.BookService; // <--- Importação necessária para BookService
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Objects; // <--- Importação necessária para Objects.requireNonNull

/**
 * Controlador para a tela de detalhes de um livro.
 * Exibe as informações completas de um livro selecionado.
 * Esta tela é puramente de visualização e não realiza operações de serviço.
 * NO ENTANTO, o BookService é injetado para flexibilidade futura.
 */
public class BookDetailsController {

    // --- Componentes FXML para exibição dos detalhes do livro ---
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
    private Label copiesLabel; // Para exibir Cópias Totais / Cópias Disponíveis

    // --- Variáveis de suporte ---
    private Stage dialogStage; // Referência para o próprio palco (Stage) do diálogo modal
    private Book book; // Adicionado para manter a referência do livro

    private BookService bookService; // <--- NOVO CAMPO PARA O BOOKSERVICE

    /**
     * Define o palco (Stage) para este controlador.
     * Essencial para permitir que o controlador feche o próprio diálogo modal.
     * @param dialogStage O palco (Stage) do diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Define o livro cujos detalhes serão exibidos nesta tela.
     * Este é o método principal chamado pelo controlador que abre este diálogo (ex: BookCardController ou BookController).
     *
     * @param book O objeto Book com os detalhes a serem exibidos.
     */
    public void setBook(Book book) {
        this.book = book; // Armazena o livro
        updateUI(); // Chama o método para atualizar a interface
    }

    /**
     * Define o serviço de livros.
     * Este método deve ser chamado durante a inicialização da aplicação (geralmente pelo RootLayoutController
     * ou pelo BookController) para injetar a dependência.
     * Embora esta tela seja primariamente de visualização, a injeção é útil para flexibilidade futura.
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) { // <--- NOVO MÉTODO SETTER
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo em BookDetailsController.");
    }


    /**
     * Atualiza os campos da UI com as informações do livro.
     */
    private void updateUI() {
        if (book != null) {
            titleLabel.setText("Título: " + book.getTitle());
            authorLabel.setText("Autor: " + book.getAuthor());
            isbnLabel.setText("ISBN: " + book.getIsbn());
            publisherLabel.setText("Editora: " + book.getPublisher());
            yearLabel.setText("Ano: " + book.getYear());
            genreLabel.setText("Gênero: " + book.getGenre());
            copiesLabel.setText("Cópias: " + book.getAvailableCopies() + "/" + book.getTotalCopies());

            // Se você tinha registeredAt e lastUpdate e quer exibi-los, adicione a lógica aqui.
            // Exemplo:
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            // if (book.getRegisteredAt() != null) {
            //     registeredAtLabel.setText("Registrado em: " + book.getRegisteredAt().format(formatter));
            // } else {
            //     registeredAtLabel.setText("Registrado em: N/A");
            // }
            // if (book.getLastUpdate() != null) {
            //     lastUpdateLabel.setText("Última Atualização: " + book.getLastUpdate().format(formatter));
            // } else {
            //     lastUpdateLabel.setText("Última Atualização: N/A");
            // }
        } else {
            // Limpa os campos ou exibe uma mensagem padrão se o livro for nulo
            titleLabel.setText("Título: N/A");
            authorLabel.setText("Autor: N/A");
            isbnLabel.setText("ISBN: N/A");
            publisherLabel.setText("Editora: N/A");
            yearLabel.setText("Ano: N/A");
            genreLabel.setText("Gênero: N/A");
            copiesLabel.setText("Cópias: N/A");
        }
    }

    /**
     * Método de inicialização FXML.
     * Geralmente usado para configurar listeners ou inicializar componentes que não dependem do objeto Book.
     */
    @FXML
    private void initialize() {
        // Não há necessidade de lógica de inicialização complexa aqui,
        // pois os detalhes do livro são preenchidos por setBook().
    }

    /**
     * Manipula o evento de clique do botão de fechar.
     * Fecha o diálogo modal.
     */
    @FXML
    private void handleClose() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    // Método logError adicionado para depuração, como em outras classes
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