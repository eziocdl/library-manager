package com.managerlibrary.services;

import com.managerlibrary.daos.implement.LoanDAOImpl; // Considerar mudar para interfaces sempre que possível
import com.managerlibrary.daos.interfaces.LoanDAO;
import com.managerlibrary.entities.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerenciar as operações de negócio relacionadas a empréstimos.
 * Atua como uma camada intermediária entre os controladores e o DAO de empréstimos,
 * aplicando regras de negócio como cálculo de multas.
 */
public class LoanService {

    private final LoanDAO loanDAO;

    /**
     * Construtor do LoanService.
     *
     * @param loanDAO A implementação de LoanDAO a ser utilizada para acesso a dados.
     * Não pode ser nula.
     */
    public LoanService(LoanDAO loanDAO) {
        this.loanDAO = Objects.requireNonNull(loanDAO, "LoanDAO não pode ser nulo.");
    }

    /**
     * Adiciona um novo empréstimo ao banco de dados.
     * Realiza validações de negócio antes de persistir o empréstimo.
     *
     * @param loan O objeto Loan a ser adicionado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o empréstimo for nulo ou inválido (ex: datas, livro/usuário nulos).
     */
    public void addLoan(Loan loan) throws SQLException {
        if (loan == null) {
            throw new IllegalArgumentException("O empréstimo não pode ser nulo.");
        }
        if (loan.getBook() == null || loan.getBook().getId() <= 0) {
            throw new IllegalArgumentException("O livro associado ao empréstimo é inválido.");
        }
        if (loan.getUser() == null || loan.getUser().getId() <= 0) {
            throw new IllegalArgumentException("O usuário associado ao empréstimo é inválido.");
        }
        if (loan.getLoanDate() == null) {
            throw new IllegalArgumentException("A data de empréstimo não pode ser nula.");
        }
        // CORREÇÃO AQUI: Usar getExpectedReturnDate()
        if (loan.getExpectedReturnDate() == null) {
            throw new IllegalArgumentException("A data de devolução prevista não pode ser nula.");
        }
        // CORREÇÃO AQUI: Usar getExpectedReturnDate()
        if (loan.getExpectedReturnDate().isBefore(loan.getLoanDate())) {
            throw new IllegalArgumentException("A data de devolução prevista não pode ser anterior à data de empréstimo.");
        }

        loan.setStatus("Ativo");
        loan.setActualReturnDate(null);
        loan.setFine(0.0);

        loanDAO.insertLoan(loan);
    }

    /**
     * Busca um empréstimo pelo ID, carregando seus detalhes completos
     * (incluindo informações do livro e do usuário associados).
     *
     * @param id O ID do empréstimo a ser buscado.
     * @return O empréstimo encontrado com seus detalhes, ou null se não for encontrado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o ID for inválido (menor ou igual a zero).
     */
    public Loan getLoanById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do empréstimo inválido.");
        }
        return loanDAO.findLoanByIdWithDetails(id);
    }

    /**
     * Busca todos os empréstimos registrados no banco de dados.
     *
     * @return Uma lista com todos os empréstimos encontrados. Retorna uma lista vazia se não houver empréstimos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = loanDAO.findAllLoans(); // Se findAllLoans() não trouxer detalhes, use findAllLoansWithBookAndUser()
        return loans != null ? loans : new ArrayList<>();
    }

    /**
     * Atualiza as informações de um empréstimo existente no banco de dados.
     *
     * @param loan O objeto Loan com as informações atualizadas.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o empréstimo for nulo ou inválido (ex: ID nulo ou zero).
     */
    public void updateLoan(Loan loan) throws SQLException {
        if (loan == null || loan.getId() <= 0) {
            throw new IllegalArgumentException("Empréstimo para atualização inválido.");
        }
        loanDAO.updateLoan(loan);
    }

    /**
     * Remove um empréstimo do banco de dados pelo seu ID.
     *
     * @param id O ID do empréstimo a ser removido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     * @throws IllegalArgumentException Se o ID for inválido.
     * @throws IllegalStateException Se o empréstimo não puder ser removido (ex: está ativo).
     */
    public void deleteLoan(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do empréstimo para exclusão inválido.");
        }
        Loan loanToDelete = loanDAO.findLoanByIdWithDetails(id);
        // Só permite remover se o empréstimo já foi devolvido ou se não for encontrado (neste caso, delete silenciosamente)
        if (loanToDelete != null && loanToDelete.getActualReturnDate() == null) {
            throw new IllegalStateException("Não é possível remover um empréstimo ativo. Marque-o como devolvido primeiro.");
        }
        loanDAO.deleteLoan(id);
    }

    /**
     * Retorna todos os empréstimos com os detalhes completos do livro e do usuário associados.
     *
     * @return Uma lista de empréstimos com seus detalhes completos. Retorna uma lista vazia se não houver empréstimos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Loan> getAllLoansWithDetails() throws SQLException {
        List<Loan> loans = loanDAO.findAllLoansWithBookAndUser();
        return loans != null ? loans : new ArrayList<>();
    }

    /**
     * Marca um empréstimo como devolvido.
     * Atualiza a data de devolução efetiva para a data atual, define o status como "Devolvido"
     * e calcula a multa por atraso, atualizando o empréstimo no banco de dados.
     *
     * @param loan O objeto Loan a ser marcado como devolvido.
     * @throws SQLException Se ocorrer um erro ao atualizar o empréstimo.
     * @throws IllegalArgumentException Se o empréstimo for nulo ou já tiver sido devolvido.
     */
    public void markLoanAsReturned(Loan loan) throws SQLException {
        if (loan == null) {
            throw new IllegalArgumentException("Empréstimo não pode ser nulo ao marcar como devolvido.");
        }
        if (loan.getActualReturnDate() != null) {
            throw new IllegalArgumentException("Este empréstimo já foi devolvido.");
        }

        loan.setActualReturnDate(LocalDate.now());
        loan.setStatus("Devolvido");
        // CORREÇÃO AQUI: Usar getExpectedReturnDate()
        double multa = calculateLateFee(loan.getExpectedReturnDate(), loan.getActualReturnDate());
        loan.setFine(multa);
        loanDAO.updateLoan(loan);
    }

    /**
     * Calcula a multa por atraso na devolução de um livro.
     * A multa é calculada com base nos dias de atraso e uma taxa fixa por dia (R$ 0.50).
     *
     * @param expectedReturnDate A data prevista de devolução.
     * @param actualReturnDate   A data real da devolução.
     * @return O valor da multa, ou 0.0 se não houver atraso ou datas inválidas.
     */
    public double calculateLateFee(LocalDate expectedReturnDate, LocalDate actualReturnDate) {
        if (expectedReturnDate == null || actualReturnDate == null) {
            System.err.println("Erro: Datas de devolução nulas para cálculo de multa.");
            return 0.0;
        }

        // Se a data de devolução real for DEPOIS da data prevista, há atraso
        if (actualReturnDate.isAfter(expectedReturnDate)) {
            long daysLate = ChronoUnit.DAYS.between(expectedReturnDate, actualReturnDate);
            double feePerDay = 0.50;
            return daysLate * feePerDay;
        }
        return 0.0;
    }

    /**
     * Este método busca empréstimos com base em um termo de pesquisa que pode ser parte
     * do título do livro, nome do usuário ou CPF do usuário.
     *
     * @param searchTerm O termo de pesquisa.
     * @return Uma lista de empréstimos que correspondem ao termo de pesquisa.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Loan> searchLoans(String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllLoansWithDetails(); // Se a busca está vazia, retorna todos
        }
        // Este método precisa ser implementado no seu LoanDAOImpl
        // Exemplo:
        // return loanDAO.searchLoansByBookTitleOrUserNameOrUserCpf(searchTerm);
        // Por enquanto, podemos fazer a filtragem em memória se o DAO ainda não tiver esse método otimizado.
        // No entanto, para grandes volumes de dados, a busca no DAO é mais eficiente.

        // IMPLEMENTAÇÃO TEMPORÁRIA EM MEMÓRIA (se o DAO não tem searchLoans):
        List<Loan> all = getAllLoansWithDetails();
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        return all.stream()
                .filter(loan -> {
                    boolean matchesBook = loan.getBook() != null && loan.getBook().getTitle().toLowerCase().contains(lowerCaseSearchTerm);
                    boolean matchesUser = false;
                    if (loan.getUser() != null) {
                        matchesUser = loan.getUser().getName().toLowerCase().contains(lowerCaseSearchTerm) ||
                                (loan.getUser().getCpf() != null && loan.getUser().getCpf().toLowerCase().contains(lowerCaseSearchTerm));
                    }
                    return matchesBook || matchesUser;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retorna todos os empréstimos com um status específico.
     *
     * @param status O status do empréstimo (ex: "Ativo", "Devolvido").
     * @return Uma lista de empréstimos com o status especificado.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Loan> getLoansByStatus(String status) throws SQLException {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status não pode ser nulo ou vazio.");
        }
        // Este método precisa ser implementado no seu LoanDAOImpl
        // Exemplo:
        // return loanDAO.findLoansByStatusWithDetails(status);

        // IMPLEMENTAÇÃO TEMPORÁRIA EM MEMÓRIA (se o DAO não tem getLoansByStatus):
        List<Loan> all = getAllLoansWithDetails();
        String lowerCaseStatus = status.toLowerCase();
        return all.stream()
                .filter(loan -> loan.getStatus() != null && loan.getStatus().toLowerCase().equals(lowerCaseStatus))
                .collect(Collectors.toList());
    }

    /**
     * Retorna todos os empréstimos que estão atrasados.
     *
     * @return Uma lista de empréstimos atrasados.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Loan> getOverdueLoans() throws SQLException {
        // Este método precisa ser implementado no seu LoanDAOImpl
        // Exemplo:
        // return loanDAO.findOverdueLoansWithDetails();

        // IMPLEMENTAÇÃO TEMPORÁRIA EM MEMÓRIA (se o DAO não tem findOverdueLoans):
        List<Loan> all = getAllLoansWithDetails();
        LocalDate today = LocalDate.now();
        return all.stream()
                .filter(loan -> loan.getActualReturnDate() == null && "Ativo".equalsIgnoreCase(loan.getStatus()) &&
                        loan.getExpectedReturnDate() != null && today.isAfter(loan.getExpectedReturnDate()))
                .collect(Collectors.toList());
    }
}