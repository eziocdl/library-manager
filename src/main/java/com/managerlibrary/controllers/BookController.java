package com.managerlibrary.controllers;

import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.entities.Book;
import com.managerlibrary.infra.DataBaseConnection;
import com.managerlibrary.services.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para a tela principal de livros. Exibe os livros em cards, permite
 * adicionar, pesquisar, visualizar detalhes, editar e remover livros.
 */
public class BookController {

    @FXML
    private FlowPane booksFlowPane;
    @FXML
    private TextField searchBookTextField;
    @FXML
    private Button searchBookButton;

    private BookService bookService;
    private RootLayoutController rootLayoutController; // Referência ao controlador principal (se necessário)

    /**
     * Construtor do BookController. Inicializa o BookService com uma instância de BookDAOImpl.
     *
     * @throws SQLException Se ocorrer um erro ao conectar ao banco de dados.
     */
    public BookController() throws SQLException {
        this.bookService = new BookService(new BookDAOImpl(DataBaseConnection.getConnection()));
    }

    /**
     * Define o controlador principal da aplicação (RootLayoutController).
     *
     * @param rootLayoutController O controlador principal.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    /**
     * Método de inicialização do controlador. Chamado após o FXML ser carregado.
     * Carrega todos os livros ao iniciar a tela.
     */
    @FXML
    public void initialize() {
        System.out.println("BookController: Método initialize() chamado.");
        loadAllBooks();
    }

    /**
     * Exibe a tela de adicionar um novo livro em um diálogo modal.
     */
    @FXML
    public void showAddBookView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Pane addBookView = loader.load();

            AddBookViewController addBookViewController = loader.getController();
            addBookViewController.setBookController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Adicionar Novo Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(booksFlowPane.getScene().getWindow());
            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            loadAllBooks(); // Recarrega os livros após o diálogo ser fechado

        } catch (IOException e) {
            logError("Erro ao carregar AddBookView.fxml", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar a tela de adicionar livro.");
        }
    }

    /**
     * Manipula a ação de buscar livros. Filtra os livros exibidos com base no termo de busca.
     */
    @FXML
    private void handleSearchBook() {
        String searchTerm = searchBookTextField.getText();
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                List<Book> searchResults = bookService.findBooksByTitle(searchTerm);
                updateBookDisplay(searchResults);
            } catch (SQLException e) {
                logError("Erro ao buscar livros", e);
                showAlert("Erro na Busca", "Ocorreu um erro ao buscar livros.");
            }
        } else {
            loadAllBooks(); // Se o campo de busca estiver vazio, recarrega todos os livros
        }
    }

    /**
     * Carrega todos os livros do banco de dados e atualiza a exibição.
     */
    private void loadAllBooks() {
        System.out.println("BookController: Método loadingBooks() chamado.");
        try {
            List<Book> books = bookService.findAllBooks();
            updateBookDisplay(books);
        } catch (SQLException e) {
            logError("Erro ao carregar livros", e);
            showAlert("Erro ao Carregar", "Ocorreu um erro ao carregar os livros.");
        }
    }

    /**
     * Atualiza a exibição dos livros no FlowPane com a lista de livros fornecida.
     * Limpa os cards existentes e cria novos cards para cada livro na lista.
     *
     * @param bookList A lista de livros a serem exibidos.
     */
    private void updateBookDisplay(List<Book> bookList) {
        ObservableList<Book> observableBookList = FXCollections.observableArrayList(bookList);
        booksFlowPane.getChildren().clear();
        for (Book book : observableBookList) {
            VBox bookCard = createBookCard(book);
            booksFlowPane.getChildren().add(bookCard);
        }
    }

    /**
     * Cria um card visual (VBox) para um livro específico, carregando o FXML do BookCardView
     * e configurando o controlador com o livro e este controlador.
     *
     * @param book O livro para o qual o card será criado.
     * @return O VBox representando o card do livro.
     */
    private VBox createBookCard(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookCardView.fxml"));
            VBox card = loader.load();
            BookCardController controller = loader.getController();
            controller.setBook(book);
            controller.setBookListController(this);
            return card;
        } catch (IOException e) {
            logError("Erro ao carregar BookCardView.fxml", e);
            // Lidar com o erro ao carregar o FXML, retornando um VBox vazio como fallback
            return new VBox();
        }
    }

    /**
     * Exibe os detalhes de um livro em um diálogo modal.
     *
     * @param book  O livro a ser exibido.
     * @param owner A janela pai do diálogo.
     */
    public void showBookDetails(Book book, Window owner) {
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Detalhes do Livro");
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.initOwner(owner);

        VBox detailsLayout = new VBox(10);
        detailsLayout.setStyle("-fx-padding: 15;");
        javafx.scene.control.Label titleLabelDetails = new javafx.scene.control.Label("Título: " + book.getTitle());
        javafx.scene.control.Label authorLabelDetails = new javafx.scene.control.Label("Autor: " + book.getAuthor());
        javafx.scene.control.Label isbnLabelDetails = new javafx.scene.control.Label("ISBN: " + book.getIsbn());
        javafx.scene.control.Label publisherLabelDetails = new javafx.scene.control.Label("Editora: " + book.getPublisher());
        javafx.scene.control.Label yearLabelDetails = new javafx.scene.control.Label("Ano: " + book.getYear());
        javafx.scene.control.Label genreLabelDetails = new javafx.scene.control.Label("Gênero: " + book.getGenre());
        javafx.scene.control.Label availabilityLabelDetails = new javafx.scene.control.Label("Disponíveis: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
        Button closeButton = new Button("Fechar");
        closeButton.setOnAction(e -> detailsStage.close());

        detailsLayout.getChildren().addAll(titleLabelDetails, authorLabelDetails, isbnLabelDetails, publisherLabelDetails, yearLabelDetails, genreLabelDetails, availabilityLabelDetails, closeButton);

        Scene detailsScene = new Scene(detailsLayout);
        detailsStage.setScene(detailsScene);
        detailsStage.showAndWait();
    }

    /**
     * Carrega a tela de edição de um livro em um diálogo modal, preenchendo os campos
     * com os dados do livro a ser editado.
     *
     * @param bookToEdit   O livro a ser editado.
     * @param sourceButton O botão que disparou a ação (usado para definir a janela pai).
     */
    public void loadEditBookView(Book bookToEdit, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Pane addBookView = loader.load();

            AddBookViewController addBookViewController = loader.getController();
            addBookViewController.setBookController(this);
            addBookViewController.setBookToEdit(bookToEdit);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(sourceButton.getScene().getWindow());

            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (addBookViewController.isSaveClicked()) {
                Book editedBook = addBookViewController.getBook();
                if (editedBook != null) {
                    updateBook(editedBook);
                }
            }
            loadAllBooks(); // Recarrega os livros após a edição
        } catch (IOException e) {
            logError("Erro ao carregar tela de edição", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar a tela de edição de livro.");
        }
    }

    /**
     * Exibe um diálogo de confirmação para remover um livro. Se o usuário confirmar,
     * o livro é removido do banco de dados.
     *
     * @param book  O livro a ser removido.
     * @param owner A janela pai do diálogo de confirmação.
     */
    public void confirmRemoveBook(Book book, Window owner) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Remoção");
        confirmation.setHeaderText("Remover o livro: " + book.getTitle());
        confirmation.setContentText("Tem certeza que deseja remover este livro?");
        confirmation.initOwner(owner);

        Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            deleteBook(book.getId());
        }
    }

    /**
     * Insere um novo livro no banco de dados e recarrega a lista de livros.
     *
     * @param book O livro a ser inserido.
     */
    public void insertNewBook(Book book) {
        try {
            bookService.insertBook(book);
            loadAllBooks();
        } catch (SQLException e) {
            logError("Erro ao inserir novo livro", e);
            showAlert("Erro ao Salvar", "Ocorreu um erro ao adicionar o livro.");
        }
    }

    /**
     * Atualiza as informações de um livro existente no banco de dados e recarrega a lista de livros.
     *
     * @param book O livro com as informações atualizadas.
     */
    public void updateBook(Book book) {
        try {
            bookService.updateBook(book);
            loadAllBooks();
        } catch (SQLException e) {
            logError("Erro ao atualizar livro", e);
            showAlert("Erro ao Salvar", "Ocorreu um erro ao atualizar o livro.");
        }
    }

    /**
     * Remove um livro do banco de dados com o ID especificado e recarrega a lista de livros.
     *
     * @param id O ID do livro a ser removido.
     */
    public void deleteBook(int id) {
        try {
            bookService.deleteBook(id);
            loadAllBooks();
        } catch (SQLException e) {
            logError("Erro ao remover livro", e);
            showAlert("Erro ao Remover", "Ocorreu um erro ao remover o livro.");
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

    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida.
     */
    private void logError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}