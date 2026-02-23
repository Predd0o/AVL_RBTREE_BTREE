public class AVLTest {

    static int testsPassed = 0;
    static int testsFailed = 0;

    // ── helper de assert ────────────────────────────────────────────────────
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

    // ── testes ──────────────────────────────────────────────────────────────
    static void testarInsercaoSimples() {
        System.out.println("\n--- Inserção Simples ---");
        AVL avl = new AVL();

        avl.insert(10);
        assertEquals("raiz deve ser 10", 10, avl.root.value);
        assertEquals("altura da raiz deve ser 1", 1, avl.root.height);
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
        assertEquals("filho esquerdo da raiz deve ser 3", 3, avl.root.left.value);
        assertEquals("filho direito da raiz deve ser 10", 10, avl.root.right.value);
    }

    static void testarBalanceamentoDireitaDireita() {
        System.out.println("\n--- Rotação Esquerda (Direita-Direita) ---");
        AVL avl = new AVL();

        // inserindo 3, 5, 10 → provoca rotação esquerda
        // antes: 3          depois:   5
        //         \                  / \
        //          5                3   10
        //           \
        //            10
        avl.insert(3);
        avl.insert(5);
        avl.insert(10);

        assertEquals("nova raiz deve ser 5", 5, avl.root.value);
        assertEquals("filho esquerdo da raiz deve ser 3", 3, avl.root.left.value);
        assertEquals("filho direito da raiz deve ser 10", 10, avl.root.right.value);
    }

    static void testarBalanceamentoEsquerdaDireita() {
        System.out.println("\n--- Rotação Dupla Esquerda-Direita ---");
        AVL avl = new AVL();

        // inserindo 10, 3, 6 → provoca rotação dupla esquerda-direita
        // antes:  10        depois:   6
        //        /                   / \
        //       3                   3   10
        //        \
        //         6
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
        // antes: 3          depois:   6
        //         \                  / \
        //          10               3   10
        //         /
        //        6
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

        avl.remove(5); // remove folha
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

        avl.remove(15); // remove nó com dois filhos, substituto deve ser 20
        assertNull("nó 15 não deve mais existir", avl.search(15));
        assertNotNull("nó 12 ainda deve existir", avl.search(12));
        assertNotNull("nó 20 ainda deve existir", avl.search(20));
    }

    static void testarAltura() {
        System.out.println("\n--- Altura ---");
        AVL avl = new AVL();

        avl.insert(10);
        assertEquals("altura com 1 nó deve ser 1", 1, avl.root.height);

        avl.insert(5);
        avl.insert(15);
        assertEquals("altura com 3 nós balanceados deve ser 2", 2, avl.root.height);
    }

    static void testarFatorDeBalanceamento() {
        System.out.println("\n--- Fator de Balanceamento ---");
        AVL avl = new AVL();

        avl.insert(10);
        avl.insert(5);
        avl.insert(15);

        // árvore balanceada, FB da raiz deve ser 0
        assertEquals("FB da raiz deve ser 0", 0, avl.getBalanceFactor(avl.root));
    }

    // ── main ────────────────────────────────────────────────────────────────
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

        System.out.println("\n══════════════════════════════");
        System.out.println("Total: " + (testsPassed + testsFailed) +
                " | ✅ " + testsPassed + " | ❌ " + testsFailed);
    }
}