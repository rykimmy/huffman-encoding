import java.io.*;
import java.util.*;

/**
 * Compresses and decompresses text files using a Huffman Tree
 * @author Anand McCoole (TA: Jason Pak), Dartmouth CS10, 10 Feb 2022
 * @author Ryan Kim (TA: Caroline Hall), Dartmouth CS10, 10 Feb 2022
 */

public class HuffmanTree {
    String filename;
    String sub;

    // Constructor to set the filename
    public HuffmanTree(String name) {
        filename = name;
        sub = filename.substring(0, filename.length() - 4);
    }
    /**
     * Reads the file and creates initial TreeMap of characters to their int frequencies
     * @return Map of char:frequency
     * @throws IOException
     */
    public TreeMap<Character, Integer> charFrequencies() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        TreeMap<Character, Integer> frequencyMap = new TreeMap<>();

        try {
            int r;

            while ((r = fileReader.read()) != -1) {
                Character ch = (char) r;
                if (frequencyMap.containsKey(ch)) {
                    frequencyMap.put(ch, frequencyMap.get(ch) + 1);
                }
                else {
                    frequencyMap.put(ch, 1);
                }
            }
            //fileReader.close();
            return frequencyMap;
        } catch (IOException e){
            System.err.println("issue with file");
        } finally {
            fileReader.close();
        }
        return frequencyMap;
    }

    /**
     * Turns every key:value pair in the map into a priority queue full of single node Binary Trees
     * @param freqMap, the map that maps characters to their frequency
     * @return initTree, PriorityQueue that holds BinaryTrees that hold TreeElements
     */
    public PriorityQueue<BinaryTree<TreeElement>> initialTreePQ(TreeMap<Character, Integer> freqMap) {
        TreeComparator comp = new TreeComparator();
        PriorityQueue<BinaryTree<TreeElement>> initTree = new PriorityQueue<>(comp);
        Set<Character> keys = freqMap.keySet();
        for (Character key : keys) {
            TreeElement e = new TreeElement(key, freqMap.get(key));
            BinaryTree<TreeElement> bt = new BinaryTree<>(e, null, null);
            initTree.add(bt);
        }
        return initTree;
    }

    /**
     * Goes through the BinaryTrees in the PQ and continuously combines these trees to create a larger BinaryTree
     * @param initTree, the PQ of all the BinaryTrees
     * @return the final, singular BinaryTree holding all the characters and their frequencies
     */
    public BinaryTree<TreeElement> createHuffman(PriorityQueue<BinaryTree<TreeElement>> initTree) {
        // If there's only one tree and therefore one character value in text file
        //      Then create a new tree with children as the only previous-existing tree as left, right children
        if (initTree.size() == 1) {
            BinaryTree<TreeElement> onlyT = initTree.remove();
            TreeElement newE = new TreeElement(null, onlyT.getData().getFreq());
            BinaryTree<TreeElement> newT = new BinaryTree<>(newE, onlyT, onlyT);
            return newT;
        }
        while (initTree.size() >= 2) {
            BinaryTree<TreeElement> t1 = initTree.remove();
            BinaryTree<TreeElement> t2 = initTree.remove();

            int fsum = t1.getData().getFreq() + t2.getData().getFreq();
            TreeElement newE = new TreeElement(null, fsum);
            BinaryTree<TreeElement> newT = new BinaryTree<>(newE, t1, t2);

            initTree.add(newT);
        }
        return initTree.remove();
    }

    /**
     * Uses a recursive helper to traverse down the tree and map all the characters to their codes
     * @param huffTree
     * @return
     */
    public Map<Character, String> codeRetrieval(BinaryTree<TreeElement> huffTree){
        Map<Character, String> codeMap = new TreeMap<>();
        String code = "";
        retrievalHelper(codeMap, huffTree, code);
        return codeMap;
    }

    /**
     * Helper function that traverses through tree and creates the code for each character, then mapping it
     * @param codeMap
     * @param huffTree
     * @param code
     */
    public void retrievalHelper(Map<Character, String> codeMap, BinaryTree<TreeElement> huffTree, String code){
        if (huffTree.isLeaf()) {
            codeMap.put(huffTree.getData().getData(), code);
        }
        if (huffTree.hasLeft()) {
            retrievalHelper(codeMap, huffTree.getLeft(), code + "0");
        }
        if (huffTree.hasRight()) {
            retrievalHelper(codeMap, huffTree.getRight(), code + "1");
        }
    }

    /**
     * Compresses the file by writing the bit codes of each char into a new file
     * @param codeMap
     * @throws IOException
     */
    public void compress(Map<Character, String> codeMap) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        BufferedBitWriter bitOutput = new BufferedBitWriter(sub + "_compressed.txt");

        try {
            int r;
            while ((r = fileReader.read()) != -1) {
                Character ch = (char) r;
                for (Character c : codeMap.get(ch).toCharArray()) {
                    if (c == '0') { bitOutput.writeBit(false); }
                    else { bitOutput.writeBit(true);}
                }
            }
            //bitOut.close();
            //fileReader.close();
        } catch (IOException e) {
            System.err.println("issue with file");
        } finally {
            bitOutput.close();
            fileReader.close();
        }
    }

    /**
     * Decompresses the compressed file by reading its bit codes to find the correct char and writing into a new file
     * @param huffTree
     * @throws IOException
     */
    public void decompress(BinaryTree<TreeElement> huffTree) throws IOException {
        BufferedBitReader bitInput = new BufferedBitReader(sub+"_compressed.txt");
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(sub+"_decompressed.txt"));

        try {
            BinaryTree<TreeElement> thisNode = huffTree;

            while (bitInput.hasNext()) {
                boolean bit = bitInput.readBit();

                if (!bit) {
                    thisNode = thisNode.getLeft();
                } else if (bit) {
                    thisNode = thisNode.getRight();
                }
                if (thisNode.isLeaf()) {
                    char ch = thisNode.data.getData();
                    fileWriter.write(ch);
                    thisNode = huffTree;
                }
            }
        } catch (IOException e) {
            System.err.println("issue with file");
        } finally {
            bitInput.close();
            fileWriter.close();
        }
    }

    public static void main(String[] args) throws IOException {
        // Calling constructor - passing filename
        HuffmanTree huff = new HuffmanTree("inputs/WarAndPeace.txt");

        // Creates char:freq Map
        TreeMap<Character, Integer> map = huff.charFrequencies();
        System.out.println(map);

        // Creates PriorityQueue
        PriorityQueue<BinaryTree<TreeElement>> pq = huff.initialTreePQ(map);

        // Creates the HuffmanTree from the PriorityQueue
        BinaryTree<TreeElement> h = huff.createHuffman(pq);
        System.out.println(h);

        // Creates bit code Map
        Map<Character, String> m = huff.codeRetrieval(h);
        System.out.println(m);

        // Compressing and Decompressing
        huff.compress(m);
        huff.decompress(h);
    }
}
