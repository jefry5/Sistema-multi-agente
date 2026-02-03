import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class AgenteVendedor extends Agent {

    protected void setup() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("realizar-venta");
        sd.setName("Servicio-Ventas");
        registrarServicio(sd);

        System.out.println("Agente Vendedor " + getLocalName() + " listo.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String contenido = msg.getContent();
                    String[] partes = contenido.split(",");
                    
                    if(partes.length == 2) {
                        String producto = partes[0].trim();
                        String zona = partes[1].trim();

                        System.out.println(getLocalName() + ": Procesando pedido de " + producto + " para zona " + zona);
                        
                        AID agenteAlmacen = buscarServicio("verificar-stock");
                        if (agenteAlmacen != null) {
                            String respuestaStock = enviarMensajeSincrono(agenteAlmacen, producto);
                            
                            if ("DISPONIBLE".equals(respuestaStock)) {
                                AID agenteLogistica = buscarServicio("calculo-envio");
                                if (agenteLogistica != null) {
                                    String infoEnvio = enviarMensajeSincrono(agenteLogistica, zona);
                                    
                                    ACLMessage respuestaCliente = msg.createReply();
                                    respuestaCliente.setContent("COMPRA EXITOSA: " + producto + ". Detalles envio: " + infoEnvio);
                                    myAgent.send(respuestaCliente);
                                }
                            } else {
                                ACLMessage respuestaCliente = msg.createReply();
                                respuestaCliente.setContent("FALLO: Producto " + producto + " agotado.");
                                myAgent.send(respuestaCliente);
                            }
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }

    private AID buscarServicio(String tipo) {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipo);
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

    private String enviarMensajeSincrono(AID receptor, String contenido) {
        ACLMessage peticion = new ACLMessage(ACLMessage.REQUEST);
        peticion.addReceiver(receptor);
        peticion.setContent(contenido);
        send(peticion);
        
        ACLMessage respuesta = blockingReceive();
        if (respuesta != null) {
            return respuesta.getContent();
        }
        return null;
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
