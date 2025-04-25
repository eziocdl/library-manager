package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.BookDAO;
import com.managerlibrary.entities.Book;
import com.managerlibrary.infra.DataBaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {

    public BookDAOImpl() throws SQLException {
        // Conexão já é gerenciada pelo BookService
    }

    @Override
    public void insertBook(Book book) throws SQLException {
        String sql = "INSERT INTO livro (titulo, autor, isbn, genero, total_copias, copias_disponiveis) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Book> searchBook(Book book) throws SQLException {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM livro WHERE 1=1");
        if (book.getTitle() != null && !book.getTitle().isEmpty()) {
            sql.append(" AND titulo LIKE ?");
        }
        if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
            sql.append(" AND autor LIKE ?");
        }
        // Adicione outras condições de busca conforme necessário
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (book.getTitle() != null && !book.getTitle().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + book.getTitle() + "%");
            }
            if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + book.getAuthor() + "%");
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
        String sql = "SELECT * FROM livro WHERE id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT * FROM livro";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public List<Book> findAllAvailable() throws SQLException {
        List<Book> availableBooks = new ArrayList<>();
        String sql = "SELECT * FROM livro WHERE copias_disponiveis > 0";
        try (Connection conn = DataBaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                availableBooks.add(mapResultSetToBook(rs));
            }
        }
        return availableBooks;
    }

    @Override
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE livro SET titulo = ?, autor = ?, isbn = ?, genero = ?, total_copias = ?, copias_disponiveis = ? WHERE id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getTotalCopies());
            pstmt.setInt(6, book.getAvailableCopies());
            pstmt.setInt(7, book.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM livro WHERE id = ?";
        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Book> findBooksByTitle(String title) throws SQLException {
        return null;
    }

    @Override
    public List<Book> findBooksByAuthor(String author) throws SQLException {
        return null;
    }

    @Override
    public Book findBookByISBN(String isbn) throws SQLException {
        return null;
    }

    @Override
    public List<Book> findBooksByGenre(String genre) throws SQLException {
        return null;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("titulo"));
        book.setAuthor(rs.getString("autor"));
        book.setIsbn(rs.getString("isbn"));
        book.setGenre(rs.getString("genero"));
        book.setTotalCopies(rs.getInt("total_copias"));
        book.setAvailableCopies(rs.getInt("copias_disponiveis"));
        return book;
    }
}