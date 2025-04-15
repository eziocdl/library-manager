package com.managerlibrary.services;

import com.managerlibrary.daos.interfaces.BookDAO;
import com.managerlibrary.entities.Book;

import java.sql.SQLException;
import java.util.List;

public class BookService {

    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void insertBook(Book book) throws SQLException {
        bookDAO.insertBook(book);
    }

    // Método de busca genérico (pode ser usado para múltiplos critérios)
    public List<Book> searchBook(Book book) throws SQLException {
        return bookDAO.searchBook(book);
    }

    public Book findBookById(int id) throws SQLException {
        return bookDAO.findBookById(id);
    }

    public List<Book> findAllBooks() throws SQLException {
        return bookDAO.findAllBooks();
    }

    public void updateBook(Book book) throws SQLException {
        bookDAO.updateBook(book);
    }

    public void deleteBook(int id) throws SQLException {
        bookDAO.deleteBook(id);
    }

    public void decrementAvailableCopies(int bookId) throws SQLException {
        Book book = bookDAO.findBookById(bookId);
        if (book != null && book.getAvailableCopies() > 0) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookDAO.updateBook(book);
        }
    }

    public void incrementAvailableCopies(int bookId) throws SQLException {
        Book book = bookDAO.findBookById(bookId);
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookDAO.updateBook(book);
        }
    }

    // Métodos específicos de busca (sugestões)
    public List<Book> findBooksByTitle(String title) throws SQLException {
        return bookDAO.findBooksByTitle(title);
    }

    public List<Book> findBooksByAuthor(String author) throws SQLException {
        return bookDAO.findBooksByAuthor(author);
    }

    public Book findBookByISBN(String isbn) throws SQLException {
        return bookDAO.findBookByISBN(isbn);
    }

    public List<Book> findBooksByGenre(String genre) throws SQLException {
        return bookDAO.findBooksByGenre(genre);
    }
}