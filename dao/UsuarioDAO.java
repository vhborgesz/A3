package dao;

import connection.ConexaoBD;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;

public class UsuarioDAO {
        private final Connection conexao;

        public UsuarioDAO(){
            this.conexao = new ConexaoBD().getConnection();
        }
        public List<Usuario> consultarUsuario(int id) {
            List<Usuario> usuario  = new ArrayList<>();
            try {
                String sql = "SELECT * FROM dao.Usuario WHERE id_usuario = ?";
                PreparedStatement ps = conexao.prepareStatement(sql);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Usuario itemUsuario = new Usuario();
                    itemUsuario.setId(rs.getInt("id"));
                    itemUsuario.setNome(rs.getString("nome"));
                    itemUsuario.setEmail(rs.getString("email"));
                    itemUsuario.setCelular(rs.getString("celular"));
                    itemUsuario.setDataDeNascimento(rs.getString("dataDeNascimento"));
                    itemUsuario.setSenha(rs.getString("senha"));
                    usuario.add(itemUsuario);
                }
                rs.close();
                ps.close();
                return usuario;
            }
            catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao recuperar usuario!");
                throw new RuntimeException(e);
            }
            public
        }
    }
