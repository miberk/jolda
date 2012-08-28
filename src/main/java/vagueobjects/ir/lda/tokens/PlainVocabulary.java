package vagueobjects.ir.lda.tokens;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class PlainVocabulary implements Vocabulary{
    final List<String> strings =new ArrayList<String>();

    public PlainVocabulary(Collection<String> strings) {
        this.strings.addAll(strings);
    }

    public PlainVocabulary(String path ) throws IOException {
        Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNextLine()){
            strings.add(scanner.nextLine().trim());
        }
    }

    @Override
    public boolean contains(String token) {
        return strings.contains(token);
    }

    @Override
    public int size() {
        return strings.size();
    }

    @Override
    public int getId(String token) {
        for(int i=0; i< strings.size();++i){
            if(strings.get(i).equals(token)){
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String getToken(int id) {
        return strings.get(id);
    }
}
