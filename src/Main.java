import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {

        BigramIndex bigramIndex = new BigramIndex();
        /*
        bigramIndex.add("cat");
        bigramIndex.add("dog");
        bigramIndex.add("kitten");
        bigramIndex.add("cow");
        bigramIndex.add("wolf");
        bigramIndex.add("احمد");
        bigramIndex.add("اخر");
        bigramIndex.add("است");
        bigramIndex.add("ماست");
        bigramIndex.add("tiber");
        bigramIndex.add("ticer");
        bigramIndex.add("tiler");
        bigramIndex.add("timer");
        bigramIndex.add("timer");
        bigramIndex.add("tiger");
        bigramIndex.add("tiger");
        bigramIndex.add("mountain_lion");
        bigramIndex.add("car");
        bigramIndex.add("cor");
        bigramIndex.add("lord");
        bigramIndex.add("aboard");
        bigramIndex.add("abroad");
        bigramIndex.add("abandon");
        */
        try {
            FileInputStream fis=new FileInputStream("fawiki-1101-pages-articles-multistream.xml");
            Scanner scanner=new Scanner(fis);
            int line_count=0;

            System.out.println("لطفا منتظر بمانید...");
            while (scanner.hasNextLine() && line_count<2000000)
            {
                List<String> tokens=Tokenizer.tokenize(scanner.nextLine());
                for (String token : tokens) {
                    if (token.length()>1)
                    bigramIndex.add(token);
                }

                line_count++;
                if (line_count%100000==0)
                    System.out.println(line_count);
            }
            System.out.println(line_count);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        while (true) {

            System.out.println("کوئری مورد نظر خود را وارد نمایید:");
            String query = scanner.nextLine();

            ;
            System.out.println(bigramIndex.getTopQuerySuggestions(query).get(0));


            /*List<String> tokens=bigramIndex.getTopSuggestions(query);

            for (String token : tokens) {
                System.out.print(token);
                for (int i = 0; i <15-token.length() ; i++) {
                    System.out.print(" ");
                }
                System.out.print(" : edit distance is : ");
                System.out.println(EditDistance.calculateDistance(query,token));
            }
            System.out.println();
            System.out.println("best suggestion : "+bigramIndex.getBestSuggestion(query));

        */
        }


    }
}

