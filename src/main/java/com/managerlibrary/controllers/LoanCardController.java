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
            // Exibe o Nome e ID do Usuário
            if (loan.getUser() != null) {
                userNameLabel.setText("Nome do Usuário: " + loan.getUser().getName() + " (ID: " + loan.getUser().getId() + ")");
                // Exibe o CPF do Usuário
                if (loan.getUser().getCpf() != null && !loan.getUser().getCpf().isEmpty()) {
                    userCpfLabel.setText("CPF do Usuário: " + loan.getUser().getCpf());
                } else {
                    userCpfLabel.setText("CPF do Usuário: N/A");
                }
            } else {
                userNameLabel.setText("Nome do Usuário: N/A (ID: N/A)");
                userCpfLabel.setText("CPF do Usuário: N/A");
            }

            // Exibe o Título e ID do Livro
            if (loan.getBook() != null) {
                bookTitleLabel.setText("Título do Livro (ID: " + loan.getBook().getId() + ")");
            } else {
                bookTitleLabel.setText("Título do Livro (ID: N/A)");
            }

            loanDateLabel.setText(loan.getLoanDate() != null ? loan.getLoanDate().format(dateFormatter) : "N/A");
            returnDateLabel.setText(loan.getReturnDate() != null ? loan.getReturnDate().format(dateFormatter) : "N/A");

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