package AVL;

public class AVL {
    Node root;
    int size;

    public AVL() {
        this.size = 0; // inicializa o tamanho da árvore como 0
    }

    public boolean isEmpty() {
        return this.root == null; // árvore vazia se a raiz for nula
    }

    public int height(Node node) {
        if (node == null) return -1; // nulo retorna -1 para que folha fique com altura 0
        return node.height;          // retorna a altura armazenada no nó
    }

    public int getBalanceFactor(Node node) {
        if (node == null) return 0;  // proteção contra nulo
        return height(node.left) - height(node.right); // FB = altura esquerda - altura direita
    }

    public void updateHeight(Node node) {
        if (node == null) return; // proteção contra nulo
        // altura do nó = 1 + maior altura entre os filhos
        if (height(node.left) < height(node.right)) {
            node.height = height(node.right) + 1;
        } else {
            node.height = height(node.left) + 1;
        }
    }

    public Node rotateRight(Node node) {
        Node newRoot = node.left;    // o filho esquerdo vai subir
        node.left = newRoot.right;   // o filho direito do newRoot passa a ser filho esquerdo do node
        newRoot.right = node;        // node desce e vira filho direito do newRoot
        updateHeight(node);          // atualiza altura do node (agora está abaixo)
        updateHeight(newRoot);       // atualiza altura do newRoot (agora está acima)
        return newRoot;              // retorna a nova raiz da subárvore
    }

    public Node rotateLeft(Node node) {
        Node newRoot = node.right;   // o filho direito vai subir
        node.right = newRoot.left;   // o filho esquerdo do newRoot passa a ser filho direito do node
        newRoot.left = node;         // node desce e vira filho esquerdo do newRoot
        updateHeight(node);          // atualiza altura do node (agora está abaixo)
        updateHeight(newRoot);       // atualiza altura do newRoot (agora está acima)
        return newRoot;              // retorna a nova raiz da subárvore
    }

    public Node rotateLeftRight(Node node) {
        node.left = rotateLeft(node.left); // passo 1: rotaciona o filho esquerdo pra esquerda
        return rotateRight(node);          // passo 2: rotaciona a raiz pra direita
    }

    public Node rotateRightLeft(Node node) {
        node.right = rotateRight(node.right); // passo 1: rotaciona o filho direito pra direita
        return rotateLeft(node);              // passo 2: rotaciona a raiz pra esquerda
    }

    public Node rebalance(Node node) {
        if (node == null) return null; // proteção contra nulo

        // caso Esquerda-Esquerda: FB > 1 e filho esquerdo pesado à esquerda
        if (getBalanceFactor(node) > 1 && getBalanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }
        // caso Esquerda-Direita: FB > 1 e filho esquerdo pesado à direita
        else if (getBalanceFactor(node) > 1 && getBalanceFactor(node.left) < 0) {
            return rotateLeftRight(node);
        }
        // caso Direita-Direita: FB < -1 e filho direito pesado à direita
        else if (getBalanceFactor(node) < -1 && getBalanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }
        // caso Direita-Esquerda: FB < -1 e filho direito pesado à esquerda
        else if (getBalanceFactor(node) < -1 && getBalanceFactor(node.right) > 0) {
            return rotateRightLeft(node);
        }
        return node; // já está balanceado, retorna o próprio nó
    }

    private Node insert(Node node, int element) {
        if (node == null) return new Node(element); // posição encontrada, cria o nó
        else if (element < node.value)
            node.left = insert(node.left, element);   // desce pela esquerda
        else if (element > node.value)
            node.right = insert(node.right, element); // desce pela direita
        // na volta da recursão:
        updateHeight(node);     // atualiza a altura do nó atual
        return rebalance(node); // rebalanceia e retorna o nó
    }

    public void insert(int element) {
        root = insert(root, element); // chama o recursivo e atualiza a raiz
    }

    private Node search(Node node, int element) {
        if (node == null) return null;           // elemento não encontrado
        else if (element < node.value)
            return search(node.left, element);   // desce pela esquerda
        else if (element > node.value)
            return search(node.right, element);  // desce pela direita
        return node;                             // elemento encontrado, retorna o nó
    }

    public Node search(int element) {
        return search(root, element); // começa a busca pela raiz
    }

    private Node remove(Node node, int element) {
        if (node == null) return null; // elemento não existe na árvore
        else if (element < node.value)
            node.left = remove(node.left, element);   // desce pela esquerda
        else if (element > node.value)
            node.right = remove(node.right, element); // desce pela direita
        else {
            // elemento encontrado, trata os 3 casos:

            // caso 1: nó folha — remove diretamente
            if (node.left == null && node.right == null) {
                return null;
            }
            // caso 2a: só tem filho esquerdo — filho sobe no lugar do nó
            else if (node.left != null && node.right == null) {
                return node.left;
            }
            // caso 2b: só tem filho direito — filho sobe no lugar do nó
            else if (node.left == null && node.right != null) {
                return node.right;
            }
            // caso 3: tem dois filhos — substitui pelo sucessor in-order
            else {
                Node successor = minimum(node.right); // acha o menor da subárvore direita
                node.value = successor.value;         // copia o valor do sucessor pro nó atual
                node.right = remove(node.right, successor.value); // remove o sucessor
            }
        }
        updateHeight(node);     // atualiza a altura na volta da recursão
        return rebalance(node); // rebalanceia e retorna o nó
    }

    public void remove(int element) {
        root = remove(root, element); // chama o recursivo e atualiza a raiz
    }

    public Node minimum(Node node) {
        if (node == null) return null;       // proteção contra nulo
        if (node.left == null) return node;  // chegou no menor elemento
        return minimum(node.left);           // desce sempre pela esquerda
    }

    public Node maximum(Node node) {
        if (node == null) return null;       // proteção contra nulo
        if (node.right == null) return node; // chegou no maior elemento
        return maximum(node.right);          // desce sempre pela direita
    }
}

class Node {
    int value;
    Node right;
    Node left;
    int height;
    Node parent;

    public Node(int value) {
        this.value = value;
        this.height = 0; // folha começa com altura 0
    }
}


// ═══════════════════════════════════════════════════════════════
// TESTES
// ═══════════════════════════════════════════════════════════════
class AVLTest {

    static int testsPassed = 0;
    static int testsFailed = 0;

    static void assertEquals(String testName, int expected, int actual) {
        if (expected == actual) {
            System.out.println("✅ PASSOU: " + testName);
            testsPassed++;
        } else {
            System.out.println("❌ FALHOU: " + testName +
                    " | esperado: " + expected + " | obtido: " + actual);
            testsFailed++;
        }
    }

    static void assertNotNull(String testName, Object obj) {
        if (obj != null) {
            System.out.println("✅ PASSOU: " + testName);
            testsPassed++;
        } else {
            System.out.println("❌ FALHOU: " + testName + " | esperado: não nulo | obtido: null");
            testsFailed++;
        }
    }

    static void assertNull(String testName, Object obj) {
        if (obj == null) {
            System.out.println("✅ PASSOU: " + testName);
            testsPassed++;
        } else {
            System.out.println("❌ FALHOU: " + testName + " | esperado: null | obtido: não nulo");
            testsFailed++;
        }
    }

    static void testarInsercaoSimples() {
        System.out.println("\n--- Inserção Simples ---");
        AVL avl = new AVL();

        avl.insert(10);
        assertEquals("raiz deve ser 10", 10, avl.root.value);
        assertEquals("altura da raiz deve ser 0 (folha)", 0, avl.root.height);
    }

    static void testarBalanceamentoEsquerdaEsquerda() {
        System.out.println("\n--- Rotação Direita (Esquerda-Esquerda) ---");
        AVL avl = new AVL();

        // inserindo 10, 5, 3 → provoca rotação direita
        // antes:  10        depois:   5
        //        /                   / \
        //       5                   3   10
        //      /
        //     3
        avl.insert(10);
        avl.insert(5);
        avl.insert(3);

        assertEquals("nova raiz deve ser 5", 5, avl.root.value);
        assertEquals("filho esquerdo deve ser 3", 3, avl.root.left.value);
        assertEquals("filho direito deve ser 10", 10, avl.root.right.value);
        assertEquals("altura da raiz deve ser 1", 1, avl.root.height);
    }

    static void testarBalanceamentoDireitaDireita() {
        System.out.println("\n--- Rotação Esquerda (Direita-Direita) ---");
        AVL avl = new AVL();

        // inserindo 3, 5, 10 → provoca rotação esquerda
        avl.insert(3);
        avl.insert(5);
        avl.insert(10);

        assertEquals("nova raiz deve ser 5", 5, avl.root.value);
        assertEquals("filho esquerdo deve ser 3", 3, avl.root.left.value);
        assertEquals("filho direito deve ser 10", 10, avl.root.right.value);
        assertEquals("altura da raiz deve ser 1", 1, avl.root.height);
    }

    static void testarBalanceamentoEsquerdaDireita() {
        System.out.println("\n--- Rotação Dupla Esquerda-Direita ---");
        AVL avl = new AVL();

        // inserindo 10, 3, 6 → provoca rotação dupla esquerda-direita
        avl.insert(10);
        avl.insert(3);
        avl.insert(6);

        assertEquals("nova raiz deve ser 6", 6, avl.root.value);
        assertEquals("filho esquerdo deve ser 3", 3, avl.root.left.value);
        assertEquals("filho direito deve ser 10", 10, avl.root.right.value);
    }

    static void testarBalanceamentoDireitaEsquerda() {
        System.out.println("\n--- Rotação Dupla Direita-Esquerda ---");
        AVL avl = new AVL();

        // inserindo 3, 10, 6 → provoca rotação dupla direita-esquerda
        avl.insert(3);
        avl.insert(10);
        avl.insert(6);

        assertEquals("nova raiz deve ser 6", 6, avl.root.value);
        assertEquals("filho esquerdo deve ser 3", 3, avl.root.left.value);
        assertEquals("filho direito deve ser 10", 10, avl.root.right.value);
    }

    static void testarBusca() {
        System.out.println("\n--- Busca ---");
        AVL avl = new AVL();

        avl.insert(10);
        avl.insert(5);
        avl.insert(15);
        avl.insert(3);

        assertNotNull("busca por 10 deve encontrar", avl.search(10));
        assertNotNull("busca por 3 deve encontrar", avl.search(3));
        assertNotNull("busca por 15 deve encontrar", avl.search(15));
        assertNull("busca por 99 deve retornar null", avl.search(99));
    }

    static void testarRemocaoFolha() {
        System.out.println("\n--- Remoção de Folha ---");
        AVL avl = new AVL();

        avl.insert(10);
        avl.insert(5);
        avl.insert(15);

        avl.remove(5);
        assertNull("nó 5 não deve mais existir", avl.search(5));
        assertNotNull("raiz 10 ainda deve existir", avl.search(10));
    }

    static void testarRemocaoComDoisFilhos() {
        System.out.println("\n--- Remoção com Dois Filhos ---");
        AVL avl = new AVL();

        avl.insert(10);
        avl.insert(5);
        avl.insert(15);
        avl.insert(12);
        avl.insert(20);

        avl.remove(15);
        assertNull("nó 15 não deve mais existir", avl.search(15));
        assertNotNull("nó 12 ainda deve existir", avl.search(12));
        assertNotNull("nó 20 ainda deve existir", avl.search(20));
    }

    static void testarAltura() {
        System.out.println("\n--- Altura ---");
        AVL avl = new AVL();

        avl.insert(10);
        assertEquals("altura com 1 nó deve ser 0 (folha)", 0, avl.root.height);

        avl.insert(5);
        avl.insert(15);
        assertEquals("altura com 3 nós balanceados deve ser 1", 1, avl.root.height);
    }

    static void testarFatorDeBalanceamento() {
        System.out.println("\n--- Fator de Balanceamento ---");
        AVL avl = new AVL();

        avl.insert(10);
        avl.insert(5);
        avl.insert(15);

        assertEquals("FB da raiz deve ser 0", 0, avl.getBalanceFactor(avl.root));
    }

    static void testarNuloProtecao() {
        System.out.println("\n--- Proteção contra Nulo ---");
        AVL avl = new AVL();

        assertEquals("getBalanceFactor de nulo deve ser 0", 0, avl.getBalanceFactor(null));
        assertEquals("height de nulo deve ser -1", -1, avl.height(null));
        assertNull("rebalance de nulo deve retornar null", avl.rebalance(null));
        assertNull("minimum de nulo deve retornar null", avl.minimum(null));
        assertNull("maximum de nulo deve retornar null", avl.maximum(null));
    }

    public static void main(String[] args) {
        testarInsercaoSimples();
        testarBalanceamentoEsquerdaEsquerda();
        testarBalanceamentoDireitaDireita();
        testarBalanceamentoEsquerdaDireita();
        testarBalanceamentoDireitaEsquerda();
        testarBusca();
        testarRemocaoFolha();
        testarRemocaoComDoisFilhos();
        testarAltura();
        testarFatorDeBalanceamento();
        testarNuloProtecao();

        System.out.println("\n══════════════════════════════");
        System.out.println("Total: " + (testsPassed + testsFailed) +
                " | ✅ " + testsPassed + " | ❌ " + testsFailed);
    }
}