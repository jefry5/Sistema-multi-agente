import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;
import java.util.Map;

public class AgenteAlmacen extends Agent {
    private Map<String, Integer> inventario;

    protected void setup() {
        inventario = new HashMap<>();
        inventario.put("Laptop", 5);
        inventario.put("Smartphone", 0);
        inventario.put("Tablet", 10);

        // Registro en Páginas Amarillas
        ServiceDescription sd = new ServiceDescription();
        sd.setType("verificar-stock");
        sd.setName("Servicio-Almacen");
        registrarServicio(sd);

        System.out.println("Agente Almacen " + getLocalName() + " listo.");

        // Comportamiento para atender peticiones de stock
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage mensaje = receive();
                if (mensaje != null) {
                    String producto = mensaje.getContent();
                    ACLMessage reply = mensaje.createReply();

                    if (inventario.containsKey(producto) && inventario.get(producto) > 0) {
                        reply.setContent("DISPONIBLE");
                        inventario.put(producto, inventario.get(producto) - 1);
                    } else {
                        reply.setContent("AGOTADO");
                    }
                    myAgent.send(reply);
                    System.out.println(getLocalName() + ": Verifiqué stock para " + producto + " -> " + reply.getContent());
                } else {
                    block();
                }
            }
        });
    }

    protected void registrarServicio(ServiceDescription sd) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}