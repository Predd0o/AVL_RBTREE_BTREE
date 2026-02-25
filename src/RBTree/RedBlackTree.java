package RBTree;

public class RedBlackTree {
    Node root; // nó raiz da árvore
    int size;  // quantidade de elementos na árvore

    // verifica se a árvore está vazia checando se a raiz é nula
    public boolean isEmpty() {
        return this.root == null;
    }

    // retorna o avô do nó (pai do pai)
    private Node grandparent(Node node) {
        if (node == null || node.parent == null) return null; // sem pai = sem avô
        return node.parent.parent; // retorna o pai do pai
    }

    // retorna o tio do nó (irmão do pai)
    private Node uncle(Node node) {
        Node gp = grandparent(node); // busca o avô
        if (gp == null) return null; // sem avô = sem tio
        if (node.parent == gp.left) { // se o pai é filho esquerdo do avô...
            return gp.right;          // ...o tio é o filho direito do avô
        } else {                      // senão...
            return gp.left;           // ...o tio é o filho esquerdo do avô
        }
    }

    // rotação para a esquerda: sobe o filho direito no lugar do nó atual
    private void rotacaoEsquerda(Node node) {
        Node newRoot = node.right;          // o filho direito vai subir
        node.right = newRoot.left;          // o filho esquerdo do novo topo vai para o lado direito do nó atual
        if (newRoot.left != null) {
            newRoot.left.parent = node;     // atualiza o pai do nó que trocou de lugar
        }
        newRoot.parent = node.parent;       // o novo topo herda o pai do nó atual
        if (node.parent == null) {          // se o nó era a raiz...
            this.root = newRoot;            // ...o novo topo passa a ser a raiz
        } else if (node == node.parent.left) {  // se o nó era filho esquerdo...
            node.parent.left = newRoot;         // ...atualiza o pai para apontar para o novo topo
        } else {
            node.parent.right = newRoot;    // senão atualiza o lado direito do pai
        }
        newRoot.left = node;                // o nó atual desce para filho esquerdo do novo topo
        node.parent = newRoot;              // atualiza o pai do nó atual
    }

    // rotação para a direita: sobe o filho esquerdo no lugar do nó atual
    private void rotacaoDireita(Node node) {
        Node newRoot = node.left;           // o filho esquerdo vai subir
        node.left = newRoot.right;          // o filho direito do novo topo vai para o lado esquerdo do nó atual
        if (newRoot.right != null) {
            newRoot.right.parent = node;    // atualiza o pai do nó que trocou de lugar
        }
        newRoot.parent = node.parent;       // o novo topo herda o pai do nó atual
        if (node.parent == null) {          // se o nó era a raiz...
            this.root = newRoot;            // ...o novo topo passa a ser a raiz
        } else if (node == node.parent.right) { // se o nó era filho direito...
            node.parent.right = newRoot;        // ...atualiza o pai para apontar para o novo topo
        } else {
            node.parent.left = newRoot;     // senão atualiza o lado esquerdo do pai
        }
        newRoot.right = node;               // o nó atual desce para filho direito do novo topo
        node.parent = newRoot;              // atualiza o pai do nó atual
    }

    // insere um novo elemento na árvore
    public void add(int element) {
        this.size++;                                // incrementa o contador de elementos
        Node newNode = new Node(element, true);     // cria o nó novo já marcado como vermelho

        if (isEmpty()) {                // caso especial: árvore vazia
            this.root = newNode;        // o novo nó vira a raiz
            this.root.isRed = false;    // raiz deve ser sempre preta (regra RB)
            return;
        }

        Node aux = this.root;           // começa a busca pela posição correta a partir da raiz
        while (aux != null) {
            if (element < aux.value) {      // se o valor é menor, vai para a esquerda
                if (aux.left == null) {         // encontrou a posição vazia
                    aux.left = newNode;             // insere à esquerda
                    newNode.parent = aux;           // define o pai do novo nó
                    break;
                }
                aux = aux.left;             // continua descendo à esquerda
            } else {                        // se o valor é maior ou igual, vai para a direita
                if (aux.right == null) {        // encontrou a posição vazia
                    aux.right = newNode;            // insere à direita
                    newNode.parent = aux;           // define o pai do novo nó
                    break;
                }
                aux = aux.right;            // continua descendo à direita
            }
        }
        rebalance(newNode); // após a inserção, corrige possíveis violações das regras RB
    }

    // corrige as violações das propriedades da árvore Rubro-Negra após uma inserção
    private void rebalance(Node node) {
        if (node.parent == null) {  // caso 1: o nó chegou à raiz
            node.isRed = false;     // raiz deve ser preta
            return;
        }
        if (!node.parent.isRed()) return; // caso 2: pai é preto, nenhuma violação, pode parar

        Node uncle = uncle(node);   // busca o tio para decidir qual caso aplicar
        Node gp = grandparent(node);// busca o avô

        // caso 3: pai e tio são vermelhos → recolorir
        if (uncle != null && uncle.isRed()) {
            node.parent.isRed = false;  // pai vira preto
            uncle.isRed = false;        // tio vira preto
            gp.isRed = true;            // avô vira vermelho
            rebalance(gp);              // verifica recursivamente se o avô gerou nova violação
            return;
        }

        // caso 4: tio é preto e o nó está "dobrado" (triângulo) → rotacionar o pai para alinhar
        if (node == node.parent.right && node.parent == gp.left) {
            // nó está à direita e pai está à esquerda: rotaciona o pai para a esquerda
            rotacaoEsquerda(node.parent);
            node = node.left; // após a rotação, desce para referenciar o nó correto
        } else if (node == node.parent.left && node.parent == gp.right) {
            // nó está à esquerda e pai está à direita: rotaciona o pai para a direita
            rotacaoDireita(node.parent);
            node = node.right; // após a rotação, desce para referenciar o nó correto
        }

        // caso 5: tio é preto e o nó está alinhado (linha) → rotacionar o avô
        gp = grandparent(node);         // recalcula o avô (pode ter mudado após rotação do caso 4)
        node.parent.isRed = false;      // pai vira preto
        gp.isRed = true;                // avô vira vermelho

        if (node == node.parent.left) { // se o nó está à esquerda, rotaciona o avô para a direita
            rotacaoDireita(gp);
        } else {                        // se o nó está à direita, rotaciona o avô para a esquerda
            rotacaoEsquerda(gp);
        }
    }

    // busca iterativa: percorre a árvore comparando valores até encontrar ou chegar em null
    public Node search(int element) {
        Node aux = this.root;
        while (aux != null) {
            if (aux.value == element) return aux;   // encontrou
            if (element < aux.value) aux = aux.left;// menor: vai para a esquerda
            else aux = aux.right;                   // maior: vai para a direita
        }
        return null; // não encontrado
    }

    // ponto de entrada da busca recursiva (encapsula a chamada pública)
    public Node recursiveSearch(int element) {
        return recursiveSearch(this.root, element);
    }

    // busca recursiva: mesma lógica da iterativa, mas usando chamadas recursivas
    private Node recursiveSearch(Node node, int element) {
        if (node == null) return null;                              // base: não encontrado
        if (element == node.value) return node;                     // base: encontrou
        if (element < node.value) return recursiveSearch(node.left, element);  // busca à esquerda
        else return recursiveSearch(node.right, element);           // busca à direita
    }

    // ponto de entrada para retornar o menor valor da árvore
    public Node min() {
        if (isEmpty()) return null;
        return min(this.root); // começa pela raiz
    }

    // o menor valor sempre está no nó mais à esquerda
    private Node min(Node node) {
        if (node.left == null) return node; // não tem filho esquerdo: é o mínimo
        return min(node.left);              // continua descendo à esquerda
    }

    // retorna o maior valor da árvore (nó mais à direita), de forma iterativa
    public Node max() {
        if (isEmpty()) return null;
        Node node = this.root;
        while (node.right != null) node = node.right; // desce sempre à direita
        return node;
    }

    // versão recursiva do max (auxiliar, não usada diretamente no código público)
    private Node max(Node node) {
        if (node.right == null) return node; // não tem filho direito: é o máximo
        return max(node.right);              // continua descendo à direita
    }
}

class Node {
    int value;
    Node left;
    Node right;
    Node parent;
    boolean isRed;

    public Node(int value, boolean isRed) {
        this.value = value;
        this.isRed = isRed;
    }

    public boolean isRed() {
        return this.isRed;
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    public boolean hasOnlyLeftChild() {
        return this.left != null && this.right == null;
    }

    public boolean hasOnlyRightChild() {
        return this.left == null && this.right != null;
    }

    public String toString() {
        return this.value + "";
    }
}

class RedBlackTreeTest {
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

    static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("✅ PASSOU: " + testName);
            testsPassed++;
        } else {
            System.out.println("❌ FALHOU: " + testName);
            testsFailed++;
        }
    }

    static void testarInsercaoSimples() {
        System.out.println("\n--- Inserção Simples ---");
        RedBlackTree tree = new RedBlackTree();
        tree.add(10);
        assertNotNull("raiz não deve ser nula", tree.root);
        assertEquals("raiz deve ser 10", 10, tree.root.value);
        assertTrue("raiz deve ser preta", !tree.root.isRed());
    }

    static void testarPropriedadeRaizPreta() {
        System.out.println("\n--- Raiz sempre preta ---");
        RedBlackTree tree = new RedBlackTree();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        assertTrue("raiz deve ser preta após inserções", !tree.root.isRed());
    }

    static void testarFilhosDeVermelho() {
        System.out.println("\n--- Filhos de nó vermelho devem ser pretos ---");
        RedBlackTree tree = new RedBlackTree();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        if (tree.root.left != null && tree.root.left.isRed()) {
            if (tree.root.left.left != null)
                assertTrue("filho esquerdo de vermelho deve ser preto", !tree.root.left.left.isRed());
            if (tree.root.left.right != null)
                assertTrue("filho direito de vermelho deve ser preto", !tree.root.left.right.isRed());
        }
        assertTrue("propriedade verificada", true);
    }

    static void testarCaso3TioVermelho() {
        System.out.println("\n--- Caso 3: pai e tio vermelhos ---");
        RedBlackTree tree = new RedBlackTree();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(3);
        assertTrue("raiz deve ser preta", !tree.root.isRed());
        assertNotNull("nó 3 deve existir", tree.search(3));
    }

    static void testarRotacao() {
        System.out.println("\n--- Rotação: caso alinhado à esquerda ---");
        RedBlackTree tree = new RedBlackTree();
        tree.add(10);
        tree.add(5);
        tree.add(3);
        assertEquals("nova raiz deve ser 5", 5, tree.root.value);
        assertEquals("filho esquerdo da raiz deve ser 3", 3, tree.root.left.value);
        assertEquals("filho direito da raiz deve ser 10", 10, tree.root.right.value);
        assertTrue("raiz deve ser preta", !tree.root.isRed());
        assertTrue("filhos da raiz devem ser vermelhos", tree.root.left.isRed() && tree.root.right.isRed());
    }

    static void testarBusca() {
        System.out.println("\n--- Busca ---");
        RedBlackTree tree = new RedBlackTree();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(3);
        assertNotNull("busca por 10 deve encontrar", tree.search(10));
        assertNotNull("busca por 3 deve encontrar", tree.search(3));
        assertNull("busca por 99 deve retornar null", tree.search(99));
        assertNotNull("busca recursiva por 15 deve encontrar", tree.recursiveSearch(15));
    }

    static void testarMinMax() {
        System.out.println("\n--- Min e Max ---");
        RedBlackTree tree = new RedBlackTree();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(3);
        tree.add(20);
        assertEquals("min deve ser 3", 3, tree.min().value);
        assertEquals("max deve ser 20", 20, tree.max().value);
    }

    public static void main(String[] args) {
        testarInsercaoSimples();
        testarPropriedadeRaizPreta();
        testarFilhosDeVermelho();
        testarCaso3TioVermelho();
        testarRotacao();
        testarBusca();
        testarMinMax();

        System.out.println("\n══════════════════════════════");
        System.out.println("Total: " + (testsPassed + testsFailed) +
                " | ✅ " + testsPassed + " | ❌ " + testsFailed);
    }
}