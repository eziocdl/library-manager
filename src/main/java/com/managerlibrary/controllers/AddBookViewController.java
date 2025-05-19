package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddBookViewController {

    private BookController bookController; // Controlador da tela principal de livros
    private Stage dialogStage; // Palco (Stage) do diálogo modal
    private Book bookToEdit; // Livro a ser editado (se estiver no modo de edição)
    private boolean saveClicked = false; // Flag para indicar se o botão "Salvar" foi clicado
    private File selectedCoverFile; // Arquivo de imagem da capa selecionado pelo usuário

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
    private Label coverFileNameLabel; // Label para exibir o nome do arquivo da capa
    @FXML
    private ImageView coverImageView; // ImageView para exibir a capa do livro

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
     *
     * @param book O livro cuja capa será carregada.
     */
    private void loadCoverImage(Book book) {
        if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
            File file = new File(book.getCoverImagePath());
            if (file.exists()) {
                coverImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
                coverFileNameLabel.setText(file.getName());
                selectedCoverFile = file;
            } else if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                coverImageView.setImage(new javafx.scene.image.Image(book.getImageUrl()));
                coverFileNameLabel.setText("URL");
                selectedCoverFile = null;
            } else {
                clearCoverImageDisplay();
            }
        } else if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            coverImageView.setImage(new javafx.scene.image.Image(book.getImageUrl()));
            coverFileNameLabel.setText("URL");
            selectedCoverFile = null;
        } else {
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
     * Retorna null se houver erro de formato nos campos numéricos.
     *
     * @return Um objeto Book com os dados do formulário, ou null se a entrada for inválida.
     */
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
            showAlert("Erro de Formato", "Por favor, insira números válidos para Ano e Quantidade.");
            return null;
        }
        book.setGenre(genreField.getText());
        book.setImageUrl(imageUrlField.getText());
        // Define o caminho da imagem da capa. Prioriza o arquivo selecionado,
        // caso contrário, mantém o caminho existente se estiver editando.
        if (selectedCoverFile != null) {
            book.setCoverImagePath(selectedCoverFile.getAbsolutePath());
        } else if (bookToEdit != null) {
            book.setCoverImagePath(bookToEdit.getCoverImagePath());
        }
        return book;
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
                if (bookToEdit == null) {
                    bookController.insertNewBook(book);
                } else {
                    book.setId(bookToEdit.getId()); // Mantém o ID para a edição
                    bookController.updateBook(book);
                }
                dialogStage.close();
            }
            // A mensagem de erro de validação já é exibida por isInputValid() e getBook()
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
            coverImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
            imageUrlField.setText(""); // Limpa a URL ao escolher um arquivo local
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
            showAlert("Campos Inválidos", "Por favor, corrija os campos inválidos:\n" + errorMessage);
            return false;
        }
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
        alert.showAndWait();
    }
}