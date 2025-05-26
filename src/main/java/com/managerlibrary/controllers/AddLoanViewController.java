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
     * Manter @FXML se você usa items="$bookSearchOptions" no FXML,
     * mas vamos popular explicitamente no initialize.
     */
    @FXML
    private ObservableList<String> bookSearchOptions = FXCollections.observableArrayList("Título", "Autor", "ISBN", "Gênero"); // ADICIONADO "Gênero" para ser consistente com o BookDetailsController

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
        // *** NOVO: Popular o ComboBox explicitamente no initialize ***
        bookSearchCriteria.setItems(bookSearchOptions);
        // Opcional: Seleciona o primeiro item por padrão para que não comece vazio
        if (!bookSearchOptions.isEmpty()) {
            bookSearchCriteria.getSelectionModel().selectFirst();
        }

        configureBookListView();
        configureUserListView();

        // Inicializa DatePickers com a data atual e previsão de devolução
        loanDatePicker.setValue(LocalDate.now());
        returnDatePicker.setValue(LocalDate.now().plusWeeks(2)); // Exemplo: devolução em 2 semanas
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
            bookResultsListView.setItems(FXCollections.observableArrayList()); // Limpa resultados anteriores
            selectedBook = null; // Reseta o livro selecionado
            selectedBookLabel.setText("Nenhum livro selecionado");
            selectedBookImageView.setImage(null);
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
                case "gênero": // Adicionado suporte para Gênero
                    results.addAll(bookService.findBooksByGenre(searchTerm));
                    break;
                default:
                    showAlert("Critério Inválido", "Por favor, selecione um critério de busca válido para livros.");
                    return;
            }
            bookResultsListView.setItems(results);
            if (results.isEmpty()) {
                showAlert("Nenhum Resultado", "Nenhum livro encontrado com o critério e termo fornecidos.");
            }
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
                if (!image.isError()) { // Verifica se houve erro no carregamento da imagem
                    selectedBookImageView.setImage(image);
                    return; // Imagem carregada com sucesso, sai do método
                }
            } catch (Exception e) {
                logError("Tentativa de carregar capa do livro por caminho local falhou", e);
                // Se falhar, tenta carregar do recurso padrão
            }
        }

        // Se não tem caminho de capa, ou se o carregamento do caminho falhou, tenta imagem padrão
        // Usar getResourceAsStream é mais robusto para recursos internos do JAR
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default_book_icon.png"));
            selectedBookImageView.setImage(defaultImage);
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
            userResultsListView.setItems(FXCollections.observableArrayList()); // Limpa resultados anteriores
            selectedUser = null; // Reseta o usuário selecionado
            selectedUserLabel.setText("Nenhum usuário selecionado");
            return;
        }

        try {
            // Supondo que findUsersByNameOrCPFOrEmail retorne List<User>
            List<User> results = userService.findUsersByNameOrCPFOrEmail(searchTerm);
            userResultsListView.setItems(FXCollections.observableArrayList(results));
            if (results.isEmpty()) {
                showAlert("Nenhum Resultado", "Nenhum usuário encontrado com o termo fornecido.");
            }
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
        if (selectedBook.getAvailableCopies() <= 0) {
            showAlert("Livro Indisponível", "O livro selecionado não possui cópias disponíveis para empréstimo.");
            return;
        }


        Loan newLoan = new Loan();
        newLoan.setBook(selectedBook);
        newLoan.setUser(selectedUser);
        newLoan.setLoanDate(loanDate);
        newLoan.setExpectedReturnDate(returnDate); // Correção: Deveria ser ExpectedReturnDate
        newLoan.setStatus("Ativo");

        try {
            loanService.addLoan(newLoan); // Supondo que 'addLoan' é o método correto
            // Decrementa o número de cópias disponíveis do livro
            selectedBook.setAvailableCopies(selectedBook.getAvailableCopies() - 1);
            bookService.updateBook(selectedBook); // Atualiza o livro no banco de dados

            showAlert("Sucesso", "Empréstimo registrado e cópia do livro decrementada com sucesso!");
            dialogStage.close();
            if (mainLoanController != null) {
                mainLoanController.loadLoans(); // Recarrega os empréstimos na tela principal
            }
        } catch (SQLException e) {
            logError("Erro ao salvar novo empréstimo", e);
            showAlert("Erro", "Não foi possível registrar o empréstimo: " + e.getMessage());
        }
    }

    /**
     * Cancela a operação de empréstimo e fecha o diálogo.
     */
    @FXML
    private void cancelLoan() {
        dialogStage.close();
    }

    /**
     * Exibe um alerta de informação ou erro.
     *
     * @param title O título do alerta.
     * @param message A mensagem do alerta.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (title.contains("Erro") || title.contains("Inválido") || title.contains("Vazios") || title.contains("Indisponível")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
        alert.showAndWait();
    }

    /**
     * Registra uma mensagem de erro no console, incluindo a stack trace da exceção.
     *
     * @param message A mensagem de erro.
     * @param e A exceção (pode ser null).
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