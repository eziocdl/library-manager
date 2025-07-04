package com.managerlibrary.infra;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados PostgreSQL.
 * Implementa um padrão singleton para a conexão.
 */
public class DataBaseConnection {

    // Mantido private para encapsulamento e segurança.
    private static Connection connection;

    // Construtor privado para evitar instâncias da classe (é uma classe utilitária estática)
    private DataBaseConnection() {
        // Construtor privado para evitar a criação de instâncias.
    }

    /**
     * Obtém uma conexão com o banco de dados PostgreSQL.
     * Se a conexão ainda não existir ou estiver fechada, uma nova conexão é criada.
     * As informações de conexão são lidas do arquivo application.properties.
     *
     * @return A conexão com o banco de dados.
     * @throws SQLException Se ocorrer um erro ao estabelecer a conexão,
     * a exceção original é relançada para ser tratada pelo chamador.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Usa try-with-resources para garantir que o InputStream seja fechado
            try (InputStream inputStream = DataBaseConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
                Properties properties = new Properties();

                if (inputStream == null) {
                    // Lançar uma exceção mais específica ou relançar SQLException com uma mensagem clara
                    throw new SQLException("Erro: Arquivo application.properties não encontrado no classpath.");
                }

                properties.load(inputStream);

                String url = properties.getProperty("jdbc.url");
                String user = properties.getProperty("jdbc.user");
                String password = properties.getProperty("jdbc.password");

                // Class.forName("org.postgresql.Driver"); // Geralmente não é necessário para drivers JDBC 4.0+,
                // o DriverManager já deve encontrar o driver automaticamente.
                // Pode ser útil para compatibilidade com versões muito antigas do JDBC.

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Conexão com o banco de dados estabelecida com sucesso.");

            } catch (IOException e) {
                // Envolver a IOException em uma SQLException para uniformizar o tratamento de erros
                throw new SQLException("Erro ao carregar application.properties: " + e.getMessage(), e);
            } catch (SQLException e) {
                // Relança a SQLException original
                System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
                throw e; // Lança a exceção para que o chamador possa lidar com ela
            }
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados, se ela estiver aberta.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    connection = null; // Zera a referência após fechar
                    System.out.println("Conexão com o banco de dados fechada.");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
                // Você pode escolher relançar uma RuntimeException aqui se quiser forçar o tratamento,
                // mas para fechar a conexão, logar é geralmente suficiente.
            }
        }
    }
}