package exercise2;

import de.hpi.dbs2.ChosenImplementation;
import de.hpi.dbs2.exercise2.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Stack;

/**
 * This is the B+-Tree implementation you will work on.
 * Your task is to implement the insert-operation.
 *
 */
@ChosenImplementation(true)
public class BPlusTreeJava extends AbstractBPlusTree {
    public BPlusTreeJava(int order) {
        super(order);
    }

    public BPlusTreeJava(BPlusTreeNode<?> rootNode) {
        super(rootNode);
    }

    @Nullable
    @Override
    public ValueReference insert(@NotNull Integer key, @NotNull ValueReference value) {
        LeafNode leafNode = rootNode.findLeaf(key);
        Stack<BPlusTreeNode> nodeStackNode = new Stack<>();
        nodeStackNode = getParents(rootNode, key);
        int firstVein = 0;
        int lastVein = leafNode.getLeafSize();
        int leafSize = lastVein;
        if (leafNode.isEmpty()) {
            if (nodeStackNode.isEmpty()){
                BPlusTreeNode newRootNode = new InnerNode(order);
                this.rootNode = new InnerNode(order);
                newRootNode = rootNode;
                newRootNode.references[0] = leafNode;
                nodeStackNode.push(newRootNode);
            }
            leafNode.keys[firstVein] = key;
            leafNode.references[firstVein] = value;
        }else if (!isNewKey(leafNode, key, value, firstVein, lastVein)) {
            for (int currentVein = firstVein; currentVein < lastVein; currentVein++) {
                if (key == leafNode.keys[currentVein]) {
                    ValueReference oldReference = leafNode.references[currentVein];
                    leafNode.references[currentVein] = value;
                    return oldReference;
                }
            }
        }else {
            if (!leafNode.isFull()) {
                findCorrectPlace(leafNode, key, value, firstVein, lastVein);
            }else {
                if(! (leafNode.nextSibling == null)){
                    if (!leafNode.nextSibling.isFull() && key > leafNode.keys[lastVein-1]) {
                        for (int greaterVeins = leafSize - 1; greaterVeins > 0; greaterVeins--) {
                            leafNode.nextSibling.keys[firstVein + greaterVeins] = leafNode.nextSibling.keys[firstVein + (greaterVeins - 1)];
                            leafNode.nextSibling.references[firstVein + greaterVeins] = leafNode.nextSibling.references[firstVein + (greaterVeins - 1)];
                        }
                        leafNode.nextSibling.keys[firstVein] = key;
                        leafNode.nextSibling.references[firstVein] = value;
                        if (nodeStackNode == null){
                            ;//BPlusTreeNode r
                        } else {
                            BPlusTreeNode parentNode = nodeStackNode.pop();
                            for (int i = 0; i < parentNode.order; i++) {
                                if (parentNode.keys[i] == leafNode.nextSibling.keys[1]) {
                                    parentNode.keys[i] = leafNode.nextSibling.keys[0];
                                    parentNode.references[i + 1] = leafNode.nextSibling;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    int leafNodeSizeForSplit = leafNode.order + 1;
                    LeafNode leafNodeToSplit = new LeafNode(leafNodeSizeForSplit);
                    for (int vein = firstVein; vein < leafSize; vein++) {
                        leafNodeToSplit.keys[vein] = leafNode.keys[vein];
                        leafNodeToSplit.references[vein] = leafNode.references[vein];
                    }
                    findCorrectPlace(leafNodeToSplit, key, value, firstVein, leafSize);

                    for (int vein = firstVein; vein < leafNode.getLeafSize(); vein++) {
                        if (vein < (leafSize + 1) / 2) { //make sure the rounded up half gets filled in the first node
                            leafNode.keys[vein] = leafNodeToSplit.keys[vein];
                            leafNode.references[vein] = leafNodeToSplit.references[vein];
                        } else {    //the rest gets removed and filled into new leafNode
                            leafNode.keys[vein] = null;
                            leafNode.references[vein] = null;
                        }
                    }

                    LeafNode newLeafNode = new LeafNode(order);
                    for (int vein = firstVein; vein < leafNodeToSplit.getLeafSize() - (leafSize + 1) / 2; vein++) { //make sure only left over keys are filled into new leafNode
                        newLeafNode.keys[vein] = leafNodeToSplit.keys[vein + (leafSize + 1) / 2];
                        newLeafNode.references[vein] = leafNodeToSplit.references[vein + (leafSize + 1) / 2];
                    }
                    if (leafNode.nextSibling != null){
                        newLeafNode.nextSibling = leafNode.nextSibling;
                    }
                    leafNode.nextSibling = newLeafNode;

                    /*BPlusTreeNode parentNode = nodeStackNode.pop();
                    if(parentNode.isEmpty()){
                        parentNode.keys[0] = newLeafNode.keys[0];
                        parentNode.references[0+1] = newLeafNode;
                    }
                    if(parentNode.isFull()){
                        ;
                    }else {
                        //findCorrectPlace(parentNode, newLeafNode.keys[0],newLeafNode,firstVein, parentNode.getLeafSize());
                        int nodeSize = getNodeSize(parentNode);
                        System.out.println("parentnodeSize: " + nodeSize);
                        if (newLeafNode.keys[0] > parentNode.keys[nodeSize - 1]) {
                            parentNode.keys[nodeSize] = newLeafNode.keys[0];
                            parentNode.references[nodeSize+1] = newLeafNode;
                        } else {
                            for (int currentLeaf = 0; currentLeaf < nodeSize; currentLeaf++) {
                                if (newLeafNode.keys[0] < parentNode.keys[currentLeaf]) {
                                    for (int greaterLeafs = nodeSize - currentLeaf; greaterLeafs > 0; greaterLeafs--) {
                                        parentNode.keys[currentLeaf + greaterLeafs] = parentNode.keys[currentLeaf + greaterLeafs - 1];
                                        parentNode.references[currentLeaf + greaterLeafs] = leafNode.references[currentLeaf + greaterLeafs - 1];
                                    }
                                    parentNode.keys[currentLeaf] = newLeafNode.keys[0];
                                    parentNode.references[currentLeaf+1] = newLeafNode;
                                    break;
                                }
                            }
                        }
                    }*/

                }
            }
        }
        // Find LeafNode in which the key has to be inserted.
        //   It is a good idea to track the "path" to the LeafNode in a Stack or something alike.
        // Does the key already exist? Overwrite!
        //   leafNode.references[pos] = value;
        //   But remember return the old value!
        // New key - Is there still space?
        //   leafNode.keys[pos] = key;
        //   leafNode.references[pos] = value;
        //   Don't forget to update the parent keys and so on...
        // Otherwise,
        //   Split the LeafNode in two!
        //   Is parent node root?
        //     update rootNode = ... // will have only one key
        //   Was node instanceof LeafNode?
        //     update parentNode.keys[?] = ...
        //   Don't forget to update the parent keys and so on...

        // Check out the exercise slides for a flow chart of this logic.
        // If you feel stuck, try to draw what you want to do and
        // check out Ex2Main for playing around with the tree by e.g. printing or debugging it.
        // Also check out all the methods on BPlusTreeNode and how they are implemented or
        // the tests in BPlusTreeNodeTests and BPlusTreeTests!
        return null;
        //return leafNode.references[0];

    }

    public Stack getParents(BPlusTreeNode root, int key){
        Stack<BPlusTreeNode> nodeStackNode = new Stack<>();
        BPlusTreeNode thisNode = root;

        while ((thisNode instanceof LeafNode) == false) {
            nodeStackNode.push(thisNode);
            int entryReference = 0;

            for (int currentEntry = 0; currentEntry < thisNode.getLeafSize(); currentEntry++) {
                if (thisNode.keys[currentEntry] == null || thisNode.keys[currentEntry] > key) {
                    entryReference = currentEntry;
                    break;
                }
            }
            thisNode = (BPlusTreeNode) thisNode.references[entryReference];
        }
        return nodeStackNode;
    }

    public boolean isNewKey(BPlusTreeNode leafNode, int key, ValueReference value, int firstVein, int lastVein){
        for (int currentVein = firstVein; currentVein < lastVein; currentVein++){
            if (key == leafNode.keys[currentVein]){
                return false;
            }
        }
        return true;
    }

    public void findCorrectPlace(LeafNode leafNode, int key,ValueReference value, int firstVein, int lastVein){
        if (key > leafNode.keys[lastVein - 1]) {
            leafNode.keys[lastVein] = key;
            leafNode.references[lastVein] = value;
        } else {
            for (int currentVein = firstVein; currentVein < lastVein; currentVein++) {
                if (key < leafNode.keys[currentVein]) {
                    for (int greaterVeins = lastVein - currentVein; greaterVeins > 0; greaterVeins--) {
                        leafNode.keys[currentVein + greaterVeins] = leafNode.keys[currentVein + greaterVeins - 1];
                        leafNode.references[currentVein + greaterVeins] = leafNode.references[currentVein + greaterVeins - 1];
                    }
                    leafNode.keys[currentVein] = key;
                    leafNode.references[currentVein] = value;
                    break;
                }
            }
        }
    }

    public int getNodeSize(BPlusTreeNode node){
        int nodeCount = 0;
        if(!(node.keys[0] == null)) {
            for (int i = 0; i < node.order-1; i++) {
                if (node.keys[i] != null) {
                    nodeCount++;
                }
            }
        }
        return nodeCount;
    }

    public void idkYet(Stack stack, BPlusTreeNode currentNode, BPlusTreeNode neighborNode) {
       ;
    }

    /*public void insertToTree(BPlusTreeJava root, int key, ValueReference value){
        LeafNode leafNode = root.rootNode.findLeaf(key);
        System.out.println("insertToLeaf. LeafNode: " + leafNode);
    }*/
    //anstelle bplustree (vollständiger baum) besser root verlangen
    public ValueReference insertToLeaf(BPlusTreeNode root, int key, ValueReference value) {
        Stack<BPlusTreeNode> nodeStackNode = new Stack<>();
        nodeStackNode = getParents(root, key);

        LeafNode leafNode = root.findLeaf(key);
        int firstVein = 0;
        int lastVein = leafNode.getLeafSize();
        int leafSize = lastVein;
        if (leafNode.isEmpty()) {
            leafNode.keys[firstVein] = key;
            leafNode.references[firstVein] = value;

        }else if (!isNewKey(leafNode, key, value, firstVein, lastVein)) {
            for (int currentVein = firstVein; currentVein < lastVein; currentVein++) {
                if (key == leafNode.keys[currentVein]) {
                    ValueReference oldReference = leafNode.references[currentVein];
                    leafNode.references[currentVein] = value;
                    return oldReference;
                }
            }
        }else {
            if (!leafNode.isFull()) {
                findCorrectPlace(leafNode, key, value, firstVein, lastVein);
            }else {
                    if (!leafNode.nextSibling.isFull() && key > leafNode.keys[lastVein-1]) {
                        for (int greaterVeins = leafSize - 1; greaterVeins > 0; greaterVeins--) {
                            leafNode.nextSibling.keys[firstVein + greaterVeins] = leafNode.nextSibling.keys[firstVein + (greaterVeins - 1)];
                            leafNode.nextSibling.references[firstVein + greaterVeins] = leafNode.nextSibling.references[firstVein + (greaterVeins - 1)];
                        }
                        leafNode.nextSibling.keys[firstVein] = key;
                        leafNode.nextSibling.references[firstVein] = value;
                        BPlusTreeNode parentNode = nodeStackNode.pop();
                        for (int i = 0; i < parentNode.order; i++) {
                            if (parentNode.keys[i] == leafNode.nextSibling.keys[1]) {
                                parentNode.keys[i] = leafNode.nextSibling.keys[0];
                                parentNode.references[i + 1] = leafNode.nextSibling;
                                break;
                            }
                        }
                } else {
                    int leafNodeSizeForSplit = leafNode.order + 1;
                    LeafNode leafNodeToSplit = new LeafNode(leafNodeSizeForSplit);
                    for (int vein = firstVein; vein < leafSize; vein++) {
                        leafNodeToSplit.keys[vein] = leafNode.keys[vein];
                        leafNodeToSplit.references[vein] = leafNode.references[vein];
                    }
                    findCorrectPlace(leafNodeToSplit, key, value, firstVein, leafSize);

                    for (int vein = firstVein; vein < leafNode.getLeafSize(); vein++) {
                        if (vein < (leafSize + 1) / 2) { //make sure the rounded up half gets filled in the first node
                            leafNode.keys[vein] = leafNodeToSplit.keys[vein];
                            leafNode.references[vein] = leafNodeToSplit.references[vein];
                        } else {    //the rest gets removed and filled into new leafNode
                            leafNode.keys[vein] = null;
                            leafNode.references[vein] = null;
                        }
                    }

                    LeafNode newLeafNode = new LeafNode(order);
                    for (int vein = firstVein; vein < leafNodeToSplit.getLeafSize() - (leafSize + 1) / 2; vein++) { //make sure only left over keys are filled into new leafNode
                        newLeafNode.keys[vein] = leafNodeToSplit.keys[vein + (leafSize + 1) / 2];
                        newLeafNode.references[vein] = leafNodeToSplit.references[vein + (leafSize + 1) / 2];
                    }
                    newLeafNode.nextSibling = leafNode.nextSibling;
                    leafNode.nextSibling = newLeafNode;

                    BPlusTreeNode parentNode = nodeStackNode.pop();
                    if(parentNode.isEmpty()){
                        parentNode.keys[0] = newLeafNode.keys[0];
                        parentNode.references[0+1] = newLeafNode;
                    }
                    if(parentNode.isFull()){
                        /*int nodeSize = getNodeSize(parentNode);
                        int innerNodeSizeForSplit = rootNode.order + 1;
                        int entrysAlreadyDeployed = 0;
                        BPlusTreeNode innerNodeToSplit = new InnerNode(innerNodeSizeForSplit);
                        for (int i = 0; i < nodeSize; i++) {
                            innerNodeToSplit.keys[i] = leafNode.keys[i];
                            innerNodeToSplit.references[i] = leafNode;
                        }

                        for (int currentEntry = 0; currentEntry < nodeSize; currentEntry++) {
                            if (newLeafNode.keys[0] < parentNode.keys[currentEntry]) {
                                for (int greaterLeafs = nodeSize - currentEntry; greaterLeafs > 0; greaterLeafs--) {
                                    parentNode.keys[currentEntry + greaterLeafs] = parentNode.keys[currentEntry + greaterLeafs - 1];
                                    parentNode.references[currentEntry + greaterLeafs] = leafNode.references[currentEntry + greaterLeafs - 1];
                                }
                                parentNode.keys[currentEntry] = newLeafNode.keys[0];
                                parentNode.references[currentEntry+1] = newLeafNode;
                                break;
                            }
                        }

                        for (int entry = 0; entry < nodeSize; entry++) {
                            if (entry < (nodeSize + 1) / 2) { //make sure the rounded up half gets filled in the first node
                                parentNode.keys[entry] = innerNodeToSplit.keys[entry];
                                parentNode.references[entry] = innerNodeToSplit.references[entry];
                                entrysAlreadyDeployed++;
                            } else {    //the rest gets removed and filled into new leafNode
                                parentNode.keys[entry] = null;
                                parentNode.references[entry] = null;
                            }
                        }

                        InnerNode newInnerNode = new InnerNode(order);
                        for (int entry = 0; entry < entrysAlreadyDeployed; entry++) { //make sure only left over keys are filled into new leafNode
                            newInnerNode.keys[entry] = innerNodeToSplit.keys[entry + entrysAlreadyDeployed];
                            System.out.println("innerNodeToSplit.references[entry + entrysAlreadyDeployed]: " + innerNodeToSplit.references[entry + entrysAlreadyDeployed]);
                            //newInnerNode.references[entry] = innerNodeToSplit.references[entry + entrysAlreadyDeployed];
                        }*/

                    }else {
                        //findCorrectPlace(parentNode, newLeafNode.keys[0],newLeafNode,firstVein, parentNode.getLeafSize());
                        /*int nodeSize = getNodeSize(parentNode);
                        if (newLeafNode.keys[0] > parentNode.keys[nodeSize - 1]) {
                            parentNode.keys[nodeSize] = newLeafNode.keys[0];
                            parentNode.references[nodeSize+1] = newLeafNode;
                        } else {
                            for (int currentLeaf = 0; currentLeaf < nodeSize; currentLeaf++) {
                                if (newLeafNode.keys[0] < parentNode.keys[currentLeaf]) {
                                    for (int greaterLeafs = nodeSize - currentLeaf; greaterLeafs > 0; greaterLeafs--) {
                                        parentNode.keys[currentLeaf + greaterLeafs] = parentNode.keys[currentLeaf + greaterLeafs - 1];
                                        parentNode.references[currentLeaf + greaterLeafs] = leafNode.references[currentLeaf + greaterLeafs - 1];
                                    }
                                    parentNode.keys[currentLeaf] = newLeafNode.keys[0];
                                    parentNode.references[currentLeaf+1] = newLeafNode;
                                    break;
                                }
                            }
                        }*/
                    }

                }
            }
        }
        return null;
    }
}
