package com.managerlibrary.services;

import com.managerlibrary.daos.implement.LoanDAOImpl;
import com.managerlibrary.daos.interfaces.LoanDAO;
import com.managerlibrary.entities.Loan;

import java.sql.SQLException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    LoanDAO loanDAO;

    public LoanService(LoanDAOImpl loanDAO) {
        this.loanDAO = loanDAO;
    }

    /**
     * Adiciona um novo empréstimo ao banco de dados.
     *
     * @param loan O empréstimo a ser adicionado.
     * @throws SQLException Se ocorrer um erro ao adicionar o empréstimo.
     */
    public void addLoan(Loan loan) throws SQLException {
        loanDAO.insertLoan(loan);
    }

    /**
     * Busca um empréstimo pelo ID com detalhes do livro e usuário.
     *
     * @param id O ID do empréstimo a ser buscado.
     * @return O empréstimo encontrado com seus detalhes, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o empréstimo.
     */
    public Loan getLoanById(int id) throws SQLException {
        return loanDAO.getLoanById(id); // Correção: Chama o método correto do DAO
    }

    /**
     * Busca todos os empréstimos no banco de dados.
     *
     * @return Uma lista com todos os empréstimos encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os empréstimos.
     */
    public List<Loan> getAllLoans() throws SQLException {
        return loanDAO.getAllLoans();
    }

    /**
     * Atualiza um empréstimo no banco de dados.
     *
     * @param loan O empréstimo a ser atualizado.
     * @throws SQLException Se ocorrer um erro ao atualizar o empréstimo.
     */
    public void updateLoan(Loan loan) throws SQLException {
        loanDAO.updateLoan(loan);
    }

    /**
     * Remove um empréstimo pelo ID.
     *
     * @param id O ID do empréstimo a ser removido.
     * @throws SQLException Se ocorrer um erro ao remover o empréstimo.
     */
    public void deleteLoan(int id) throws SQLException {
        loanDAO.deleteLoan(id);
    }

    public List<Loan> getAllLoansWithDetails() throws SQLException {
        List<Loan> loans = loanDAO.getAllLoansWithDetails();
        if (loans == null) {
            return new ArrayList<>(); // Retorna uma lista vazia em vez de null
        }
        return loans;
    }

    /**
     * Marca um empréstimo como devolvido, atualizando a data de devolução efetiva,
     * o status e calculando a multa por atraso.
     *
     * @param loan O empréstimo a ser marcado como devolvido.
     * @throws SQLException Se ocorrer um erro ao atualizar o empréstimo.
     */
    public void markLoanAsReturned(Loan loan) throws SQLException {
        if (loan != null && loan.getActualReturnDate() == null) {
            loan.setActualReturnDate(LocalDate.now());
            loan.setStatus("Devolvido");
            double multa = calculateLateFee(loan.getReturnDate(), loan.getActualReturnDate());
            loan.setFine(multa);
            loanDAO.updateLoan(loan);
        }
    }

    /**
     * Calcula a multa por atraso na devolução de um livro.
     *
     * @param returnDate       A data prevista de devolução.
     * @param actualReturnDate A data real da devolução.
     * @return O valor da multa, ou 0.0 se não houver atraso.
     */
    private double calculateLateFee(LocalDate returnDate, LocalDate actualReturnDate) {
        if (actualReturnDate != null && actualReturnDate.isAfter(returnDate)) {
            long daysLate = ChronoUnit.DAYS.between(returnDate, actualReturnDate);
            double feePerDay = 0.50; // Exemplo da taxa por dia de atraso
            return daysLate * feePerDay;
        }
        return 0.0;
    }

    public List<Loan> getAllLoansWithBookAndUser() throws SQLException {
        return loanDAO.getAllLoansWithBookAndUser();
    }
}