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
        if (node == null) return 0; // nó nulo tem altura 0
        return node.height;         // retorna a altura armazenada no nó
    }

    public int getBalanceFactor(Node node) {
        // FB = altura da esquerda - altura da direita
        return height(node.left) - height(node.right);
    }

    public void updateHeight(Node node) {
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
            node.left = insert(node.left, element);  // desce pela esquerda
        else if (element > node.value)
            node.right = insert(node.right, element); // desce pela direita
        // na volta da recursão:
        updateHeight(node);    // atualiza a altura do nó atual
        return rebalance(node); // rebalanceia e retorna o nó
    }

    public void insert(int element) {
        root = insert(root, element); // chama o recursivo e atualiza a raiz
    }

    private Node search(Node node, int element) {
        if (node == null) return null;          // elemento não encontrado
        else if (element < node.value)
            return search(node.left, element);  // desce pela esquerda
        else if (element > node.value)
            return search(node.right, element); // desce pela direita
        return node; // elemento encontrado, retorna o nó
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
        updateHeight(node);    // atualiza a altura na volta da recursão
        return rebalance(node); // rebalanceia e retorna o nó
    }

    public void remove(int element) {
        root = remove(root, element); // chama o recursivo e atualiza a raiz
    }

    public Node minimum(Node node) {
        if (node.left == null) return node;  // chegou no menor elemento
        return minimum(node.left);           // desce sempre pela esquerda
    }

    public Node maximum(Node node) {
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
        this.height = 1; // nó recém criado é folha, altura 1
    }
}