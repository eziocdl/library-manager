package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.LoanDAO; // Importe a interface correta
import com.managerlibrary.entities.Book;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.entities.User;
import com.managerlibrary.infra.DataBaseConnection; // Use o nome correto da classe de conexão

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAOImpl implements LoanDAO {
    private Connection connection;

    public LoanDAOImpl() throws SQLException {
        this.connection = DataBaseConnection.getConnection();
    }

    @Override
    public void insertLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO emprestimo (id_livro, id_usuario, data_emprestimo, data_devolucao) VALUES (?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, loan.getBookId());
            preparedStatement.setInt(2, loan.getUserId());
            preparedStatement.setDate(3, Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(4, Date.valueOf(loan.getReturnDate()));
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Loan findLoanById(int id) throws SQLException {
        String sql = "SELECT * FROM emprestimo WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createLoan(resultSet);
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
                loans.add(createLoan(resultSet));
            }
            return loans;
        }
    }

    @Override
    public void updateLoan(Loan loan) throws SQLException {
        String sql = "UPDATE emprestimo SET id_livro = ?, id_usuario = ?, data_emprestimo = ?, data_devolucao = ? WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, loan.getBookId());
            preparedStatement.setInt(2, loan.getUserId());
            preparedStatement.setDate(3, Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(4, Date.valueOf(loan.getReturnDate()));
            preparedStatement.setInt(5, loan.getId());
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
                loan.setBookId(resultSet.getInt("id_livro"));
                loan.setUserId(resultSet.getInt("id_usuario"));
                loan.setLoanDate(resultSet.getDate("data_emprestimo").toLocalDate());
                loan.setReturnDate(resultSet.getDate("data_devolucao").toLocalDate());
                loan.setActualReturnDate(resultSet.getObject("data_devolucao_real", LocalDate.class));
                loan.setStatus(resultSet.getString("status"));
                loan.setFine(resultSet.getDouble("multa"));

                // Adicionando detalhes do Livro
                Book book = new Book();
                book.setId(resultSet.getInt("id_livro"));
                book.setTitle(resultSet.getString("book_title"));
                book.setAuthor(resultSet.getString("book_author"));
                book.setIsbn(resultSet.getString("book_isbn"));
                loan.setBook(book);

                // Adicionando detalhes do Usuário
                User user = new User();
                user.setId(resultSet.getInt("id_usuario"));
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

    /**
     * Cria um objeto Loan a partir de um ResultSet.
     *
     * @param resultSet O ResultSet contendo os dados do empréstimo.
     * @return O objeto Loan criado.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Loan createLoan(ResultSet resultSet) throws SQLException {
        Loan loan = new Loan();
        loan.setId(resultSet.getInt("id"));
        loan.setBookId(resultSet.getInt("id_livro"));
        loan.setUserId(resultSet.getInt("id_usuario"));
        loan.setLoanDate(resultSet.getDate("data_emprestimo").toLocalDate());
        loan.setReturnDate(resultSet.getDate("data_devolucao").toLocalDate());
        return loan;
    }
}