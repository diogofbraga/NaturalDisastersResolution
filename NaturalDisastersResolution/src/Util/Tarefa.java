import java.io.Serializable;

public class Tarefa implements Serializable {

    final static int ABASTECERCOMB = 1;
    final static int APAGAR = 2;
    final static int PREVENIR = 3;
    final static int ABASTECERAGUA = 4;

    int taskId;
    int tipo;
    int fireId;
    Posicao posicao;
    int minTempo; // tempo para a tarefa ser resolvida

    Tarefa(int id, int op, Posicao p){
        this.taskId = id;
        this.tipo = op;
        this.posicao = p;
    }

    Tarefa(int id, int op, int fireId, Posicao p){
        this.taskId = id;
        this.fireId = fireId;
        this.tipo = op;
        this.posicao = p;
    }

    Tarefa(int id, int op, int fireId, Posicao p, int minTempo){
        this.taskId = id;
        this.fireId = fireId;
        this.tipo = op;
        this.posicao = p;
        this.minTempo = minTempo;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public String toString() {
        return tipo == ABASTECERAGUA? "abastecer tanques":"apagar fogo" + " em célula " + posicao.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Tarefa){
            Tarefa t = (Tarefa) o;
            return this.taskId == t.taskId;
        }
        else return false;
    }
}
