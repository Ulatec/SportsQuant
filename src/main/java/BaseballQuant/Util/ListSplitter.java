package BaseballQuant.Util;

import BaseballQuant.Model.BackTestIngestObject;

import java.util.ArrayList;
import java.util.List;

public class ListSplitter {

    public static ArrayList<List<BackTestIngestObject>> split(List<BackTestIngestObject> fulllist, int numOfOutputs) {
        // get size of the list
        int size = fulllist.size();
        ArrayList<List<BackTestIngestObject>> lists = new ArrayList<>();
        int lastDivider = 0;
        for(int i = 0;i < numOfOutputs; i++){
            int newDivider = (int) Math.round(size*((i+1)/(double)numOfOutputs));
            lists.add(new ArrayList<>(fulllist.subList(lastDivider, newDivider)));
            lastDivider = newDivider;
        }
        // return an List array to accommodate both lists
        return lists;
    }

    public static ArrayList<List<String>> splitString(List<String> fulllist, int numOfOutputs) {
        // get size of the list
        int size = fulllist.size();
        ArrayList<List<String>> lists = new ArrayList<>();
        int lastDivider = 0;
        for(int i = 0;i < numOfOutputs; i++){
            int newDivider = (int) Math.round(size*((i+1)/(double)numOfOutputs));
            lists.add(new ArrayList<>(fulllist.subList(lastDivider, newDivider)));
            lastDivider = newDivider + 1;
        }
        // return an List array to accommodate both lists
        return lists;
    }
}
