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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox; // Importe o ComboBox
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors; // Importar Collectors

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

    // --- ADIÇÕES PARA RESOLVER OS ERROS FXML ---
    @FXML // Linha 23 (aproximadamente)
    private ComboBox<String> bookSearchOptions; // VERIFIQUE O TIPO GENÉRICO (<String> ou outro)
    @FXML // Linha 39 (aproximadamente)
    private ComboBox<String> userSearchOptions; // VERIFIQUE O TIPO GENÉRICO (<String> ou outro)
    // --- FIM DAS ADIÇÕES ---

    private BookService bookService;

    private RootLayoutController rootLayoutController;
    private ObservableList<Book> allBooks = FXCollections.observableArrayList();


    public BookController() throws SQLException {
        this.bookService = new BookService(new BookDAOImpl(DataBaseConnection.getConnection()));
    }

    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = Objects.requireNonNull(rootLayoutController, "RootLayoutController não pode ser nulo.");
    }

    public void loadAllBooks() {
        System.out.println("BookController: Método loadAllBooks() chamado.");
        try {
            List<Book> books = bookService.findAllBooks();
            allBooks.setAll(books);
            updateBookDisplay(allBooks);
        } catch (SQLException e) {
            logError("Erro ao carregar livros", e);
            showAlert("Erro ao Carregar", "Ocorreu um erro ao carregar os livros.");
        }
    }

    @FXML
    public void initialize() {
        System.out.println("BookController: Método initialize() chamado.");

        // Inicialize os ComboBoxes aqui, se necessário.
        // Isso é importante para que eles não estejam nulos antes de serem usados.
        if (bookSearchOptions != null) {
            bookSearchOptions.getItems().addAll("Título", "Autor", "ISBN", "Editora", "Gênero", "Ano");
            bookSearchOptions.getSelectionModel().selectFirst(); // Seleciona o primeiro item por padrão
        }

        // Assumindo que userSearchOptions também é um ComboBox no BookController,
        // se ele for um componente do FXML carregado por este controlador.
        // Se userSearchOptions pertencer a outro controlador (ex: RootLayoutController),
        // ele não deve ser declarado aqui, e sim no controlador apropriado.
        if (userSearchOptions != null) {
            userSearchOptions.getItems().addAll("Nome", "CPF", "Email"); // Exemplo de opções
            userSearchOptions.getSelectionModel().selectFirst();
        }


        searchBookTextField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchBook());
    }

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

            Window ownerWindow = null;
            if (booksFlowPane.getScene() != null && booksFlowPane.getScene().getWindow() != null) {
                ownerWindow = booksFlowPane.getScene().getWindow();
            } else if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
                ownerWindow = rootLayoutController.getPrimaryStage();
            }

            if (ownerWindow != null) {
                dialogStage.initOwner(ownerWindow);
            } else {
                logError("Não foi possível definir o proprietário do diálogo de adição de livro. O diálogo pode não ser modal.", null);
            }

            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (addBookViewController.isSaveClicked()) {
                loadAllBooks();
            }

        } catch (IOException e) {
            logError("Erro ao carregar AddBookView.fxml", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar a tela de adicionar livro.");
        }
    }

    @FXML
    private void handleSearchBook() {
        String searchTerm = searchBookTextField.getText().trim().toLowerCase();

        if (allBooks.isEmpty() && bookService != null) {
            loadAllBooks();
        }

        if (searchTerm.isEmpty()) {
            updateBookDisplay(allBooks);
        } else {
            // Usa Collectors.toList() para evitar problemas de compatibilidade de JavaFX
            List<Book> searchResults = allBooks.stream()
                    .filter(book -> {
                        boolean matchesTitle = book.getTitle() != null && book.getTitle().toLowerCase().contains(searchTerm);
                        boolean matchesAuthor = book.getAuthor() != null && book.getAuthor().toLowerCase().contains(searchTerm);
                        boolean matchesIsbn = book.getIsbn() != null && book.getIsbn().toLowerCase().contains(searchTerm);
                        boolean matchesPublisher = book.getPublisher() != null && book.getPublisher().toLowerCase().contains(searchTerm);
                        boolean matchesGenre = book.getGenre() != null && book.getGenre().toLowerCase().contains(searchTerm);
                        boolean matchesYear = String.valueOf(book.getYear()).contains(searchTerm);

                        return matchesTitle || matchesAuthor || matchesIsbn || matchesPublisher || matchesGenre || matchesYear;
                    })
                    .collect(Collectors.toList()); // Coleta para List

            updateBookDisplay(searchResults);
        }
    }

    private void updateBookDisplay(List<Book> bookList) {
        booksFlowPane.getChildren().clear();
        if (bookList != null && !bookList.isEmpty()) {
            for (Book book : bookList) {
                VBox bookCard = createBookCard(book);
                if (bookCard != null) {
                    booksFlowPane.getChildren().add(bookCard);
                }
            }
        } else {
            booksFlowPane.getChildren().add(new Label("Nenhum livro encontrado."));
        }
    }

    private VBox createBookCard(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookCardView.fxml"));
            VBox card = loader.load();
            BookCardController controller = loader.getController();
            controller.setBook(book);
            controller.setBookListController(this);
            return card;
        } catch (IOException e) {
            logError("Erro ao carregar BookCardView.fxml para o livro: " + book.getTitle(), e);
            return null;
        }
    }

    public void showBookDetails(Book book, Window owner) {
        if (book == null) {
            showAlert("Erro", "Livro não encontrado para exibir detalhes.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookDetailsView.fxml"));
            Pane detailsView = loader.load();

            BookDetailsController detailsController = loader.getController(); // Não precisa de cast explícito se a classe for a correta

            if (detailsController != null) {
                detailsController.setBook(book);
            } else {
                logError("BookDetailsController é nulo após carregar o FXML.", null);
                showAlert("Erro", "Não foi possível obter o controlador de detalhes do livro.");
                return;
            }

            Stage detailsStage = new Stage();
            detailsStage.setTitle("Detalhes do Livro");
            detailsStage.setScene(new Scene(detailsView));
            detailsStage.initModality(Modality.APPLICATION_MODAL);
            detailsStage.initOwner(owner);

            if (detailsController != null) {
                detailsController.setDialogStage(detailsStage);
            }

            detailsStage.showAndWait();

        } catch (IOException e) {
            logError("Erro ao carregar BookDetailsView.fxml", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar a tela de detalhes do livro.");
        }
    }

    public void loadEditBookView(Book bookToEdit, Window owner) {
        if (bookToEdit == null) {
            showAlert("Erro", "Livro não encontrado para editar.");
            return;
        }
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
            dialogStage.initOwner(owner);

            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            if (addBookViewController.isSaveClicked()) {
                loadAllBooks();
            }
        } catch (IOException e) {
            logError("Erro ao carregar tela de edição", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar a tela de edição de livro.");
        }
    }

    public void confirmRemoveBook(Book book, Window owner) {
        if (book == null) {
            showAlert("Erro", "Livro não encontrado para remover.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Remoção");
        confirmation.setHeaderText("Remover o livro: " + book.getTitle());
        confirmation.setContentText("Tem certeza que deseja remover este livro?");
        confirmation.initOwner(owner);

        confirmation.showAndWait().ifPresent(result -> {
            if (result == javafx.scene.control.ButtonType.OK) {
                deleteBook(book.getId());
            }
        });
    }

    public void insertNewBook(Book book) {
        try {
            bookService.insertBook(book);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro adicionado com sucesso!"); // Use Alert.AlertType.INFORMATION
            loadAllBooks();
        } catch (SQLException e) {
            logError("Erro ao inserir novo livro", e);
            showAlert("Erro ao Salvar", "Ocorreu um erro ao adicionar o livro: " + e.getMessage());
        }
    }

    public void updateBook(Book book) {
        try {
            bookService.updateBook(book);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro atualizado com sucesso!"); // Use Alert.AlertType.INFORMATION
            loadAllBooks();
        }
        catch (SQLException e) {
            logError("Erro ao atualizar livro", e);
            showAlert("Erro ao Salvar", "Ocorreu um erro ao atualizar o livro: " + e.getMessage());
        }
    }

    public void deleteBook(int id) {
        try {
            bookService.deleteBook(id);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro removido com sucesso!"); // Use Alert.AlertType.INFORMATION
            loadAllBooks();
        } catch (SQLException e) {
            logError("Erro ao remover livro", e);
            showAlert("Erro ao Remover", "Ocorreu um erro ao remover o livro: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
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