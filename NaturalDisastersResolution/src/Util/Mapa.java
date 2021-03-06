import java.util.*;
public class Mapa {

    int size;
    int numPostosComb;
    int numPostosAgua;
    int numHabitacoes;
    int numPontosFloresta;

    List<PostoCombustivel> postosCombustivel;
    List<Posicao> postosAgua;
    List<Posicao> habitacoes;
    List<Posicao> floresta;
    List<Posicao> areaArdida;

    Map<Integer, Incendio> incendios;

    Mapa(int size, int numPostosComb, int numPostosAgua, int numHabitacoes, int numPontosFloresta){
        this.size = size;
        this.numPostosComb = numPostosComb;
        this.numPostosAgua = numPostosAgua;
        this.numHabitacoes = numHabitacoes;
        this.numPontosFloresta = numPontosFloresta;
        this.postosCombustivel = new ArrayList<>();
        this.postosAgua = new ArrayList<>();
        this.habitacoes = new ArrayList<>();
        this.floresta = new ArrayList<>();
        this.areaArdida = new ArrayList<>();
        this.incendios = new HashMap<>();
    }

    /**
     * Método que permite verificar se uma determinada celula do mapa está a arder.
     */
    boolean onFire(Posicao p){
        return this.incendios.values().stream().anyMatch((incendio) -> incendio.areaAfetada.contains(p));
    }

    boolean inAreaArdida(Posicao p){
        return this.areaArdida.contains(p);
    }

    boolean posicaoLivre(Posicao p){
        for(PostoCombustivel posto : postosCombustivel)
            if(posto.pos.equals(p)) return false;

        if(postosAgua.contains(p)) return false;
        if(habitacoes.contains(p)) return false;
        if(floresta.contains(p)) return false;
        return true;
    }

    boolean insideDimensoes(Posicao pos){
        if(pos.pos_x>=0 && pos.pos_x<size && pos.pos_y>=0 && pos.pos_y<size) return true;
        else return false;
    }

    public void estabelecePosicaoPontosFixos(){
        Random rand = new Random();
        int i;

        List<Posicao> list;
        list = getPosicoesProporcional(numPostosComb);
        for(Posicao p : list) {
            postosCombustivel.add(new PostoCombustivel(p));
        }

        i=0;
        while(i<numPostosAgua){
            Posicao p;
            do{
                p = getRandPosition();
            }while(!posicaoLivre(p));
            postosAgua.add(p);
            i++;
            if(i==numPostosAgua) break;

            int afluentes = rand.nextInt(10);
            Posicao ultimaCelula = p;
            Posicao pAdjacent;
            List adj;
            for(int j=0; j<afluentes; j++){
                adj = posicoesAdjacentesLivres(ultimaCelula);
                if(adj.isEmpty()) break;
                pAdjacent = getRandAdjacentPositions(adj);
                ultimaCelula = pAdjacent;
                postosAgua.add(pAdjacent);
                i++;
                if(i==numPostosAgua) break;
            }
        }

        i=0;
        while(i<numHabitacoes){
            Posicao p;
            do{
                p = getRandPosition();
            }while(!posicaoLivre(p));
            habitacoes.add(p);
            i++;
            if(i==numHabitacoes) break;

            int vizinhos = rand.nextInt(8);
            for(int j=0; j<vizinhos; j++){
                List<Posicao> adj;
                adj = posicoesAdjacentesLivres(p);
                if(adj.isEmpty()) break;
                Posicao pAdjacent;
                do{
                    pAdjacent = getRandAdjacentPositions(adj);
                    if(habitacoes.containsAll(adj)) break;
                }while(!posicaoLivre(pAdjacent));
                habitacoes.add(pAdjacent);
                i++;
                if(i==numHabitacoes) break;
            }
        }

        i=0;
        while(i<numPontosFloresta){
            Posicao p;
            do{
                p = getRandPosition();
            }while(!posicaoLivre(p));
            floresta.add(p);
            i++;
            if(i==numPontosFloresta) break;

            int vizinhos = rand.nextInt(8);
            for(int j=0; j<vizinhos; j++){
                List<Posicao> adj;
                adj = posicoesAdjacentesLivres(p);
                if(adj.isEmpty()) break;
                Posicao pAdjacent;
                do{
                    pAdjacent = getRandAdjacentPositions(adj);
                    if(floresta.containsAll(adj)) break;
                }while(!posicaoLivre(pAdjacent));
                floresta.add(pAdjacent);
                i++;
                if(i==numPontosFloresta) break;
            }
        }

    }

    public Posicao getRandPosition() {
        Random rand = new Random();
        float x = rand.nextInt(size);
        float y = rand.nextInt(size);

        return new Posicao(x,y);
    }

    public List<Posicao> getPosicoesProporcional(int numPostos) {
        Random rand = new Random();

        List<Posicao> res = new ArrayList<>();

        if(numPostos == 1){
            Posicao p = new Posicao(size/2,size/2);
            res.add(p);
        }
        else if(numPostos == 2){
            Posicao p1 = new Posicao(size/4,size/4);
            Posicao p2 = new Posicao(size-(size/4),size-(size/4));
            res.add(p1);
            res.add(p2);
        }
        else{
            int tamDivisaoSide = size/2;
            int nivelSeparacao = 5;
            int tentativas = 5;
            int i=0;
            while(i<numPostos){
                int x_1q,y_1q,x_2q,y_2q,x_3q,y_3q,x_4q,y_4q;

                do {
                    x_1q = rand.nextInt(tamDivisaoSide);
                    y_1q = rand.nextInt(tamDivisaoSide);
                    tentativas--;
                    if(tentativas == 0) {
                        nivelSeparacao--;
                        tentativas = 5;
                    }
                }while(demasiadoJuntos(new Posicao(x_1q, y_1q), res, nivelSeparacao));
                res.add(new Posicao(x_1q,y_1q));
                i++;
                nivelSeparacao = 5;
                tentativas = 5;
                if(i==numPostos) break;

                do {
                    x_2q = rand.nextInt(size-tamDivisaoSide)+tamDivisaoSide;
                    y_2q = rand.nextInt(tamDivisaoSide);
                    tentativas--;
                    if(tentativas == 0) {
                        nivelSeparacao--;
                        tentativas = 5;
                    }
                }while( demasiadoJuntos(new Posicao(x_2q, y_2q), res, nivelSeparacao));
                res.add(new Posicao(x_2q,y_2q));
                i++;
                nivelSeparacao = 5;
                tentativas = 5;
                if(i==numPostos) break;

                do {
                    x_3q = rand.nextInt(tamDivisaoSide);
                    y_3q = rand.nextInt(size-tamDivisaoSide)+tamDivisaoSide;
                    tentativas--;
                    if(tentativas == 0) {
                        nivelSeparacao--;
                        tentativas = 5;
                    }
                }while(demasiadoJuntos(new Posicao(x_3q, y_3q), res, nivelSeparacao));
                res.add(new Posicao(x_3q,y_3q));
                i++;
                nivelSeparacao = 5;
                tentativas = 5;
                if(i==numPostos) break;

                do {
                    x_4q = rand.nextInt(size-tamDivisaoSide)+tamDivisaoSide;
                    y_4q = rand.nextInt(size-tamDivisaoSide)+tamDivisaoSide;
                    tentativas--;
                    if(tentativas == 0) {
                        nivelSeparacao--;
                        tentativas = 5;
                    }
                }while(demasiadoJuntos(new Posicao(x_4q, y_4q), res, nivelSeparacao));
                res.add(new Posicao(x_4q,y_4q));
                i++;
                nivelSeparacao = 5;
                tentativas = 5;
                if(i==numPostos) break;
            }
        }

        return res;
    }

    private boolean demasiadoJuntos(Posicao posicao, List<Posicao> res, int nivelSeparacao) {
        if(nivelSeparacao == 0) return false;
        for(Posicao p : res){
            if(Posicao.distanceBetween(posicao, p) < nivelSeparacao)
                return  true;
        }
        return false;
    }

    public Posicao getRandAdjacentPositions(List<Posicao> list){
        Random rand = new Random();
        Posicao res = list.get(rand.nextInt(list.size()));
        return res;
    }

    boolean isFireActive(int fireId){
        return this.incendios.get(fireId).areaAfetada.size() != 0;
    }

    public boolean postoC(Posicao p) {
        if (postosCombustivel.contains(p))
            return true;
        else return false;
    }

    public  boolean postoA(Posicao p) {
        if (postosAgua.contains(p))
            return true;
        else return false;
    }

    public boolean hab(Posicao p) {
        if (habitacoes.contains(p))
            return true;
        else return false;
    }

    public  boolean arvore(Posicao p) {
        if (floresta.contains(p))
            return true;
        else return false;
    }

    public void registaIncendio(FireAlert fa) {
        this.incendios.put(fa.fireID, new Incendio(fa));
    }

    public void atualizaIncendio(FireAlert fa) {
        this.incendios.get(fa.fireID).registaExpansao(fa);
    }

    public void registaCelulaApagada(int fireId, Posicao posicao) {
        this.incendios.get(fireId).registaCelulaApagada(posicao);
    }

    public boolean isWaterSource(Posicao p) {
        return this.postosAgua.contains(p);
    }

    public AbstractMap.SimpleEntry<Posicao, Integer> getPostoCombEntreAgenteIncendio(Posicao posição, Posicao incendio) {
        Posicao postoMaisProximo = null;
        int minDist = 10000;
        for(PostoCombustivel posto : this.postosCombustivel){
            int dist = Posicao.distanceBetween(posição, posto.pos) + Posicao.distanceBetween(posto.pos, incendio);
            if(posto.ativo == true && dist < minDist){
                minDist = dist;
                postoMaisProximo = posto.pos;
            }
        }

        return new AbstractMap.SimpleEntry<>(postoMaisProximo, minDist);
    }

    public AbstractMap.SimpleEntry<Posicao, Integer> getPostoAguaEntreAgenteIncendio(Posicao posição, Posicao incendio) {
        Posicao postoMaisProximo = null;
        int minDist = 10000;
        for(Posicao posto : this.postosAgua){
            int dist = Posicao.distanceBetween(posição, posto) + Posicao.distanceBetween(posto, incendio);
            if(dist < minDist){
                minDist = dist;
                postoMaisProximo = posto;
            }
        }

        return new AbstractMap.SimpleEntry<>(postoMaisProximo, minDist);
    }

    List<Posicao> getAllPostosCombustiveisAtivos(){
        List res = new ArrayList();
        for(PostoCombustivel posto : postosCombustivel){
            if(posto.ativo==true) res.add(posto.pos);
        }
        return res;
    }

    List<Posicao> posicoesAdjacentesNotOnFire(Posicao pos){
        List<Posicao> res = new ArrayList<>();
        Posicao p1 = new Posicao (pos.pos_x-1,pos.pos_y+1);
        Posicao p2 = new Posicao (pos.pos_x,pos.pos_y+1);
        Posicao p3 = new Posicao (pos.pos_x+1,pos.pos_y+1);
        Posicao p4 = new Posicao (pos.pos_x-1,pos.pos_y);
        Posicao p5 = new Posicao (pos.pos_x+1,pos.pos_y);
        Posicao p6 = new Posicao (pos.pos_x-1,pos.pos_y-1);
        Posicao p7 = new Posicao (pos.pos_x,pos.pos_y-1);
        Posicao p8 = new Posicao (pos.pos_x+1,pos.pos_y-1);

        if(insideDimensoes(p1) && !onFire(p1) && !inAreaArdida(p1)) res.add(p1);
        if(insideDimensoes(p2) && !onFire(p2) && !inAreaArdida(p2)) res.add(p2);
        if(insideDimensoes(p3) && !onFire(p3) && !inAreaArdida(p3)) res.add(p3);
        if(insideDimensoes(p4) && !onFire(p4) && !inAreaArdida(p4)) res.add(p4);
        if(insideDimensoes(p5) && !onFire(p5) && !inAreaArdida(p5)) res.add(p5);
        if(insideDimensoes(p6) && !onFire(p6) && !inAreaArdida(p6)) res.add(p6);
        if(insideDimensoes(p7) && !onFire(p7) && !inAreaArdida(p7)) res.add(p7);
        if(insideDimensoes(p8) && !onFire(p8) && !inAreaArdida(p8)) res.add(p8);

        return res;
    }

    List<Posicao> posicoesAdjacentesLivres(Posicao pos){
        List<Posicao> res = new ArrayList<>();
        Posicao p1 = new Posicao (pos.pos_x-1,pos.pos_y+1);
        Posicao p2 = new Posicao (pos.pos_x,pos.pos_y+1);
        Posicao p3 = new Posicao (pos.pos_x+1,pos.pos_y+1);
        Posicao p4 = new Posicao (pos.pos_x-1,pos.pos_y);
        Posicao p5 = new Posicao (pos.pos_x+1,pos.pos_y);
        Posicao p6 = new Posicao (pos.pos_x-1,pos.pos_y-1);
        Posicao p7 = new Posicao (pos.pos_x,pos.pos_y-1);
        Posicao p8 = new Posicao (pos.pos_x+1,pos.pos_y-1);

        if(insideDimensoes(p1) && posicaoLivre(p1)) res.add(p1);
        if(insideDimensoes(p2) && posicaoLivre(p2)) res.add(p2);
        if(insideDimensoes(p3) && posicaoLivre(p3)) res.add(p3);
        if(insideDimensoes(p4) && posicaoLivre(p4)) res.add(p4);
        if(insideDimensoes(p5) && posicaoLivre(p5)) res.add(p5);
        if(insideDimensoes(p6) && posicaoLivre(p6)) res.add(p6);
        if(insideDimensoes(p7) && posicaoLivre(p7)) res.add(p7);
        if(insideDimensoes(p8) && posicaoLivre(p8)) res.add(p8);

        return res;
    }

    List<Posicao> posicoesFlorestaAdjacenteNotOnFire(Posicao pos){
        List<Posicao> res = new ArrayList<>();
        Posicao p1 = new Posicao (pos.pos_x-1,pos.pos_y+1);
        Posicao p2 = new Posicao (pos.pos_x,pos.pos_y+1);
        Posicao p3 = new Posicao (pos.pos_x+1,pos.pos_y+1);
        Posicao p4 = new Posicao (pos.pos_x-1,pos.pos_y);
        Posicao p5 = new Posicao (pos.pos_x+1,pos.pos_y);
        Posicao p6 = new Posicao (pos.pos_x-1,pos.pos_y-1);
        Posicao p7 = new Posicao (pos.pos_x,pos.pos_y-1);
        Posicao p8 = new Posicao (pos.pos_x+1,pos.pos_y-1);

        if(insideDimensoes(p1) && floresta.contains(p1) && !onFire(p1) && !inAreaArdida(p1)) res.add(p1);
        if(insideDimensoes(p2) && floresta.contains(p2) && !onFire(p2) && !inAreaArdida(p2)) res.add(p2);
        if(insideDimensoes(p3) && floresta.contains(p3) && !onFire(p3) && !inAreaArdida(p3)) res.add(p3);
        if(insideDimensoes(p4) && floresta.contains(p4) && !onFire(p4) && !inAreaArdida(p4)) res.add(p4);
        if(insideDimensoes(p5) && floresta.contains(p5) && !onFire(p5) && !inAreaArdida(p5)) res.add(p5);
        if(insideDimensoes(p6) && floresta.contains(p6) && !onFire(p6) && !inAreaArdida(p6)) res.add(p6);
        if(insideDimensoes(p7) && floresta.contains(p7) && !onFire(p7) && !inAreaArdida(p7)) res.add(p7);
        if(insideDimensoes(p8) && floresta.contains(p8) && !onFire(p8) && !inAreaArdida(p8)) res.add(p8);

        return res;
    }

    List<Posicao> posicoesFlorestaAdjacente(Posicao pos){
        List<Posicao> res = new ArrayList<>();
        Posicao p1 = new Posicao (pos.pos_x-1,pos.pos_y+1);
        Posicao p2 = new Posicao (pos.pos_x,pos.pos_y+1);
        Posicao p3 = new Posicao (pos.pos_x+1,pos.pos_y+1);
        Posicao p4 = new Posicao (pos.pos_x-1,pos.pos_y);
        Posicao p5 = new Posicao (pos.pos_x+1,pos.pos_y);
        Posicao p6 = new Posicao (pos.pos_x-1,pos.pos_y-1);
        Posicao p7 = new Posicao (pos.pos_x,pos.pos_y-1);
        Posicao p8 = new Posicao (pos.pos_x+1,pos.pos_y-1);

        if(insideDimensoes(p1) && floresta.contains(p1) && !inAreaArdida(p1)) res.add(p1);
        if(insideDimensoes(p2) && floresta.contains(p2) && !inAreaArdida(p2)) res.add(p2);
        if(insideDimensoes(p3) && floresta.contains(p3) && !inAreaArdida(p3)) res.add(p3);
        if(insideDimensoes(p4) && floresta.contains(p4) && !inAreaArdida(p4)) res.add(p4);
        if(insideDimensoes(p5) && floresta.contains(p5) && !inAreaArdida(p5)) res.add(p5);
        if(insideDimensoes(p6) && floresta.contains(p6) && !inAreaArdida(p6)) res.add(p6);
        if(insideDimensoes(p7) && floresta.contains(p7) && !inAreaArdida(p7)) res.add(p7);
        if(insideDimensoes(p8) && floresta.contains(p8) && !inAreaArdida(p8)) res.add(p8);

        return res;
    }

}
