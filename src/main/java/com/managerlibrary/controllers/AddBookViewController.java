package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.services.BookService; // Importe o BookService
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects; // Importe Objects para requireNonNull

/**
 * Controlador para a tela de adicionar/editar um livro.
 * Gerencia a interação do usuário com o formulário de entrada de dados de livros,
 * incluindo validações, seleção de imagem de capa e comunicação com o BookController principal.
 */
public class AddBookViewController {

    private BookController bookController;
    private BookService bookService; // NOVO: Referência ao BookService
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

    /**
     * Método de inicialização do controlador. Chamado após o FXML ser carregado.
     * Atualmente não possui lógica de inicialização específica.
     */
    @FXML
    public void initialize() {
        // Nenhuma inicialização específica por enquanto
    }

    /**
     * Define o controlador da tela principal de livros que interage com este diálogo.
     *
     * @param bookController O controlador da tela principal de livros.
     */
    public void setBookController(BookController bookController) {
        this.bookController = bookController;
    }

    /**
     * NOVO MÉTODO: Define o serviço de livros para que este controlador possa
     * realizar operações de negócio (salvar/atualizar).
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo em AddBookViewController.");
    }

    /**
     * Define o palco (Stage) deste diálogo modal.
     *
     * @param dialogStage O palco do diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Define o livro a ser editado. Se um livro for passado, o diálogo será preenchido
     * com os dados do livro para edição. Se for nulo, o diálogo será para adicionar um novo livro.
     *
     * @param book O livro a ser editado, ou nulo para adicionar um novo livro.
     */
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
            loadCoverImage(book);
        }
    }

    /**
     * Carrega a imagem da capa do livro, seja do caminho do arquivo local ou da URL.
     * A lógica foi refatorada para chamar métodos específicos de exibição.
     *
     * @param book O livro cuja capa será carregada.
     */
    private void loadCoverImage(Book book) {
        boolean hasCoverPath = book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty();
        boolean hasImageUrl = book.getImageUrl() != null && !book.getImageUrl().isEmpty();

        if (hasCoverPath) {
            File file = new File(book.getCoverImagePath());
            if (file.exists()) {
                displayCoverImageFromFile(file);
            } else if (hasImageUrl) {
                displayCoverImageFromUrl(book.getImageUrl());
            } else {
                clearCoverImageDisplay();
            }
        } else if (hasImageUrl) {
            displayCoverImageFromUrl(book.getImageUrl());
        } else {
            clearCoverImageDisplay();
        }
    }

    /**
     * Exibe a imagem da capa a partir de um arquivo local.
     * Em caso de erro, registra o erro e limpa a exibição.
     *
     * @param file O arquivo de imagem da capa.
     */
    private void displayCoverImageFromFile(File file) {
        try {
            Image coverImage = new Image(file.toURI().toString());
            coverImageView.setImage(coverImage);
            coverFileNameLabel.setText(file.getName());
            selectedCoverFile = file;
        } catch (Exception e) {
            logError("Erro ao carregar imagem do arquivo: " + file.getAbsolutePath(), e);
            clearCoverImageDisplay();
        }
    }

    /**
     * Exibe a imagem da capa a partir de uma URL.
     * Em caso de erro, registra o erro e limpa a exibição.
     *
     * @param imageUrl A URL da imagem da capa.
     */
    private void displayCoverImageFromUrl(String imageUrl) {
        try {
            Image coverImage = new Image(imageUrl);
            coverImageView.setImage(coverImage);
            coverFileNameLabel.setText("URL");
            selectedCoverFile = null;
        } catch (Exception e) {
            logError("Erro ao carregar imagem da URL: " + imageUrl, e);
            clearCoverImageDisplay();
        }
    }

    /**
     * Limpa a exibição da imagem da capa, definindo a imagem e o nome do arquivo para o estado padrão.
     */
    private void clearCoverImageDisplay() {
        coverImageView.setImage(null);
        coverFileNameLabel.setText("Nenhuma imagem");
        selectedCoverFile = null;
    }

    /**
     * Cria um objeto Book com os dados inseridos nos campos do formulário.
     * Retorna null se houver erro de formato nos campos numéricos, exibindo um alerta.
     *
     * @return Um objeto Book com os dados do formulário, ou null se a entrada for inválida.
     */
    public Book getBook() {
        try {
            int year = Integer.parseInt(yearField.getText());
            int totalCopies = Integer.parseInt(quantityField.getText());

            Book book = new Book();
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setIsbn(isbnField.getText());
            book.setPublisher(publisherField.getText());
            book.setYear(year);
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(totalCopies);
            book.setGenre(genreField.getText());
            book.setImageUrl(imageUrlField.getText());

            if (selectedCoverFile != null) {
                book.setCoverImagePath(selectedCoverFile.getAbsolutePath());
            } else if (bookToEdit != null) {
                book.setCoverImagePath(bookToEdit.getCoverImagePath());
            }
            return book;

        } catch (NumberFormatException e) {
            showAlert("Erro de Formato", "Por favor, insira números válidos para Ano e Quantidade.");
            return null;
        }
    }

    /**
     * Retorna a flag que indica se o botão "Salvar" foi clicado.
     *
     * @return true se o botão "Salvar" foi clicado, false caso contrário.
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }

    /**
     * Manipula o evento de clique no botão "Salvar". Valida a entrada do usuário e,
     * se válida, cria ou atualiza o livro através do BookController e fecha o diálogo.
     */
    @FXML
    private void saveBook() {
        if (isInputValid()) {
            saveClicked = true;
            Book book = getBook();
            if (book != null) {
                if (bookService == null) { // Adiciona verificação de null para bookService
                    logError("BookService não está injetado em AddBookViewController. Não foi possível salvar o livro.", new IllegalStateException("BookService é nulo."));
                    showAlert("Erro Interno", "Não foi possível salvar o livro devido a um problema de inicialização.");
                    return;
                }

                try {
                    if (bookToEdit == null) {
                        bookService.insertBook(book); // Usa bookService
                        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro adicionado com sucesso!");
                    } else {
                        book.setId(bookToEdit.getId());
                        bookService.updateBook(book); // Usa bookService
                        showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro atualizado com sucesso!");
                    }
                    dialogStage.close();
                    if (bookController != null) { // Chama o loadAllBooks no BookController para atualizar a lista
                        bookController.loadAllBooks();
                    }
                } catch (Exception e) { // Captura exceções do serviço (SQL, etc.)
                    logError("Erro ao salvar/atualizar livro via BookService", e);
                    showAlert("Erro ao Salvar", "Ocorreu um erro ao salvar o livro: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Manipula o evento de clique no botão "Cancelar", fechando o diálogo modal.
     */
    @FXML
    private void cancelAddBookView() {
        dialogStage.close();
    }

    /**
     * Abre um diálogo para o usuário escolher um arquivo de imagem para a capa do livro.
     * Atualiza a ImageView e a Label com o arquivo selecionado, e limpa o campo de URL.
     */
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
            displayCoverImageFromFile(file);
            imageUrlField.setText("");
        }
    }

    /**
     * Valida os campos de entrada do formulário. Exibe um diálogo de erro se algum
     * campo for inválido.
     *
     * @return true se todos os campos forem válidos, false caso contrário.
     */
    private boolean isInputValid() {
        String errorMessage = "";

        errorMessage = appendErrorMessage(errorMessage, titleField.getText() == null || titleField.getText().trim().isEmpty(), "Título inválido!\n");
        errorMessage = appendErrorMessage(errorMessage, authorField.getText() == null || authorField.getText().trim().isEmpty(), "Autor inválido!\n");
        errorMessage = appendErrorMessage(errorMessage, isbnField.getText() == null || isbnField.getText().trim().isEmpty(), "ISBN inválido!\n");
        errorMessage = appendErrorMessage(errorMessage, publisherField.getText() == null || publisherField.getText().trim().isEmpty(), "Editora inválida!\n");

        if (yearField.getText() == null || yearField.getText().trim().isEmpty()) {
            errorMessage = appendErrorMessage(errorMessage, true, "Ano inválido!\n");
        } else {
            try {
                Integer.parseInt(yearField.getText());
            } catch (NumberFormatException e) {
                errorMessage = appendErrorMessage(errorMessage, true, "Ano deve ser um número!\n");
            }
        }

        errorMessage = appendErrorMessage(errorMessage, genreField.getText() == null || genreField.getText().trim().isEmpty(), "Gênero inválido!\n");

        if (quantityField.getText() == null || quantityField.getText().trim().isEmpty()) {
            errorMessage = appendErrorMessage(errorMessage, true, "Quantidade inválida!\n");
        } else {
            try {
                Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException e) {
                errorMessage = appendErrorMessage(errorMessage, true, "Quantidade deve ser um número!\n");
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Campos Inválidos", "Por favor, corrija os campos inválidos:\n" + errorMessage);
            return false;
        }
    }

    /**
     * Anexa uma mensagem de erro à mensagem atual se a condição for verdadeira.
     *
     * @param currentMessage A mensagem de erro atual.
     * @param condition      A condição que, se verdadeira, adiciona a mensagem.
     * @param messageToAppend A mensagem a ser anexada.
     * @return A mensagem de erro atualizada.
     */
    private String appendErrorMessage(String currentMessage, boolean condition, String messageToAppend) {
        if (condition) {
            return currentMessage + messageToAppend;
        }
        return currentMessage;
    }

    /**
     * Exibe um diálogo de alerta com a mensagem especificada.
     *
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Opcional: Se o diálogo de AddBookView precisa de owner para seus próprios alertas
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
        alert.showAndWait();
    }

    /**
     * Exibe um diálogo de alerta com tipo específico.
     *
     * @param type    O tipo de alerta (INFORMATION, ERROR, WARNING, etc.).
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
        alert.showAndWait();
    }

    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida.
     */
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