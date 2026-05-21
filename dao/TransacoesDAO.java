package dao;

import connection.ConexaoBD;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import model.Transacoes;

public class TransacoesDAO {
    private final Connection conexao;

    public TransacoesDAO(){
        this.conexao = new ConexaoBD().getConnection();
    }
    public List<Transacoes> consultarExtrato(int idUsuario) {
        List<Transacoes> extrato = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Transacao WHERE id_usuario = ?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transacoes itemTransacao = new Transacoes();
                itemTransacao.setUsuarioId(rs.getInt("usuarioId"));
                itemTransacao.setDescricao(rs.getString("descricao"));
                itemTransacao.setValor(rs.getDouble("valor"));
                itemTransacao.setData(rs.getString("data"));
                itemTransacao.setHora(rs.getString("hora"));
                itemTransacao.setTransacao(rs.getString("transacao"));
                extrato.add(itemTransacao);
            }
            rs.close();
            ps.close();
            return extrato;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Erro ao recuperar extrato!");
            throw new RuntimeException(e);
        }
    }
}
