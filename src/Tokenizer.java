import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static List<String> tokenize(String string){
        StringBuilder temp=new StringBuilder();
        List<String> distinctTokens =new ArrayList<>();
        for (int i = 0; i <string.length() ; i++) {
            char tempChar=string.charAt(i);
            if (WhiteList.isWhite(tempChar)){
                temp.append(CharacterNormalizer.normalize(tempChar));

            }
            else if (temp.length()>0){
                distinctTokens.add(temp.toString());
                temp=new StringBuilder();
            }
        }
        if (temp.length()>0)
            distinctTokens.add(temp.toString());
        return distinctTokens;
    }
}
