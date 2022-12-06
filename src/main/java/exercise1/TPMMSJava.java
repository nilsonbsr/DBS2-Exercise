package exercise1;

import de.hpi.dbs2.ChosenImplementation;
import de.hpi.dbs2.dbms.*;
import de.hpi.dbs2.dbms.utils.BlockSorter;
import de.hpi.dbs2.exercise1.SortOperation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

@ChosenImplementation(true)
public class TPMMSJava extends SortOperation {
    public TPMMSJava(@NotNull BlockManager manager, int sortColumnIndex) {
        super(manager, sortColumnIndex);
    }

    @Override
    public int estimatedIOCost(@NotNull Relation relation) {
        int iOCostPerSublist =  3 * relation.getEstimatedSize();
        return iOCostPerSublist;
    }

    @Override
    public void sort(@NotNull Relation relation, @NotNull BlockOutput output) {

       BlockManager blockmanager = getBlockManager();
       
        // check if memory is large enough if not throw exception
        if(relation.getEstimatedSize() > blockmanager.getFreeBlocks() * 3){
           throw new RelationSizeExceedsCapacityException();
       }

        //Block segmentation
        int blockSegmentaion = (int)Math.floor(relation.getEstimatedSize() / blockmanager.getFreeBlocks());

        ArrayList<Block> sublist = new ArrayList<>(blockSegmentaion);
        ArrayList<Block> block = new ArrayList<>();
        ColumnDefinition columnDefinition = relation.getColumns();
        int sortedIndex = getSortColumnIndex();


        // in case the blocks ain't empty sort them and wipe them from memory
        if (!block.isEmpty()){
            BlockSorter.INSTANCE.sort(relation, block, columnDefinition.getColumnComparator(sortedIndex));
            for(Iterator<Block> blockIterator = relation.iterator(); blockIterator.hasNext();){
                Block b = blockIterator.next();
                blockmanager.release(b, false);
            }
           // Iterator<Block> blockIterator = relation.iterator();
            //blockIterator.hasNext();

        }
        // for each block in relation we add the blocks in our relation and then if we have free blocks we can sort them by index
        /*if(blockmanager.getFreeBlocks() != 0){
            for(Iterator<Block> blockIterator = relation.iterator(); blockIterator.hasNext();){
                Block b = blockIterator.next();
            }*/

        // iterate through blocks, load and add to current list till full and then move it to disc
        for(Block blk: relation){
            Block b = blockmanager.load(blk);
            block.add(b);
            //int currentBlockListIndex = 0;
           // ArrayList<Block> blockElement = new ArrayList<>();

            if(blockmanager.getFreeBlocks() != 0){
                //blockmanager.load(b);
                //block.add(b);
                BlockSorter.INSTANCE.sort(relation, block, columnDefinition.getColumnComparator(sortedIndex));
                getBlockManager().release(blk, true);

            }
        }



        }





    }

