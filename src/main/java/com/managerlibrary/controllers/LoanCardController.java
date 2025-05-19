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
            // Exibe a capa do livro
            if (loan.getBook() != null && loan.getBook().getCoverImagePath() != null && !loan.getBook().getCoverImagePath().isEmpty()) {
                try {
                    File file = new File(loan.getBook().getCoverImagePath());
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        bookCoverImageView.setImage(image);
                    } else {
                        // Se o arquivo não existir no caminho do banco de dados,
                        // deixa o ImageView vazio ou exibe uma mensagem de erro no console.
                        bookCoverImageView.setImage(null);
                        System.err.println("Arquivo de imagem não encontrado: " + loan.getBook().getCoverImagePath());
                    }
                } catch (Exception e) {
                    // Lidar com erro ao carregar a imagem
                    bookCoverImageView.setImage(null);
                    System.err.println("Erro ao carregar imagem do livro: " + loan.getBook().getCoverImagePath() + " - " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Se não houver URL no banco de dados, deixe o ImageView vazio.
                bookCoverImageView.setImage(null);
            }

            // Exibe o Título do Livro
            if (loan.getBook() != null) {
                bookTitleLabel.setText(loan.getBook().getTitle());
            } else {
                bookTitleLabel.setText("Título do Livro: N/A");
            }

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

    @FXML
    private void editLoan() {
        if (loan != null && loanController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditLoanView.fxml"));
                Parent root = loader.load();

                EditLoanViewController controller = loader.getController();
                controller.setLoan(loan);
                controller.setLoanService(loanController.getLoanService()); // Passa o LoanService
                controller.setBookService(loanController.getBookService()); // Passa o BookService
                controller.setUserService(loanController.getUserService()); // Passa o UserService
                controller.setMainLoanController(loanController); // Passa o LoanController para atualizar a lista

                Stage stage = new Stage();
                stage.setTitle("Editar Empréstimo");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                // Lógica para exibir mensagem de erro ao carregar a tela de edição
            }
        }
    }

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
}