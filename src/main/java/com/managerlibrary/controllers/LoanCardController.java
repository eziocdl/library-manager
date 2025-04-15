package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

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

    private Loan loan;
    private LoanController loanController;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setLoan(Loan loan) {
        this.loan = loan;
        displayLoanDetails();
    }

    public void setLoanController(LoanController loanController) {
        this.loanController = loanController;
    }

    public void displayLoanDetails() {
        if (loan != null) {
            // Supondo que você tenha acesso ao nome e CPF do usuário e título do livro
            // Isso pode exigir que você carregue esses detalhes no LoanController ou
            // adicione esses atributos à entidade Loan (como discutimos antes).
            userNameLabel.setText("Nome do Usuário (ID: " + loan.getUserId() + ")"); // Placeholder
            userCpfLabel.setText("CPF do Usuário (Simulado)"); // Placeholder
            bookTitleLabel.setText("Título do Livro (ID: " + loan.getBookId() + ")"); // Placeholder
            loanDateLabel.setText(loan.getLoanDate().format(dateFormatter));
            returnDateLabel.setText(loan.getReturnDate().format(dateFormatter));

            if (loan.getActualReturnDate() != null) {
                actualReturnDateLabel.setText("Devolvido em: " + loan.getActualReturnDate().format(dateFormatter));
                actualReturnDateLabel.setVisible(true);
                returnButton.setVisible(false); // Esconde o botão de devolver se já foi devolvido
            } else {
                actualReturnDateLabel.setVisible(false);
                returnButton.setVisible(true);
            }

            if (loan.getFine() > 0) {
                fineLabel.setText(String.format("Multa: R$ %.2f", loan.getFine()));
                fineLabel.setVisible(true);
            } else {
                fineLabel.setVisible(false);
            }
        }
    }

    @FXML
    private void markAsReturned() {
        if (loan != null && loanController != null) {
            loanController.markLoanAsReturned(loan);
        }
    }

    @FXML
    private void showLoanDetails() {
        if (loan != null && loanController != null) {
            loanController.showLoanDetails(loan);
        }
    }
}
