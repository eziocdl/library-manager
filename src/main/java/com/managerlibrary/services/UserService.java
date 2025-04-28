package com.managerlibrary.services;

import com.managerlibrary.daos.implement.UserDAOImpl; // Importe a implementação do DAO
import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class UserService {
    private UserDAO userDAO;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private int nextId = 1;

    public UserService() {
        this.userDAO = new UserDAOImpl(); // Inicialize o UserDAOImpl no construtor padrão
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void addUser(User user) throws SQLException {
        if (!isValidUser(user)) {
            throw new IllegalArgumentException("Dados do usuário inválidos.");
        }
        if (userDAO != null) {
            userDAO.insertUser(user);
        } else {
            // Isso agora só deve acontecer se a inicialização do DAO falhar
            user.setId(nextId++);
            users.add(user);
        }
    }

    public User getUserById(int id) throws SQLException {
        if (userDAO != null) {
            return userDAO.findUserById(id);
        } else {
            return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        }
    }

    public List<User> getAllUsers() throws SQLException {
        if (userDAO != null) {
            return userDAO.findAllUsers();
        } else {
            return users;
        }
    }

    public void updateUser(User user) throws SQLException {
        if (!isValidUser(user)) {
            throw new IllegalArgumentException("Dados do usuário inválidos.");
        }
        if (userDAO != null) {
            userDAO.updateUser(user);
        } else {
            int index = -1;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == user.getId()) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                users.set(index, user);
            }
        }
    }

    public void deleteUser(int id) throws SQLException {
        if (userDAO != null) {
            userDAO.deleteUser(id);
        } else {
            users.removeIf(user -> user.getId() == id);
        }
    }

    public List<User> findUsersByName(String name) throws SQLException {
        if (userDAO != null) {
            return userDAO.findUsersByName(name);
        } else {
            return users.stream()
                    .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    public User findUserByCPF(String cpf) throws SQLException {
        if (userDAO != null) {
            return userDAO.findUserByCPF(cpf);
        } else {
            return users.stream()
                    .filter(user -> user.getCpf().equals(cpf))
                    .findFirst()
                    .orElse(null);
        }
    }

    public List<User> findUsersByNameOrCPFOrEmail(String searchTerm) throws SQLException {
        if (userDAO != null) {
            return userDAO.findUsersByNameOrCPFOrEmail(searchTerm); // Assumindo que seu DAO tem este método
        } else {
            String lowerSearchTerm = searchTerm.toLowerCase();
            List<User> results = new ArrayList<>();
            for (User user : users) {
                if (user.getName().toLowerCase().contains(lowerSearchTerm) ||
                        user.getCpf().contains(searchTerm) ||
                        user.getEmail().toLowerCase().contains(lowerSearchTerm)) {
                    results.add(user);
                }
            }
            return results;
        }
    }

    private boolean isValidUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return false;
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
            if (!pattern.matcher(user.getEmail()).matches()) {
                return false;
            }
        }
        if (user.getCpf() != null && !user.getCpf().isEmpty() && !user.getCpf().matches("\\d{11}")) {
            return false;
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty() && !user.getPhone().matches("\\d+")) {
            return false;
        }
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    // Alias para consistência com o nome usado no UserController
    public void removeUser(int id) throws SQLException {
        deleteUser(id);
    }
}