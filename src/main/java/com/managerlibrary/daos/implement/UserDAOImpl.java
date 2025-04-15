package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.User;
import com.managerlibrary.infra.DataBaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO Usuario (nome, endereco, telefone, email, cpf) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getEmail()); // Adicionado email
            preparedStatement.setString(5, user.getCpf());   // Adicionado cpf
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
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
            return users;
        }
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Usuario SET nome = ?, endereco = ?, telefone = ?, email = ?, cpf = ? WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getEmail()); // Adicionado email
            preparedStatement.setString(5, user.getCpf());   // Adicionado cpf
            preparedStatement.setInt(6, user.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<User> findUsersByName(String name) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE nome LIKE ?";
        List<User> users = new ArrayList<>();
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
            return users;
        }
    }

    @Override
    public User findUserByCPF(String cpf) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE cpf = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cpf);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return createUser(resultSet);
            }
            return null;
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
        user.setEmail(resultSet.getString("email")); // Mapeando email
        user.setCpf(resultSet.getString("cpf"));     // Mapeando cpf
        return user;
    }
}