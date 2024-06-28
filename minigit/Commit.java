package miniGit;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Commit implements Serializable {
    private Commit prev;
    private SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
    private String message;
    private Date timestamp;


    private Tree tree;


    public Commit(String msg, Commit prev, Hashtable<String,String> stage, Hashtable<String,String> removalStage){
        this.message=msg;
        this.prev=prev;
        this.timestamp= new Date();
        if(this.prev!=null){
        this.tree=new Tree(this.prev.tree);}
        else{this.tree=new Tree();}
        if(stage!=null){
            for(String value : stage.values()) {
                this.tree.putBlob(new Blob(Utils.sha1(value), value));
            }
        }
        if (removalStage!=null){
            for(String value : removalStage.values()) {
                this.tree.removeBlob(new Blob(Utils.sha1(value), value));
            }

        }

    }
    public String getDate(){
        return format.format(this.timestamp);
    }

    public Commit getPrev(){
        return this.prev;
    }

    public String getMessage(){
        return this.message;
    }

    public Tree getTree(){
        return this.tree;
    }
    public String getInfo(){
        return this.getDate()+";"+this.getMessage();
    }
    public class Blob{
        private String hash;
        private String content;
        public Blob(String hash,String content){
            this.hash=hash;
            this.content=content;
        }
        public String getContent(){return this.content;}
        public String getHash(){return this.hash;}
    }
    public class Tree{

        private Hashtable<String, Blob> blobs;
        private Hashtable<String, Tree> subtrees;

        public Tree() {
            this.blobs = new Hashtable<>();
            this.subtrees = new Hashtable<>();
        }

        public Tree(Tree other) {
            this.blobs = new Hashtable<>(other.blobs);
            this.subtrees = new Hashtable<>(other.subtrees);
        }
         public void putBlob(Blob b){
             blobs.put(b.getHash(),b);

         }

         public void removeBlob(Blob b){
            blobs.remove(b.getHash());
         }
         public void putTree(Tree t){
             subtrees.put(Utils.sha1(t),t);
         }

         public boolean contains(String str){
            Blob b= new Blob(str,Utils.sha1(str));
            return this.blobs.contains(b)||this.subtrees.contains(b);
         }
    }
}