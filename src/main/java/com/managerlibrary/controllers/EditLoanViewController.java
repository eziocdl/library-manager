package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import com.managerlibrary.services.BookService; // ADICIONADO
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService; // ADICIONADO
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.util.Objects; // Para Objects.requireNonNull

/**
 * Controlador para a tela de edição de um empréstimo existente. Permite modificar
 * a data de devolução e o status do empréstimo.
 */
public class EditLoanViewController {

    @FXML
    private Label loanIdLabel;
    @FXML
    private Label bookInfoLabel;
    @FXML
    private Label userInfoLabel;
    @FXML
    private Label loanDateLabel;
    @FXML
    private DatePicker returnDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;

    private Loan currentLoan; // O empréstimo atualmente sendo editado
    private LoanService loanService;
    private BookService bookService; // RE-ADICIONADO
    private UserService userService; // RE-ADICIONADO
    private LoanController mainLoanController; // Controlador da tela principal de empréstimos
    private Stage dialogStage; // Palco (Stage) do diálogo modal
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Define o empréstimo a ser editado e preenche os campos da tela com seus dados.
     *
     * @param loan O empréstimo a ser editado.
     */
    public void setLoan(Loan loan) {
        this.currentLoan = Objects.requireNonNull(loan, "Loan não pode ser nulo.");
        populateFields();
    }

    /**
     * Define o serviço de empréstimos.
     *
     * @param loanService O serviço de empréstimos a ser utilizado.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = Objects.requireNonNull(loanService, "LoanService não pode ser nulo.");
    }

    /**
     * **ADICIONADO:** Define o serviço de livros.
     * Necessário para o LoanService interno ou futuras validações.
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }

    /**
     * **ADICIONADO:** Define o serviço de usuários.
     * Necessário para o LoanService interno ou futuras validações.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
    }

    /**
     * Define o controlador da tela principal de empréstimos para atualizar a lista após a edição.
     *
     * @param mainLoanController O controlador da tela principal de empréstimos.
     */
    public void setMainLoanController(LoanController mainLoanController) {
        this.mainLoanController = Objects.requireNonNull(mainLoanController, "MainLoanController não pode ser nulo.");
    }

    /**
     * Define o palco (Stage) deste diálogo modal.
     *
     * @param dialogStage O palco do diálogo.
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = Objects.requireNonNull(dialogStage, "DialogStage não pode ser nulo.");
    }

    /**
     * Método de inicialização do controlador. Define as opções para o ComboBox de status.
     */
    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList("Ativo", "Devolvido", "Atrasado", "Perdido")); // Adicionei "Perdido" como uma opção comum
    }

    /**
     * Preenche os campos da tela com os dados do empréstimo atual.
     */
    private void populateFields() {
        if (currentLoan != null) {
            loanIdLabel.setText(String.valueOf(currentLoan.getId()));
            if (currentLoan.getBook() != null) {
                bookInfoLabel.setText(currentLoan.getBook().getTitle() + " (" + currentLoan.getBook().getAuthor() + ")");
            } else {
                bookInfoLabel.setText("Informação do Livro N/A");
            }
            if (currentLoan.getUser() != null) {
                userInfoLabel.setText(currentLoan.getUser().getName() + " (" + currentLoan.getUser().getCpf() + ")");
            } else {
                userInfoLabel.setText("Informação do Usuário N/A");
            }

            if (currentLoan.getLoanDate() != null) {
                loanDateLabel.setText(currentLoan.getLoanDate().format(dateFormatter));
            } else {
                loanDateLabel.setText("Data de Empréstimo N/A");
            }

            if (currentLoan.getReturnDate() != null) {
                returnDatePicker.setValue(currentLoan.getReturnDate());
            } else {
                returnDatePicker.setValue(null); // Limpa a data se não houver
            }

            if (currentLoan.getStatus() != null) {
                statusComboBox.setValue(currentLoan.getStatus());
            } else {
                statusComboBox.setValue("Ativo"); // Define um valor padrão se o status for nulo
            }
        }
    }

    /**
     * Salva as alterações feitas no empréstimo (data de devolução e status) no banco de dados
     * e atualiza a lista de empréstimos na tela principal.
     */
    @FXML
    private void saveEditedLoan() {
        // Validações para garantir que os serviços estejam injetados
        if (currentLoan == null) {
            showAlert("Erro Interno", "Nenhum empréstimo selecionado para salvar. Por favor, reinicie a operação.");
            return;
        }
        if (loanService == null) {
            showAlert("Erro de Inicialização", "LoanService não foi injetado. Impossível salvar.");
            logError("LoanService é nulo ao tentar salvar o empréstimo.", new IllegalStateException("LoanService é nulo."));
            return;
        }
        // Se BookService e UserService forem usados em validações do LoanService, a verificação neles também seria prudente.
        // Por agora, assumimos que o LoanService já garante a existência deles.

        if (returnDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", "Por favor, selecione a data de devolução.");
            return;
        }
        if (statusComboBox.getValue() == null || statusComboBox.getValue().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campo Obrigatório", "Por favor, selecione o status do empréstimo.");
            return;
        }
        if (currentLoan.getLoanDate() != null && returnDatePicker.getValue().isBefore(currentLoan.getLoanDate())) {
            showAlert(Alert.AlertType.WARNING, "Data Inválida", "A data de devolução não pode ser anterior à data de empréstimo.");
            return;
        }

        // Atualiza o objeto Loan com as novas informações
        currentLoan.setReturnDate(returnDatePicker.getValue());
        currentLoan.setStatus(statusComboBox.getValue());

        try {
            loanService.updateLoan(currentLoan);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo atualizado com sucesso!");
            if (mainLoanController != null) {
                mainLoanController.loadAllLoans(); // Atualiza a lista principal
            }
            closeEditLoanView();
        } catch (SQLException e) {
            logError("Erro ao salvar edição do empréstimo no banco de dados", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Ocorreu um erro ao salvar as alterações do empréstimo: " + e.getMessage());
        } catch (IllegalArgumentException e) { // Captura exceções de validação de serviço
            logError("Erro de validação de negócio ao salvar empréstimo", e);
            showAlert(Alert.AlertType.WARNING, "Erro de Validação", "Falha na validação: " + e.getMessage());
        } catch (Exception e) { // Captura qualquer outra exceção inesperada
            logError("Erro inesperado ao salvar edição do empréstimo", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Ocorreu um erro inesperado ao salvar as alterações do empréstimo.");
        }
    }

    /**
     * Fecha a tela de edição de empréstimo sem salvar as alterações.
     */
    @FXML
    private void cancelEditLoan() {
        closeEditLoanView();
    }

    /**
     * Fecha a janela (Stage) da tela de edição de empréstimo.
     */
    private void closeEditLoanView() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    /**
     * Exibe um diálogo de alerta com a mensagem especificada.
     * Sobrecarga para permitir especificação do tipo de alerta.
     *
     * @param alertType O tipo de alerta (INFORMATION, WARNING, ERROR).
     * @param title     O título do alerta.
     * @param content   O conteúdo da mensagem do alerta.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        if (dialogStage != null) {
            alert.initOwner(dialogStage); // Define o proprietário do alerta para que apareça centralizado
        }
        alert.showAndWait();
    }

    /**
     * Sobrecarga para manter compatibilidade, usando ERROR como padrão.
     */
    private void showAlert(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, content);
    }


    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida (pode ser null se for apenas uma mensagem).
     */
    private void logError(String message, Exception e) {
        System.err.print(message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace(); // Imprime a pilha de chamadas para depuração
        } else {
            System.err.println(); // Apenas nova linha se não houver exceção
        }
    }
}