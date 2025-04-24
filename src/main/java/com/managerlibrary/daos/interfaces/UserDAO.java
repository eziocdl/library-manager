package com.managerlibrary.daos.interfaces;

import com.managerlibrary.entities.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {

    /**
     * Insere um novo usuário no banco de dados.
     *
     * @param user O usuário a ser inserido.
     * @throws SQLException Se ocorrer um erro ao inserir o usuário.
     */
    void insertUser(User user) throws SQLException;

    /**
     * Busca um usuário pelo ID.
     *
     * @param id O ID do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o usuário.
     */
    User findUserById(int id) throws SQLException;

    /**
     * Busca todos os usuários no banco de dados.
     *
     * @return Uma lista com todos os usuários encontrados.
     * @throws SQLException Se ocorrer um erro ao buscar os usuários.
     */
    List<User> findAllUsers() throws SQLException;

    /**
     * Atualiza um usuário no banco de dados.
     *
     * @param user O usuário a ser atualizado.
     * @throws SQLException Se ocorrer um erro ao atualizar o usuário.
     */
    void updateUser(User user) throws SQLException;

    /**
     * Deleta um usuário pelo ID.
     *
     * @param id O ID do usuário a ser deletado.
     * @throws SQLException Se ocorrer um erro ao deletar o usuário.
     */
    void deleteUser(int id) throws SQLException;

    /**
     * Busca usuários pelo nome (pode ser uma busca parcial).
     *
     * @param name O nome ou parte do nome a ser buscado.
     * @return Uma lista de usuários que correspondem ao critério de busca.
     * @throws SQLException Se ocorrer um erro ao buscar os usuários.
     */
    List<User> findUsersByName(String name) throws SQLException;

    /**
     * Busca um usuário pelo CPF (deve ser uma busca exata).
     *
     * @param cpf O CPF do usuário a ser buscado.
     * @return O usuário encontrado, ou null se não encontrado.
     * @throws SQLException Se ocorrer um erro ao buscar o usuário.
     */
    User findUserByCPF(String cpf) throws SQLException;

    /**
     * Busca usuários pelo nome, CPF ou email (pode ser uma busca parcial no nome e email, exata no CPF).
     *
     * @param searchTerm O termo de busca.
     * @return Uma lista de usuários que correspondem ao critério de busca.
     * @throws SQLException Se ocorrer um erro ao buscar os usuários.
     */
    List<User> findUsersByNameOrCPFOrEmail(String searchTerm) throws SQLException;
}