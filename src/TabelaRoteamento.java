/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */

import java.util.ArrayList;
import java.util.List;

class TabelaRoteamento {
    private List<LinhaRoteamento> tabela;

    TabelaRoteamento() {
        this.tabela = new ArrayList<>();
    }

    void inserirLinhaRoteamento(String redeDestino, String mascara, String gateway, String portaInterface) {
        tabela.add(new LinhaRoteamento(redeDestino, mascara, gateway, Integer.parseInt(portaInterface)));
    }

    List<LinhaRoteamento> getTabela() {
        return tabela;
    }
}

class LinhaRoteamento {
    private String RedeDestino;
    private String Mascara;
    private String Gateway;
    private int PortaInterface;

    LinhaRoteamento(String redeDestino, String mascara, String gateway, int portaInterface) {
        this.RedeDestino = redeDestino;
        this.Mascara = validateMask(mascara);
        this.Gateway = gateway;
        this.PortaInterface = portaInterface;
    }

    private String validateMask(String mascara) {
        if (!mascara.contains(".")) {
            int cidr = Integer.parseInt(mascara);
            if (cidr >= 0 && cidr <= 32) {
                return SubnetUtils.formatCidrNotationToMask(cidr);
            }
        }
        return mascara;
    }

    String getRedeDestino() {
        return RedeDestino;
    }

    String getMascara() {
        return Mascara;
    }

    int getCidrNotation() {
        return SubnetUtils.formatMaskToCidrNotation(getMascara());
    }

    String getGateway() {
        return Gateway;
    }

    int getPortaInterface() {
        return PortaInterface;
    }
}
