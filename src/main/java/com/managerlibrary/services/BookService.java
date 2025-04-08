package com.managerlibrary.services;

import com.managerlibrary.daos.interfaces.BookDAO;
import com.managerlibrary.entities.Book;

import java.sql.SQLException;
import java.util.List;

public class BookService {

    private BookDAO bookDAO;

    public BookService() {
        this.bookDAO = bookDAO;
    }

    /**
     * Adiciona um novo livro ao banco de dados.
     *
     * @throws SQLException Se ocorrer um erro ao adicionar o livro.
     */

    public void addBook() throws SQLException {
        bookDAO.insertBook(new Book());
    }

    /**
     * Busca um livro pelo ID.
     *
     * @param id O ID do livro a ser buscado.
     * @return O livro encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o livro.
     */
    public Book getBookById(int id) throws SQLException {
        return bookDAO.findBookById(id);
    }

    /**
     * Busca todos os livros no banco de dados.
     *
     * @return Uma lista com todos os livros encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os livros.
     */

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAllBooks();
    };

    /**
     * Atualiza um livro no banco de dados.
     *
     * @param book O livro a ser atualizado.
     * @throws SQLException Se ocorrer um erro ao atualizar o livro.
     */

    public void updateBook(Book book) throws SQLException {
        bookDAO.updateBook(book);
    }

    /**
     * Remove um livro pelo ID.
     *
     * @param id O ID do livro a ser removido.
     * @throws SQLException Se ocorrer um erro ao remover o livro.
     */
    public void removeBook(int id) throws SQLException {
        bookDAO.deleteBook(id);
    }





}
