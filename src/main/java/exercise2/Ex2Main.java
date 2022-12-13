package exercise2;

import de.hpi.dbs2.exercise2.*;

public class Ex2Main {
    public static void main(String[] args) {
        int order = 4;

        BPlusTreeNode<?> root = BPlusTreeNode.buildTree(order,
            new AbstractBPlusTree.Entry[][]{
                new AbstractBPlusTree.Entry[]{
                        new AbstractBPlusTree.Entry(2, new ValueReference(0)),
                        new AbstractBPlusTree.Entry(3, new ValueReference(1)),
                        new AbstractBPlusTree.Entry(5, new ValueReference(2))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(7, new ValueReference(3)),
                    new AbstractBPlusTree.Entry(11, new ValueReference(4))
                }
            },
            new AbstractBPlusTree.Entry[][]{
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(13, new ValueReference(5)),
                    new AbstractBPlusTree.Entry(17, new ValueReference(6)),
                    new AbstractBPlusTree.Entry(19, new ValueReference(7))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(23, new ValueReference(8)),
                    new AbstractBPlusTree.Entry(29, new ValueReference(9))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(31, new ValueReference(10)),
                    new AbstractBPlusTree.Entry(37, new ValueReference(11)),
                    new AbstractBPlusTree.Entry(41, new ValueReference(12))
                },
                new AbstractBPlusTree.Entry[]{
                    new AbstractBPlusTree.Entry(43, new ValueReference(13)),
                    new AbstractBPlusTree.Entry(47, new ValueReference(14))
                }
            }
        );
        System.out.println(root);

        BPlusTreeJava tree = new BPlusTreeJava(root);
        //System.out.println(tree);
        /*System.out.println("leafNode-------------------------------------------------------------------");
        LeafNode leafNode = new LeafNode(4);
        System.out.println(leafNode);
        System.out.println("innerNode-------------------------------------------------------------------");
        InnerNode innerNode = new InnerNode(4);
        System.out.println(innerNode);*/

        /*
         * playground
         * ~ feel free to experiment with the tree and tree nodes here
         */

        //tree.insertToTree(tree, 3, new ValueReference(0));
        System.out.println("insert into leaf begin-------------------------------------------------------------------");
        tree.insertToLeaf(root, 6, new ValueReference(1337));
        System.out.println("after first insert-------------------------------------------------------------------");
        tree.insertToLeaf(root, 12, new ValueReference(1338));
        tree.insertToLeaf(root, 8, new ValueReference(1338));
        tree.insertToLeaf(root, 10, new ValueReference(1338));
        tree.insertToLeaf(root, 9, new ValueReference(1338));
        System.out.println("insert into leaf end-------------------------------------------------------------------");
        System.out.println(root);
    }
}
