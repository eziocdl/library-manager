package com.managerlibrary.services;

import com.managerlibrary.daos.implement.LoanDAOImpl;
import com.managerlibrary.daos.interfaces.LoanDAO;
import com.managerlibrary.entities.Loan;

import java.sql.SQLException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
     * Busca um empréstimo pelo ID.
     *
     * @param id O ID do empréstimo a ser buscado.
     * @return O empréstimo encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o empréstimo.
     */
    public Loan getLoanById(int id) throws SQLException {
        return loanDAO.findLoanById(id);
    }

    /**
     * Busca todos os empréstimos no banco de dados.
     *
     * @return Uma lista com todos os empréstimos encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os empréstimos.
     */
    public List<Loan> getAllLoans() throws SQLException {
        return loanDAO.findAllLoans();
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
    public void removeLoan(int id) throws SQLException {
        loanDAO.deleteLoan(id);
    }

    public List<Loan> getAllLoansWithDetails() throws SQLException {
        if (loanDAO != null) {
            return loanDAO.findAllLoansWithDetails();
        } else {
            // Simulação (se necessário)
            return null; // Ou sua lista simulada
        }
    }

    /**
     * Marca um empréstimo como devolvido, atualizando a data de devolução efetiva,
     * o status e calculando a multa por atraso.
     *
     * @param loanId           O ID do empréstimo a ser marcado como devolvido.
     * @param actualReturnDate A data real da devolução.
     * @throws SQLException Se ocorrer um erro ao atualizar o empréstimo.
     */
    public void markAsReturned(int loanId, LocalDate actualReturnDate) throws SQLException {
        Loan loan = loanDAO.findLoanById(loanId);
        if (loan != null && loan.getActualReturnDate() == null) {
            loan.setActualReturnDate(actualReturnDate);
            loan.setStatus("Devolvido");
            double multa = calculateLateFee(loan.getReturnDate(), actualReturnDate);
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
            double feePerDay = 0.50; // Example of the late fee per day
            return daysLate * feePerDay;
        }
        return 0.0;
    }
}