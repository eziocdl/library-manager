package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controlador para o card individual de um empréstimo exibido na lista de empréstimos.
 * Responsável por exibir os detalhes do empréstimo e fornecer ações como marcar como
 * devolvido, visualizar detalhes, editar e remover o empréstimo.
 */
public class LoanCardController {

    @FXML
    private VBox loanCard;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userCpfLabel;
    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label loanDateLabel;
    @FXML
    private Label returnDateLabel;
    @FXML
    private Label actualReturnDateLabel;
    @FXML
    private Label fineLabel;
    @FXML
    private Button returnButton;
    @FXML
    private ImageView bookCoverImageView;
    @FXML
    private Button detailsButton;
    @FXML
    private Button editButton;
    @FXML
    private Button removeButton;

    private Loan loan;
    private LoanController loanController;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Define o empréstimo a ser exibido neste card e atualiza as informações visuais.
     *
     * @param loan O empréstimo a ser exibido.
     */
    public void setLoan(Loan loan) {
        this.loan = loan;
        displayLoanDetails();
    }

    /**
     * Define o controlador da tela principal de empréstimos para permitir ações na lista.
     *
     * @param loanController O controlador da tela principal de empréstimos.
     */
    public void setLoanController(LoanController loanController) {
        this.loanController = loanController;
    }

    /**
     * Atualiza os elementos visuais do card com os detalhes do empréstimo.
     * Exibe informações do usuário, livro, datas de empréstimo e devolução,
     * data de devolução real (se houver), multa (se houver) e a capa do livro.
     */
    public void displayLoanDetails() {
        if (loan != null) {
            loadBookCover();
            displayBookInfo();
            displayUserInfo();
            displayDatesAndFine();
            updateReturnButtonVisibility();
        }
    }

    /**
     * Carrega a capa do livro, se disponível, e exibe no ImageView.
     * Se o arquivo não for encontrado ou ocorrer um erro, o ImageView é limpo
     * e uma mensagem de erro é impressa no console.
     */
    private void loadBookCover() {
        bookCoverImageView.setImage(null); // Limpa a imagem anterior
        if (loan.getBook() != null && loan.getBook().getCoverImagePath() != null && !loan.getBook().getCoverImagePath().isEmpty()) {
            try {
                File file = new File(loan.getBook().getCoverImagePath());
                if (file.exists()) {
                    bookCoverImageView.setImage(new Image(file.toURI().toString()));
                } else {
                    System.err.println("Arquivo de imagem não encontrado: " + loan.getBook().getCoverImagePath());
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem do livro: " + loan.getBook().getCoverImagePath() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Exibe o título do livro no label correspondente.
     */
    private void displayBookInfo() {
        bookTitleLabel.setText(loan.getBook() != null ? loan.getBook().getTitle() : "Título do Livro: N/A");
    }

    /**
     * Exibe o nome e CPF do usuário no label correspondente.
     */
    private void displayUserInfo() {
        if (loan.getUser() != null) {
            userNameLabel.setText("Nome do Usuário: " + loan.getUser().getName() + " (ID: " + loan.getUser().getId() + ")");
            userCpfLabel.setText(loan.getUser().getCpf() != null && !loan.getUser().getCpf().isEmpty() ? "CPF do Usuário: " + loan.getUser().getCpf() : "CPF do Usuário: N/A");
        } else {
            userNameLabel.setText("Nome do Usuário: N/A (ID: N/A)");
            userCpfLabel.setText("CPF do Usuário: N/A");
        }
    }

    /**
     * Exibe as datas de empréstimo e devolução, e a multa (se houver).
     */
    private void displayDatesAndFine() {
        loanDateLabel.setText(loan.getLoanDate() != null ? loan.getLoanDate().format(dateFormatter) : "N/A");
        returnDateLabel.setText(loan.getReturnDate() != null ? loan.getReturnDate().format(dateFormatter) : "N/A");

        if (loan.getActualReturnDate() != null) {
            actualReturnDateLabel.setText("Devolvido em: " + loan.getActualReturnDate().format(dateFormatter));
            actualReturnDateLabel.setVisible(true);
        } else {
            actualReturnDateLabel.setVisible(false);
        }

        fineLabel.setVisible(loan.getFine() > 0);
        if (loan.getFine() > 0) {
            fineLabel.setText(String.format("Multa: R$ %.2f", loan.getFine()));
        }
    }

    /**
     * Atualiza a visibilidade do botão de devolver com base na data de devolução real.
     */
    private void updateReturnButtonVisibility() {
        returnButton.setVisible(loan.getActualReturnDate() == null);
    }

    /**
     * Chama o método no LoanController para marcar o empréstimo como devolvido.
     */
    @FXML
    private void markAsReturned() {
        if (loan != null && loanController != null) {
            loanController.markLoanAsReturned(loan);
        }
    }

    /**
     * Chama o método no LoanController para exibir os detalhes completos do empréstimo.
     */
    @FXML
    private void showLoanDetails() {
        if (loan != null && loanController != null) {
            loanController.showLoanDetails(loan);
        }
    }

    /**
     * Carrega a tela de edição do empréstimo em um diálogo modal, passando os dados necessários.
     */
    @FXML
    private void editLoan() {
        if (loan != null && loanController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditLoanView.fxml"));
                Parent root = loader.load();

                EditLoanViewController controller = loader.getController();
                controller.setLoan(loan);
                controller.setLoanService(loanController.getLoanService());
                controller.setBookService(loanController.getBookService());
                controller.setUserService(loanController.getUserService());
                controller.setMainLoanController(loanController);

                Stage stage = new Stage();
                stage.setTitle("Editar Empréstimo");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                logError("Erro ao carregar tela de edição de empréstimo", e);
                showAlert("Erro ao Carregar", "Não foi possível carregar a tela de edição do empréstimo.");
            }
        }
    }

    /**
     * Exibe um diálogo de confirmação para remover o empréstimo e chama o método no LoanController para removê-lo.
     */
    @FXML
    private void removeLoan() {
        if (loan != null && loanController != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remover Empréstimo");
            alert.setHeaderText("Confirmar Remoção");
            alert.setContentText("Tem certeza que deseja remover este empréstimo?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                loanController.removeLoan(loan);
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