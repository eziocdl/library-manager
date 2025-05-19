package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

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
    private BookService bookService;
    private UserService userService;
    private LoanController mainLoanController; // Controlador da tela principal de empréstimos
    private Stage dialogStage; // Palco (Stage) do diálogo modal
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Formato para exibir datas

    /**
     * Define o empréstimo a ser editado e preenche os campos da tela com seus dados.
     *
     * @param loan O empréstimo a ser editado.
     */
    public void setLoan(Loan loan) {
        this.currentLoan = loan;
        populateFields();
    }

    /**
     * Define o serviço de empréstimos.
     *
     * @param loanService O serviço de empréstimos a ser utilizado.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Define o serviço de livros (pode ser útil para buscar informações adicionais do livro, se necessário).
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Define o serviço de usuários (pode ser útil para buscar informações adicionais do usuário, se necessário).
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Define o controlador da tela principal de empréstimos para atualizar a lista após a edição.
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
     * Método de inicialização do controlador. Define as opções para o ComboBox de status.
     */
    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList("Ativo", "Devolvido", "Atrasado"));
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
                bookInfoLabel.setText("N/A");
            }
            if (currentLoan.getUser() != null) {
                userInfoLabel.setText(currentLoan.getUser().getName() + " (" + currentLoan.getUser().getCpf() + ")");
            } else {
                userInfoLabel.setText("N/A");
            }
            loanDateLabel.setText(currentLoan.getLoanDate().format(dateFormatter));
            returnDatePicker.setValue(currentLoan.getReturnDate());
            statusComboBox.setValue(currentLoan.getStatus());
        }
    }

    /**
     * Salva as alterações feitas no empréstimo (data de devolução e status) no banco de dados
     * e atualiza a lista de empréstimos na tela principal.
     */
    @FXML
    private void saveEditedLoan() {
        if (currentLoan != null) {
            currentLoan.setReturnDate(returnDatePicker.getValue());
            currentLoan.setStatus(statusComboBox.getValue());

            try {
                loanService.updateLoan(currentLoan);
                if (mainLoanController != null) {
                    mainLoanController.loadAllLoans(); // Atualiza a lista na tela principal
                }
                closeEditLoanView();
            } catch (Exception e) {
                logError("Erro ao salvar edição do empréstimo", e);
                showAlert("Erro ao Salvar", "Ocorreu um erro ao salvar as alterações do empréstimo.");
            }
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
        Stage stage = (Stage) loanIdLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Exibe um diálogo de alerta com a mensagem especificada.
     *
     * @param title   O título do alerta.
     * @param content O conteúdo da mensagem do alerta.
     */
    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
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