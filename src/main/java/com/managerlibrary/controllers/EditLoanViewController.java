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

    private Loan currentLoan;
    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private LoanController mainLoanController;
    private Stage dialogStage;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setLoan(Loan loan) {
        this.currentLoan = loan;
        populateFields();
    }

    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMainLoanController(LoanController mainLoanController) {
        this.mainLoanController = mainLoanController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    public void initialize() {
        // Define as opções para o ComboBox de status
        statusComboBox.setItems(FXCollections.observableArrayList("Ativo", "Devolvido", "Atrasado"));
    }

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
                e.printStackTrace();
                // Lógica para exibir mensagem de erro ao salvar
            }
        }
    }

    @FXML
    private void cancelEditLoan() {
        closeEditLoanView();
    }

    private void closeEditLoanView() {
        Stage stage = (Stage) loanIdLabel.getScene().getWindow();
        stage.close();
    }
}