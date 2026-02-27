package AVL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class AVLpadraoJA {
    private Node root; // referência para o nó raiz da árvore
    private int size;// quantidade de elementos na árvore
    private boolean rotated = false;

    // construtor: inicializa size como -1 (convenção usada na lógica de add)
    public AVLpadraoJA() {
        this.size = -1;
    }

    // retorna true se a árvore não tiver nenhum nó (raiz nula)
    public boolean isEmpty() {
        return this.root == null;
    }
    public boolean wasRotated() {
        return this.rotated;
    }

    // -------------------------------------------------------------------------
    // ADIÇÃO ITERATIVA
    // -------------------------------------------------------------------------

    public void add(int element) {

        this.size += 1; // incrementa o tamanho antes de inserir

        if (isEmpty()) this.root = new Node(element); // se a árvore está vazia, o novo nó vira a raiz
        else {

            Node aux = this.root; // começa a busca pela raiz

            while (aux != null) { // percorre a árvore até encontrar o lugar correto

                if (element < aux.value) { // vai para a subárvore esquerda se o elemento for menor
                    if (aux.left == null) { // encontrou o lugar de inserção à esquerda
                        Node newNode = new Node(element); // cria o novo nó
                        aux.left = newNode;               // conecta como filho esquerdo
                        newNode.parent = aux;             // aponta o pai do novo nó

                        Node unbalanced = checkBalance(newNode); // verifica se algum ancestral ficou desbalanceado
                        if (unbalanced != null) callBestRotation(unbalanced); // realiza a rotação necessária

                        return; // inserção concluída
                    }

                    aux = aux.left; // desce para o filho esquerdo e continua buscando
                } else {
                    if (aux.right == null) { // encontrou o lugar de inserção à direita
                        Node newNode = new Node(element); // cria o novo nó
                        aux.right = newNode;              // conecta como filho direito
                        newNode.parent = aux;             // aponta o pai do novo nó

                        Node unbalanced = checkBalance(newNode); // verifica desbalanceamento nos ancestrais
                        if (unbalanced != null) callBestRotation(unbalanced); // realiza a rotação necessária

                        return; // inserção concluída
                    }

                    aux = aux.right; // desce para o filho direito e continua buscando
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // VERIFICAÇÃO DE BALANCEAMENTO
    // -------------------------------------------------------------------------

    // sobe da folha recém-inserida até a raiz procurando o primeiro nó desbalanceado
    public Node checkBalance(Node node) {
        Node aux = node; // começa pelo nó inserido

        while (aux != null) {
            if (!aux.isBalanced()) { // se o fator de balanceamento saiu de [-1, 1], achou o problema
                return aux;          // retorna o nó desbalanceado
            } else if (aux.parent != null) {
                aux = aux.parent; // sobe para o nó pai
            } else {
                break; // chegou na raiz sem encontrar desbalanceamento
            }
        }

        return null; // árvore está balanceada
    }

    // -------------------------------------------------------------------------
    // ADIÇÃO RECURSIVA
    // -------------------------------------------------------------------------

    public void recursiveAdd(int element) {

        if (isEmpty())
            this.root = new Node(element); // caso base: árvore vazia, insere na raiz
        else {
            Node aux = this.root;
            recursiveAdd(aux, element); // inicia a recursão a partir da raiz
        }
        this.size += 1; // incrementa o tamanho após a inserção
    }

    // método auxiliar recursivo — navega pela árvore e insere o elemento no lugar correto
    public void recursiveAdd(Node node, int element) {

        if (element < node.value) { // elemento menor: vai para a subárvore esquerda
            if (node.left == null) { // folha encontrada à esquerda
                Node newNode = new Node(element);
                node.left = newNode;     // insere como filho esquerdo
                newNode.parent = node;   // registra o pai

                rebalance(node); // rebalanceia subindo a partir do pai da folha inserida

                return;
            }
            recursiveAdd(node.left, element); // desce recursivamente para a esquerda
            rebalance(node); // ao voltar da recursão, verifica e corrige o balanceamento deste nó
        } else {
            if (node.right == null) { // folha encontrada à direita
                Node newNode = new Node(element);
                node.right = newNode;    // insere como filho direito
                newNode.parent = node;   // registra o pai

                rebalance(node); // rebalanceia subindo a partir do pai da folha inserida

                return;
            }
            recursiveAdd(node.right, element); // desce recursivamente para a direita
            rebalance(node); // ao voltar da recursão, verifica e corrige o balanceamento deste nó
        }
    }

    // verifica se o nó está desbalanceado e, se estiver, chama a rotação adequada
    private void rebalance(Node node) {
        int balance = node.balance(); // obtém o fator de balanceamento (altura esq - altura dir)

        if (Math.abs(balance) > 1) { // fator fora de [-1, 1] indica desbalanceamento
            callBestRotation(node);  // realiza a rotação necessária
        }
    }

    // -------------------------------------------------------------------------
    // ROTAÇÕES
    // -------------------------------------------------------------------------

    // analisa o nó desbalanceado e decide qual tipo de rotação aplicar
    public void callBestRotation(Node unbalanced) {
        Node x = unbalanced;

        if (x.isLeftPending()) {   // subárvore esquerda é mais alta → caso Left
            Node y = x.left;       // filho esquerdo é o pivot

            if (y.left != null) rotateRight(x); // filho do pivot está à esquerda → rotação simples direita (LL)
            else {
                rotateLeft(y); rotateRight(x);  // filho do pivot está à direita → rotação dupla esquerda-direita (LR)
            }

        } else {                   // subárvore direita é mais alta → caso Right
            Node y = x.right;      // filho direito é o pivot

            if (y.right != null) rotateLeft(x); // filho do pivot está à direita → rotação simples esquerda (RR)
            else {
                rotateRight(y); rotateLeft(x);  // filho do pivot está à esquerda → rotação dupla direita-esquerda (RL)
            }
        }
    }

    // rotação simples à direita: o filho esquerdo sobe e o nó atual desce para a direita
    public void rotateRight(Node node) {
        Node newRoot = node.left;         // o filho esquerdo se tornará o novo "sub-raiz"
        newRoot.parent = node.parent;     // o novo sub-raiz herda o pai do nó rotacionado

        node.left = newRoot.right;        // o filho direito do novo sub-raiz passa a ser filho esquerdo do nó que desceu
        newRoot.right = node;             // o nó rotacionado vai para a direita do novo sub-raiz

        node.parent = newRoot;            // atualiza o pai do nó que desceu

        if (newRoot.parent != null) {     // se o novo sub-raiz não é a raiz da árvore toda...
            if (newRoot.parent.left == node)
                newRoot.parent.left = newRoot;  // substitui o ponteiro esquerdo do avô
            else
                newRoot.parent.right = newRoot; // substitui o ponteiro direito do avô
        } else
            this.root = newRoot; // o novo sub-raiz passa a ser a raiz da árvore inteira

        rotated = true;
        System.out.println("rot_dir(" + node.value + ")");
        System.out.println(preOrder());
    }

    // rotação simples à esquerda: o filho direito sobe e o nó atual desce para a esquerda
    public void rotateLeft(Node node) {
        Node newRoot = node.right;        // o filho direito se tornará o novo "sub-raiz"
        newRoot.parent = node.parent;     // o novo sub-raiz herda o pai do nó rotacionado

        node.right = newRoot.left;        // o filho esquerdo do novo sub-raiz passa a ser filho direito do nó que desceu
        newRoot.left = node;              // o nó rotacionado vai para a esquerda do novo sub-raiz

        node.parent = newRoot;            // atualiza o pai do nó que desceu

        if (newRoot.parent != null) {     // se o novo sub-raiz não é a raiz da árvore toda...
            if (newRoot.parent.right == node)
                newRoot.parent.right = newRoot; // substitui o ponteiro direito do avô
            else
                newRoot.parent.left = newRoot;  // substitui o ponteiro esquerdo do avô
        } else
            this.root = newRoot; // o novo sub-raiz passa a ser a raiz da árvore inteira

        rotated = true;
        System.out.println("rot_esq(" + node.value + ")");
        System.out.println(preOrder());
    }

    // verifica recursivamente se todos os nós têm BF no intervalo [-1, 1]
    private boolean checkIsAVL(Node node) {
        if (node == null) return true; // nó nulo é válido — caso base

        int BF = height(node.left) - height(node.right); // calcula o fator de balanceamento do nó atual

        if (BF < -1 || BF > 1) return false; // BF fora do intervalo — árvore não é AVL

        return checkIsAVL(node.left) && checkIsAVL(node.right); // verifica recursivamente os filhos
    }

    // calcula a altura de um nó: null = 0, folha = 1, outros = 1 + maior filho
    public int height(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public ArrayList<String> inOrder() {
        ArrayList<String> list = new ArrayList<>();
        inOrder(this.root, list);
        return list;
    }

    private void inOrder(Node node, ArrayList<String> list) {
        if (node == null) return;                          // caso base
        inOrder(node.left, list);                          // vai para esquerda primeiro
        int BF = height(node.left) - height(node.right);  // calcula BF do nó atual
        list.add(node.value + "," + BF);                   // visita o nó NO MEIO
        inOrder(node.right, list);                         // vai para direita
    }

    public ArrayList<String> postOrder() {
        ArrayList<String> list = new ArrayList<>();
        postOrder(this.root, list);
        return list;
    }

    private void postOrder(Node node, ArrayList<String> list) {
        if (node == null) return;                          // caso base
        postOrder(node.left, list);                        // vai para esquerda primeiro
        postOrder(node.right, list);                       // vai para direita depois
        int BF = height(node.left) - height(node.right);  // calcula BF do nó atual
        list.add(node.value + "," + BF);                   // visita o nó POR ÚLTIMO
    }
    // -------------------------------------------------------------------------
    // BFS (PERCURSO EM LARGURA)
    // -------------------------------------------------------------------------

    // percorre a árvore nível a nível (BFS) e retorna os elementos em ordem de visita
    public ArrayList<Integer> bfs() {
        ArrayList<Integer> list = new ArrayList<Integer>(); // lista que armazena os resultados
        Deque<Node> queue = new LinkedList<Node>();          // fila para controlar a ordem de visita

        if (!isEmpty()) {
            queue.addLast(this.root); // começa pela raiz
            while (!queue.isEmpty()) {
                Node current = queue.removeFirst(); // retira o nó da frente da fila

                list.add(current.value); // registra o valor do nó visitado

                if (current.left != null)
                    queue.addLast(current.left);  // enfileira o filho esquerdo, se existir
                if (current.right != null)
                    queue.addLast(current.right); // enfileira o filho direito, se existir
            }
        }
        return list;
    }

    // retorna a quantidade de elementos da árvore
    public int size() {
        return this.size;
    }

    public ArrayList<Integer> preOrder(){
        ArrayList<Integer> list = new ArrayList<>();
        preOrder(this.root, list);
        return list;
    }
    public void preOrder(Node node, ArrayList<Integer> list) {
        if (node == null) return;
        list.add(node.value);
        preOrder(node.left, list);
        preOrder(node.right, list);
    }
}

// -------------------------------------------------------------------------
// CLASSE NODE
// -------------------------------------------------------------------------

class Node {

    int value;   // valor armazenado no nó
    int height;  // altura do nó (não usada diretamente; calculada via height())
    Node left;   // filho esquerdo
    Node right;  // filho direito
    Node parent; // pai do nó (necessário para subir na árvore sem recursão)

    // construtor: cria um nó folha com altura 0
    Node(int v) {
        this.height = 0;
        this.value = v;
    }

    // retorna true se o nó tiver apenas filho esquerdo
    public boolean hasOnlyLeftChild() {
        return (this.left != null && this.right == null);
    }

    // retorna true se o nó tiver apenas filho direito
    public boolean hasOnlyRightChild() {
        return (this.left == null && this.right != null);
    }

    // retorna true se o nó não tiver filhos (é uma folha)
    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    // calcula recursivamente a altura do nó:
    // folha = 0; outros = 1 + maior altura entre filhos esquerdo e direito
    public int height() {
        if (this.left == null && this.right == null)
            return 0; // nó folha tem altura 0
        else if (this.left == null) {
            return 1 + this.right.height(); // só tem filho direito
        } else if (this.right == null) {
            return 1 + this.left.height();  // só tem filho esquerdo
        } else {
            return 1 + max(this.left.height(), this.right.height()); // tem os dois filhos
        }
    }

    // retorna o maior entre dois inteiros (utilizado no cálculo de altura)
    private int max(int height1, int height2) {
        if (height1 >= height2)
            return height1;
        return height2;
    }

    // calcula o fator de balanceamento: altura(esquerda) - altura(direita)
    // nós ausentes contam como altura -1
    public int balance() {
        int left = this.left == null ? -1 : this.left.height();   // -1 se não há filho esquerdo
        int right = this.right == null ? -1 : this.right.height(); // -1 se não há filho direito
        return left - right; // positivo = pendente à esquerda; negativo = pendente à direita
    }

    // retorna true se o nó está mais "pesado" à esquerda (fator >= 1)
    public boolean isLeftPending() {
        int left = this.left == null ? -1 : this.left.height();
        int right = this.right == null ? -1 : this.right.height();
        return left - right >= 1;
    }

    // retorna true se o nó está mais "pesado" à direita (fator <= -1)
    public boolean isRightPending() {
        int left = this.left == null ? -1 : this.left.height();
        int right = this.right == null ? -1 : this.right.height();
        return left - right <= -1;
    }

    // retorna true se o nó está balanceado (fator de balanceamento entre -1 e 1, inclusive)
    public boolean isBalanced() {
        int left = this.left == null ? -1 : this.left.height();
        int right = this.right == null ? -1 : this.right.height();
        return left - right >= -1 && left - right <= 1;
    }
}