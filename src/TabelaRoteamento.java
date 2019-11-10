import java.util.ArrayList;
import java.util.List;

public class TabelaRoteamento {
    List<LinhaRoteamento> tabela;

    public TabelaRoteamento() {
        this.tabela = new ArrayList<LinhaRoteamento>();
    }

    public void inserirLinhaRoteamento(String redeDestino, String mascara, String gateway, String portaInterface) {
        tabela.add(new LinhaRoteamento(redeDestino, mascara, gateway, portaInterface));
    }

    public List<LinhaRoteamento> getTabela() {
        return tabela;
    }
}

class LinhaRoteamento {
    private String RedeDestino;
    private String Mascara;
    private String Gateway;
    private String PortaInterface;

    public LinhaRoteamento(String redeDestino, String mascara, String gateway, String portaInterface) {
        this.RedeDestino = redeDestino;
        this.Mascara = mascara;
        this.Gateway = gateway;
        this.PortaInterface = portaInterface;
    }

    public String getRedeDestino() {
        return RedeDestino;
    }

    public String getMascara() {
        return Mascara;
    }

    public String getGateway() {
        return Gateway;
    }

    public String getPortaInterface() {
        return PortaInterface;
    }
}
