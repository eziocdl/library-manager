package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.LoanDAO;
import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {

    private Connection connection;

    public LoanDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insertLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loan (book_id, user_id, loan_date, return_date, actual_return_date, status, fine) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getBook().getId());
            pstmt.setInt(2, loan.getUser().getId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getReturnDate()));
            pstmt.setDate(5, loan.getActualReturnDate() == null ? null : Date.valueOf(loan.getActualReturnDate()));
            pstmt.setString(6, loan.getStatus());
            pstmt.setDouble(7, loan.getFine());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Loan getLoanById(int id) throws SQLException {
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, " +
                "b.title AS book_title, b.author AS book_author, b.cover_image_path AS book_cover_image_path, " +
                "u.name AS user_name, u.cpf AS user_cpf, b.id AS b_id, u.id AS u_id " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id " +
                "WHERE l.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("loan_id"));
                loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
                loan.setReturnDate(rs.getDate("return_date").toLocalDate());
                Date actualReturnDate = rs.getDate("actual_return_date");
                loan.setActualReturnDate(actualReturnDate != null ? actualReturnDate.toLocalDate() : null);
                loan.setStatus(rs.getString("status"));
                loan.setFine(rs.getDouble("fine"));

                Book book = new Book();
                book.setId(rs.getInt("b_id"));
                book.setTitle(rs.getString("book_title"));
                book.setAuthor(rs.getString("book_author"));
                book.setCoverImagePath(rs.getString("book_cover_image_path"));
                loan.setBook(book);

                User user = new User();
                user.setId(rs.getInt("u_id"));
                user.setName(rs.getString("user_name"));
                user.setCpf(rs.getString("user_cpf"));
                loan.setUser(user);

                return loan;
            }
            return null;
        }
    }

    @Override
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT id, book_id, user_id, loan_date, return_date, actual_return_date, status, fine FROM loan";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("id"));
                loan.setBook(new Book(rs.getInt("book_id"))); // Apenas o ID do livro por enquanto
                loan.setUser(new User(rs.getInt("user_id")));   // Apenas o ID do usuário por enquanto
                loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
                loan.setReturnDate(rs.getDate("return_date").toLocalDate());
                Date actualReturnDate = rs.getDate("actual_return_date");
                loan.setActualReturnDate(actualReturnDate != null ? actualReturnDate.toLocalDate() : null);
                loan.setStatus(rs.getString("status"));
                loan.setFine(rs.getDouble("fine"));
                loans.add(loan);
            }
        }
        return loans;
    }

    @Override
    public void updateLoan(Loan loan) throws SQLException {
        String sql = "UPDATE loan SET book_id = ?, user_id = ?, loan_date = ?, return_date = ?, actual_return_date = ?, status = ?, fine = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getBook().getId());
            pstmt.setInt(2, loan.getUser().getId());
            pstmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            pstmt.setDate(4, Date.valueOf(loan.getReturnDate()));
            pstmt.setDate(5, loan.getActualReturnDate() == null ? null : Date.valueOf(loan.getActualReturnDate()));
            pstmt.setString(6, loan.getStatus());
            pstmt.setDouble(7, loan.getFine());
            pstmt.setInt(8, loan.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteLoan(int id) throws SQLException {
        String sql = "DELETE FROM loan WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void markAsReturned(int loanId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE loan SET actual_return_date = ?, status = 'Devolvido' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(returnDate));
            pstmt.setInt(2, loanId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Loan> getAllLoansWithBookAndUser() {
        return null;
    }

    @Override
    public List<Loan> getAllLoansWithDetails() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, " +
                "b.title AS book_title, b.author AS book_author, b.cover_image_path AS book_cover_image_path, " +
                "u.name AS user_name, u.cpf AS user_cpf, b.id AS b_id, u.id AS u_id " +
                "FROM loan l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN \"users\" u ON l.user_id = u.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setId(rs.getInt("loan_id"));
                loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
                loan.setReturnDate(rs.getDate("return_date").toLocalDate());
                Date actualReturnDate = rs.getDate("actual_return_date");
                loan.setActualReturnDate(actualReturnDate != null ? actualReturnDate.toLocalDate() : null);
                loan.setStatus(rs.getString("status"));
                loan.setFine(rs.getDouble("fine"));

                Book book = new Book();
                book.setId(rs.getInt("b_id"));
                book.setTitle(rs.getString("book_title"));
                book.setAuthor(rs.getString("book_author"));
                book.setCoverImagePath(rs.getString("book_cover_image_path"));
                loan.setBook(book);

                User user = new User();
                user.setId(rs.getInt("u_id"));
                user.setName(rs.getString("user_name"));
                user.setCpf(rs.getString("user_cpf"));
                loan.setUser(user);

                loans.add(loan);
            }
        }
        return loans;
    }
}