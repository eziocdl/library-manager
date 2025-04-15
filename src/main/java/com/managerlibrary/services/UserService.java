package com.managerlibrary.services;

import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private int nextId = 1;

    public UserService() {
        // Se você quiser simular o DAO em memória inicialmente
        // this.userDAO = null;
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Adiciona um novo usuário ao banco de dados (ou à lista em memória na simulação).
     *
     * @param user O usuário a ser adicionado.
     * @throws SQLException Se ocorrer um erro ao adicionar o usuário (na implementação real).
     */
    public void addUser(User user) throws SQLException {
        if (userDAO != null) {
            userDAO.insertUser(user);
        } else {
            user.setId(nextId++);
            users.add(user);
        }
    }

    /**
     * Busca um usuário pelo ID (do banco de dados ou da lista em memória).
     *
     * @param id O ID do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o usuário (na implementação real).
     */
    public User getUserById(int id) throws SQLException {
        if (userDAO != null) {
            return userDAO.findUserById(id);
        } else {
            return users.stream().filter(user -> user.getId() == id).findFirst().orElse(null);
        }
    }

    /**
     * Busca todos os usuários (do banco de dados ou da lista em memória).
     *
     * @return Uma lista com todos os usuários encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os usuários (na implementação real).
     */
    public List<User> getAllUsers() throws SQLException {
        if (userDAO != null) {
            return userDAO.findAllUsers();
        } else {
            return users;
        }
    }

    /**
     * Atualiza um usuário no banco de dados (ou na lista em memória).
     *
     * @param user O usuário a ser atualizado.
     * @throws SQLException Se ocorrer um erro ao atualizar o usuário (na implementação real).
     */
    public void updateUser(User user) throws SQLException {
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

    /**
     * Remove um usuário pelo ID (do banco de dados ou da lista em memória).
     *
     * @param id O ID do usuário a ser removido.
     * @throws SQLException Se ocorrer um erro ao remover o usuário (na implementação real).
     */
    public void removeUser(int id) throws SQLException {
        if (userDAO != null) {
            userDAO.deleteUser(id);
        } else {
            users.removeIf(user -> user.getId() == id);
        }
    }
}