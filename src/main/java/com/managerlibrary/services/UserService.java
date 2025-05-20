package com.managerlibrary.services;

import com.managerlibrary.daos.interfaces.UserDAO; // Usando a interface para maior flexibilidade
import com.managerlibrary.entities.User;
import java.sql.SQLException;
import java.util.ArrayList; // Para retornar listas vazias em vez de null
import java.util.List;
import java.util.Objects; // Para Objects.requireNonNull
import java.util.regex.Pattern; // Para validação de email

/**
 * Serviço responsável por gerenciar as operações de negócio relacionadas a usuários.
 * Atua como uma camada intermediária entre os controladores e o DAO de usuários,
 * aplicando regras de negócio e validações.
 */
public class UserService {
    private final UserDAO userDAO; // Declarado como final e sem a ObservableList

    /**
     * Construtor do UserService.
     * Este construtor exige uma implementação de UserDAO, garantindo que o serviço
     * sempre opere com persistência de dados.
     *
     * @param userDAO A implementação de UserDAO a ser utilizada para acesso a dados.
     * Não pode ser nula.
     */
    public UserService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "UserDAO não pode ser nulo.");
    }

    /**
     * Adiciona um novo usuário ao banco de dados.
     *
     * @param user O objeto User a ser adicionado.
     * @throws IllegalArgumentException Se os dados do usuário forem inválidos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public void addUser(User user) throws SQLException {
        if (!isValidUser(user)) {
            throw new IllegalArgumentException("Dados do usuário inválidos.");
        }
        // Poderia adicionar uma verificação de CPF/Email duplicado aqui antes de inserir.
        userDAO.insertUser(user);
    }

    /**
     * Busca um usuário pelo seu ID.
     *
     * @param id O ID do usuário a ser buscado.
     * @return O objeto User correspondente ao ID, ou null se não for encontrado.
     * @throws IllegalArgumentException Se o ID for inválido (menor ou igual a zero).
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public User getUserById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido.");
        }
        return userDAO.findUserById(id);
    }

    /**
     * Retorna todos os usuários cadastrados no banco de dados.
     *
     * @return Uma lista de todos os usuários. Retorna uma lista vazia se não houver usuários.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = userDAO.findAllUsers();
        return users != null ? users : new ArrayList<>(); // Garante que nunca retorna null
    }

    /**
     * Atualiza as informações de um usuário existente no banco de dados.
     *
     * @param user O objeto User com as informações atualizadas.
     * @throws IllegalArgumentException Se os dados do usuário ou o ID forem inválidos.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public void updateUser(User user) throws SQLException {
        if (user == null || user.getId() <= 0 || !isValidUser(user)) {
            throw new IllegalArgumentException("Dados do usuário para atualização inválidos.");
        }
        // Poderia adicionar uma verificação de CPF/Email duplicado para o caso de update.
        userDAO.updateUser(user);
    }

    /**
     * Exclui um usuário do banco de dados pelo seu ID.
     *
     * @param id O ID do usuário a ser excluído.
     * @throws IllegalArgumentException Se o ID for inválido.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public void deleteUser(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do usuário para exclusão inválido.");
        }
        // Lógica de negócio: pode-se verificar se o usuário tem empréstimos ativos antes de excluir.
        userDAO.deleteUser(id);
    }

    /**
     * Busca usuários pelo nome.
     *
     * @param name O nome do usuário a ser buscado (pode ser parcial).
     * @return Uma lista de usuários que correspondem ao nome. Retorna uma lista vazia se não houver correspondências.
     * @throws IllegalArgumentException Se o termo de busca for nulo ou vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<User> findUsersByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Termo de busca para nome não pode ser vazio.");
        }
        List<User> users = userDAO.findUsersByName(name);
        return users != null ? users : new ArrayList<>();
    }

    /**
     * Busca um usuário pelo seu CPF.
     *
     * @param cpf O CPF do usuário a ser buscado.
     * @return O objeto User correspondente ao CPF, ou null se não for encontrado.
     * @throws IllegalArgumentException Se o CPF for nulo ou vazio.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public User findUserByCPF(String cpf) throws SQLException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.");
        }
        return userDAO.findUserByCPF(cpf);
    }

    /**
     * Busca usuários pelo nome, CPF ou email.
     *
     * @param searchTerm O termo de busca (nome, CPF ou email parcial).
     * @return Uma lista de usuários que correspondem ao termo de busca. Retorna uma lista vazia se não houver correspondências.
     * @throws SQLException Se ocorrer um erro de acesso ao banco de dados.
     */
    public List<User> findUsersByNameOrCPFOrEmail(String searchTerm) throws SQLException {
        // Se o termo de busca for vazio, pode-se retornar todos os usuários ou uma lista vazia.
        // Aqui, optamos por retornar todos os usuários se a busca for vazia.
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers();
        }
        List<User> users = userDAO.findUsersByNameOrCPFOrEmail(searchTerm);
        return users != null ? users : new ArrayList<>();
    }

    /**
     * Realiza a validação dos dados de um objeto User.
     *
     * @param user O objeto User a ser validado.
     * @return true se o usuário for válido, false caso contrário.
     */
    private boolean isValidUser(User user) {
        if (user == null) {
            return false;
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return false;
        }
        // Validação de Email (se não for nulo/vazio, deve ser válido)
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
            if (!pattern.matcher(user.getEmail()).matches()) {
                return false;
            }
        }
        // Validação de CPF (se não for nulo/vazio, deve ter 11 dígitos numéricos)
        if (user.getCpf() != null && !user.getCpf().isEmpty() && !user.getCpf().matches("\\d{11}")) {
            return false;
        }
        // Validação de Telefone (se não for nulo/vazio, deve conter apenas dígitos)
        if (user.getPhone() != null && !user.getPhone().isEmpty() && !user.getPhone().matches("\\d+")) {
            return false;
        }
        // Validação de Endereço (se for obrigatório, descomente a linha abaixo)
        // if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
        //     return false;
        // }
        return true;
    }
}