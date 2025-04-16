package com.managerlibrary.daos.interfaces;

import com.managerlibrary.entities.Loan;

import java.sql.SQLException;
import java.util.List;

public interface LoanDAO {

    /**
     * Insere um novo empréstimo no banco de dados.
     *
     * @param loan O empréstimo a ser inserido.
     * @throws SQLException Se ocorrer um erro ao inserir o empréstimo.
     */
    void insertLoan(Loan loan) throws SQLException;

    /**
     * Busca um empréstimo pelo ID.
     *
     * @param id O ID do empréstimo a ser buscado.
     * @return O empréstimo encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o empréstimo.
     */

    Loan findLoanById(int id) throws SQLException;

    /**
     * Busca todos os empréstimos no banco de dados.
     *
     * @return Uma lista com todos os empréstimos encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os empréstimos.
     */

    List<Loan> findAllLoans() throws SQLException;

    /**
     * Atualiza um empréstimo no banco de dados.
     *
     * @param loan O empréstimo a ser atualizado.
     * @throws SQLException Se ocorrer um erro ao atualizar o empréstimo.
     */

    void updateLoan(Loan loan) throws SQLException;


    /**
     * Deleta um empréstimo pelo ID.
     *
     * @param id O ID do empréstimo a ser deletado.
     * @throws SQLException Se ocorrer um erro ao deletar o empréstimo.
     */


    void deleteLoan(int id) throws SQLException;


    List<Loan> getAllLoansWithDetails() throws SQLException;


}
