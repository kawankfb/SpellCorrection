import java.util.*;

public class Trie {
    private char value;
    Map<Character,Trie> children;
    Set<String> tokens;
    public Trie(char value){
        this.value=value;
        children=new HashMap();
        tokens=new HashSet<String>();
    }
    public Trie(){
        value=(char)0;
        children=new HashMap();
        tokens=new HashSet<String>();
    }
    public boolean isLeaf(){
        return children.size()==0;
    }

    public void add(String key,String token){
        if (key.length()==0)
        tokens.add(token);
        else{
            children.putIfAbsent(key.charAt(0),new Trie(key.charAt(0)));
            children.get(key.charAt(0)).add(key.substring(1),token);
        }
    }
    public Set<String> getTokens(String key){
        int depth=key.length();
        if (depth==0)
            return tokens;
        else{
            Set<String> tokens=null;
            if (children.get(key.charAt(0))!=null)
            tokens = children.get(key.charAt(0)).getTokens(key.substring(1));
            if (tokens==null)
                return new HashSet<String>();
        return tokens;
        }

    }

}
