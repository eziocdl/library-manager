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

    private Loan currentLoan;     // O empréstimo atualmente sendo editado
    private Loan originalLoan;    // Para armazenar o estado original do empréstimo para comparação
    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
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
        // Criar uma cópia do empréstimo original para comparação posterior
        // Usa o construtor completo da classe Loan, incluindo o campo 'returned'
        this.originalLoan = new Loan(loan.getId(), loan.getBook(), loan.getUser(),
                loan.getLoanDate(), loan.getExpectedReturnDate(),
                loan.getActualReturnDate(), loan.getStatus(), loan.getFine(), loan.isReturned());
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
     * Define o serviço de livros.
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }

    /**
     * Define o serviço de usuários.
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
        // As opções de status que o usuário pode SETAR diretamente.
        // "Atrasado" é tipicamente um status calculado, não definido pelo usuário.
        // "Perdido" pode ter uma lógica de negócio mais complexa.
        statusComboBox.setItems(FXCollections.observableArrayList("Ativo", "Devolvido"));
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

            // O DatePicker é para a data de devolução REAL (actualReturnDate)
            // Se o empréstimo foi devolvido, mostra a data. Caso contrário, deixa vazio.
            if (currentLoan.getActualReturnDate() != null) {
                returnDatePicker.setValue(currentLoan.getActualReturnDate());
            } else {
                returnDatePicker.setValue(null); // Permite ao usuário definir ou deixar nulo
            }

            if (currentLoan.getStatus() != null) {
                statusComboBox.setValue(currentLoan.getStatus());
            } else {
                statusComboBox.setValue("Ativo"); // Valor padrão se o status for nulo
            }
        }
    }

    /**
     * Salva as alterações feitas no empréstimo (data de devolução e status) no banco de dados
     * e atualiza a lista de empréstimos na tela principal.
     */
    @FXML
    private void saveEditedLoan() {
        // Validações iniciais de serviços
        if (currentLoan == null) {
            showAlert("Erro Interno", "Nenhum empréstimo selecionado para salvar. Por favor, reinicie a operação.");
            return;
        }
        if (loanService == null || bookService == null) {
            showAlert("Erro de Inicialização", "Serviços (LoanService, BookService) não foram injetados. Impossível salvar.");
            logError("Serviços de empréstimo ou livro são nulos ao tentar salvar o empréstimo.", new IllegalStateException("Serviços nulos."));
            return;
        }

        // Obtém os novos valores dos campos da UI
        LocalDate newActualReturnDate = returnDatePicker.getValue();
        String newStatus = statusComboBox.getValue();

        // Validação de consistência entre status e data de devolução real
        if ("Devolvido".equalsIgnoreCase(newStatus) && newActualReturnDate == null) {
            showAlert(Alert.AlertType.WARNING, "Dados Inválidos", "Se o status é 'Devolvido', a 'Data de Devolução Real' deve ser preenchida.");
            return;
        }
        if ("Ativo".equalsIgnoreCase(newStatus) && newActualReturnDate != null) {
            showAlert(Alert.AlertType.WARNING, "Dados Inválidos", "Se o status é 'Ativo', a 'Data de Devolução Real' não pode estar preenchida.");
            return;
        }

        // Validação de data de devolução real vs data de empréstimo (se a data real for preenchida)
        if (newActualReturnDate != null && currentLoan.getLoanDate() != null && newActualReturnDate.isBefore(currentLoan.getLoanDate())) {
            showAlert(Alert.AlertType.WARNING, "Data Inválida", "A data de devolução real não pode ser anterior à data de empréstimo.");
            return;
        }


        // Salva o estado original para referência antes de atualizar currentLoan
        // Usa originalLoan.getActualReturnDate() e originalLoan.getStatus() para verificar o estado anterior
        boolean wasReturnedOriginally = originalLoan.getActualReturnDate() != null && "Devolvido".equalsIgnoreCase(originalLoan.getStatus());
        // A flag 'returned' da Loan original também pode ser usada para maior robustez,
        // mas o 'actualReturnDate != null' combinado com o status já captura o estado.
        // boolean wasReturnedOriginally = originalLoan.isReturned(); // Alternativa se confiar 100% no campo boolean

        // Determina o estado futuro com base nos novos valores da UI
        boolean willBeReturnedNow = newActualReturnDate != null && "Devolvido".equalsIgnoreCase(newStatus);


        // ATUALIZA O OBJETO currentLoan COM OS NOVOS VALORES DOS CAMPOS DA UI
        currentLoan.setActualReturnDate(newActualReturnDate);
        currentLoan.setStatus(newStatus);
        // O campo 'returned' na entidade Loan deve ser atualizado aqui também.
        // Ele deve ser true se 'actualReturnDate' não for nula e o status for "Devolvido".
        currentLoan.setReturned(willBeReturnedNow);


        try {
            // Lógica para ajustar o contador de cópias do livro e a multa
            if (wasReturnedOriginally && !willBeReturnedNow) {
                // Cenário 1: Empréstimo estava DEVOLVIDO e agora está sendo REABERTO (ou definido para Ativo)
                if (currentLoan.getBook() != null) {
                    int currentAvailable = currentLoan.getBook().getAvailableCopies();
                    currentLoan.getBook().setAvailableCopies(currentAvailable - 1); // Decrementa a cópia
                    bookService.updateBook(currentLoan.getBook()); // Salva a alteração no livro
                }
                currentLoan.setFine(0.0); // Zera a multa ao reabrir
            } else if (!wasReturnedOriginally && willBeReturnedNow) {
                // Cenário 2: Empréstimo estava ATIVO e agora está sendo DEVOLVIDO
                if (currentLoan.getBook() != null) {
                    int currentAvailable = currentLoan.getBook().getAvailableCopies();
                    currentLoan.getBook().setAvailableCopies(currentAvailable + 1); // Incrementa a cópia
                    bookService.updateBook(currentLoan.getBook()); // Salva a alteração no livro
                }
                // Recalcula a multa
                double multa = loanService.calculateLateFee(currentLoan.getExpectedReturnDate(), currentLoan.getActualReturnDate());
                currentLoan.setFine(multa);
            } else if (willBeReturnedNow) {
                // Cenário 3: Empréstimo já estava DEVOLVIDO e continua DEVOLVIDO
                // (pode ser que a data real de devolução ou status foi apenas ajustada)
                // Recalcula a multa para garantir consistência.
                double multa = loanService.calculateLateFee(currentLoan.getExpectedReturnDate(), currentLoan.getActualReturnDate());
                currentLoan.setFine(multa);
            }
            // Se o empréstimo permaneceu Ativo, não faz nada com as cópias ou multa (a multa já seria 0)


            loanService.updateLoan(currentLoan); // Salva as alterações do empréstimo no banco de dados

            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Empréstimo atualizado com sucesso!");
            if (mainLoanController != null) {
                mainLoanController.loadLoans(); // Atualiza a lista principal após a edição
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