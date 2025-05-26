package com.managerlibrary.controllers;

import com.managerlibrary.entities.Loan;
import com.managerlibrary.services.BookService;
import com.managerlibrary.services.LoanService;
import com.managerlibrary.services.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.Objects;
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
    private Label returnDateLabel; // Este agora é a ExpectedReturnDate
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
    private LoanController loanController; // Referência ao LoanController pai
    private LoanService loanService; // NOVO: Serviço de empréstimos
    private BookService bookService; // NOVO: Serviço de livros
    private UserService userService; // NOVO: Serviço de usuários

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Define o empréstimo a ser exibido neste card e atualiza as informações visuais.
     *
     * @param loan O empréstimo a ser exibido.
     */
    public void setLoan(Loan loan) {
        this.loan = Objects.requireNonNull(loan, "Empréstimo não pode ser nulo.");
        displayLoanDetails();
    }

    /**
     * Define o controlador da tela principal de empréstimos para permitir ações na lista.
     *
     * @param loanController O controlador da tela principal de empréstimos.
     */
    public void setLoanController(LoanController loanController) {
        this.loanController = Objects.requireNonNull(loanController, "LoanController não pode ser nulo.");
    }

    // --- NOVOS MÉTODOS DE INJEÇÃO DE SERVIÇOS ---
    /**
     * Define o serviço de empréstimos para este controlador.
     *
     * @param loanService O serviço de empréstimos a ser utilizado.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = Objects.requireNonNull(loanService, "LoanService não pode ser nulo.");
    }

    /**
     * Define o serviço de livros para este controlador.
     *
     * @param bookService O serviço de livros a ser utilizado.
     */
    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "BookService não pode ser nulo.");
    }

    /**
     * Define o serviço de usuários para este controlador.
     *
     * @param userService O serviço de usuários a ser utilizado.
     */
    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "UserService não pode ser nulo.");
    }
    // --- FIM DOS NOVOS MÉTODOS ---

    /**
     * Método de inicialização. Chamado automaticamente após o FXML ser carregado.
     * Geralmente usado para configurar listeners ou inicializações que não dependem de dados.
     */
    @FXML
    private void initialize() {
        // Nada específico para inicializar aqui, pois os dados são setados via setLoan()
        // e os serviços via os setters.
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
            // Limpa os campos se o empréstimo for nulo ou inválido
            bookCoverImageView.setImage(null);
            bookTitleLabel.setText("Título do Livro: N/A");
            userNameLabel.setText("Usuário: N/A");
            userCpfLabel.setText("CPF: N/A");
            loanDateLabel.setText("Empréstimo: N/A");
            returnDateLabel.setText("Devolução Prevista: N/A");
            actualReturnDateLabel.setVisible(false);
            fineLabel.setVisible(false);
            returnButton.setVisible(false);
            detailsButton.setDisable(true);
            editButton.setDisable(true);
            removeButton.setDisable(true);
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
            imageToSet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default_book_icon.png"), "Recurso default_book_icon.png não encontrado."));
            if (imageToSet.isError()) {
                throw new IOException("Erro ao carregar default_book_icon.png do stream.");
            }
        } catch (Exception e) { // Usar Exception para pegar NullPointerException também
            logError("Erro ao carregar imagem padrão /images/default_book_icon.png", e);
            imageToSet = null; // Não foi possível carregar nenhuma imagem
        }
        bookCoverImageView.setImage(imageToSet);
    }

    /**
     * Exibe o título do livro no label correspondente.
     */
    private void displayBookInfo() {
        bookTitleLabel.setText("Livro: " + (loan.getBook() != null ? loan.getBook().getTitle() : "N/A"));
    }

    /**
     * Exibe o nome e CPF do usuário no label correspondente.
     */
    private void displayUserInfo() {
        if (loan.getUser() != null) {
            userNameLabel.setText("Usuário: " + loan.getUser().getName());
            userCpfLabel.setText(loan.getUser().getCpf() != null && !loan.getUser().getCpf().isEmpty() ? "CPF: " + loan.getUser().getCpf() : "CPF: N/A");
        } else {
            userNameLabel.setText("Usuário: N/A");
            userCpfLabel.setText("CPF: N/A");
        }
    }

    /**
     * Exibe as datas de empréstimo e devolução (prevista), e a multa (se houver).
     */
    private void displayDatesAndFine() {
        loanDateLabel.setText("Empréstimo: " + (loan.getLoanDate() != null ? loan.getLoanDate().format(dateFormatter) : "N/A"));
        // CORREÇÃO AQUI: Usar getExpectedReturnDate()
        returnDateLabel.setText("Devolução Prevista: " + (loan.getExpectedReturnDate() != null ? loan.getExpectedReturnDate().format(dateFormatter) : "N/A"));

        if (loan.getActualReturnDate() != null) {
            actualReturnDateLabel.setText("Devolvido em: " + loan.getActualReturnDate().format(dateFormatter));
            actualReturnDateLabel.setVisible(true);
        } else {
            actualReturnDateLabel.setVisible(false);
        }

        // Exibe multa apenas se for maior que zero
        fineLabel.setVisible(loan.getFine() > 0);
        if (loan.getFine() > 0) {
            fineLabel.setText(String.format("Multa: R$ %.2f", loan.getFine()));
        } else {
            fineLabel.setText(""); // Limpa o texto se não houver multa
        }
    }

    /**
     * Atualiza a visibilidade do botão de devolver com base na data de devolução real.
     * Também desabilita botões de edição/remoção para empréstimos devolvidos.
     */
    private void updateReturnButtonVisibility() {
        boolean isReturned = loan.getActualReturnDate() != null;
        returnButton.setVisible(!isReturned); // Botão "Devolver" visível se NÃO devolvido
        editButton.setDisable(false); // <--- ALTERADO: O botão "Editar" agora está sempre habilitado.
        removeButton.setDisable(isReturned); // O botão "Remover" continua desabilitado para empréstimos devolvidos.
    }

    /**
     * Chama o método no LoanController para marcar o empréstimo como devolvido.
     */
    @FXML
    private void markAsReturned() {
        if (loan != null && loanController != null) {
            loanController.markLoanAsReturned(loan);
        } else {
            logError("Não foi possível marcar empréstimo como devolvido. Dependências (loan ou loanController) ausentes.", null);
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível marcar empréstimo como devolvido.");
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
            logError("Não foi possível mostrar detalhes do empréstimo. Dependências (loan ou loanController) ausentes.", null);
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível mostrar detalhes do empréstimo.");
        }
    }

    /**
     * Carrega a tela de edição do empréstimo em um diálogo modal, passando os dados e serviços necessários.
     */
    @FXML
    private void editLoan() {
        if (loan == null || loanController == null) {
            logError("Não foi possível carregar a tela de edição. Empréstimo ou LoanController é nulo.", null);
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar a tela de edição do empréstimo.");
            return;
        }

        // Verifica se os serviços necessários para a tela de edição estão disponíveis
        if (loanService == null || bookService == null || userService == null) {
            logError("Serviços (LoanService, BookService, UserService) não disponíveis no LoanCardController para edição.", null);
            showAlert(Alert.AlertType.ERROR, "Erro de Serviço", "Os serviços necessários não estão disponíveis para editar o empréstimo.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditLoanView.fxml"));
            VBox root = loader.load();

            EditLoanViewController controller = loader.getController();
            controller.setLoan(loan); // Passa o empréstimo para o controlador de edição

            // Passa os serviços injetados no LoanCardController para o EditLoanViewController
            controller.setLoanService(loanService);
            controller.setBookService(bookService);
            controller.setUserService(userService);
            controller.setMainLoanController(loanController); // Passa o LoanController pai

            Stage stage = new Stage();
            stage.setTitle("Editar Empréstimo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            // Definir o proprietário do palco do diálogo para centralização
            if (loanCard != null && loanCard.getScene() != null && loanCard.getScene().getWindow() instanceof Stage) {
                stage.initOwner(loanCard.getScene().getWindow());
            } else if (loanController.getRootLayoutController() != null && loanController.getRootLayoutController().getPrimaryStage() != null) {
                stage.initOwner(loanController.getRootLayoutController().getPrimaryStage());
            }

            controller.setDialogStage(stage); // Passa o stage para o controlador de edição
            stage.showAndWait();

        } catch (IOException e) {
            logError("Erro ao carregar tela de edição de empréstimo", e);
            showAlert(Alert.AlertType.ERROR, "Erro ao Carregar", "Não foi possível carregar a tela de edição do empréstimo: " + e.getMessage());
        } catch (Exception e) { // Captura outras exceções inesperadas
            logError("Erro inesperado ao abrir a tela de edição de empréstimo", e);
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro inesperado ao abrir a tela de edição do empréstimo: " + e.getMessage());
        }
    }

    /**
     * Exibe um diálogo de confirmação para remover o empréstimo e chama o método no LoanController para removê-lo.
     */
    @FXML
    private void removeLoan() {
        if (loan == null || loanController == null) {
            logError("Não foi possível remover o empréstimo. Empréstimo ou LoanController é nulo.", null);
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível remover o empréstimo.");
            return;
        }

        // Verifica se o livro e usuário do empréstimo são nulos antes de tentar acessá-los
        String bookTitle = (loan.getBook() != null) ? loan.getBook().getTitle() : "livro desconhecido";
        String userName = (loan.getUser() != null) ? loan.getUser().getName() : "usuário desconhecido";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remover Empréstimo");
        alert.setHeaderText("Confirmar Remoção");
        alert.setContentText("Tem certeza que deseja remover o empréstimo do livro \"" + bookTitle + "\" para o usuário \"" + userName + "\"?");

        // Definir o proprietário do alerta para centralização
        if (loanCard != null && loanCard.getScene() != null && loanCard.getScene().getWindow() instanceof Stage) {
            alert.initOwner(loanCard.getScene().getWindow());
        } else if (loanController.getRootLayoutController() != null && loanController.getRootLayoutController().getPrimaryStage() != null) {
            alert.initOwner(loanController.getRootLayoutController().getPrimaryStage());
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            loanController.removeLoan(loan);
        }
    }

    // Apenas manter esta sobrecarga de showAlert
    /**
     * Exibe um diálogo de alerta com o tipo, título e conteúdo especificados.
     *
     * @param alertType O tipo do alerta (INFORMATION, ERROR, WARNING, etc.).
     * @param title     O título do alerta.
     * @param content   O conteúdo da mensagem do alerta.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Definir o proprietário do alerta para centralização, se possível
        if (loanCard != null && loanCard.getScene() != null && loanCard.getScene().getWindow() instanceof Stage) {
            alert.initOwner(loanCard.getScene().getWindow());
        } else if (loanController != null && loanController.getRootLayoutController() != null && loanController.getRootLayoutController().getPrimaryStage() != null) {
            alert.initOwner(loanController.getRootLayoutController().getPrimaryStage());
        }
        alert.showAndWait();
    }


    /**
     * Registra uma mensagem de erro no console.
     *
     * @param message A mensagem de erro.
     * @param e       A exceção ocorrida, pode ser nula.
     */
    private void logError(String message, Exception e) {
        System.err.print("ERRO no LoanCardController: " + message);
        if (e != null) {
            System.err.println(": " + e.getMessage());
            e.printStackTrace();
        } else {
            System.err.println();
        }
    }
}