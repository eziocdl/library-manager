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
        String sql = "INSERT INTO users (name, address, phone, email, cpf) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getCpf());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public User findUserById(int id) throws SQLException {
        String sql = "SELECT id, name, address, phone, email, cpf FROM users WHERE id = ?";
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
        String sql = "SELECT id, name, address, phone, email, cpf FROM users";
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
        String sql = "UPDATE users SET name = ?, address = ?, phone = ?, email = ?, cpf = ? WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getCpf());
            preparedStatement.setInt(6, user.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<User> findUsersByName(String name) throws SQLException {
        String sql = "SELECT id, name, address, phone, email, cpf FROM users WHERE LOWER(name) LIKE ?";
        List<User> users = new ArrayList<>();
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + name.toLowerCase() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
            return users;
        }
    }

    @Override
    public User findUserByCPF(String cpf) throws SQLException {
        String sql = "SELECT id, name, address, phone, email, cpf FROM users WHERE cpf = ?";
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

    @Override
    public List<User> findUsersByNameOrCPFOrEmail(String searchTerm) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, address, phone, email, cpf FROM users WHERE LOWER(name) LIKE ? OR cpf LIKE ? OR LOWER(email) LIKE ?";
        try (Connection connection = DataBaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm.toLowerCase() + "%";
            preparedStatement.setString(1, likeTerm);
            preparedStatement.setString(2, searchTerm); // Busca exata para CPF
            preparedStatement.setString(3, likeTerm);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
            return users;
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
        user.setName(resultSet.getString("name"));
        user.setAddress(resultSet.getString("address"));
        user.setPhone(resultSet.getString("phone"));
        user.setEmail(resultSet.getString("email"));
        user.setCpf(resultSet.getString("cpf"));
        return user;
    }
}