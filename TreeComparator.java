import java.util.Comparator;

public class TreeComparator implements Comparator<BinaryTree<TreeElement>> {

    public int compare(BinaryTree<TreeElement> o1, BinaryTree<TreeElement> o2) {
        return o1.data.getFreq() - o2.data.getFreq();
    }
}
