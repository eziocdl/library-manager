package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.BookDAO;
import com.managerlibrary.entities.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {

    private Connection connection;

    public BookDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.setString(7, book.getPublisher());
            pstmt.setInt(8, book.getYear());
            pstmt.setString(9, book.getImageUrl());
            pstmt.setString(10, book.getCoverImagePath());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                book.setId(generatedKeys.getInt(1));
            }
        }
    }

    @Override
    public List<Book> searchBook(Book book) throws SQLException {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (book.getTitle() != null && !book.getTitle().isEmpty()) {
            sql.append(" AND title ILIKE ?");
            params.add("%" + book.getTitle() + "%");
        }
        if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
            sql.append(" AND author ILIKE ?");
            params.add("%" + book.getAuthor() + "%");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public Book findBookById(int id) throws SQLException {
        String sql = "SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
            return null;
        }
    }

    @Override
    public List<Book> findAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao executar consulta findAllBooks: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return books;
    }

    @Override
    public List<Book> findAllAvailable() throws SQLException {
        List<Book> availableBooks = new ArrayList<>();
        String sql = "SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books WHERE available_copies > 0";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                availableBooks.add(mapResultSetToBook(rs));
            }
        }
        return availableBooks;
    }

    @Override
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, genre = ?, total_copies = ?, available_copies = ?, publisher = ?, year = ?, image_url = ?, cover_image_path = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.setString(7, book.getPublisher());
            pstmt.setInt(8, book.getYear());
            pstmt.setString(9, book.getImageUrl());
            pstmt.setString(10, book.getCoverImagePath());
            pstmt.setInt(11, book.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Book> findBooksByTitle(String title) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books WHERE title ILIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> findBooksByAuthor(String author) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books WHERE author ILIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + author + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public Book findBookByISBN(String isbn) throws SQLException {
        String sql = "SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
            return null;
        }
    }

    @Override
    public List<Book> findBooksByGenre(String genre) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, isbn, genre, total_copies, available_copies, publisher, year, image_url, cover_image_path FROM books WHERE genre ILIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + genre + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public void incrementAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void decrementAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setGenre(rs.getString("genre"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));
        book.setPublisher(rs.getString("publisher"));
        book.setYear(rs.getInt("year"));
        book.setImageUrl(rs.getString("image_url"));
        book.setCoverImagePath(rs.getString("cover_image_path"));
        return book;
    }
}