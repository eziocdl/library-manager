package com.managerlibrary.controllers;

import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para a tela de adicionar um novo empréstimo. Permite a seleção de um livro,
 * um usuário e as datas de empréstimo e devolução para criar um novo registro de empréstimo.
 */
public class AddLoanViewController {

    @FXML
    private ComboBox<String> bookSearchCriteria;
    @FXML
    private TextField bookSearchTextField;
    @FXML
    private ListView<Book> bookResultsListView;
    @FXML
    private Label selectedBookLabel;
    @FXML
    private ImageView selectedBookImageView; // Exibe a capa do livro selecionado

    // Removendo userSearchCriteria do FXML e, portanto, do Java.
    // @FXML
    // private ComboBox<String> userSearchCriteria;
    @FXML
    private TextField userSearchTextField;
    @FXML
    private ListView<User> userResultsListView;
    @FXML
    private Label selectedUserLabel;

    @FXML
    private DatePicker loanDatePicker;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private LoanController mainLoanController; // Controlador da tela principal de empréstimos
    private Stage dialogStage; // Palco (Stage) do diálogo modal

    private Book selectedBook; // Livro selecionado para o empréstimo
    private User selectedUser; // Usuário selecionado para o empréstimo

    /**
     * Opções para o critério de busca de livros no ComboBox.
     * Necessita da anotação @FXML para ser acessível via a ligação $ no FXML.
     */
    @FXML // <--- ADICIONADO @FXML AQUI
    private ObservableList<String> bookSearchOptions = FXCollections.observableArrayList("Título", "Autor", "ISBN");

    /**
     * Opções para o critério de busca de usuários no ComboBox.
     * REMOVIDO: Conforme decisão de simplificar a busca de usuário.
     */
    // private ObservableList<String> userSearchOptions = FXCollections.observableArrayList("Nome", "CPF", "Email");

    /**
     * Define o serviço de empréstimos.
     *
     * @param loanService O serviço de empréstimos a ser utilizado.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Define o serviço de livros.
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Define o serviço de usuários.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Define o controlador da tela principal de empréstimos para atualizar a lista após a adição.
     *
     * @param mainLoanController O controlador da tela principal de empréstimos.
     */
    public void setMainLoanController(LoanController mainLoanController) {
        this.mainLoanController = mainLoanController;
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
     * Método de inicialização do controlador. Configura os ComboBoxes com as opções de busca
     * e define CellFactories para formatar a exibição dos itens nas ListViews.
     */
    @FXML
    public void initialize() {
        // Removido: bookSearchCriteria.setItems(bookSearchOptions);
        // A ligação '$bookSearchOptions' no FXML já cuida disso.
        // userSearchCriteria.setItems(userSearchOptions); // Removido, pois userSearchCriteria não está mais no FXML
        configureBookListView();
        configureUserListView();
    }

    /**
     * Configura a ListView para exibir os resultados da busca de livros, mostrando título e autor.
     */
    private void configureBookListView() {
        bookResultsListView.setCellFactory(param -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                } else {
                    setText(book.getTitle() + " - " + book.getAuthor());
                }
            }
        });
    }

    /**
     * Configura a ListView para exibir os resultados da busca de usuários, mostrando nome e CPF.
     */
    private void configureUserListView() {
        userResultsListView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getName() + " - " + user.getCpf());
                }
            }
        });
    }

    /**
     * Realiza a busca de livros com base no critério e no termo de busca fornecidos.
     * Os resultados são exibidos na ListView de livros.
     */
    @FXML
    private void searchBook() {
        String criteria = bookSearchCriteria.getValue();
        String searchTerm = bookSearchTextField.getText().trim();
        ObservableList<Book> results = FXCollections.observableArrayList();

        if (criteria == null || searchTerm.isEmpty()) {
            showAlert("Campos Vazios", "Por favor, selecione um critério e digite um termo para buscar livros.");
            return;
        }

        try {
            switch (criteria.toLowerCase()) {
                case "título":
                    results.addAll(bookService.findBooksByTitle(searchTerm));
                    break;
                case "autor":
                    results.addAll(bookService.findBooksByAuthor(searchTerm));
                    break;
                case "isbn":
                    Book book = bookService.findBookByISBN(searchTerm);
                    if (book != null) {
                        results.add(book);
                    }
                    break;
                default:
                    showAlert("Critério Inválido", "Por favor, selecione um critério de busca válido para livros.");
                    return;
            }
            bookResultsListView.setItems(results);
        } catch (SQLException e) {
            logError("Erro ao buscar livros", e);
            showAlert("Erro na Busca", "Ocorreu um erro ao buscar livros.");
        }
    }

    /**
     * Manipula o evento de clique em um item da ListView de livros, selecionando o livro
     * e exibindo seu título, autor e capa (se disponível).
     *
     * @param event O evento de clique do mouse.
     */
    @FXML
    private void selectBook(MouseEvent event) {
        selectedBook = bookResultsListView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            selectedBookLabel.setText("Livro selecionado: " + selectedBook.getTitle() + " - " + selectedBook.getAuthor());
            displayBookCover(selectedBook);
        } else {
            selectedBookLabel.setText("Nenhum livro selecionado");
            selectedBookImageView.setImage(null);
        }
    }

    /**
     * Exibe a capa do livro selecionado no ImageView. Carrega a imagem do arquivo local
     * ou exibe uma imagem padrão se o caminho não for encontrado ou ocorrer um erro.
     *
     * @param book O livro cuja capa será exibida.
     */
    private void displayBookCover(Book book) {
        // Verifica se há um caminho de capa e se o arquivo existe
        if (book.getCoverImagePath() != null && !book.getCoverImagePath().isEmpty()) {
            try {
                // Tenta carregar a imagem do caminho do arquivo local
                Image image = new Image("file:" + book.getCoverImagePath());
                if (image.isError()) { // Verifica se houve erro no carregamento da imagem
                    throw new Exception("Erro ao carregar imagem do caminho local: " + book.getCoverImagePath());
                }
                selectedBookImageView.setImage(image);
                return; // Imagem carregada com sucesso, sai do método
            } catch (Exception e) {
                logError("Tentativa de carregar capa do livro por caminho local falhou", e);
                // Se falhar, tenta carregar do recurso padrão
            }
        }

        // Se não tem caminho de capa, ou se o carregamento do caminho falhou, tenta imagem padrão
        // Usar getResourceAsStream é mais robusto para recursos internos do JAR
        try {
            selectedBookImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_book_icon.png")));
        } catch (Exception e) {
            logError("Erro ao carregar imagem padrão de capa do livro", e);
            // Em último caso, não exibe imagem
            selectedBookImageView.setImage(null);
        }
    }


    /**
     * Realiza a busca de usuários com base no termo de busca fornecido.
     * Os resultados são exibidos na ListView de usuários.
     */
    @FXML
    private void searchUser() {
        String searchTerm = userSearchTextField.getText().trim();

        if (searchTerm.isEmpty()) {
            showAlert("Campo Vazio", "Por favor, digite um termo para buscar usuários.");
            return;
        }

        try {
            List<User> results = userService.findUsersByNameOrCPFOrEmail(searchTerm);
            userResultsListView.setItems(FXCollections.observableArrayList(results));
        } catch (SQLException e) {
            logError("Erro ao buscar usuários", e);
            showAlert("Erro na Busca", "Ocorreu um erro ao buscar usuários.");
        }
    }

    /**
     * Manipula o evento de clique em um item da ListView de usuários, selecionando o usuário
     * e exibindo seu nome e CPF.
     *
     * @param event O evento de clique do mouse.
     */
    @FXML
    private void selectUser(MouseEvent event) {
        selectedUser = userResultsListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUserLabel.setText("Usuário selecionado: " + selectedUser.getName() + " - " + selectedUser.getCpf());
        } else {
            selectedUserLabel.setText("Nenhum usuário selecionado");
        }
    }

    /**
     * Salva um novo empréstimo no banco de dados utilizando os livros, usuários e datas selecionados.
     * Atualiza a lista de empréstimos na tela principal e fecha o diálogo.
     */
    @FXML
    private void saveNewLoan() {
        LocalDate loanDate = loanDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();

        if (selectedBook == null) {
            showAlert("Campos Obrigatórios", "Por favor, selecione um livro.");
            return;
        }
        if (selectedUser == null) {
            showAlert("Campos Obrigatórios", "Por favor, selecione um usuário.");
            return;
        }
        if (loanDate == null) {
            showAlert("Campos Obrigatórios", "Por favor, selecione a data de empréstimo.");
            return;
        }
        if (returnDate == null) {
            showAlert("Campos Obrigatórios", "Por favor, selecione a data de devolução.");
            return;
        }
        if (returnDate.isBefore(loanDate)) {
            showAlert("Datas Inválidas", "A data de devolução não pode ser anterior à data de empréstimo.");
            return;
        }

        Loan newLoan = new Loan();
        newLoan.setBook(selectedBook);
        newLoan.setUser(selectedUser);
        newLoan.setLoanDate(loanDate);
        newLoan.setReturnDate(returnDate);
        newLoan.setStatus("Ativo");

        try {
            loanService.addLoan(newLoan);
            showAlert("Sucesso", "Empréstimo salvo com sucesso!");
            if (mainLoanController != null) {
                mainLoanController.loadAllLoans();
            }
            closeDialog();
        } catch (SQLException e) {
            logError("Erro ao salvar empréstimo", e);
            showAlert("Erro ao Salvar", "Ocorreu um erro ao salvar o empréstimo: " + e.getMessage());
        } catch (IllegalArgumentException e) { // Captura exceções de validação do serviço
            logError("Validação de empréstimo falhou", e);
            showAlert("Erro de Validação", "Falha na validação: " + e.getMessage());
        }
    }

    /**
     * Fecha o diálogo modal de adicionar empréstimo.
     */
    @FXML
    private void cancelLoan() {
        closeDialog();
    }

    /**
     * Exibe um diálogo de alerta com a mensagem especificada.
     *
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // CORREÇÃO: Define o proprietário do alerta para que ele apareça centralizado sobre a janela correta
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
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Fecha o diálogo modal atual.
     */
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}