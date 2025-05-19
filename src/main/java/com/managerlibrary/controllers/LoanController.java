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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
     * Define o serviço de empréstimos e carrega todos os empréstimos.
     *
     * @param loanService O serviço de empréstimos a ser utilizado.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
        loadAllLoans();
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
     *
     * @param rootLayoutController O controlador principal.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
        try {
            Connection connection = DataBaseConnection.getConnection();
            this.bookService = new BookService(new BookDAOImpl(connection));
            this.userService = new UserService(new UserDAOImpl(connection));
            this.loanService = new LoanService(new LoanDAOImpl(connection));
            loadAllLoans();
        } catch (SQLException e) {
            logError("Erro ao inicializar serviços", e);
            showAlert("Erro ao inicializar serviços", "Não foi possível inicializar os serviços de empréstimo.");
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
     * (Atualmente vazio, a lógica de inicialização está em setRootLayoutController).
     */
    @FXML
    public void initialize() {
        // Qualquer inicialização adicional para o LoanController
    }

    /**
     * Carrega todos os empréstimos do banco de dados, incluindo os detalhes de livro e usuário,
     * e atualiza a exibição.
     */
    public void loadAllLoans() {
        try {
            List<Loan> loans = loanService.getAllLoansWithDetails();
            allLoans.setAll(loans);
            displayLoans(allLoans);
        } catch (SQLException e) {
            logError("Erro ao carregar empréstimos", e);
            showAlert("Erro ao carregar empréstimos", "Não foi possível carregar os empréstimos do banco de dados.");
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
        for (Loan loan : loans) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanCardView.fxml"));
                VBox loanCard = loader.load();
                loanCard.getStyleClass().add("loan-card");
                LoanCardController controller = loader.getController();
                controller.setLoan(loan);
                controller.setLoanController(this);
                loansVBox.getChildren().add(loanCard);
            } catch (IOException e) {
                logError("Erro ao carregar LoanCardView.fxml", e);
                showAlert("Erro ao carregar card de empréstimo", "Não foi possível carregar o card de empréstimo.");
            }
        }
    }

    /**
     * Exibe a tela de adicionar um novo empréstimo em um diálogo modal, injetando os serviços necessários.
     */
    @FXML
    public void showAddLoanView() {
        try {
            URL location = getClass().getResource("/views/AddLoanView.fxml");
            System.out.println("LoanController.showAddLoanView: URL do FXML: " + location);
            if (location == null) {
                System.err.println("Erro: Não foi possível encontrar o arquivo FXML em /views/AddLoanView.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();

            AddLoanViewController addLoanController = loader.getController();
            System.out.println("LoanController.showAddLoanView: Controlador AddLoanViewController obtido: " + addLoanController);
            System.out.println("LoanController.showAddLoanView: bookService em LoanController: " + bookService);
            System.out.println("LoanController.showAddLoanView: userService em LoanController: " + userService);
            System.out.println("LoanController.showAddLoanView: loanService em LoanController: " + loanService);

            if (addLoanController != null) {
                addLoanController.setBookService(bookService);
                addLoanController.setUserService(userService);
                addLoanController.setLoanService(loanService);
                addLoanController.setMainLoanController(this);
                Stage stage = new Stage();
                addLoanController.setDialogStage(stage);
                System.out.println("LoanController.showAddLoanView: Serviços e Stage setados no AddLoanViewController.");
                stage.setTitle("Novo Empréstimo");
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.err.println("LoanController.showAddLoanView: Erro ao obter o controlador AddLoanViewController!");
            }
        } catch (IOException e) {
            logError("Erro ao carregar AddLoanView.fxml", e);
            showAlert("Erro ao carregar tela de adicionar empréstimo", "Não foi possível carregar a tela de adicionar empréstimo.");
        }
    }

    /**
     * Manipula a ação de buscar empréstimos. Filtra a lista de empréstimos com base no termo de busca
     * em título do livro, nome do usuário ou CPF do usuário.
     *
     * @param event O evento de ação (geralmente do botão de busca).
     */
    @FXML
    public void handleSearchLoans(ActionEvent event) {
        String searchTerm = searchTextField.getText().trim().toLowerCase();
        if (!searchTerm.isEmpty()) {
            ObservableList<Loan> searchResults = allLoans.stream()
                    .filter(loan -> {
                        String bookTitle = loan.getBook().getTitle().toLowerCase();
                        String userName = loan.getUser().getName().toLowerCase();
                        String userCPF = loan.getUser().getCpf().toLowerCase();
                        return bookTitle.contains(searchTerm) || userName.contains(searchTerm) || userCPF.contains(searchTerm);
                    })
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
            displayLoans(searchResults);
        } else {
            displayLoans(allLoans);
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
                .filter(loan -> loan.getActualReturnDate() == null && today.isAfter(loan.getReturnDate()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(overdueLoans);
    }

    /**
     * Marca um empréstimo como devolvido, definindo a data de devolução real como a data atual
     * e atualizando o banco de dados e a exibição.
     *
     * @param loan O empréstimo a ser marcado como devolvido.
     */
    @FXML
    public void markLoanAsReturned(Loan loan) {
        if (loan != null && loan.getActualReturnDate() == null) {
            loan.setActualReturnDate(LocalDate.now());
            try {
                loanService.updateLoan(loan);
                loadAllLoans();
                showAlert("Sucesso", "Empréstimo marcado como devolvido.");
            } catch (SQLException e) {
                logError("Erro ao marcar devolução", e);
                showAlert("Erro ao marcar devolução", "Não foi possível atualizar a informação de devolução no banco de dados.");
            }
        } else if (loan != null && loan.getActualReturnDate() != null) {
            showAlert("Informação", "Este empréstimo já foi devolvido em " + loan.getActualReturnDate().toString() + ".");
        } else {
            showAlert("Erro", "Não foi possível processar a devolução. Informação do empréstimo inválida.");
        }
    }

    /**
     * Exibe os detalhes completos de um empréstimo em um diálogo modal.
     *
     * @param loan O empréstimo a ser exibido.
     */
    @FXML
    public void showLoanDetails(Loan loan) {
        if (loan != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanDetailsView.fxml"));
                Parent root = loader.load();
                LoanDetailsController controller = loader.getController();
                controller.setLoan(loan);
                controller.setBookService(bookService);
                controller.setUserService(userService);
                Stage stage = new Stage();
                stage.setTitle("Detalhes do Empréstimo");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                logError("Erro ao exibir detalhes", e);
                showAlert("Erro ao exibir detalhes", "Não foi possível carregar a tela de detalhes do empréstimo.");
            }
        }
    }

    /**
     * Remove um empréstimo do banco de dados e atualiza a exibição.
     *
     * @param loan O empréstimo a ser removido.
     */
    public void removeLoan(Loan loan) {
        if (loan != null) {
            try {
                loanService.deleteLoan(loan.getId());
                loadAllLoans();
                showAlert("Sucesso", "Empréstimo removido com sucesso.");
            } catch (SQLException e) {
                logError("Erro ao remover empréstimo", e);
                showAlert("Erro ao remover empréstimo", "Não foi possível remover o empréstimo do banco de dados.");
            }
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