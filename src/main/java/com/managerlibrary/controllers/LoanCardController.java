package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent; // Removido: Não utilizado diretamente.
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
    private VBox loanCard; // Mantido, pode ser útil para estilos ou layout
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
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        } else {
            // Limpa os campos se o empréstimo for nulo
            bookCoverImageView.setImage(null);
            bookTitleLabel.setText("N/A");
            userNameLabel.setText("N/A");
            userCpfLabel.setText("N/A");
            loanDateLabel.setText("N/A");
            returnDateLabel.setText("N/A");
            actualReturnDateLabel.setVisible(false);
            fineLabel.setVisible(false);
            returnButton.setVisible(false);
        }
    }

    /**
     * Carrega a capa do livro, se disponível, e exibe no ImageView.
     * Se o arquivo não for encontrado ou ocorrer um erro, uma imagem padrão é exibida.
     */
    private void loadBookCover() {
        Image imageToSet = null;

        // Limpa a imagem anterior
        bookCoverImageView.setImage(null);

        // Tenta carregar a imagem do caminho do livro
        if (loan.getBook() != null && loan.getBook().getCoverImagePath() != null && !loan.getBook().getCoverImagePath().isEmpty()) {
            try {
                File file = new File(loan.getBook().getCoverImagePath());
                if (file.exists()) {
                    imageToSet = new Image(file.toURI().toString());
                    if (!imageToSet.isError()) {
                        bookCoverImageView.setImage(imageToSet);
                        return; // Imagem carregada com sucesso, sai do método
                    } else {
                        logError("Erro ao carregar imagem do livro do arquivo (Image.isError()): " + loan.getBook().getCoverImagePath(), null);
                    }
                } else {
                    // logError("Arquivo de imagem não encontrado: " + loan.getBook().getCoverImagePath(), null); // Para debug
                }
            } catch (Exception e) {
                logError("Erro ao carregar imagem do livro do arquivo: " + loan.getBook().getCoverImagePath(), e);
            }
        }

        // Se a imagem do livro não foi carregada ou houve erro, tenta uma imagem padrão
        try {
            // Certifique-se de que o caminho para a imagem padrão está correto no seu projeto
            imageToSet = new Image(getClass().getResourceAsStream("/images/default_book_icon.png"));
            if (imageToSet.isError()) {
                throw new IOException("Erro ao carregar default_book_icon.png");
            }
        } catch (IOException e) {
            logError("Erro ao carregar imagem padrão /images/default_book_icon.png", e);
            imageToSet = null; // Não foi possível carregar nenhuma imagem
        }
        bookCoverImageView.setImage(imageToSet);
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
            userNameLabel.setText("Nome do Usuário: " + loan.getUser().getName());
            userCpfLabel.setText(loan.getUser().getCpf() != null && !loan.getUser().getCpf().isEmpty() ? "CPF: " + loan.getUser().getCpf() : "CPF: N/A");
        } else {
            userNameLabel.setText("Usuário: N/A");
            userCpfLabel.setText("CPF: N/A");
        }
    }

    /**
     * Exibe as datas de empréstimo e devolução, e a multa (se houver).
     */
    private void displayDatesAndFine() {
        loanDateLabel.setText("Empréstimo: " + (loan.getLoanDate() != null ? loan.getLoanDate().format(dateFormatter) : "N/A"));
        returnDateLabel.setText("Devolução Prevista: " + (loan.getReturnDate() != null ? loan.getReturnDate().format(dateFormatter) : "N/A"));

        if (loan.getActualReturnDate() != null) {
            actualReturnDateLabel.setText("Devolvido em: " + loan.getActualReturnDate().format(dateFormatter));
            actualReturnDateLabel.setVisible(true);
        } else {
            actualReturnDateLabel.setVisible(false);
        }

        fineLabel.setVisible(loan.getFine() > 0);
        if (loan.getFine() > 0) {
            fineLabel.setText(String.format("Multa: R$ %.2f", loan.getFine()));
        } else {
            fineLabel.setText(""); // Limpa o texto se não houver multa
        }
    }

    /**
     * Atualiza a visibilidade do botão de devolver com base na data de devolução real.
     */
    private void updateReturnButtonVisibility() {
        // O botão de "Devolver" só deve ser visível se o livro ainda não foi devolvido
        returnButton.setVisible(loan.getActualReturnDate() == null);
        // Opcional: Desabilitar botões de edição/remoção para empréstimos já devolvidos
        boolean isReturned = loan.getActualReturnDate() != null;
        editButton.setDisable(isReturned);
        removeButton.setDisable(isReturned);
    }

    /**
     * Chama o método no LoanController para marcar o empréstimo como devolvido.
     */
    @FXML
    private void markAsReturned() {
        if (loan != null && loanController != null) {
            loanController.markLoanAsReturned(loan);
        } else {
            logError("Não foi possível marcar empréstimo como devolvido. Dependências ausentes.", null);
        }
    }

    /**
     * Chama o método no LoanController para exibir os detalhes completos do empréstimo.
     */
    @FXML
    private void showLoanDetails() {
        if (loan != null && loanController != null) {
            loanController.showLoanDetails(loan);
        } else {
            logError("Não foi possível mostrar detalhes do empréstimo. Dependências ausentes.", null);
        }
    }

    /**
     * Carrega a tela de edição do empréstimo em um diálogo modal, passando os dados necessários.
     */
    @FXML
    private void editLoan() {
        if (loan == null || loanController == null) {
            logError("Não foi possível carregar a tela de edição. Empréstimo ou LoanController é nulo.", null);
            showAlert("Erro", "Não foi possível carregar a tela de edição do empréstimo.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditLoanView.fxml"));
            VBox root = loader.load(); // loader.load() retorna Object, VBox é mais específico

            EditLoanViewController controller = loader.getController();
            controller.setLoan(loan);
            // É crucial que o LoanController tenha esses serviços disponíveis
            controller.setLoanService(loanController.getLoanService());
            // Os serviços de livro e usuário não são estritamente necessários para EditLoanView
            // a menos que você edite informações de livro/usuário diretamente lá.
            // Se não forem usados, considere removê-los do EditLoanViewController.
            controller.setBookService(loanController.getBookService()); // Opcional, dependendo da necessidade real
            controller.setUserService(loanController.getUserService()); // Opcional, dependendo da necessidade real
            controller.setMainLoanController(loanController);

            Stage stage = new Stage();
            stage.setTitle("Editar Empréstimo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            // Definir o proprietário do palco do diálogo, para centralização
            stage.initOwner(loanCard.getScene().getWindow());
            controller.setDialogStage(stage); // Passa o stage para o controlador de edição
            stage.showAndWait();

        } catch (IOException e) {
            logError("Erro ao carregar tela de edição de empréstimo", e);
            showAlert("Erro ao Carregar", "Não foi possível carregar a tela de edição do empréstimo.");
        }
    }

    /**
     * Exibe um diálogo de confirmação para remover o empréstimo e chama o método no LoanController para removê-lo.
     */
    @FXML
    private void removeLoan() {
        if (loan == null || loanController == null) {
            logError("Não foi possível remover o empréstimo. Empréstimo ou LoanController é nulo.", null);
            showAlert("Erro", "Não foi possível remover o empréstimo.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remover Empréstimo");
        alert.setHeaderText("Confirmar Remoção");
        alert.setContentText("Tem certeza que deseja remover o empréstimo do livro \"" + loan.getBook().getTitle() + "\" para o usuário \"" + loan.getUser().getName() + "\"?");
        // Definir o proprietário do alerta para centralização
        alert.initOwner(loanCard.getScene().getWindow());


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            loanController.removeLoan(loan);
        }
    }

    /**
     * Exibe um diálogo de alerta com o tipo, título e conteúdo especificados.
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
     * @param e       A exceção ocorrida, pode ser nula.
     */
    private void logError(String message, Exception e) {
        System.err.print(message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }
}