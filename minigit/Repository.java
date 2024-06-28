package miniGit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;


/** Represents a MiniGit repository.
 *  @author Kevin Zhu
 */
public class Repository {
    public static final File MINIGIT_PATH = new File("D:\\.minigit");
    private Hashtable<String,Commit> branch;
    private Commit head;
    private Hashtable<String, String> stage= new Hashtable<>();
    private Hashtable<String,String> removalStage=new Hashtable<>();
    private String current;

    public Repository(){
    }
    public void init() {
        if (MINIGIT_PATH.exists()) {
            exitWithError("A MiniGit version-control system already exists in the current directory.");
        }
        Commit dummy = new Commit("initial commit",null,null,null);
        this.branch=new Hashtable<>();
        this.head=dummy;
        this.stage=null;
        this.current="main";


        MINIGIT_PATH.mkdir();
        String address=MINIGIT_PATH.getAbsolutePath();
        File headFile = Utils.join(address, "HEAD");
        Utils.writeContents(headFile,Utils.join(address,"main").getAbsolutePath());
        File objectsFile= Utils.join(address,"objects");
        objectsFile.mkdir();

        this.branch.put("main",dummy);
    }
//have not checked if the file is equal to or not for now
    public void add(String[] args) {
        try{
            String address=MINIGIT_PATH.getAbsolutePath();
            String stage_address=address.substring(0,address.length()-"\\.minigit".length());

            File f = Utils.join(stage_address, args[1]);
            String contents=Utils.readContentsAsString(f);
            String hash=Utils.sha1(contents);
            this.stage.put(contents,hash);


            File indexFile= new File(address+"/index");
            Utils.writeContents(indexFile,hash);
            ///if(f.equals(head)){this.stage=null;} need to check how to access everything in commits
        }
        catch(IllegalArgumentException e){
            exitWithError("File does not exist");
        }
    }

    public void commit(String[] args) {
        if(args.length==0){exitWithError("Please enter a commit message");}
        if(stage==null){exitWithError("No changes added to the commit");}
        Commit newCommit= new Commit(args[1],head,this.stage,this.removalStage);
        this.head=newCommit;
        this.stage=new Hashtable<String,String>();
        this.branch.put(this.current,newCommit);

    }

    public void rm(String[] args) {
        String address=MINIGIT_PATH.getAbsolutePath();
        String stage_address=address.substring(0,address.length()-"\\.minigit".length());

        File f = Utils.join(stage_address, args[1]);
        String contents=Utils.readContentsAsString(f);
        String hash=Utils.sha1(contents);
        if(this.stage.containsKey(contents)){
        this.stage.remove(contents);}
        else if(head.getTree().contains(contents)){
            this.removalStage.put(contents,hash);
            Utils.restrictedDelete(f);
        }
    }

    public void log() {
        Commit recent=head;
        while(recent.getPrev()!=null){
            System.out.println(recent.getInfo());
            recent=recent.getPrev();
        }
    }

    public void globalLog() {
        String address=MINIGIT_PATH.getAbsolutePath();
        address=address.substring(0,address.length()-"\\.minigit".length());
        System.out.println(Utils.plainFilenamesIn(address));
    }

    public void find(String[] args) {
        String address=MINIGIT_PATH.getAbsolutePath();
        address=address.substring(0,address.length()-"\\.minigit".length());
        boolean found= Utils.plainFilenamesIn(address).contains(args[0]);
        if(found){System.out.println(Utils.sha1(new Commit(null,null,null,null)));}
        else{System.out.println("Found no commit with that message.");}
    }

    public void checkout(String[] args) {
        this.head=branch.get(args[0]);

    }

    public void branch(String[] args) {
        if(branch.containsKey(args[0])){
        exitWithError("A branch with that name already exists");}
        branch.put(args[0],head);
    }

    public void rmBranch(String[] args) {
        if(!branch.containsKey(args[0])){
            exitWithError("A branch with that name does not exist.");}
        if(head.equals(args[0])){exitWithError("Cannot remove the current branch");}
        branch.remove(args[0]);
    }

    public void reset(String[] args) {
        // TODO
    }

    public void status() {
        // TODO
    }

    public void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            exitWithError("Incorrect operands.");
        }
    }

    public void validateLessThan(String cmd, String[] args, int n) {
        if (args.length > n) {
            exitWithError("Incorrect operands.");
        }
    }

    public void validateInit() {
        if (!MINIGIT_PATH.exists()) {
            exitWithError("Not in an initialized MiniGit directory.");
        }
    }

    public void exitWithError(String message) {
        System.out.println(message);
        System.exit(0);
    }
}
