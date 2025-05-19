package com.managerlibrary.daos.implement;

import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private Connection connection;

    private static final String INSERT_USER_SQL = "INSERT INTO users (name, address, phone, email, cpf, profile_image_path) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String FIND_USER_BY_ID_SQL = "SELECT id, name, address, phone, email, cpf, profile_image_path FROM users WHERE id = ?";
    private static final String FIND_ALL_USERS_SQL = "SELECT id, name, address, phone, email, cpf, profile_image_path FROM users";
    private static final String UPDATE_USER_SQL = "UPDATE users SET name = ?, address = ?, phone = ?, email = ?, cpf = ?, profile_image_path = ? WHERE id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?";
    private static final String FIND_USERS_BY_NAME_SQL = "SELECT id, name, address, phone, email, cpf, profile_image_path FROM users WHERE LOWER(name) LIKE ?";
    private static final String FIND_USER_BY_CPF_SQL = "SELECT id, name, address, phone, email, cpf, profile_image_path FROM users WHERE cpf = ?";
    private static final String FIND_USERS_BY_NAME_OR_CPF_OR_EMAIL_SQL = "SELECT id, name, address, phone, email, cpf, profile_image_path FROM users WHERE LOWER(name) LIKE ? OR cpf LIKE ? OR LOWER(email) LIKE ?";

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
        user.setProfileImagePath(resultSet.getString("profile_image_path"));
        return user;
    }

    @Override
    public void insertUser(User user) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getCpf());
            preparedStatement.setString(6, user.getProfileImagePath());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public User findUserById(int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_SQL)) {
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
        List<User> users = new ArrayList<>();
        System.out.println("UserDAOImpl.findAllUsers: Executando consulta: " + FIND_ALL_USERS_SQL);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_USERS_SQL)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getAddress());
            preparedStatement.setString(3, user.getPhone());
            preparedStatement.setString(4, user.getEmail());
            preparedStatement.setString(5, user.getCpf());
            preparedStatement.setString(6, user.getProfileImagePath());
            preparedStatement.setInt(7, user.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void deleteUser(int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<User> findUsersByName(String name) throws SQLException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERS_BY_NAME_SQL)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_CPF_SQL)) {
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
        try (PreparedStatement preparedStatement = connection.prepareStatement(FIND_USERS_BY_NAME_OR_CPF_OR_EMAIL_SQL)) {
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