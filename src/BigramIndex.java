import java.util.*;

public class BigramIndex{
    Trie trie;
    Map<String,Integer> distinctTokens;
    Map<String,List> cachedCorrections;
    public BigramIndex(){
        trie=new Trie();
        distinctTokens=new HashMap<>();
        cachedCorrections=new HashMap<>();
    }

    public void add(String token){
        token='$'+token+'$';
        distinctTokens.putIfAbsent(token,0);
        int count= distinctTokens.get(token)+1;
        distinctTokens.put(token,count);
        if (token.length()<2)
            return;
        String[] bigrams=getBigrams(token);
        for (int i = 0; i <bigrams.length; i++) {
            trie.add(bigrams[i],token);
        }

    }
    private String[] getBigrams(String token){
        if (token.length()<2)
            return new String[0];
        String[] bigrams=new String[token.length()-1];
        for (int i = 0; i <token.length()-1 ; i++) {
            bigrams[i]=token.substring(i,i+2);
        }
        return bigrams;
    }


    public Set<String> getSuggestions(String token){
        token='$'+token+'$';
        Set<String> suggestions=new HashSet<>();
        if (distinctTokens.containsKey(token)) {
            suggestions.add(token);
            return suggestions;
        }
            String[] bigrams=getBigrams(token);
        for (String bigram : bigrams) {
            suggestions.addAll(trie.getTokens(bigram));
        }
        return suggestions;
    }
    public int getTokenUsageCount(String token){
        return distinctTokens.get(token);
    }

    public List<String> getTopSuggestions(String token){
        if (cachedCorrections.containsKey(token)){
            return cachedCorrections.get(token);
        }
        int n=10;//top n suggestions
        Set<String> suggestions=getSuggestions(token);
        List topSuggestions=new ArrayList();


        if (suggestions.size()<2) {
            if (suggestions.size()<1)
            {
                cachedCorrections.putIfAbsent(token,topSuggestions);
                return topSuggestions;
            }
            for (String suggestion : suggestions) {
                if (suggestion.startsWith("$"))
                topSuggestions.add(suggestion.substring(1,suggestion.length()-1));
                else topSuggestions.add(suggestion);
            }

            cachedCorrections.putIfAbsent(token,topSuggestions);
            return topSuggestions;
        }

        token='$'+token+'$';


        int count=0;
        int[] editDistance=new int[n];
        for (int i = 0; i <editDistance.length ; i++) {
            editDistance[i]=1000000;
        }
        int[] usageCount=new int[n];
        String[] tokens=new String[n];
        for (String suggestion : suggestions) {
            if (count<n){
                count++;}
            int i;
            int ed=EditDistance.calculateDistance(token,suggestion);//edit distance
            int tuc=getTokenUsageCount(suggestion);//token usage count
            for (i = 0; i <count ; i++) {
                if (ed<=editDistance[i])
                    break;
            }

            for (; i <count ; i++) {
                if (tuc>=usageCount[i] || ed<editDistance[i])
                    break;
            }
            if (i==count && i<n)
            {tokens[i]=suggestion;
            editDistance[i]=ed;
            usageCount[i]=tuc;
            }
            else if (i<count){
                for (int j = count-1; j >i ; j--) {
                    editDistance[j]=editDistance[j-1];
                    usageCount[j]=usageCount[j-1];
                    tokens[j]=tokens[j-1];
                }
                editDistance[i]=ed;
                usageCount[i]=tuc;
                tokens[i]=suggestion;
            }

        }


        for (int i = 0; i <count ; i++) {
           topSuggestions.add(tokens[i].substring(1,tokens[i].length()-1));
        }
        cachedCorrections.putIfAbsent(token,topSuggestions);
        return topSuggestions;
    }

    public String getBestSuggestion(String token){
        if (token.length()<2)
            return token;
        List<String> top=getTopSuggestions(token);
        if (top.isEmpty())
            return token;
            else {
            String temp=top.get(0);
            if (temp.startsWith("$"))
                return temp.substring(1,temp.length()-1);
            else return temp;
        }

    }
    public List<String> getTopQuerySuggestions(String query,int maxSuggestionCount){
        String[] tokens=query.split(" ");
        HashMap<String,List<String>> table=new HashMap<String,List<String>>();

        for (String token : tokens) {
            if (token.equals("AND") ||token.equals("NOT") ||token.equals("OR"))
            {
                List<String> temp = new ArrayList<>();
                temp.add(token);
                table.putIfAbsent(token,temp);
            }
            else {
                String corrected_token=getBestSuggestion(token);
                if (corrected_token.equals(token)) {
                    List<String> temp = new ArrayList<>();
                    temp.add(token);
                    table.putIfAbsent(token,temp);
                }
                else {
                table.putIfAbsent(token,getTopSuggestions(token));
                }
            }
        }

        ArrayList<String> topQuerySuggestions=new ArrayList<>();
        String[][] permutations=new String[tokens.length][];
        for (int i = 0; i <permutations.length ; i++) {
            List<String> tokenSuggestions=table.get(tokens[i]);
            permutations[i]=new String[tokenSuggestions.size()];
            int j=0;
            for (String tokenSuggestion : tokenSuggestions) {
                permutations[i][j]=tokenSuggestion;
                j++;
            }
        }

        int length= permutations.length;
        int[] ints=new int[length];
        int[] limits=new int[length];
        for (int i = 0; i <limits.length ; i++) {
            limits[i]=permutations[i].length-1;
        }
        while (ints[0]<=limits[0] && topQuerySuggestions.size()<maxSuggestionCount)
        {
            StringBuilder tempSB=new StringBuilder();
            int turn=0;
            for (int i : ints) {
                tempSB.append(permutations[turn][i]);
                tempSB.append(' ');
                turn++;
            }
            topQuerySuggestions.add(tempSB.toString());
            ints[length-1]++;
            for (int i = length-1; i >=0 ; i--) {
                if (ints[i]>limits[i]) {
                    if (i==0)
                        break;
                    ints[i - 1]++;
                    ints[i]=0;
                }
            }
        }

        return  topQuerySuggestions;
    }

}
