// pra quem quiser clonar meu repo no github

package connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexaoBD {
    private static String USERNAME = "SEU_USUARIO";
    private static String PASSWORD = "SUA_SENHA";
    private static String DRIVER = "org.postgresql.Driver";
    private static String URL = "jdbc:postgresql://SEU_HOST:5432/postgres";

    public Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar no banco!");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao importar driver!");
            throw new RuntimeException(ex);
        }
    }
}