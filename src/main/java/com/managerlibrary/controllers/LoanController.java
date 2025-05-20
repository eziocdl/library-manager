package com.managerlibrary.controllers;

import com.managerlibrary.daos.implement.BookDAOImpl;
import com.managerlibrary.daos.implement.LoanDAOImpl;
import com.managerlibrary.daos.implement.UserDAOImpl;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.infra.DataBaseConnection;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Necessário para loader.load()
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
// import java.net.URL; // Removido: Não utilizado diretamente após refatoração
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para a tela principal de empréstimos. Exibe os empréstimos em cards,
 * permite adicionar, pesquisar, filtrar, marcar como devolvido, visualizar detalhes,
 * editar e remover empréstimos.
 */
public class LoanController {

    @FXML
    private VBox loansVBox;
    @FXML
    private TextField searchTextField;

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private ObservableList<Loan> allLoans = FXCollections.observableArrayList();
    private RootLayoutController rootLayoutController;

    /**
     * Define o serviço de empréstimos.
     * Chama loadAllLoans() para carregar os dados iniciais.
     *
     * @param loanService O serviço de empréstimos a ser utilizado.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
        loadAllLoans(); // Carrega os empréstimos assim que o serviço é definido
    }

    /**
     * Obtém o serviço de empréstimos.
     *
     * @return O serviço de empréstimos.
     */
    public LoanService getLoanService() {
        return loanService;
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
     * Obtém o serviço de livros.
     *
     * @return O serviço de livros.
     */
    public BookService getBookService() {
        return bookService;
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
     * Obtém o serviço de usuários.
     *
     * @return O serviço de usuários.
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * Define o controlador principal da aplicação (RootLayoutController) e inicializa os serviços.
     * Carrega todos os empréstimos ao inicializar o controlador principal.
     * <p>
     * Nota: A inicialização dos serviços aqui duplica a responsabilidade de setLoanService.
     * Se os serviços são injetados separadamente, esta parte pode ser removida
     * ou ajustada para apenas carregar os empréstimos se os serviços já estiverem definidos.
     * Considerando o fluxo, esta é a inicialização primária.
     *
     * @param rootLayoutController O controlador principal.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
        // Verifica se os serviços já foram inicializados (por injeção externa, por exemplo)
        if (this.loanService == null || this.bookService == null || this.userService == null) {
            try {
                Connection connection = DataBaseConnection.getConnection();
                this.bookService = new BookService(new BookDAOImpl(connection));
                this.userService = new UserService(new UserDAOImpl(connection));
                this.loanService = new LoanService(new LoanDAOImpl(connection));
                loadAllLoans(); // Carrega os empréstimos após a inicialização bem-sucedida dos serviços
            } catch (SQLException e) {
                logError("Erro ao inicializar serviços no setRootLayoutController", e);
                showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Não foi possível inicializar os serviços de empréstimo. Por favor, verifique a conexão com o banco de dados.");
            }
        } else {
            // Se os serviços já foram setados (ex: via injeção externa), apenas garante que os empréstimos sejam carregados.
            loadAllLoans();
        }
    }

    /**
     * Obtém o controlador principal da aplicação (RootLayoutController).
     *
     * @return O controlador principal.
     */
    public RootLayoutController getRootLayoutController() {
        return rootLayoutController;
    }

    /**
     * Método de inicialização do controlador. Chamado após o FXML ser carregado.
     * Pode ser usado para configurações de UI que não dependem de dados carregados.
     */
    @FXML
    public void initialize() {
        // Ex: Configurar listeners para o searchTextField aqui, se necessário.
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchLoans());
    }

    /**
     * Carrega todos os empréstimos do banco de dados, incluindo os detalhes de livro e usuário,
     * e atualiza a exibição.
     */
    public void loadAllLoans() {
        try {
            if (loanService == null) {
                logError("LoanService é nulo ao tentar carregar empréstimos.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Serviço", "O serviço de empréstimos não está disponível.");
                return;
            }
            List<Loan> loans = loanService.getAllLoansWithDetails();
            allLoans.setAll(loans);
            displayLoans(allLoans);
        } catch (SQLException e) {
            logError("Erro ao carregar empréstimos do banco de dados", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Empréstimos", "Não foi possível carregar os empréstimos do banco de dados.");
        }
    }

    /**
     * Exibe a lista de empréstimos fornecida no VBox, criando um LoanCard para cada empréstimo.
     * Limpa os cards existentes antes de adicionar os novos.
     *
     * @param loans A lista de empréstimos a serem exibidos.
     */
    private void displayLoans(ObservableList<Loan> loans) {
        loansVBox.getChildren().clear();
        if (loans.isEmpty()) {
            // Opcional: Adicionar uma mensagem informando que não há empréstimos para exibir
            Label noLoansLabel = new Label("Nenhum empréstimo encontrado.");
            loansVBox.getChildren().add(noLoansLabel);
            return;
        }

        for (Loan loan : loans) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanCardView.fxml"));
                VBox loanCard = loader.load();
                loanCard.getStyleClass().add("loan-card"); // Adiciona classe CSS
                LoanCardController controller = loader.getController();
                controller.setLoan(loan);
                controller.setLoanController(this); // Permite que o card se comunique de volta com este controlador
                loansVBox.getChildren().add(loanCard);
            } catch (IOException e) {
                logError("Erro ao carregar LoanCardView.fxml para empréstimo " + loan.getId(), e);
                showAlert(Alert.AlertType.ERROR, "Erro de Exibição", "Não foi possível carregar o card para um empréstimo.");
            }
        }
    }

    /**
     * Exibe a tela de adicionar um novo empréstimo em um diálogo modal, injetando os serviços necessários.
     */
    @FXML
    public void showAddLoanView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddLoanView.fxml"));
            Parent root = loader.load();

            AddLoanViewController addLoanController = loader.getController();

            // Verificar se os serviços estão inicializados antes de passá-los
            if (bookService == null || userService == null || loanService == null) {
                logError("Serviços (BookService, UserService, LoanService) não inicializados em LoanController.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Os serviços necessários não estão disponíveis para adicionar um empréstimo.");
                return;
            }

            addLoanController.setBookService(bookService);
            addLoanController.setUserService(userService);
            addLoanController.setLoanService(loanService);
            addLoanController.setMainLoanController(this); // Permite que AddLoanController chame loadAllLoans()

            Stage stage = new Stage();
            stage.setTitle("Novo Empréstimo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            // Definir o proprietário do palco do diálogo, para centralização
            stage.initOwner(loansVBox.getScene().getWindow());
            addLoanController.setDialogStage(stage); // Passa o stage para o controlador de adição
            stage.showAndWait(); // showAndWait para bloquear a tela principal até o diálogo fechar

        } catch (IOException e) {
            logError("Erro ao carregar AddLoanView.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Abrir Tela", "Não foi possível carregar a tela para adicionar empréstimo.");
        }
    }

    /**
     * Manipula a ação de buscar empréstimos. Filtra a lista de empréstimos com base no termo de busca
     * em título do livro, nome do usuário ou CPF do usuário.
     * Pode ser chamado por um botão ou por um listener de texto.
     */
    @FXML
    public void handleSearchLoans() { // Removido ActionEvent event pois pode ser chamado por listener
        String searchTerm = searchTextField.getText().trim().toLowerCase();

        if (allLoans.isEmpty() && loanService != null) {
            // Tenta carregar novamente se a lista estiver vazia (caso a inicialização tenha falhado antes)
            loadAllLoans();
        }

        if (searchTerm.isEmpty()) {
            displayLoans(allLoans); // Exibe todos os empréstimos se a busca estiver vazia
        } else {
            ObservableList<Loan> searchResults = allLoans.stream()
                    .filter(loan -> {
                        // Evita NullPointerException se book ou user for nulo
                        boolean matchesBook = loan.getBook() != null && loan.getBook().getTitle().toLowerCase().contains(searchTerm);
                        boolean matchesUser = false;
                        if (loan.getUser() != null) {
                            matchesUser = loan.getUser().getName().toLowerCase().contains(searchTerm) ||
                                    (loan.getUser().getCpf() != null && loan.getUser().getCpf().toLowerCase().contains(searchTerm));
                        }
                        return matchesBook || matchesUser;
                    })
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            displayLoans(searchResults);
        }
    }

    /**
     * Exibe todos os empréstimos.
     *
     * @param event O evento de ação.
     */
    @FXML
    void filterLoansByAll(ActionEvent event) {
        displayLoans(allLoans);
    }

    /**
     * Exibe apenas os empréstimos ativos (com data de devolução real nula).
     *
     * @param event O evento de ação.
     */
    @FXML
    void filterLoansByActive(ActionEvent event) {
        ObservableList<Loan> activeLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(activeLoans);
    }

    /**
     * Exibe apenas os empréstimos devolvidos (com data de devolução real preenchida).
     *
     * @param event O evento de ação.
     */
    @FXML
    void filterLoansByReturned(ActionEvent event) {
        ObservableList<Loan> returnedLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() != null)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(returnedLoans);
    }

    /**
     * Exibe apenas os empréstimos atrasados (ativos e com data de devolução anterior à data atual).
     *
     * @param event O evento de ação.
     */
    @FXML
    void filterLoansByOverdue(ActionEvent event) {
        LocalDate today = LocalDate.now();
        ObservableList<Loan> overdueLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null && loan.getReturnDate() != null && today.isAfter(loan.getReturnDate()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(overdueLoans);
    }

    /**
     * Marca um empréstimo como devolvido, definindo a data de devolução real como a data atual
     * e atualizando o banco de dados e a exibição.
     *
     * @param loan O empréstimo a ser marcado como devolvido.
     */
    public void markLoanAsReturned(Loan loan) {
        if (loan == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Empréstimo inválido para devolução.");
            return;
        }

        if (loan.getActualReturnDate() != null) {
            showAlert(Alert.AlertType.INFORMATION, "Informação", "Este empréstimo já foi devolvido em " + loan.getActualReturnDate().toString() + ".");
            return;
        }

        loan.setActualReturnDate(LocalDate.now());
        // Se houver lógica de cálculo de multa, ela deve ser feita aqui ou no LoanService
        // loan.setFine(calcularMulta(loan)); // Exemplo

        try {
            if (loanService == null) {
                logError("LoanService é nulo ao tentar marcar empréstimo como devolvido.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Serviço", "O serviço de empréstimos não está disponível.");
                return;
            }
            loanService.updateLoan(loan);
            loadAllLoans(); // Recarrega a lista para refletir a mudança
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo marcado como devolvido com sucesso!");
        } catch (SQLException e) {
            logError("Erro ao marcar empréstimo como devolvido no banco de dados", e);
            showAlert(Alert.AlertType.ERROR, "Erro de Atualização", "Não foi possível atualizar a informação de devolução no banco de dados: " + e.getMessage());
        }
    }

    /**
     * Exibe os detalhes completos de um empréstimo em um diálogo modal.
     *
     * @param loan O empréstimo a ser exibido.
     */
    public void showLoanDetails(Loan loan) {
        if (loan == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Empréstimo inválido para exibir detalhes.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanDetailsView.fxml"));
            Parent root = loader.load();
            LoanDetailsController controller = loader.getController();

            // Verificar se os serviços estão inicializados antes de passá-los
            if (bookService == null || userService == null) {
                logError("Serviços (BookService, UserService) não inicializados em LoanController para detalhes.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Os serviços necessários não estão disponíveis para exibir detalhes.");
                return;
            }

            controller.setLoan(loan);
            controller.setBookService(bookService);
            controller.setUserService(userService); // Pode ser necessário para mostrar detalhes do usuário no LoanDetailsView

            Stage stage = new Stage();
            stage.setTitle("Detalhes do Empréstimo: " + loan.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            // Definir o proprietário do palco do diálogo, para centralização
            stage.initOwner(loansVBox.getScene().getWindow());
            stage.showAndWait();
        } catch (IOException e) {
            logError("Erro ao carregar LoanDetailsView.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Abrir Tela", "Não foi possível carregar a tela de detalhes do empréstimo.");
        }
    }

    /**
     * Remove um empréstimo do banco de dados e atualiza a exibição.
     *
     * @param loan O empréstimo a ser removido.
     */
    public void removeLoan(Loan loan) {
        if (loan == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Empréstimo inválido para remoção.");
            return;
        }
        try {
            if (loanService == null) {
                logError("LoanService é nulo ao tentar remover empréstimo.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Serviço", "O serviço de empréstimos não está disponível.");
                return;
            }
            loanService.deleteLoan(loan.getId());
            loadAllLoans(); // Recarrega a lista
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo removido com sucesso!");
        } catch (SQLException e) {
            logError("Erro ao remover empréstimo do banco de dados", e);
            showAlert(Alert.AlertType.ERROR, "Erro de Exclusão", "Não foi possível remover o empréstimo do banco de dados: " + e.getMessage());
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
        alert.showAndWait();
    }

    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida, pode ser nula.
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