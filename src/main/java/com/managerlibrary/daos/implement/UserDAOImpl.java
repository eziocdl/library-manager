package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

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

    @Override
    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, address, phone, email, cpf) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
        List<User> users = new ArrayList<>();
        System.out.println("UserDAOImpl.findAllUsers: Executando consulta: " + sql);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("UserDAOImpl.findAllUsers: Consulta executada com sucesso.");
            while (resultSet.next()) {
                users.add(createUser(resultSet));
            }
            System.out.println("UserDAOImpl.findAllUsers: Encontrados " + users.size() + " usuários.");
            return users;
        } catch (SQLException e) {
            System.err.println("UserDAOImpl.findAllUsers: Erro ao executar consulta: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, address = ?, phone = ?, email = ?, cpf = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<User> findUsersByName(String name) throws SQLException {
        String sql = "SELECT id, name, address, phone, email, cpf FROM users WHERE LOWER(name) LIKE ?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
}