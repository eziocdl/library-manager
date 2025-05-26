package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.services.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent; // Adicionado para showAddBookView(ActionEvent event)
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Usar Parent em vez de Pane para root de carregamento
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType; // Adicionado para confirmação de remoção
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox; // VBox para o card
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window; // Para initOwner

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional; // Adicionado para Optional em confirmação
import java.util.stream.Collectors;

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
    private Button searchBookButton; // Se este botão for usado, ele acionaria handleSearchBook

    @FXML
    private ComboBox<String> bookSearchOptions;

    private BookService bookService;
    private RootLayoutController rootLayoutController;
    private ObservableList<Book> allBooks = FXCollections.observableArrayList();

    // Construtor padrão removido, pois a injeção será via setters e initialize é chamado pelo FXMLLoader.

    /**
     * Define o serviço de livros.
     * Este método DEVE ser chamado pelo RootLayoutController para injetar a dependência.
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }

    /**
     * Define o controlador principal da aplicação (RootLayoutController).
     *
     * @param rootLayoutController O controlador principal.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = Objects.requireNonNull(rootLayoutController, "RootLayoutController não pode ser nulo.");
    }

    /**
     * Carrega e exibe todos os livros em formato de cards no FlowPane.
     * Este método deve ser chamado APÓS o BookService ser injetado.
     */
    public void loadAllBooks() {
        System.out.println("BookController: Método loadAllBooks() chamado.");
        if (booksFlowPane == null) {
            logError("booksFlowPane é nulo. Não é possível exibir os cards.", null);
            return;
        }
        booksFlowPane.getChildren().clear(); // Limpa antes de carregar
        if (bookService == null) {
            logError("ERRO CRÍTICO: BookService não injetado em BookController. Não é possível carregar dados.", new IllegalStateException("BookService é nulo."));
            showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "O serviço de livros não está disponível. Por favor, reinicie a aplicação.");
            return;
        }
        try {
            // Use o método correto do seu BookService (findAllBooks ou getAllBooks)
            List<Book> books = bookService.findAllBooks();
            allBooks.setAll(books);
            updateBookDisplay(allBooks);
        } catch (SQLException e) {
            logError("Erro ao carregar livros", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", "Ocorreu um erro ao carregar os livros: " + e.getMessage());
        }
    }

    /**
     * Método de inicialização do controlador.
     * Chamado automaticamente pelo FXMLLoader após carregar o FXML e injetar os campos @FXML.
     * Não deve carregar dados aqui, pois os serviços podem ainda não ter sido injetados.
     */
    @FXML
    public void initialize() {
        System.out.println("BookController: Método initialize() chamado.");

        if (bookSearchOptions != null) {
            bookSearchOptions.getItems().addAll("Título", "Autor", "ISBN", "Editora", "Gênero", "Ano");
            bookSearchOptions.getSelectionModel().selectFirst();
        } else {
            System.err.println("WARN: ComboBox 'bookSearchOptions' não encontrado no FXML do BookController.");
        }

        searchBookTextField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchBook());
        // REMOVIDO: loadAllBooks(); // Não chame aqui, pois o bookService não foi injetado ainda.
    }

    /**
     * Exibe a tela para adicionar um novo livro em um diálogo modal.
     */
    @FXML
    public void showAddBookView(ActionEvent event) {
        showAddBookView(); // Chama a versão sem ActionEvent
    }

    public void showAddBookView() { // Versão para ser chamada programaticamente
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Parent addBookView = loader.load(); // Use Parent para o elemento raiz

            AddBookViewController addBookViewController = loader.getController();

            if (bookService == null) {
                logError("BookService é nulo ao criar AddBookViewController. Não é possível injetar.", new IllegalStateException("BookService é nulo."));
                showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "O serviço de livros não está disponível para adicionar. Por favor, reinicie a aplicação.");
                return;
            }
            addBookViewController.setBookService(bookService);
            addBookViewController.setBookController(this); // Passa a referência deste controlador

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Adicionar Novo Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // Tenta definir o proprietário do palco do diálogo para centralização
            if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
                dialogStage.initOwner(rootLayoutController.getPrimaryStage());
            } else if (booksFlowPane != null && booksFlowPane.getScene() != null && booksFlowPane.getScene().getWindow() != null) {
                dialogStage.initOwner(booksFlowPane.getScene().getWindow());
            } else {
                logError("Não foi possível definir o proprietário do diálogo de adição de livro.", null);
            }

            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Após o diálogo fechar, recarrega a lista se algo foi salvo
            if (addBookViewController.isSaveClicked()) {
                loadAllBooks();
            }

        } catch (IOException e) {
            logError("Erro ao carregar AddBookView.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", "Não foi possível carregar a tela de adicionar livro: " + e.getMessage());
        }
    }

    /**
     * Lógica para buscar livros com base no termo de pesquisa.
     */
    @FXML
    private void handleSearchBook() {
        String searchTerm = searchBookTextField.getText().trim().toLowerCase();
        String searchOption = bookSearchOptions != null ? bookSearchOptions.getSelectionModel().getSelectedItem() : "Título";

        // REMOVIDO: O bloco de fallback para carregar todos os livros.
        // A lista allBooks DEVE ser populada por loadAllBooks() após a injeção inicial.

        if (searchTerm.isEmpty()) {
            updateBookDisplay(allBooks); // Exibe todos os livros se a busca estiver vazia
        } else {
            List<Book> searchResults = allBooks.stream()
                    .filter(book -> {
                        if (book == null) return false; // Evita NPE
                        String valueToSearch = "";
                        switch (searchOption) {
                            case "Título":
                                valueToSearch = book.getTitle();
                                break;
                            case "Autor":
                                valueToSearch = book.getAuthor();
                                break;
                            case "ISBN":
                                valueToSearch = book.getIsbn();
                                break;
                            case "Editora":
                                valueToSearch = book.getPublisher();
                                break;
                            case "Gênero":
                                valueToSearch = book.getGenre();
                                break;
                            case "Ano":
                                valueToSearch = String.valueOf(book.getYear());
                                break;
                            default:
                                valueToSearch = book.getTitle(); // Padrão se a opção for nula/inválida
                        }
                        return valueToSearch != null && valueToSearch.toLowerCase().contains(searchTerm);
                    })
                    .collect(Collectors.toList());

            updateBookDisplay(searchResults);
        }
    }

    /**
     * Atualiza a exibição dos cards de livros com uma nova lista de livros.
     *
     * @param bookList A lista de livros a serem exibidos.
     */
    private void updateBookDisplay(List<Book> bookList) {
        if (booksFlowPane == null) {
            logError("booksFlowPane é nulo. Não é possível atualizar a exibição.", null);
            return;
        }
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

    /**
     * Cria um card visual para exibir as informações do livro usando o FXML BookCardView.fxml.
     *
     * @param book O livro cujas informações serão exibidas no card.
     * @return O VBox (card) criado para o livro, ou null em caso de erro.
     */
    private VBox createBookCard(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookCardView.fxml"));
            VBox card = loader.load();
            BookCardController controller = loader.getController();

            controller.setBook(book);
            controller.setBookListController(this); // Passa a referência deste controlador
            controller.setRootLayoutController(this.rootLayoutController); // PASSA O ROOTLAYOUTCONTROLLER
            controller.setBookService(this.bookService); // <--- Injeta o BookService no BookCardController

            return card;
        } catch (IOException e) {
            logError("Erro ao carregar BookCardView.fxml para o livro: " + (book != null ? book.getTitle() : "null"), e);
            return null;
        }
    }

    /**
     * Exibe os detalhes de um livro em um diálogo modal.
     *
     * @param book  O livro cujos detalhes serão exibidos.
     * @param owner A janela proprietária para o diálogo modal.
     */
    public void showBookDetails(Book book, Window owner) {
        if (book == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Livro não encontrado para exibir detalhes.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookDetailsView.fxml"));
            Parent detailsView = loader.load(); // Use Parent para o elemento raiz

            BookDetailsController detailsController = loader.getController();

            if (detailsController != null) {
                detailsController.setBook(book);
                detailsController.setBookService(bookService); // <--- Injeta o BookService no BookDetailsController
                // Se BookDetailsController tiver um setRootLayoutController, chame-o
                // detailsController.setRootLayoutController(rootLayoutController);
            } else {
                logError("BookDetailsController é nulo após carregar o FXML.", null);
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível obter o controlador de detalhes do livro.");
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
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", "Não foi possível carregar a tela de detalhes do livro: " + e.getMessage());
        }
    }

    /**
     * Carrega a tela de edição de livro em um diálogo modal.
     *
     * @param bookToEdit O livro a ser editado.
     * @param owner      A janela proprietária para o diálogo modal.
     */
    public void loadEditBookView(Book bookToEdit, Window owner) {
        if (bookToEdit == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Livro não encontrado para editar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Parent addBookView = loader.load(); // Use Parent para o elemento raiz

            AddBookViewController addBookViewController = loader.getController();
            addBookViewController.setBookController(this);
            addBookViewController.setBookToEdit(bookToEdit);

            if (bookService == null) {
                logError("BookService é nulo ao criar AddBookViewController para edição. Não é possível injetar.", new IllegalStateException("BookService é nulo."));
                showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "O serviço de livros não está disponível para edição.");
                return;
            }
            addBookViewController.setBookService(bookService);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(owner);

            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Após o diálogo fechar, recarrega a lista se algo foi salvo
            if (addBookViewController.isSaveClicked()) {
                loadAllBooks();
            }
        } catch (IOException e) {
            logError("Erro ao carregar tela de edição", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", "Não foi possível carregar a tela de edição de livro: " + e.getMessage());
        }
    }

    /**
     * Exibe um diálogo de confirmação antes de remover um livro.
     *
     * @param book  O livro a ser removido.
     * @param owner A janela proprietária para o diálogo modal.
     */
    public void confirmRemoveBook(Book book, Window owner) {
        if (book == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Livro não encontrado para remover.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Remoção");
        confirmation.setHeaderText("Remover o livro: " + book.getTitle());
        confirmation.setContentText("Tem certeza que deseja remover este livro?");
        confirmation.initOwner(owner);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteBook(book.getId());
        }
    }

    /**
     * Insere um novo livro usando o serviço de livros.
     *
     * @param book O livro a ser inserido.
     */
    public void insertNewBook(Book book) {
        if (bookService == null) {
            logError("BookService é nulo ao tentar inserir livro.", new IllegalStateException("BookService não injetado."));
            showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "O serviço de livros não está disponível para inserir.");
            return;
        }
        try {
            bookService.insertBook(book);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro adicionado com sucesso!");
            loadAllBooks();
        } catch (SQLException e) {
            logError("Erro ao inserir novo livro", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Ocorreu um erro ao adicionar o livro: " + e.getMessage());
        }
    }

    /**
     * Atualiza um livro existente usando o serviço de livros.
     *
     * @param book O livro a ser atualizado.
     */
    public void updateBook(Book book) {
        if (bookService == null) {
            logError("BookService é nulo ao tentar atualizar livro.", new IllegalStateException("BookService não injetado."));
            showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "O serviço de livros não está disponível para atualizar.");
            return;
        }
        try {
            bookService.updateBook(book);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro atualizado com sucesso!");
            loadAllBooks();
        }
        catch (SQLException e) {
            logError("Erro ao atualizar livro", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Ocorreu um erro ao atualizar o livro: " + e.getMessage());
        }
    }

    /**
     * Deleta um livro usando o serviço de livros.
     *
     * @param id O ID do livro a ser deletado.
     */
    public void deleteBook(int id) {
        if (bookService == null) {
            logError("BookService é nulo ao tentar deletar livro.", new IllegalStateException("BookService não injetado."));
            showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "O serviço de livros não está disponível para deletar.");
            return;
        }
        try {
            bookService.deleteBook(id);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro removido com sucesso!");
            loadAllBooks();
        } catch (SQLException e) {
            logError("Erro ao remover livro", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Remover", "Ocorreu um erro ao remover o livro: " + e.getMessage());
        }
    }

    /**
     * Exibe um diálogo de alerta com o tipo, título e conteúdo especificados.
     *
     * @param alertType O tipo do alerta (INFORMATION, ERROR, WARNING, etc.).
     * @param title     O título do alerta.
     * @param content   O conteúdo da mensagem do alerta.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Tenta definir o proprietário do palco do alerta para centralização
        if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
            alert.initOwner(rootLayoutController.getPrimaryStage());
        } else if (booksFlowPane != null && booksFlowPane.getScene() != null && booksFlowPane.getScene().getWindow() != null) {
            alert.initOwner(booksFlowPane.getScene().getWindow());
        }
        alert.showAndWait();
    }

    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida, pode ser nula.
     */
    private void logError(String message, Exception e) {
        System.err.print("ERRO: " + message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }
}