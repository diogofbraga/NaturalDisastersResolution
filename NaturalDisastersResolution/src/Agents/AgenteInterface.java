import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.sql.Time;


public class AgenteInterface extends Agent {

    GUI gui;

    AID centralAgent;
    Mapa mapa;
    int requestFreq = 1000; //ms



    protected void setup(){
        Object[] args = this.getArguments();

        this.mapa = (Mapa) args[0];

        this.centralAgent = DFManager.findAgent(this, "Central");

        this.gui = new GUI(mapa);
        gui.getFrame().setVisible(true);

        this.addBehaviour(new InfoReceiver());
        this.addBehaviour(new InfoRequester(this, this.requestFreq));
    }

    class InfoReceiver extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String sendersName = msg.getSender().getLocalName();
                if (sendersName.equals("Central") && msg.getPerformative() == ACLMessage.INFORM) {
                    System.out.println(new Time(System.currentTimeMillis()) + ": Resposta recebida");

                    try {
                        DeltaSimulationStatus stats = (DeltaSimulationStatus) msg.getContentObject();
                        updateGui(stats);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

            } else {
                block();
            }

        }

    }


    class InfoRequester extends TickerBehaviour {

        public InfoRequester(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            requestInfo();
        }
    }

    private void requestInfo() {
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
        msg.addReceiver(this.centralAgent);
        msg.setContent("1");
        send(msg);
    }

    private void updateGui(DeltaSimulationStatus stats) {
        this.gui.mapGrid.updateGrid(stats);
    }
}
