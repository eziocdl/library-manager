package com.managerlibrary.services;

import com.managerlibrary.daos.LoanDAO;
import com.managerlibrary.entities.Loan;

import java.sql.SQLException;
import java.util.List;

public class LoanService {

    LoanDAO loanDAO;

    public LoanService(LoanDAO loanDAO) {
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


}
