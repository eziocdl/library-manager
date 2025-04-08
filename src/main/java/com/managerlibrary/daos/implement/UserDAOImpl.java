package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.User;
import com.managerlibrary.infra.DataBaseConnection;

import java.sql.*;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO Usuario (nome, endereco, telefone) VALUES (?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.executeUpdate();
        }


    }

    @Override
    public User findUserById(int id) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createUser(resultSet);
            }
            return null;
        }


    }

    @Override
    public List<User> findAllUsers() throws SQLException {
        String sql = "SELECT * FROM Usuario";
        try (Connection connection = DataBaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            List<User> users = new java.util.ArrayList<>();
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
            return users;
        }
        
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Usuario SET nome = ?, endereco = ?, telefone = ? WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setInt(4, user.getId());
            preparedStatement.executeUpdate();
        }


    }

    @Override
    public void deleteUser(int id) throws SQLException {
        String Sql = "DELETE FROM Usuario WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Cria um objeto Usuario a partir de um ResultSet.
     *
     * @param resultSet O ResultSet contendo os dados do usuário.
     * @return O objeto Usuario criado.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */

    private User createUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("nome"));
        user.setAddress(resultSet.getString("endereco"));
        user.setPhone(resultSet.getString("telefone"));
        return user;
    }
}
