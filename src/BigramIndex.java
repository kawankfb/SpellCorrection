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
        Set<String> suggestions=filter(getSuggestions(token),token);
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

    private Set<String> filter(Set<String> suggestions,String token){
        return suggestions;
        /*
        double sum=0;
        Set<String> filteredSuggestions=new HashSet<>();

        double[] scores=new double[suggestions.size()];
        String[] tokens=new String[suggestions.size()];
        String[] tokenBigrams=getBigrams(token);
        int count=0;
        for (String suggestion : suggestions) {
            int sharedBigramsCount=0;
            for (String bigram : getBigrams(suggestion)) {
                for (String tokenBigram : tokenBigrams) {
                    if (bigram.equals(tokenBigram))
                        sharedBigramsCount++;
                }
            }
            tokens[count]=suggestion;
            scores[count]=sharedBigramsCount/(double)(suggestion.length()+token.length());
            sum=sum+scores[count];
            count++;
        }
        sum=sum/count;
        System.out.println("sum is :"+sum);
        for (int i = 0; i <scores.length ; i++) {
            if (scores[i]>sum)
                filteredSuggestions.add(tokens[i]);
        }

        return filteredSuggestions;*/
    }
    public List<String> getTopQuerySuggestions(String query){
        List<String> topSuggestions=new ArrayList<String>();
        String[] tokens=query.split(" ");
        HashMap<String,List<String>> table=new HashMap<String,List<String>>();

        int number_of_corrections=0;
        for (String token : tokens) {
            if (token.equals("AND") ||token.equals("NOT") ||token.equals("OR"))
                continue;
            else {
                String corrected_token=getBestSuggestion(token);
                if (corrected_token.equals(token))
                    continue;

                else {
                table.putIfAbsent(token,getTopSuggestions(token));
                number_of_corrections++;
                }
            }
        }

        //adding the best  suggestion to the list
        StringBuilder temp=new StringBuilder();
        for (String token : tokens) {
            if (table.get(token)==null) {
                temp.append(token);
                temp.append(' ');
            }
            else {
                temp.append(table.get(token).get(0));
                temp.append(' ');
            }
            }
        String newQuery=temp.substring(0,temp.length()-1);
        topSuggestions.add(newQuery);
        //end of best suggestion

        //creating 9 extra suggestions to have 10 overall suggestions
        /*
        int[] correction_index=new int[number_of_corrections];
        for (int i = 0; i <9 ; i++) {
            boolean all_permutations_explored=false;
            int corrected_token_index=0;
            temp=new StringBuilder();
            for (String token : tokens) {
                if (table.get(token)==null) {
                    temp.append(token);
                    temp.append(' ');
                }
                else {

                    temp.append(table.get(token).get(correction_index[corrected_token_index]));
                    correction_index[corrected_token_index]++;
                    if (table.get(token).size()==correction_index[corrected_token_index])
                    {if (corrected_token_index==0)
                    all_permutations_explored=true;
                    else {
                        correction_index[corrected_token_index]=0;
                        correction_index[corrected_token_index-1]++;
                    }



                    }

                    corrected_token_index++;
                    temp.append(' ');
                }
            }

            newQuery=temp.substring(0,temp.length()-1);
            topSuggestions.add(newQuery);
            if (all_permutations_explored)
                break;

        }
        for (String topSuggestion : topSuggestions) {
            System.out.println(topSuggestion);
        }
        */

        return  topSuggestions;
    }

}
