package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * Controlador para a tela de detalhes de um empréstimo. Exibe informações
 * completas sobre o empréstimo selecionado.
 */
public class LoanDetailsController {

    private Loan loan;
    private BookService bookService;
    private UserService userService;
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

    /**
     * Define o empréstimo a ser exibido e atualiza os campos da tela com seus detalhes.
     *
     * @param loan O empréstimo a ser exibido.
     */
    public void setLoan(Loan loan) {
        this.loan = loan;
        displayLoanDetails();
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
     * Preenche os labels da tela com os detalhes do empréstimo.
     * Se o livro ou usuário associado ao empréstimo for nulo, exibe "N/A".
     * Formata as datas usando o padrão definido.
     */
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

    /**
     * Fecha a tela de detalhes do empréstimo.
     */
    @FXML
    private void closeLoanDetailsView() {
        Stage stage = (Stage) loanIdLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Método de inicialização do controlador. (Atualmente vazio, pode ser usado para inicializações específicas da tela).
     */
    @FXML
    public void initialize() {
        // Inicializações da tela de detalhes, se necessário
    }
}