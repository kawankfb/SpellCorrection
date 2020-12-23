public class WhiteList {
    public static boolean isWhite(char character){
        return (character >= 1568 && character <= 1617)//arabic and persian letters
                || (character >= 1646 && character <= 1749)//arabic and persian letters
                || (character >= 1774 && character <= 1792);//arabic and persian letters
    }
}
