package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.BookDAO;
import com.managerlibrary.entities.Book;
import com.managerlibrary.infra.DataBaseConnection;

import java.sql.*;
import java.util.List;


public class BookDAOImpl implements BookDAO {

    @Override
    public void insertBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn) VALUES (?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getIsbn());
            preparedStatement.executeUpdate();
        }


    }

    @Override
    public Book findBookById(int id) throws SQLException {
        String sql = "SELECT * FROM Books WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createBook(resultSet);
            }
            return null;
        }

    }


    @Override
    public List<Book> findAllBooks() throws SQLException {
        String sql = "SELECT * FROM books";
        try (Connection connection = DataBaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            List<Book> books = new java.util.ArrayList<>();
            while (resultSet.next()) {
                books.add(createBook(resultSet));
            }
            return books;
        }


    }

    @Override
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ? WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getIsbn());
            preparedStatement.setInt(4, book.getId());
            preparedStatement.executeUpdate();
        }

    }

    @Override
    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }

    }

    /**
     * Cria um objeto Book a partir de um ResultSet.
     *
     * @param resultSet O ResultSet contendo os dados do livro.
     * @return O objeto Livro criado.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */

    private Book createBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getInt("id"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthor(resultSet.getString("author"));
        book.setIsbn(resultSet.getString("isbn"));
        return book;
    }
}
