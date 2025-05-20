package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image; // Para a imagem da capa, se houver
import javafx.scene.image.ImageView; // Para exibir a imagem da capa
import javafx.stage.Stage;

import java.io.IOException; // Para tratamento de erro ao carregar imagem
import java.io.InputStream; // Para carregar recursos internos

/**
 * Controlador para a tela de detalhes de um livro.
 * Exibe as informações completas de um livro selecionado.
 * Esta tela é puramente de visualização e não realiza operações de serviço.
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
    @FXML
    private ImageView coverImageView; // Para exibir a capa do livro

    // --- Variáveis de suporte ---
    private Stage dialogStage; // Referência para o próprio palco (Stage) do diálogo modal

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
        if (book != null) {
            titleLabel.setText("Título: " + book.getTitle());
            authorLabel.setText("Autor: " + book.getAuthor());
            isbnLabel.setText("ISBN: " + book.getIsbn());
            publisherLabel.setText("Editora: " + book.getPublisher());
            yearLabel.setText("Ano: " + book.getYear()); // Alterei para 'Ano:' para ser mais conciso
            genreLabel.setText("Gênero: " + book.getGenre());
            // Exemplo de como exibir cópias: "Disponíveis: X/Y"
            copiesLabel.setText("Cópias: " + book.getAvailableCopies() + "/" + book.getTotalCopies());

            // Carrega e exibe a imagem da capa
            displayBookCover(book);
        } else {
            // Limpa os campos ou exibe uma mensagem padrão se o livro for nulo
            titleLabel.setText("Título: N/A");
            authorLabel.setText("Autor: N/A");
            isbnLabel.setText("ISBN: N/A");
            publisherLabel.setText("Editora: N/A");
            yearLabel.setText("Ano: N/A");
            genreLabel.setText("Gênero: N/A");
            copiesLabel.setText("Cópias: N/A");
            coverImageView.setImage(null); // Limpa a imagem também
        }
    }

    /**
     * Carrega e exibe a imagem da capa do livro.
     * Tenta carregar do caminho local e, se falhar, usa uma imagem padrão.
     * @param book O livro cuja capa será exibida.
     */
    private void displayBookCover(Book book) {
        // Verifica se há um caminho de capa e se o arquivo existe
        if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
            try {
                Image image = new Image("file:" + book.getCoverImagePath());
                if (image.isError()) {
                    System.err.println("Erro ao carregar imagem do caminho local: " + book.getCoverImagePath());
                    // Tenta carregar a imagem padrão se o caminho local falhar
                    loadDefaultCoverImage();
                    return;
                }
                coverImageView.setImage(image);
                return; // Imagem carregada com sucesso
            } catch (Exception e) {
                System.err.println("Exceção ao carregar capa do livro por caminho local: " + e.getMessage());
                // Se ocorrer uma exceção, tenta carregar a imagem padrão
            }
        }
        // Se não tem caminho de capa, ou se o carregamento do caminho falhou, tenta imagem padrão
        loadDefaultCoverImage();
    }

    /**
     * Carrega a imagem padrão de capa do livro a partir dos recursos da aplicação.
     */
    private void loadDefaultCoverImage() {
        try (InputStream is = getClass().getResourceAsStream("/images/default_book_icon.png")) {
            if (is != null) {
                coverImageView.setImage(new Image(is));
            } else {
                System.err.println("Recurso /images/default_book_icon.png não encontrado.");
                coverImageView.setImage(null); // Em último caso, não exibe imagem
            }
        } catch (IOException e) {
            System.err.println("Erro de I/O ao carregar imagem padrão: " + e.getMessage());
            coverImageView.setImage(null);
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
        // Você pode, por exemplo, configurar tooltips ou listeners se precisar.
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
}