package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.BookDAO;
import com.managerlibrary.entities.Book;
import com.managerlibrary.infra.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {
    @Override
    public void insertBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, publisher, year, genre, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getIsbn());
            preparedStatement.setString(4, book.getPublisher());
            preparedStatement.setString(5, book.getYear());
            preparedStatement.setString(6, book.getGenre());
            preparedStatement.setInt(7, book.getTotalCopies());
            preparedStatement.setInt(8, book.getAvailableCopies());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Book findBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
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
            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(createBook(resultSet));
            }
            return books;
        }
    }

    @Override
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, publisher = ?, year = ?, genre = ?, total_copies = ?, available_copies = ? WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getIsbn());
            preparedStatement.setString(4, book.getPublisher());
            preparedStatement.setString(5, book.getYear());
            preparedStatement.setString(6, book.getGenre());
            preparedStatement.setInt(7, book.getTotalCopies());
            preparedStatement.setInt(8, book.getAvailableCopies());
            preparedStatement.setInt(9, book.getId());
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

    @Override
    public List<Book> searchBook(Book book) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (book.getTitle() != null && !book.getTitle().isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + book.getTitle() + "%");
        }
        if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
            sql.append(" AND author LIKE ?");
            params.add("%" + book.getAuthor() + "%");
        }
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            sql.append(" AND isbn LIKE ?");
            params.add("%" + book.getIsbn() + "%");
        }
        if (book.getPublisher() != null && !book.getPublisher().isEmpty()) {
            sql.append(" AND publisher LIKE ?");
            params.add("%" + book.getPublisher() + "%");
        }
        if (book.getYear() != null && !book.getYear().isEmpty()) {
            sql.append(" AND year LIKE ?");
            params.add("%" + book.getYear() + "%");
        }
        if (book.getGenre() != null && !book.getGenre().isEmpty()) {
            sql.append(" AND genre LIKE ?");
            params.add("%" + book.getGenre() + "%");
        }

        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Book> books = new ArrayList<>();
            while (resultSet.next()) {
                books.add(createBook(resultSet));
            }
            return books;
        }
    }

    private Book createBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getInt("id"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthor(resultSet.getString("author"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setPublisher(resultSet.getString("publisher"));
        book.setYear(resultSet.getString("year"));
        book.setGenre(resultSet.getString("genre"));
        book.setTotalCopies(resultSet.getInt("total_copies"));
        book.setAvailableCopies(resultSet.getInt("available_copies"));
        return book;
    }
}