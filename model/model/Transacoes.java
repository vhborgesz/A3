package model;
public class Transacoes {
private int id;
private int usuarioId;
private String descricao; 
private double valor; 
private String data;
private String hora;
private String transacao;


public int getId() { return id ; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId ; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getDescricao() { return descricao ; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor ; }
    public void setValor(double valor) { this.valor = valor; }

    public String getData() { return data ; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getTransacao() { return transacao; }
    public void setTransacao(String transacao) { this.transacao = transacao; }
};
