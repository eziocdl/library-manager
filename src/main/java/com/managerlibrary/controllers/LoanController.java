package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import com.managerlibrary.services.LoanService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LoanController {

    @FXML
    private VBox loansVBox; // Alterado para corresponder ao fx:id no LoanView.fxml

    private LoanService loanService;
    private ObservableList<Loan> allLoans = FXCollections.observableArrayList();
    private RootLayoutController rootLayoutController;

    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
        loadAllLoans(); // Carrega todos os empréstimos ao inicializar
    }

    public void setRootLayoutController(RootLayoutController rootLayoutController) {
        this.rootLayoutController = rootLayoutController;
    }

    @FXML
    public void initialize() {
        // Qualquer inicialização adicional
    }

    public void loadAllLoans() {
        try {
            List<Loan> loans = loanService.getAllLoansWithDetails();
            allLoans.setAll(loans); // Preenche a lista observável com todos os empréstimos
            displayLoans(allLoans); // Exibe todos os empréstimos inicialmente
        } catch (SQLException e) {
            showAlert("Erro ao carregar empréstimos", "Não foi possível carregar os empréstimos do banco de dados.");
            e.printStackTrace();
        }
    }

    private void displayLoans(ObservableList<Loan> loans) {
        loansVBox.getChildren().clear();
        for (Loan loan : loans) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanCardView.fxml"));
                VBox loanCard = loader.load();
                LoanCardController controller = loader.getController();
                controller.setLoan(loan);
                controller.setLoanController(this);
                loansVBox.getChildren().add(loanCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void showAddLoanView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddLoanView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Adicionar Novo Empréstimo");
            stage.initModality(Modality.APPLICATION_MODAL);
            AddLoanViewController controller = loader.getController();
            controller.setLoanService(loanService);
            // Supondo que você tenha BookService e UserService injetados no LoanController
            // Se não tiver, você precisará injetá-los aqui ou passar de outra forma
            // controller.setBookService(bookService);
            // controller.setUserService(userService);
            controller.setMainLoanController(this);
            controller.setDialogStage(stage);
            stage.showAndWait();
            loadAllLoans(); // Recarrega os empréstimos após adicionar um novo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void markLoanAsReturned(Loan loan) {
        try {
            loanService.markAsReturned(loan.getId(), LocalDate.now());
            loadAllLoans(); // Recarrega a lista após a devolução
        } catch (SQLException e) {
            showAlert("Erro ao marcar como devolvido", "Não foi possível marcar o empréstimo como devolvido.");
            e.printStackTrace();
        }
    }

    public void showLoanDetails(Loan loan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoanDetailsView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Detalhes do Empréstimo");
            stage.initModality(Modality.APPLICATION_MODAL);
            LoanDetailsController controller = loader.getController();
            controller.setLoan(loan);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void filterLoansByAll(ActionEvent event) {
        displayLoans(allLoans);
    }

    @FXML
    void filterLoansByActive(ActionEvent event) {
        ObservableList<Loan> activeLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(activeLoans);
    }

    @FXML
    void filterLoansByReturned(ActionEvent event) {
        ObservableList<Loan> returnedLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() != null)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(returnedLoans);
    }

    @FXML
    void filterLoansByOverdue(ActionEvent event) {
        LocalDate today = LocalDate.now();
        ObservableList<Loan> overdueLoans = allLoans.stream()
                .filter(loan -> loan.getActualReturnDate() == null && today.isAfter(loan.getReturnDate()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        displayLoans(overdueLoans);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}