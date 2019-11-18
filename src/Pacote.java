import java.io.Serializable;

public class Pacote implements Serializable {

    private String enderecoOrigem;
    private String enderecoDestino;
    private String conteudo;
    private int ttl;

    Pacote(String enderecoOrigem, String enderecoDestino, String conteudo) {
        this.enderecoOrigem = enderecoOrigem;
        this.enderecoDestino = enderecoDestino;
        this.conteudo = conteudo;
        this.ttl = 5;
    }

    String getEnderecoOrigem() {
        return enderecoOrigem;
    }

    public void setEnderecoOrigem(String enderecoOrigem) {
        this.enderecoOrigem = enderecoOrigem;
    }

    String getEnderecoDestino() {
        return enderecoDestino;
    }

    public void setEnderecoDestino(String enderecoDestino) {
        this.enderecoDestino = enderecoDestino;
    }

    String getConteudo() {
        return conteudo;
    }

    int getTtl() {
        return ttl;
    }

    void decrementarTtl() {
        this.ttl--;
    }
}
