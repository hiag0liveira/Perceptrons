import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Definindo o conjunto de vetores de entrada
        ArrayList<float[]> vetorDeVetoresX = new ArrayList<>();
        vetorDeVetoresX.add(new float[] { 1, 1, 1, 1, 0, 0 }); // Gripe
        vetorDeVetoresX.add(new float[] { 1, 0, 1, 1, 0, 1 }); // Gripe
        vetorDeVetoresX.add(new float[] { 1, 0, 1, 1, 1, 1 }); // Covid
        vetorDeVetoresX.add(new float[] { 1, 0, 0, 1, 1, 1 }); // Covid
        vetorDeVetoresX.add(new float[] { 1, 1, 1, 0, 1, 0 }); // Alergia
        vetorDeVetoresX.add(new float[] { 1, 1, 0, 1, 0, 0 }); // Alergia

        // Inicializando os vetores de pesos sinápticos
        ArrayList<float[]> vetorDeVetoresW = inicializarVetorDeVetoresW(vetorDeVetoresX.size());

        // Treinando o perceptron
        treinarPerceptron(vetorDeVetoresX, vetorDeVetoresW);

        // Solicitar entrada do usuário
        System.out.println("Insira um vetor (separe os valores por vírgula):");
        String input = scanner.nextLine();
        // Converter a entrada em um vetor de float
        float[] vetorX = parseInput(input);

        if (vetorX.length != 6) {
            System.out.println("O vetor deve ter 6 valores.");
        } else {
            // Calcular a classificação do vetor
            float[] V = treinarVetor(vetorX, vetorDeVetoresW);
            float[] Y = calcularY(V);

            // Exibir o resultado
            System.out.println("Resultado da classificação:");
            if (Y[0] == 1) {
                System.out.println("Gripe");
            } else if (Y[1] == 1) {
                System.out.println("Covid");
            } else if (Y[2] == 1) {
                System.out.println("Alergia");
            } else {
                System.out.println("Não foi possível classificar o vetor.");
            }
        }

        scanner.close();
    }

    // Inicializa os vetores de pesos sinápticos com valores aleatórios
    public static ArrayList<float[]> inicializarVetorDeVetoresW(int size) {
        ArrayList<float[]> vetorDeVetoresW = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            float[] vetorW = new float[6];
            for (int j = 0; j < vetorW.length; j++) {
                vetorW[j] = random.nextInt(2); // 0 ou 1
            }
            vetorDeVetoresW.add(vetorW);
        }
        return vetorDeVetoresW;
    }

    // Treina o perceptron ajustando os vetores de pesos sinápticos
    public static void treinarPerceptron(ArrayList<float[]> vetorDeVetoresX, ArrayList<float[]> vetorDeVetoresW) {
        boolean t = true;
        int contador = 0;

        while (t) {
            contador++;
            if (testarCondicaoDeParadaGlobal(vetorDeVetoresX, vetorDeVetoresW)) {
                t = false;
            }
        }

        System.out.println("Contador: " + contador);
        System.out.println("Vetor de Sinapses (W):");
        for (float[] vetorW : vetorDeVetoresW) {
            System.out.println(Arrays.toString(vetorW));
        }
    }

    // Calcula o vetor de soma para um vetor de entrada
    public static float[] treinarVetor(float[] vetorX, ArrayList<float[]> vetorDeVetoresW) {
        float[] V = new float[vetorDeVetoresW.size()];
        for (int i = 0; i < vetorDeVetoresW.size(); i++) {
            float[] vetorW = vetorDeVetoresW.get(i);
            float v = 0;
            for (int j = 0; j < vetorX.length; j++) {
                v += vetorX[j] * vetorW[j];
            }
            V[i] = v;
        }
        return V;
    }

    // Aplica a função de ativação para obter a classificação final
    public static float[] calcularY(float[] V) {
        float[] Y = new float[V.length];
        for (int i = 0; i < V.length; i++) {
            Y[i] = V[i] > 0 ? 1 : 0;
        }
        return Y;
    }

    // Calcula o vetor de erros para um determinado índice
    public static float[] calcularErro(float[] Y, int index) {
        float[] e = new float[Y.length];
        for (int i = 0; i < Y.length; i++) {
            if (i == index) {
                e[i] = 1 - Y[i];
            } else {
                e[i] = -Y[i];
            }
        }
        return e;
    }

    // Verifica se a condição de parada é satisfeita para um vetor de erros
    public static boolean testarCondicaoDeParada(float[] e) {
        float E = 0;
        for (float value : e) {
            E += value * value;
        }
        E /= e.length;
        return E <= 0.01;
    }

    // Verifica se a condição de parada global é satisfeita para todos os vetores de entrada
    public static boolean testarCondicaoDeParadaGlobal(ArrayList<float[]> vetorDeVetoresX,
                                                       ArrayList<float[]> vetorDeVetoresW) {
        for (int i = 0; i < vetorDeVetoresX.size(); i++) {
            float[] vetorX = vetorDeVetoresX.get(i);
            float[] V = treinarVetor(vetorX, vetorDeVetoresW);
            float[] Y = calcularY(V);
            float[] e = calcularErro(Y, i);
            if (!testarCondicaoDeParada(e)) {
                atualizarSinapses(vetorX, e, vetorDeVetoresW);
                return false;
            }
        }
        return true;
    }

    // Atualiza os vetores de pesos sinápticos com base nos erros e no vetor de entrada
    public static void atualizarSinapses(float[] vetorX, float[] e, ArrayList<float[]> vetorDeVetoresW) {
        float taxaAprendizado = 0.5f;
        for (int i = 0; i < vetorDeVetoresW.size(); i++) {
            float[] vetorW = vetorDeVetoresW.get(i);
            for (int j = 0; j < vetorX.length; j++) {
                vetorW[j] += taxaAprendizado * e[i] * vetorX[j];
            }
        }
    }

    // Converte a entrada do usuário em um vetor de floats
    public static float[] parseInput(String input) {
        String[] values = input.split(",");
        float[] vetorX = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            vetorX[i] = Float.parseFloat(values[i].trim());
        }
        return vetorX;
    }
}
