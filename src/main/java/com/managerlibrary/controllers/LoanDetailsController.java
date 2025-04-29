package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class LoanDetailsController {

    private Loan loan;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private Label loanIdLabel;
    @FXML
    private Label bookIdLabel;
    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userCPFLabel;
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
            if (loan.getBook() != null) {
                bookIdLabel.setText(String.valueOf(loan.getBook().getId()));
                bookTitleLabel.setText(loan.getBook().getTitle());
            } else {
                bookIdLabel.setText("N/A");
                bookTitleLabel.setText("N/A");
            }
            if (loan.getUser() != null) {
                userIdLabel.setText(String.valueOf(loan.getUser().getId()));
                userNameLabel.setText(loan.getUser().getName());
                userCPFLabel.setText(loan.getUser().getCpf());
            } else {
                userIdLabel.setText("N/A");
                userNameLabel.setText("N/A");
                userCPFLabel.setText("N/A");
            }
            loanDateLabel.setText(loan.getLoanDate() != null ? loan.getLoanDate().format(dateFormatter) : "N/A");
            returnDateLabel.setText(loan.getReturnDate() != null ? loan.getReturnDate().format(dateFormatter) : "N/A");
            actualReturnDateLabel.setText(loan.getActualReturnDate() != null ? loan.getActualReturnDate().format(dateFormatter) : "Não Devolvido");
            statusLabel.setText(loan.getStatus());
            fineLabel.setText(String.format("%.2f", loan.getFine()));
        }
    }

    @FXML
    private void closeLoanDetailsView() {
        Stage stage = (Stage) loanIdLabel.getScene().getWindow();
        stage.close();
    }
}