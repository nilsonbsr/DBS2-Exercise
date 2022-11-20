package exercise1;

import de.hpi.dbs2.ChosenImplementation;
import de.hpi.dbs2.dbms.*;
import de.hpi.dbs2.exercise1.SortOperation;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Objects;

@ChosenImplementation(true)
public class TPMMSJava extends SortOperation {
    public TPMMSJava(@NotNull BlockManager manager, int sortColumnIndex) {
        super(manager, sortColumnIndex);
    }

    @Override
    public int estimatedIOCost(@NotNull Relation relation) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void sort(@NotNull Relation relation, @NotNull BlockOutput output) {
        int i = 0;
        for (Block a : relation) {
            Block b = getBlockManager().load(a);
            System.out.println("Block: " + i +", Tupel 0: " + b.get(0));
            System.out.println("Block: " + i +", Tupel 1: " + b.get(1));
            System.out.println("Block: " + i +", Tupel: 0, Column 0: " + b.get(0).get(0));
            System.out.println("Block: " + i +", Tupel: 0, Column 1: " + b.get(0).get(1));
            System.out.println("Block: " + i +", Tupel: 0, Column 2: " + b.get(0).get(2));
            System.out.println();
            /*Tuple<Int, String, Float> niceTriple = Tuple.with=(363, "ce786853-89b1-3e64-b99a-c98ec095b421", -0.6351907754802413);
            if (b.get(0) = niceTriple){

            }*/
            int tollerInt = 363;
            if ((Integer)b.get(1).get(0) == tollerInt){
                System.out.println("FU DBS 2");
                System.out.println(b.get(1).getColumnCount());
            }

            i++;
            getBlockManager().release(b, false);
        }
        System.out.println("i count: " + i);
    }
}
