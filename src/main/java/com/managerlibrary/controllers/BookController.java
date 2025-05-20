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
import javafx.scene.control.Label; // Adicionado para a mensagem "Nenhum livro encontrado."
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window; // Importado Window para o parâmetro owner

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects; // Para Objects.requireNonNull

// ADICIONADO: Importar BookDetailsController para que seja reconhecido
// import com.managerlibrary.controllers.BookDetailsController; // Já estava presente no seu código, mas deixei o comentário

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

    // ADICIONADO: Variável para armazenar a referência ao RootLayoutController
    private RootLayoutController rootLayoutController;
    // ADICIONADO: Para manter a lista completa de livros e permitir busca local
    private ObservableList<Book> allBooks = FXCollections.observableArrayList();


    /**
     * Construtor do BookController. Inicializa o BookService com uma instância de BookDAOImpl.
     * **ATENÇÃO:** Inicializar serviços no construtor do controlador pode ser problemático
     * se a conexão ao banco de dados ou o DAO dependem de um contexto maior (como o App).
     * É preferível que o BookService seja injetado via um setter ou através de um framework de DI.
     * Mantenha essa inicialização aqui apenas se tiver certeza que é o comportamento desejado.
     *
     * @throws SQLException Se ocorrer um erro ao conectar ao banco de dados.
     */
    public BookController() throws SQLException {
        // ATENÇÃO: Verifique se DataBaseConnection.getConnection() retorna uma conexão válida aqui.
        // É mais robusto ter o BookService injetado (via setBookService) pela classe App.
        // Por agora, estou mantendo sua lógica de inicialização no construtor.
        this.bookService = new BookService(new BookDAOImpl(DataBaseConnection.getConnection()));
    }

    /**
     * ADICIONADO: Define o BookService para este controlador.
     * Este é o método preferencial para injetar o serviço.
     *
     * @param bookService O serviço de livros a ser injetado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }


    /**
     * ADICIONADO: Define o controlador principal da aplicação (RootLayoutController).
     *
     * @param rootLayoutController O controlador principal.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = Objects.requireNonNull(rootLayoutController, "RootLayoutController não pode ser nulo.");
    }

    /**
     * ADICIONADO: Carrega todos os livros do banco de dados e atualiza a exibição.
     * Este método é chamado para "refrescar" a lista visível e para carregar todos os livros.
     */
    public void loadAllBooks() {
        System.out.println("BookController: Método loadAllBooks() chamado.");
        try {
            List<Book> books = bookService.findAllBooks();
            allBooks.setAll(books); // Armazena todos os livros para busca local
            updateBookDisplay(allBooks); // Exibe todos os livros
        } catch (SQLException e) {
            logError("Erro ao carregar livros", e);
            showAlert("Erro ao Carregar", "Ocorreu um erro ao carregar os livros.");
        }
    }


    /**
     * Método de inicialização do controlador. Chamado após o FXML ser carregado.
     * Carrega todos os livros ao iniciar a tela.
     */
    @FXML
    public void initialize() {
        System.out.println("BookController: Método initialize() chamado.");
        // Removido refreshBookDisplay() daqui, loadAllBooks() será chamado pelo RootLayoutController
        // ou MainApp quando a tela for exibida.
        // Adiciona listener para a busca automática ao digitar
        searchBookTextField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchBook());
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
            addBookViewController.setBookController(this); // Permite que AddBookViewController chame insertNewBook/updateBook

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Adicionar Novo Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // Tenta definir o proprietário do diálogo
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

            // Ao retornar do diálogo (se seja por salvar ou cancelar), a lista é atualizada.
            // A chamada a insertNewBook/updateBook já invoca loadAllBooks(), então não é estritamente necessário aqui
            // a menos que você queira um refresh incondicional ao fechar o diálogo.
            if (addBookViewController.isSaveClicked()) { // Verifica se o usuário clicou em salvar
                loadAllBooks(); // Recarrega a lista apenas se o salvamento foi concluído
            }

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
        String searchTerm = searchBookTextField.getText().trim().toLowerCase(); // Usa trim e toLowerCase para busca flexível

        if (allBooks.isEmpty() && bookService != null) { // Se allBooks estiver vazia, tenta carregar
            loadAllBooks(); // Tenta carregar todos os livros novamente, caso não tenha sido feito.
        }

        if (searchTerm.isEmpty()) {
            updateBookDisplay(allBooks); // Se o campo de busca estiver vazio, exibe todos os livros
        } else {
            // Filtra a ObservableList 'allBooks' que já contém todos os livros carregados
            List<Book> searchResults = allBooks.stream()
                    .filter(book -> {
                        boolean matchesTitle = book.getTitle() != null && book.getTitle().toLowerCase().contains(searchTerm);
                        boolean matchesAuthor = book.getAuthor() != null && book.getAuthor().toLowerCase().contains(searchTerm);
                        boolean matchesIsbn = book.getIsbn() != null && book.getIsbn().toLowerCase().contains(searchTerm);
                        boolean matchesPublisher = book.getPublisher() != null && book.getPublisher().toLowerCase().contains(searchTerm);
                        boolean matchesGenre = book.getGenre() != null && book.getGenre().toLowerCase().contains(searchTerm);
                        boolean matchesYear = String.valueOf(book.getYear()).contains(searchTerm); // Converte ano para String

                        return matchesTitle || matchesAuthor || matchesIsbn || matchesPublisher || matchesGenre || matchesYear;
                    })
                    .collect(java.util.stream.Collectors.toList()); // Coleta para List
            updateBookDisplay(searchResults);
        }
    }


    /**
     * Atualiza a exibição dos livros no FlowPane com a lista de livros fornecida.
     * Limpa os cards existentes e cria novos cards para cada livro na lista.
     *
     * @param bookList A lista de livros a serem exibidos.
     */
    private void updateBookDisplay(List<Book> bookList) {
        booksFlowPane.getChildren().clear();
        if (bookList != null && !bookList.isEmpty()) {
            for (Book book : bookList) {
                VBox bookCard = createBookCard(book);
                if (bookCard != null) { // Garante que o card foi criado com sucesso
                    booksFlowPane.getChildren().add(bookCard);
                }
            }
        } else {
            // Opcional: Adicionar uma mensagem para o usuário quando não houver livros
            booksFlowPane.getChildren().add(new Label("Nenhum livro encontrado."));
        }
    }

    /**
     * Cria um card visual (VBox) para um livro específico, carregando o FXML do BookCardView
     * e configurando o controlador com o livro e este controlador.
     *
     * @param book O livro para o qual o card será criado.
     * @return O VBox representando o card do livro, ou null em caso de erro.
     */
    private VBox createBookCard(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookCardView.fxml"));
            VBox card = loader.load();
            BookCardController controller = loader.getController();
            controller.setBook(book);
            controller.setBookListController(this); // O BookCardController precisa saber quem é o BookController
            return card;
        } catch (IOException e) {
            logError("Erro ao carregar BookCardView.fxml para o livro: " + book.getTitle(), e);
            // Retorna null para indicar falha na criação do card
            return null;
        }
    }

    /**
     * Exibe os detalhes de um livro em um diálogo modal.
     *
     * @param book  O livro a ser exibido.
     * @param owner A janela pai do diálogo.
     */
    public void showBookDetails(Book book, Window owner) {
        if (book == null) {
            showAlert("Erro", "Livro não encontrado para exibir detalhes.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/BookDetailsView.fxml"));
            Pane detailsView = loader.load();

            // CORREÇÃO: Fazer o cast explícito para BookDetailsController
            BookDetailsController detailsController = (BookDetailsController) loader.getController();

            // CORREÇÃO: O método setBook(Book) existe em BookDetailsController
            if (detailsController != null) {
                detailsController.setBook(book); // Passa o livro para o controlador de detalhes
            } else {
                logError("BookDetailsController é nulo após carregar o FXML.", null);
                showAlert("Erro", "Não foi possível obter o controlador de detalhes do livro.");
                return;
            }


            Stage detailsStage = new Stage();
            detailsStage.setTitle("Detalhes do Livro");
            detailsStage.setScene(new Scene(detailsView));
            detailsStage.initModality(Modality.APPLICATION_MODAL);
            detailsStage.initOwner(owner); // Define o proprietário do palco do diálogo

            // ADICIONADO: Injetar o Stage no BookDetailsController para que ele possa se fechar
            if (detailsController != null) {
                detailsController.setDialogStage(detailsStage);
            }

            detailsStage.showAndWait();

        } catch (IOException e) {
            logError("Erro ao carregar BookDetailsView.fxml", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar a tela de detalhes do livro.");
        }
    }

    /**
     * Carrega a tela de edição de um livro em um diálogo modal, preenchendo os campos
     * com os dados do livro a ser editado.
     *
     * @param bookToEdit   O livro a ser editado.
     * @param owner A janela pai do diálogo (Stage ou Window).
     */
    public void loadEditBookView(Book bookToEdit, Window owner) { // Alterado Button para Window para ser mais genérico
        if (bookToEdit == null) {
            showAlert("Erro", "Livro não encontrado para editar.");
            return;
        }
        try {
            // Reutilizando AddBookView.fxml para edição
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddBookView.fxml"));
            Pane addBookView = loader.load();

            AddBookViewController addBookViewController = loader.getController();
            addBookViewController.setBookController(this); // Permite que AddBookViewController chame updateBook
            addBookViewController.setBookToEdit(bookToEdit); // Preenche os campos para edição

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Editar Livro");
            dialogStage.setScene(new Scene(addBookView));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(owner); // Usa o owner genérico

            addBookViewController.setDialogStage(dialogStage);
            dialogStage.showAndWait();

            // Após o diálogo ser fechado, verificamos se o usuário clicou em salvar
            if (addBookViewController.isSaveClicked()) {
                loadAllBooks(); // Recarrega a lista após edição bem-sucedida
            }
            // else { // Não precisamos recarregar se o usuário simplesmente cancelou
            //     loadAllBooks(); // Recarrega mesmo se não salvou, para garantir consistência
            // }
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
        if (book == null) {
            showAlert("Erro", "Livro não encontrado para remover.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Remoção");
        confirmation.setHeaderText("Remover o livro: " + book.getTitle());
        confirmation.setContentText("Tem certeza que deseja remover este livro?");
        confirmation.initOwner(owner); // Define o proprietário do alerta

        confirmation.showAndWait().ifPresent(result -> {
            if (result == javafx.scene.control.ButtonType.OK) {
                deleteBook(book.getId());
            }
        });
    }

    /**
     * Insere um novo livro no banco de dados e recarrega a lista de livros.
     *
     * @param book O livro a ser inserido.
     */
    public void insertNewBook(Book book) {
        try {
            bookService.insertBook(book);
            showAlert("Sucesso", "Livro adicionado com sucesso!");
            loadAllBooks(); // Recarrega a lista
        } catch (SQLException e) {
            logError("Erro ao inserir novo livro", e);
            showAlert("Erro ao Salvar", "Ocorreu um erro ao adicionar o livro: " + e.getMessage());
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
            showAlert("Sucesso", "Livro atualizado com sucesso!");
            loadAllBooks(); // Recarrega a lista
        }
        catch (SQLException e) {
            logError("Erro ao atualizar livro", e);
            showAlert("Erro ao Salvar", "Ocorreu um erro ao atualizar o livro: " + e.getMessage());
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
            showAlert("Sucesso", "Livro removido com sucesso!");
            loadAllBooks(); // Recarrega a lista
        } catch (SQLException e) {
            logError("Erro ao remover livro", e);
            showAlert("Erro ao Remover", "Ocorreu um erro ao remover o livro: " + e.getMessage());
        }
    }

    /**
     * Exibe um diálogo de alerta com o tipo, título e conteúdo especificados.
     *
     * @param type    O tipo de alerta (e.g., Alert.AlertType.ERROR, Alert.AlertType.INFORMATION).
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Sobrecarga de showAlert para o caso de erro (padrão).
     *
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
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