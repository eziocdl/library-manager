import com.managerlibrary.infra.DataBaseConnection;

import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionTest {

    public static void main(String[] args) {
        try {
            Connection connection = DataBaseConnection.getConnection();
            if (connection != null && !connection.isClosed()) {
                System.out.println("Conexão com o banco de dados bem-sucedida!");
                DataBaseConnection.closeConnection(); // Fechar a conexão após o teste
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
