package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.BookDAO;
import com.managerlibrary.daos.interfaces.LoanDAO;
import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import com.managerlibrary.infra.DataBaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {
    private Connection connection;
    private final BookDAO bookDAO;
    private final UserDAO userDAO;

    public LoanDAOImpl() throws SQLException {
        this.connection = DataBaseConnection.getConnection();
        this.bookDAO = new BookDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public void insertLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO emprestimo (book_id, user_id, loan_date, return_date, actual_return_date, status, fine) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, loan.getBook().getId());
            preparedStatement.setInt(2, loan.getUser().getId());
            preparedStatement.setDate(3, Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(4, Date.valueOf(loan.getReturnDate()));
            preparedStatement.setDate(5, loan.getActualReturnDate() != null ? Date.valueOf(loan.getActualReturnDate()) : null);
            preparedStatement.setString(6, loan.getStatus());
            preparedStatement.setDouble(7, loan.getFine());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Loan findLoanById(int id) throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, return_date, actual_return_date, status, fine FROM emprestimo WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createLoanWithDetails(resultSet);
            }
            return null;
        }
    }

    @Override
    public List<Loan> findAllLoans() throws SQLException {
        String sql = "SELECT id, book_id, user_id, loan_date, return_date, actual_return_date, status, fine FROM emprestimo";
        try (Connection connection = DataBaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            List<Loan> loans = new ArrayList<>();
            while (resultSet.next()) {
                loans.add(createLoanWithDetails(resultSet));
            }
            return loans;
        }
    }

    @Override
    public void updateLoan(Loan loan) throws SQLException {
        String sql = "UPDATE emprestimo SET book_id = ?, user_id = ?, loan_date = ?, return_date = ?, actual_return_date = ?, status = ?, fine = ? WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, loan.getBook().getId());
            preparedStatement.setInt(2, loan.getUser().getId());
            preparedStatement.setDate(3, Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(4, Date.valueOf(loan.getReturnDate()));
            preparedStatement.setDate(5, loan.getActualReturnDate() != null ? Date.valueOf(loan.getActualReturnDate()) : null);
            preparedStatement.setString(6, loan.getStatus());
            preparedStatement.setDouble(7, loan.getFine());
            preparedStatement.setInt(8, loan.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteLoan(int id) throws SQLException {
        String sql = "DELETE FROM emprestimo WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Loan> getAllLoansWithDetails() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataBaseConnection.getConnection();
            String sql = "SELECT " +
                    "l.id AS loan_id, l.book_id, l.user_id, l.loan_date, l.return_date, l.actual_return_date, l.status, l.fine, " +
                    "b.title AS book_title, b.author AS book_author, b.isbn AS book_isbn, " +
                    "u.nome AS user_name, u.email AS user_email " +
                    "FROM emprestimo l " +
                    "JOIN books b ON l.book_id = b.id " +
                    "JOIN usuario u ON l.user_id = u.id";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Loan loan = new Loan();
                loan.setId(resultSet.getInt("loan_id"));
                loan.setLoanDate(resultSet.getDate("loan_date").toLocalDate());
                loan.setReturnDate(resultSet.getDate("return_date").toLocalDate());
                loan.setActualReturnDate(resultSet.getObject("actual_return_date", LocalDate.class));
                loan.setStatus(resultSet.getString("status"));
                loan.setFine(resultSet.getDouble("fine"));

                Book book = new Book();
                book.setTitle(resultSet.getString("book_title"));
                book.setAuthor(resultSet.getString("book_author"));
                book.setIsbn(resultSet.getString("book_isbn"));
                loan.setBook(book);

                User user = new User();
                user.setName(resultSet.getString("user_name"));
                user.setEmail(resultSet.getString("user_email"));
                loan.setUser(user);

                loans.add(loan);
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }
        return loans;
    }

    @Override
    public void markAsReturned(int loanId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE emprestimo SET actual_return_date = ?, status = 'Devolvido' WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, Date.valueOf(returnDate));
            preparedStatement.setInt(2, loanId);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Cria um objeto Loan com detalhes de Book e User a partir de um ResultSet.
     *
     * @param resultSet O ResultSet contendo os dados do empréstimo.
     * @return O objeto Loan criado com os detalhes de Book e User.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Loan createLoanWithDetails(ResultSet resultSet) throws SQLException {
        Loan loan = new Loan();
        loan.setId(resultSet.getInt("id"));
        loan.setLoanDate(resultSet.getDate("loan_date").toLocalDate());
        loan.setReturnDate(resultSet.getDate("return_date").toLocalDate());
        loan.setActualReturnDate(resultSet.getObject("actual_return_date", LocalDate.class));
        loan.setStatus(resultSet.getString("status"));
        loan.setFine(resultSet.getDouble("fine"));

        int bookId = resultSet.getInt("book_id");
        Book book = bookDAO.findBookById(bookId);
        loan.setBook(book);

        int userId = resultSet.getInt("user_id");
        User user = userDAO.findUserById(userId);
        loan.setUser(user);

        return loan;
    }
}