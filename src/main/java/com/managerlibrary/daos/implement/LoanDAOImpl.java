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
        this.bookDAO = new BookDAOImpl(); // Inicialize o BookDAOImpl
        this.userDAO = new UserDAOImpl(); // Inicialize o UserDAOImpl
    }

    // ... (seu método insertLoan está correto) ...

    @Override
    public void insertLoan(Loan loan) throws SQLException {

    }

    @Override
    public Loan findLoanById(int id) throws SQLException {
        String sql = "SELECT * FROM emprestimo WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createLoanWithDetails(resultSet); // Use o método que busca os detalhes
            }
            return null;
        }
    }

    @Override
    public List<Loan> findAllLoans() throws SQLException {
        String sql = "SELECT * FROM emprestimo";
        try (Connection connection = DataBaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            List<Loan> loans = new ArrayList<>();
            while (resultSet.next()) {
                loans.add(createLoanWithDetails(resultSet)); // Use o método que busca os detalhes
            }
            return loans;
        }
    }

    @Override
    public void updateLoan(Loan loan) throws SQLException {

    }

    @Override
    public void deleteLoan(int id) throws SQLException {

    }

    // ... (seu método updateLoan está correto) ...
    // ... (seu método deleteLoan está correto) ...

    @Override
    public List<Loan> getAllLoansWithDetails() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DataBaseConnection.getConnection();
            String sql = "SELECT " +
                    "l.id AS loan_id, l.id_livro, l.id_usuario, l.data_emprestimo, l.data_devolucao, l.data_devolucao_real, l.status, l.multa, " +
                    "b.titulo AS book_title, b.autor AS book_author, b.isbn AS book_isbn, " +
                    "u.nome AS user_name, u.email AS user_email " +
                    "FROM emprestimo l " +
                    "JOIN livro b ON l.id_livro = b.id " +
                    "JOIN usuario u ON l.id_usuario = u.id";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Loan loan = new Loan();
                loan.setId(resultSet.getInt("loan_id"));
                loan.setLoanDate(resultSet.getDate("data_emprestimo").toLocalDate());
                loan.setReturnDate(resultSet.getDate("data_devolucao").toLocalDate());
                loan.setActualReturnDate(resultSet.getObject("data_devolucao_real", LocalDate.class));
                loan.setStatus(resultSet.getString("status"));
                loan.setFine(resultSet.getDouble("multa"));

                // Buscando e associando o Livro
                int bookId = resultSet.getInt("id_livro");
                Book book = bookDAO.findBookById(bookId);
                loan.setBook(book);

                // Buscando e associando o Usuário
                int userId = resultSet.getInt("id_usuario");
                User user = userDAO.findUserById(userId);
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
        loan.setLoanDate(resultSet.getDate("data_emprestimo").toLocalDate());
        loan.setReturnDate(resultSet.getDate("data_devolucao").toLocalDate());

        int bookId = resultSet.getInt("id_livro");
        Book book = bookDAO.findBookById(bookId);
        loan.setBook(book);

        int userId = resultSet.getInt("id_usuario");
        User user = userDAO.findUserById(userId);
        loan.setUser(user);

        return loan;
    }
}