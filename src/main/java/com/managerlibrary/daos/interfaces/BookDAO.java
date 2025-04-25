package com.managerlibrary.daos.interfaces;

import com.managerlibrary.entities.Book;

import java.sql.SQLException;
import java.util.List;

public interface BookDAO {

    void insertBook(Book book) throws SQLException;

    List<Book> searchBook(Book book) throws SQLException;

    Book findBookById(int id) throws SQLException;

    List<Book> findAllBooks() throws SQLException;

    void updateBook(Book book) throws SQLException;

    void deleteBook(int id) throws SQLException;

    // Novos métodos de busca específicos
    List<Book> findBooksByTitle(String title) throws SQLException;

    List<Book> findBooksByAuthor(String author) throws SQLException;

    Book findBookByISBN(String isbn) throws SQLException;

    List<Book> findBooksByGenre(String genre) throws SQLException;

    List<Book> findAllAvailable() throws SQLException;
}