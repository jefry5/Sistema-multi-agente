
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class AgenteLogistica extends Agent {

    protected void setup() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("calculo-envio");
        sd.setName("Servicio-Logistica");
        registrarServicio(sd);

        System.out.println("Agente Logistica " + getLocalName() + " listo.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String zona = msg.getContent();
                    ACLMessage reply = msg.createReply();

                    String respuestaLogistica;
                    if (zona.equalsIgnoreCase("Norte")) {
                        respuestaLogistica = "Costo: $10 - Tiempo: 24h";
                    } else if (zona.equalsIgnoreCase("Sur")) {
                        respuestaLogistica = "Costo: $20 - Tiempo: 48h";
                    } else {
                        respuestaLogistica = "Costo: $15 - Tiempo: 36h";
                    }

                    reply.setContent(respuestaLogistica);
                    myAgent.send(reply);
                    System.out.println(getLocalName() + ": Calcule envio para zona " + zona);
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