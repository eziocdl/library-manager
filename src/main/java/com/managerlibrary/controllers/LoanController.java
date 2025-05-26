package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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

    // Serviços que serão injetados
    private LoanService loanService;
    private BookService bookService;
    private UserService userService;

    private ObservableList<Loan> allLoans = FXCollections.observableArrayList();
    private RootLayoutController rootLayoutController;

    /**
     * Define o serviço de empréstimos. Este método deve ser chamado durante a inicialização
     * da aplicação para injetar a dependência.
     *
     * @param loanService O serviço de empréstimos a ser utilizado.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = Objects.requireNonNull(loanService, "LoanService não pode ser nulo.");
    }

    /**
     * Define o serviço de livros. Este método deve ser chamado durante a inicialização
     * da aplicação para injetar a dependência.
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }

    /**
     * Define o serviço de usuários. Este método deve ser chamado durante a inicialização
     * da aplicação para injetar a dependência.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
    }

    /**
     * Define o controlador principal da aplicação (RootLayoutController).
     * Útil para comunicação entre controladores ou para obter o Stage principal.
     *
     * @param rootLayoutController O controlador principal.
     */
    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
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
     * Método de inicialização do controlador. Chamado automaticamente após o FXML ser carregado
     * e as injeções @FXML terem sido feitas.
     * É o lugar ideal para configurar listeners. A carga de dados deve ser feita APÓS a injeção dos serviços.
     */
    @FXML
    public void initialize() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> handleSearchLoans());
        // loadLoans(); // Não chame loadLoans() aqui, pois os serviços podem não ter sido injetados ainda.
        // O RootLayoutController deve chamar loadLoans() após setar os serviços.
    }

    /**
     * Carrega todos os empréstimos do banco de dados, incluindo os detalhes de livro e usuário,
     * e atualiza a exibição.
     * Este é o método central para recarregar a lista de empréstimos na UI.
     */
    public void loadLoans() {
        System.out.println("LoanController: Método loadLoans() chamado.");
        // ESSENCIAL: Assumimos que os serviços (loanService, bookService, userService)
        // JÁ FORAM INJETADOS pelo RootLayoutController ANTES deste método ser chamado.
        if (loanService == null || bookService == null || userService == null) {
            logError("ERRO CRÍTICO: Serviços de empréstimo/livro/usuário não injetados em LoanController. Não é possível carregar dados.", null);
            showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Os serviços de banco de dados não foram inicializados corretamente. Por favor, reinicie a aplicação.");
            return;
        }

        try {
            List<Loan> loans = loanService.getAllLoansWithDetails(); // Assumindo que este método traz os detalhes
            allLoans.setAll(loans); // Atualiza a ObservableList
            displayLoans(allLoans); // Atualiza a UI
        } catch (SQLException e) {
            logError("Erro ao carregar empréstimos do banco de dados", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar Empréstimos", "Não foi possível carregar os empréstimos do banco de dados: " + e.getMessage());
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
            Label noLoansLabel = new Label("Nenhum empréstimo encontrado.");
            noLoansLabel.getStyleClass().add("label-info"); // Adiciona uma classe CSS para estilização (opcional)
            loansVBox.getChildren().add(noLoansLabel);
            return;
        }

        for (Loan loan : loans) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanCardView.fxml"));
                VBox loanCard = loader.load();
                loanCard.getStyleClass().add("loan-card"); // Adiciona classe CSS para estilização

                LoanCardController controller = loader.getController();
                // Injetando os serviços no LoanCardController
                controller.setLoanService(loanService);
                controller.setBookService(bookService);
                controller.setUserService(userService);

                controller.setLoan(loan);
                controller.setLoanController(this); // Passa a referência deste controlador para o card
                loansVBox.getChildren().add(loanCard);
            } catch (IOException e) {
                logError("Erro ao carregar LoanCardView.fxml para empréstimo " + (loan != null ? loan.getId() : "null"), e);
                showAlert(Alert.AlertType.ERROR, "Erro de Exibição", "Não foi possível carregar o card para um empréstimo: " + e.getMessage());
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

            // É CRUCIAL que os serviços não sejam nulos aqui
            if (bookService == null || userService == null || loanService == null) {
                logError("Serviços (BookService, UserService, LoanService) não inicializados em LoanController ao abrir AddLoanView.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Os serviços necessários não estão disponíveis para adicionar um empréstimo. Por favor, reinicie a aplicação ou verifique a configuração.");
                return;
            }

            // Injeta os serviços no AddLoanViewController
            addLoanController.setBookService(bookService);
            addLoanController.setUserService(userService);
            addLoanController.setLoanService(loanService);
            // Passa a referência deste LoanController para o AddLoanViewController,
            // permitindo que AddLoanController chame loadLoans() após a adição.
            addLoanController.setMainLoanController(this);

            Stage stage = new Stage();
            stage.setTitle("Novo Empréstimo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            // Tenta definir o proprietário do palco do diálogo para centralização
            if (loansVBox != null && loansVBox.getScene() != null && loansVBox.getScene().getWindow() instanceof Stage) {
                stage.initOwner(loansVBox.getScene().getWindow());
            } else if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
                stage.initOwner(rootLayoutController.getPrimaryStage());
            }

            addLoanController.setDialogStage(stage); // Passa o stage para o controlador de adição
            stage.showAndWait(); // showAndWait para bloquear a tela principal até o diálogo fechar

            // Após o diálogo fechar (seja por salvar ou cancelar), recarrega a lista de empréstimos
            loadLoans(); // <-- CHAMA loadLoans() AQUI PARA ATUALIZAR A LISTA NA TELA PRINCIPAL
        } catch (IOException e) {
            logError("Erro ao carregar AddLoanView.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Abrir Tela", "Não foi possível carregar a tela para adicionar empréstimo: " + e.getMessage());
        } catch (Exception e) { // Captura qualquer outra exceção inesperada
            logError("Erro inesperado ao abrir a tela de Novo Empréstimo", e);
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado ao abrir a tela de Novo Empréstimo: " + e.getMessage());
        }
    }

    /**
     * Manipula a ação de buscar empréstimos. Filtra a lista de empréstimos com base no termo de busca
     * em título do livro, nome do usuário ou CPF do usuário.
     * Pode ser chamado por um botão ou por um listener de texto no TextField.
     */
    @FXML
    public void handleSearchLoans() {
        String searchTerm = searchTextField.getText().trim().toLowerCase();

        // allLoans DEVE ESTAR POPULADO QUANDO loadLoans() É CHAMADO INICIALMENTE.
        // Não é ideal tentar recarregar aqui, pois a lista principal já deveria estar carregada.
        // if (allLoans.isEmpty() && loanService != null) {
        //     try {
        //         allLoans.setAll(loanService.getAllLoansWithDetails());
        //     } catch (SQLException e) {
        //         logError("Erro ao recarregar empréstimos para busca no 'handleSearchLoans'", e);
        //     }
        // }

        if (searchTerm.isEmpty()) {
            displayLoans(allLoans); // Exibe todos os empréstimos se a busca estiver vazia
        } else {
            ObservableList<Loan> searchResults = allLoans.stream()
                    .filter(loan -> {
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
     * @param event O evento de ação (de um botão, por exemplo).
     */
    @FXML
    void filterLoansByAll(ActionEvent event) {
        displayLoans(allLoans);
    }

    /**
     * Exibe apenas os empréstimos ativos (com data de devolução real nula e status "Ativo").
     *
     * @param event O evento de ação.
     */
    @FXML
    void filterLoansByActive(ActionEvent event) {
        ObservableList<Loan> activeLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null && "Ativo".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(activeLoans);
    }

    /**
     * Exibe apenas os empréstimos devolvidos (com data de devolução real preenchida e status "Devolvido").
     *
     * @param event O evento de ação.
     */
    @FXML
    void filterLoansByReturned(ActionEvent event) {
        ObservableList<Loan> returnedLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() != null && "Devolvido".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(returnedLoans);
    }

    /**
     * Exibe apenas os empréstimos atrasados (ativos e com data de devolução prevista anterior à data atual).
     *
     * @param event O evento de ação.
     */
    @FXML
    void filterLoansByOverdue(ActionEvent event) {
        LocalDate today = LocalDate.now();
        ObservableList<Loan> overdueLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null && "Ativo".equalsIgnoreCase(loan.getStatus()) &&
                        loan.getExpectedReturnDate() != null && today.isAfter(loan.getExpectedReturnDate()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(overdueLoans);
    }

    /**
     * Marca um empréstimo como devolvido, definindo a data de devolução real como a data atual,
     * atualizando o status e incrementando as cópias disponíveis do livro.
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
        loan.setStatus("Devolvido"); // Define o status como Devolvido

        try {
            if (loanService == null || bookService == null) {
                logError("Serviços de empréstimo ou livro não estão disponíveis para marcar como devolvido.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Serviço", "O serviço de empréstimos ou livros não está disponível.");
                return;
            }

            loanService.updateLoan(loan); // Atualiza o empréstimo no banco de dados

            // Incrementa o número de cópias disponíveis do livro
            if (loan.getBook() != null) {
                // Obtenha a cópia mais recente do livro do banco de dados para evitar inconsistências
                // caso o livro tenha sido atualizado por outro lugar
                // ou simplesmente use o objeto book já associado ao empréstimo se for confiável
                // Por simplicidade, vamos usar o objeto associado:
                int currentAvailable = loan.getBook().getAvailableCopies();
                loan.getBook().setAvailableCopies(currentAvailable + 1);
                bookService.updateBook(loan.getBook()); // Atualiza o livro no banco de dados
            }

            loadLoans(); // Recarrega a lista para refletir a mudança
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo marcado como devolvido e cópia do livro incrementada com sucesso!");
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

            if (bookService == null || userService == null) {
                logError("Serviços (BookService, UserService) não inicializados em LoanController para detalhes.", null);
                showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Os serviços necessários não estão disponíveis para exibir detalhes.");
                return;
            }

            controller.setLoan(loan);
            controller.setBookService(bookService);
            controller.setUserService(userService);

            Stage stage = new Stage();
            stage.setTitle("Detalhes do Empréstimo: " + loan.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            if (loansVBox != null && loansVBox.getScene() != null && loansVBox.getScene().getWindow() instanceof Stage) {
                stage.initOwner(loansVBox.getScene().getWindow());
            } else if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
                stage.initOwner(rootLayoutController.getPrimaryStage());
            }
            stage.showAndWait();
        } catch (IOException e) {
            logError("Erro ao carregar LoanDetailsView.fxml", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Abrir Tela", "Não foi possível carregar a tela de detalhes do empréstimo: " + e.getMessage());
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
            loadLoans(); // Recarrega a lista
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
        // Tenta definir o proprietário do palco do alerta para centralização
        if (rootLayoutController != null && rootLayoutController.getPrimaryStage() != null) {
            alert.initOwner(rootLayoutController.getPrimaryStage());
        } else if (loansVBox != null && loansVBox.getScene() != null && loansVBox.getScene().getWindow() != null) {
            alert.initOwner(loansVBox.getScene().getWindow());
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