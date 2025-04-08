package com.managerlibrary.daos.interfaces;

import com.managerlibrary.entities.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookDAO {

    /**
     * Insere um novo livro no banco de dados.
     *
     * @param book O livro a ser inserido.
     * @throws SQLException Se ocorrer um erro ao inserir o livro.
     */

    void insertBook(Book book) throws SQLException;

    /**
     * Busca um livro pelo ID.
     *
     * @param id O ID do livro a ser buscado.
     * @return O livro encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o livro.
     */

    Book findBookById(int id) throws SQLException;

    /**
     * Busca todos os livros no banco de dados.
     *
     * @return Uma lista com todos os livros encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os livros.
     */
    List<Book> findAllBooks() throws SQLException;

    /**
     * Atualiza um livro no banco de dados.
     *
     * @param book O livro a ser atualizado.
     * @throws SQLException Se ocorrer um erro ao atualizar o livro.
     */

    void updateBook(Book book) throws SQLException;

    /**
     * Deleta um livro pelo ID.
     *
     * @param id O ID do livro a ser deletado.
     * @throws SQLException Se ocorrer um erro ao deletar o livro.
     */
    void deleteBook(int id) throws SQLException;



}
