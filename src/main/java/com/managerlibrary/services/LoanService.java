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
        if (loan.getReturnDate() == null) {
            throw new IllegalArgumentException("A data de devolução prevista não pode ser nula.");
        }
        if (loan.getReturnDate().isBefore(loan.getLoanDate())) {
            throw new IllegalArgumentException("A data de devolução não pode ser anterior à data de empréstimo.");
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
        // CORRIGIDO: Método renomeado no DAO
        return loanDAO.findLoanByIdWithDetails(id);
    }

    /**
     * Busca todos os empréstimos registrados no banco de dados.
     *
     * @return Uma lista com todos os empréstimos encontrados. Retorna uma lista vazia se não houver empréstimos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Loan> getAllLoans() throws SQLException {
        // CORRIGIDO: Método renomeado no DAO
        List<Loan> loans = loanDAO.findAllLoans();
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
        // Usando o método correto do DAO para buscar o empréstimo para validação
        Loan loanToDelete = loanDAO.findLoanByIdWithDetails(id);
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
        // CORRIGIDO: Método renomeado no DAO
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
        double multa = calculateLateFee(loan.getReturnDate(), loan.getActualReturnDate());
        loan.setFine(multa);
        loanDAO.updateLoan(loan);
    }

    /**
     * Calcula a multa por atraso na devolução de um livro.
     * A multa é calculada com base nos dias de atraso e uma taxa fixa por dia (R$ 0.50).
     *
     * @param returnDate       A data prevista de devolução.
     * @param actualReturnDate A data real da devolução.
     * @return O valor da multa, ou 0.0 se não houver atraso ou datas inválidas.
     */
    private double calculateLateFee(LocalDate returnDate, LocalDate actualReturnDate) {
        if (returnDate == null || actualReturnDate == null) {
            System.err.println("Erro: Datas de devolução nulas para cálculo de multa.");
            return 0.0;
        }

        if (actualReturnDate.isAfter(returnDate)) {
            long daysLate = ChronoUnit.DAYS.between(returnDate, actualReturnDate);
            double feePerDay = 0.50;
            return daysLate * feePerDay;
        }
        return 0.0;
    }

    /**
     * Retorna todos os empréstimos com os detalhes completos do livro e do usuário associados.
     * Este método é funcionalmente idêntico a `getAllLoansWithDetails()`.
     * Poderia ser um alias ou um deles ser removido se a funcionalidade for a mesma.
     *
     * @return Uma lista de empréstimos com seus detalhes completos. Retorna uma lista vazia se não houver empréstimos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<Loan> getAllLoansWithBookAndUser() throws SQLException {
        // Este método já estava correto, pois chamava o método do DAO com o mesmo nome.
        return loanDAO.findAllLoansWithBookAndUser();
    }
}