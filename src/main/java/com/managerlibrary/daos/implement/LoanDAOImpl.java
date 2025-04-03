package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.LoanDAO;
import com.managerlibrary.entities.Loan;
import com.managerlibrary.infra.DataBaseConnection;

import java.sql.*;
import java.util.List;


public class LoanDAOImpl implements LoanDAO {


    @Override
    public void insertLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO emprestimo (id, id_livro, id_usuario, data_emprestimo, data_devolucao) VALUES (?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, loan.getBookId());
            preparedStatement.setInt(2, loan.getUserId());
            preparedStatement.setDate(3, java.sql.Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(4, java.sql.Date.valueOf(loan.getReturnDate()));
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
            List<Loan> loans = new java.util.ArrayList<>();
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
            preparedStatement.setDate(3, java.sql.Date.valueOf(loan.getLoanDate()));
            preparedStatement.setDate(4, java.sql.Date.valueOf(loan.getReturnDate()));
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
