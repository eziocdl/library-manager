package com.managerlibrary.services;

import com.managerlibrary.daos.interfaces.UserDAO;
import com.managerlibrary.entities.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
}

    /**
     * Adiciona um novo usuário ao banco de dados.
     *
     * @param usuario O usuário a ser adicionado.
     * @throws SQLException Se ocorrer um erro ao adicionar o usuário.
     */
    public void addUser(User usuario) throws SQLException {
        userDAO.insertUser(usuario);
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id O ID do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o usuário.
     */
    public User getUserById(int id) throws SQLException {
        return userDAO.findUserById(id);
    }

    /**
     * Busca todos os usuários no banco de dados.
     *
     * @return Uma lista com todos os usuários encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os usuários.
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAllUsers();
    }

    /**
     * Atualiza um usuário no banco de dados.
     *
     * @param user O usuário a ser atualizado.
     * @throws SQLException Se ocorrer um erro ao atualizar o usuário.
     */
    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }

    /**
     * Remove um usuário pelo ID.
     *
     * @param id O ID do usuário a ser removido.
     * @throws SQLException Se ocorrer um erro ao remover o usuário.
     */
    public void removeUser(int id) throws SQLException {
        userDAO.deleteUser(id);
    }

}
