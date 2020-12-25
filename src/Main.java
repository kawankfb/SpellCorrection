import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        BigramIndex bigramIndex = new BigramIndex();
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

        }


    }
}

