package com.managerlibrary.daos.interfaces;

import com.managerlibrary.entities.Loan;

import java.sql.SQLException;
import java.time.LocalDate;
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
     * Busca um empréstimo pelo ID, carregando os detalhes completos do livro e usuário associados.
     *
     * @param id O ID do empréstimo a ser buscado.
     * @return O empréstimo encontrado com seus detalhes completos, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o empréstimo.
     */
    Loan findLoanByIdWithDetails(int id) throws SQLException; // RENOMEADO para maior clareza

    /**
     * Busca todos os empréstimos no banco de dados, sem carregar os detalhes completos
     * do livro e do usuário associados (apenas IDs ou referências básicas).
     * Útil para operações que não exigem a entidade Book e User completa.
     *
     * @return Uma lista com todos os empréstimos encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os empréstimos.
     */
    List<Loan> findAllLoans() throws SQLException; // RENOMEADO para consistência com BookDAO

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
     * Busca todos os empréstimos no banco de dados, carregando os detalhes completos
     * do livro e do usuário associados a cada empréstimo.
     *
     * @return Uma lista com todos os empréstimos e seus detalhes.
     * @throws SQLException Se ocorrer um erro ao buscar os empréstimos.
     */
    List<Loan> findAllLoansWithBookAndUser() throws SQLException; // UNIFICADO E RENOMEADO, mantendo o nome mais claro

    /**
     * Marca um empréstimo como devolvido, atualizando a data de devolução e o status.
     *
     * @param loanId     O ID do empréstimo a ser marcado como devolvido.
     * @param returnDate A data real da devolução.
     * @throws SQLException Se ocorrer um erro ao atualizar o empréstimo.
     */
    void markAsReturned(int loanId, LocalDate returnDate) throws SQLException;

    // Métodos de busca adicionais que podem ser úteis:

    /**
     * Busca empréstimos por um usuário específico, com detalhes do livro e usuário.
     * @param userId O ID do usuário.
     * @return Lista de empréstimos do usuário.
     * @throws SQLException
     */
    List<Loan> findLoansByUserId(int userId) throws SQLException;

    /**
     * Busca empréstimos por um livro específico, com detalhes do livro e usuário.
     * @param bookId O ID do livro.
     * @return Lista de empréstimos do livro.
     * @throws SQLException
     */
    List<Loan> findLoansByBookId(int bookId) throws SQLException;

    /**
     * Busca empréstimos com base em seu status (Ex: "Ativo", "Devolvido", "Atrasado").
     * @param status O status do empréstimo.
     * @return Lista de empréstimos com o status especificado.
     * @throws SQLException
     */
    List<Loan> findLoansByStatus(String status) throws SQLException;
}