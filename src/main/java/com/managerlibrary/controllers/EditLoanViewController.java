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
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Controlador para a tela de edição de um empréstimo existente. Permite modificar
 * a data de devolução prevista, a data de devolução real e o status do empréstimo.
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
    private DatePicker expectedReturnDatePicker; // AGORA É UM DATEPICKER
    @FXML
    private DatePicker actualReturnDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;

    private Loan currentLoan;
    private Loan originalLoan;
    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private LoanController mainLoanController;
    private Stage dialogStage;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setLoan(Loan loan) {
        this.currentLoan = Objects.requireNonNull(loan, "Loan não pode ser nulo.");
        this.originalLoan = new Loan(loan.getId(), loan.getBook(), loan.getUser(),
                loan.getLoanDate(), loan.getExpectedReturnDate(),
                loan.getActualReturnDate(), loan.getStatus(), loan.getFine(), loan.isReturned());
        populateFields();
    }

    public void setLoanService(LoanService loanService) {
        this.loanService = Objects.requireNonNull(loanService, "LoanService não pode ser nulo.");
    }

    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }

    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
    }

    public void setMainLoanController(LoanController mainLoanController) {
        this.mainLoanController = Objects.requireNonNull(mainLoanController, "MainLoanController não pode ser nulo.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = Objects.requireNonNull(dialogStage, "DialogStage não pode ser nulo.");
    }

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList("Ativo", "Devolvido"));

        // Listener para limpar a data de devolução real se o status for mudado para "Ativo"
        statusComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Ativo".equalsIgnoreCase(newVal) && !"Ativo".equalsIgnoreCase(oldVal) && actualReturnDatePicker.getValue() != null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmação de Reabertura");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Você está reabrindo o empréstimo (status 'Ativo'). A 'Data de Devolução Real' será automaticamente limpa. Deseja continuar?");

                if (dialogStage != null) {
                    confirmation.initOwner(dialogStage);
                } else {
                    System.err.println("WARN: dialogStage is null. Alert might not be positioned correctly.");
                }

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        actualReturnDatePicker.setValue(null);
                    } else {
                        statusComboBox.setValue(oldVal);
                    }
                });
            }
        });
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

            // Popula o NOVO DATEPICKER para a data de devolução PREVISTA
            expectedReturnDatePicker.setValue(currentLoan.getExpectedReturnDate());

            // O DatePicker para a data de devolução REAL
            actualReturnDatePicker.setValue(currentLoan.getActualReturnDate());

            // Define o status no ComboBox
            if ("Atrasado".equalsIgnoreCase(currentLoan.getStatus())) {
                statusComboBox.setValue("Ativo");
            } else if (currentLoan.getStatus() != null) {
                statusComboBox.setValue(currentLoan.getStatus());
            } else {
                statusComboBox.setValue("Ativo");
            }
        }
    }

    /**
     * Limpa a data selecionada no DatePicker da Data de Devolução Real.
     */
    @FXML
    private void clearActualReturnDate() {
        actualReturnDatePicker.setValue(null);
    }


    /**
     * Salva as alterações feitas no empréstimo (data de devolução e status) no banco de dados
     * e atualiza a lista de empréstimos na tela principal.
     */
    @FXML
    private void saveEditedLoan() {
        if (currentLoan == null) {
            showAlert(Alert.AlertType.ERROR, "Erro Interno", "Nenhum empréstimo selecionado para salvar. Por favor, reinicie a operação.");
            return;
        }
        if (loanService == null || bookService == null) {
            showAlert(Alert.AlertType.ERROR, "Erro de Inicialização", "Serviços (LoanService, BookService) não foram injetados. Impossível salvar.");
            logError("Serviços de empréstimo ou livro são nulos ao tentar salvar o empréstimo.", new IllegalStateException("Serviços nulos."));
            return;
        }

        // Obtém os valores dos DatePickers e ComboBox
        LocalDate newExpectedReturnDate = expectedReturnDatePicker.getValue(); // Obtém a nova data prevista
        LocalDate newActualReturnDate = actualReturnDatePicker.getValue();
        String selectedStatusFromUI = statusComboBox.getValue();

        // VALIDAÇÃO 1: Data de devolução prevista não pode ser nula
        if (newExpectedReturnDate == null) {
            showAlert(Alert.AlertType.WARNING, "Dados Inválidos", "A 'Data de Devolução Prevista' não pode ser vazia.");
            return;
        }

        // VALIDAÇÃO 2: Data de devolução prevista não pode ser anterior à data de empréstimo.
        if (newExpectedReturnDate.isBefore(currentLoan.getLoanDate())) {
            showAlert(Alert.AlertType.WARNING, "Data Inválida", "A 'Data de Devolução Prevista' não pode ser anterior à 'Data do Empréstimo'.");
            return;
        }

        // VALIDAÇÃO 3: Data de devolução real não pode ser anterior à data de empréstimo.
        if (newActualReturnDate != null && currentLoan.getLoanDate() != null && newActualReturnDate.isBefore(currentLoan.getLoanDate())) {
            showAlert(Alert.AlertType.WARNING, "Data Inválida", "A 'Data de Devolução Real' não pode ser anterior à 'Data do Empréstimo'.");
            return;
        }

        // --- LÓGICA DE TRANSIÇÃO DE STATUS E CÁLCULO DE MULTA ---
        boolean wasReturnedOriginally = originalLoan.isReturned();
        String finalStatusToSave;
        boolean willBeReturnedNow;

        if ("Devolvido".equalsIgnoreCase(selectedStatusFromUI)) {
            // Se o usuário selecionou "Devolvido", a data de devolução real DEVE estar preenchida.
            if (newActualReturnDate == null) {
                showAlert(Alert.AlertType.WARNING, "Dados Inválidos", "Se o status é 'Devolvido', a 'Data de Devolução Real' deve ser preenchida.");
                return;
            }
            willBeReturnedNow = true;
            // Determina se o status final é "Atrasado" ou "Devolvido"
            if (newActualReturnDate.isAfter(newExpectedReturnDate)) { // Usa a NOVA data prevista para o cálculo
                finalStatusToSave = "Atrasado";
            } else {
                finalStatusToSave = "Devolvido";
            }
        } else { // O usuário selecionou "Ativo"
            willBeReturnedNow = false;
            finalStatusToSave = "Ativo";
            newActualReturnDate = null; // Garante que a data real seja NULA se o status final for Ativo
        }

        // Atualiza o objeto currentLoan
        currentLoan.setExpectedReturnDate(newExpectedReturnDate); // ATUALIZA A DATA PREVISTA
        currentLoan.setActualReturnDate(newActualReturnDate);
        currentLoan.setStatus(finalStatusToSave);
        currentLoan.setReturned(willBeReturnedNow);

        try {
            // Lógica para ajustar o contador de cópias do livro e a multa
            if (wasReturnedOriginally && !currentLoan.isReturned()) {
                // Devolvido -> Ativo (Reabrindo)
                if (currentLoan.getBook() != null) {
                    bookService.decrementAvailableCopies(currentLoan.getBook().getId());
                }
                currentLoan.setFine(0.0);
            } else if (!wasReturnedOriginally && currentLoan.isReturned()) {
                // Ativo -> Devolvido/Atrasado (Primeira devolução)
                if (currentLoan.getBook() != null) {
                    bookService.incrementAvailableCopies(currentLoan.getBook().getId());
                }
                if (newActualReturnDate != null) {
                    // Calcula multa usando a NOVA data prevista
                    double multa = loanService.calculateLateFee(newExpectedReturnDate, newActualReturnDate);
                    currentLoan.setFine(multa);
                } else {
                    currentLoan.setFine(0.0);
                }
            } else if (wasReturnedOriginally && currentLoan.isReturned()) {
                // Já estava devolvido/atrasado e continua (edição da data real ou prevista)
                if (newActualReturnDate != null) {
                    // Recalcula multa usando a NOVA data prevista e a nova data real
                    double multa = loanService.calculateLateFee(newExpectedReturnDate, newActualReturnDate);
                    currentLoan.setFine(multa);
                } else {
                    currentLoan.setFine(0.0);
                }
            }

            loanService.updateLoan(currentLoan);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo atualizado com sucesso!");
            if (mainLoanController != null) {
                mainLoanController.loadLoans();
            }
            closeEditLoanView();
        } catch (SQLException e) {
            logError("Erro ao salvar edição do empréstimo no banco de dados", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Ocorreu um erro ao salvar as alterações do empréstimo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logError("Erro de validação de negócio ao salvar empréstimo", e);
            showAlert(Alert.AlertType.WARNING, "Erro de Validação", "Falha na validação: " + e.getMessage());
        } catch (Exception e) {
            logError("Erro inesperado ao salvar edição do empréstimo", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Salvar", "Ocorreu um erro inesperado ao salvar as alterações do empréstimo.");
        }
    }

    @FXML
    private void cancelEditLoan() {
        closeEditLoanView();
    }

    private void closeEditLoanView() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
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