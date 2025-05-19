package com.managerlibrary.daos.interfaces;

import com.managerlibrary.entities.Loan;

import java.sql.SQLException;
import java.time.LocalDate;import java.util.List;

public interface LoanDAO {

    /**
     * Insere um novo empréstimo no banco de dados.
     *
     * @param loan O empréstimo a ser inserido.
     * @throws SQLException Se ocorrer um erro ao inserir o empréstimo.
     */
    void insertLoan(Loan loan) throws SQLException;

    /**
     * Busca um empréstimo pelo ID com detalhes do livro e usuário.
     *
     * @param id O ID do empréstimo a ser buscado.
     * @return O empréstimo encontrado com seus detalhes, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o empréstimo.
     */
    Loan getLoanById(int id) throws SQLException;

    /**
     * Busca todos os empréstimos no banco de dados (sem detalhes completos).
     *
     * @return Uma lista com todos os empréstimos encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os empréstimos.
     */
    List<Loan> getAllLoans() throws SQLException;

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

    /**
     * Busca todos os empréstimos com detalhes do livro e usuário.
     *
     * @return Uma lista com todos os empréstimos e seus detalhes.
     * @throws SQLException Se ocorrer um erro ao buscar os empréstimos.
     */
    List<Loan> getAllLoansWithDetails() throws SQLException;

    /**
     * Marca um empréstimo como devolvido, atualizando a data de devolução.
     *
     * @param loanId     O ID do empréstimo a ser marcado como devolvido.
     * @param returnDate A data real da devolução.
     * @throws SQLException Se ocorrer um erro ao atualizar o empréstimo.
     */
    void markAsReturned(int loanId, LocalDate returnDate) throws SQLException;

    List<Loan> getAllLoansWithBookAndUser();
}
