package org.synchrnoss.test.util.cucumber.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FolderIterator
{
    private List<String> classes = new ArrayList<>();
    private List<String> exactor = new ArrayList<>();

    public FolderIterator(String path){
        locatePassingExactorFiles(path);
    }

    /* change name to locatEpASSINGEXACTORFILES */
    public void locatePassingExactorFiles(String path) {
        File dir = new File(path);
        List<File> files = Arrays.asList(dir.listFiles());
        for (File child : files) {
            if (child.isDirectory()) {
                locatePassingExactorFiles(child.getPath());
            } else {
                file(child);
            }
        }
    }

    public  void file(File current){
        if(current.getName().contains(".act") && current.getPath().contains("passing")){
            exactor.add(current.getPath());
        }
    }

    public List<String> getFiles() {
        return exactor;
    }
}
