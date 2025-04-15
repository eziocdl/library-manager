package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoanDetailsController {

    private Loan loan;

    @FXML
    private Label loanIdLabel;
    @FXML
    private Label bookIdLabel;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label loanDateLabel;
    @FXML
    private Label returnDateLabel;
    @FXML
    private Label actualReturnDateLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label fineLabel;

    public void setLoan(Loan loan) {
        this.loan = loan;
        displayLoanDetails();
    }

    public void displayLoanDetails() {
        if (loan != null) {
            loanIdLabel.setText(String.valueOf(loan.getId()));
            bookIdLabel.setText(String.valueOf(loan.getBookId()));
            userIdLabel.setText(String.valueOf(loan.getUserId()));
            loanDateLabel.setText(loan.getLoanDate().toString());
            returnDateLabel.setText(loan.getReturnDate().toString());
            actualReturnDateLabel.setText(loan.getActualReturnDate() != null ? loan.getActualReturnDate().toString() : "Não Devolvido");
            statusLabel.setText(loan.getStatus());
            fineLabel.setText(String.format("%.2f", loan.getFine()));
        }
    }

    @FXML
    private void closeLoanDetailsView() {
        // Obtém a janela atual e a fecha
        Stage stage = (Stage) loanIdLabel.getScene().getWindow();
        stage.close();
    }
}

