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
                String sql = "SELECT * FROM Usuario WHERE id_usuario = ?";
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
        }
            public Usuario autenticarUsuario(String email, String senha) {
                try {
                    String sql = "SELECT * FROM Usuario WHERE email = ? AND senha = ?";
                    PreparedStatement ps = conexao.prepareStatement(sql);
                    ps.setString(1, email);
                    ps.setString(2, senha);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Usuario usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNome(rs.getString("nome"));
                        usuario.setEmail(rs.getString("email"));
                        usuario.setCelular(rs.getString("celular"));
                        usuario.setDataDeNascimento(rs.getString("dataDeNascimento"));
                        usuario.setSenha(rs.getString("senha"));
                        return usuario;
                    }
                    return null;

            }   catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao autenticar usuario");
                    throw new RuntimeException(e);
                }
            }
    public Usuario buscarPorEmail(String email) {
        try {
            String sql = "SELECT * FROM Usuario WHERE email = ?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCelular(rs.getString("celular"));
                usuario.setDataDeNascimento(rs.getString("dataDeNascimento"));
                usuario.setSenha(rs.getString("senha"));
                return usuario;
            }
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar usuário!");
            throw new RuntimeException(e);
        }
    }

    public void inserirUsuario(Usuario u) {
        try {
            String sql = "INSERT INTO Usuario (nome, email, celular, dataDeNascimento, senha) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getCelular());
            ps.setString(4, u.getDataDeNascimento());
            ps.setString(5, u.getSenha());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir usuário!");
            throw new RuntimeException(e);
        }
    }

    public void atualizarUsuario(Usuario u) {
        try {
            String sql = "UPDATE Usuario SET nome=?, email=?, celular=?, dataDeNascimento=?, senha=? WHERE id_usuario=?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getCelular());
            ps.setString(4, u.getDataDeNascimento());
            ps.setString(5, u.getSenha());
            ps.setInt(6, u.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar usuário!");
            throw new RuntimeException(e);
        }
    }

    public void excluirUsuario(int id) {
        try {
            String sql = "DELETE FROM Usuario WHERE id_usuario=?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir usuário!");
            throw new RuntimeException(e);
        }
    }
    public Usuario buscarPorId(int id) {
        try {
            String sql = "SELECT * FROM Usuario WHERE id = ?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCelular(rs.getString("celular"));
                usuario.setDataDeNascimento(rs.getString("dataDeNascimento"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setSaldo(rs.getDouble("saldo"));
                return usuario;
            }
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar usuário!");
            throw new RuntimeException(e);
        }
    }

    public void descontarSaldo(int id, double valor) {
        try {
            String sql = "UPDATE Usuario SET saldo = saldo - ? WHERE id = ?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setDouble(1, valor);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao descontar saldo!");
            throw new RuntimeException(e);
        }
    }
        }

