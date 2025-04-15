package com.managerlibrary.infra;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados PostgreSQL.
 */
public class DataBaseConnection {

    public static Connection connection;

    /**
     * Obtém uma conexão com o banco de dados PostgreSQL.
     * Se a conexão ainda não existir ou estiver fechada, uma nova conexão é criada.
     * As informações de conexão são lidas do arquivo application.properties.
     *
     * @return A conexão com o banco de dados ou null em caso de erro.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Carrega as propriedades do arquivo application.properties
                Properties properties = new Properties();
                InputStream inputStream = DataBaseConnection.class.getClassLoader().getResourceAsStream("application.properties");

                if(inputStream != null){
                    properties.load(inputStream);
                } else{
                    System.err.println("Erro ao carregar application.properties: application.properties not found");
                    return null; // Retorna null em caso de erro ao ler o arquivo
                }

                // Obtém as informações de conexão das propriedades
                String url = properties.getProperty("jdbc.url");
                String user = properties.getProperty("jdbc.user");
                String password = properties.getProperty("jdbc.password");

                // Carrega o driver JDBC do PostgreSQL
                Class.forName("org.postgresql.Driver");

                // Estabelece a conexão com o banco de dados
                connection = DriverManager.getConnection(url, user, password);

                System.out.println("Conexão com o banco de dados estabelecida com sucesso.");

            } catch (ClassNotFoundException e) {
                System.err.println("Erro ao carregar o driver JDBC: " + e.getMessage());
                return null; // Retorna null em caso de erro
            } catch (SQLException e) {
                System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
                return null; // Retorna null em caso de erro
            } catch (IOException e) {
                System.err.println("Erro ao carregar application.properties: " + e.getMessage());
                return null; // Retorna null em caso de erro
            }
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados, se ela estiver aberta.
     *
     * @throws SQLException Se ocorrer um erro ao fechar a conexão.
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexão com o banco de dados fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}