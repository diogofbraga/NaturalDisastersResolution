public class Drone extends AgenteParticipativo {

    static int capacidadeMaxAgua = 3;
    static int capacidadeMaxCombustivel = 5;
    static int velocidade = 6;

    // allow tunable parameters
    static void changeParameters(int maxAgua, int maxCombustivel, int velocidade){
        Drone.capacidadeMaxAgua = maxAgua;
        Drone.capacidadeMaxCombustivel = maxCombustivel;
        Drone.velocidade = velocidade;
    }

    protected void setup(){
        super.initStatus(Drone.capacidadeMaxAgua, Drone.capacidadeMaxCombustivel, Drone.velocidade);
        super.setup();
    }

}