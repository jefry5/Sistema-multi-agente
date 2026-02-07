import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class AgenteComprador extends Agent {

    private String[] listaCompras = {"Laptop,Norte", "Smartphone,Sur", "Tablet,Centro"};
    private int indice = 0;

    protected void setup() {
        System.out.println("Agente Comprador " + getLocalName() + " iniciando compras...");

        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                if (indice < listaCompras.length) {
                    String pedido = listaCompras[indice];
                    
                    AID vendedor = buscarVendedor();
                    
                    if (vendedor != null) {
                        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                        msg.addReceiver(vendedor);
                        msg.setContent(pedido);
                        myAgent.send(msg);
                        System.out.println("Comprador: Envie solicitud para " + pedido);
                        
                        ACLMessage respuesta = myAgent.blockingReceive(5000);
                        if (respuesta != null) {
                            System.out.println("Comprador: Respuesta recibida -> " + respuesta.getContent());
                        } else {
                            System.out.println("Comprador: No recibi respuesta a tiempo.");
                        }
                        
                        indice++;
                    } else {
                        System.out.println("Comprador: No encontre vendedor disponible en Paginas Amarillas.");
                    }
                } else {
                    System.out.println("Comprador: Ya realice todas mis compras. Terminando.");
                    stop();
                    doDelete();
                }
            }
        });
    }

    private AID buscarVendedor() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("realizar-venta");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            if (result.length > 0) {
                return result[0].getName();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;
    }
}