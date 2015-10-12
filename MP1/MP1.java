import java.io.*;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];
        // read file
        FileReader readFile=new FileReader(inputFileName);
        BufferedReader bufferedReader=new BufferedReader(readFile);
        String line=null; String[] allLines=new String[50000]; int i=0;
        while((line=bufferedReader.readLine())!=null){
            allLines[i++]=line;
            //System.out.println(line);
        }
        HashSet<String> hs=new HashSet<String>();
        HashMap<String,Integer> hm=new HashMap<String,Integer>();
        for(i=0;i<stopWordsArray.length;i++){
            if(!hs.contains(stopWordsArray[i]))hs.add(stopWordsArray[i]);
        }
        Integer[] indexs=getIndexes();
        for(i=0;i<indexs.length;i++){
            int index=indexs[i].intValue();
            StringTokenizer st = new StringTokenizer(allLines[index],delimiters);
            while(st.hasMoreTokens()){
                String tempStr=st.nextToken().toLowerCase();
                if(!hs.contains(tempStr)){
                    if(!hm.containsKey(tempStr)){
                        hm.put(tempStr,1);
                    }else{
                        hm.put(tempStr,hm.get(tempStr)+1);
                    }
                }
            }
        }
        // sort map
        Set<Entry<String,Integer>> set=hm.entrySet();
        List<Entry<String,Integer>> list=new ArrayList<Entry<String,Integer>>(set);
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>()
        {
            public int compare(Map.Entry<String,Integer> e1,Map.Entry<String,Integer> e2){
                if(e1.getValue()!=e2.getValue()){
                    return -1*(e1.getValue().compareTo(e2.getValue()));
                }else{
                    return (e1.getKey().compareTo(e2.getKey()));
                }
            }
        });
        i=0;
        for(Map.Entry<String,Integer> entry:list){
            System.out.println(entry.getKey()+" = "+entry.getValue());
            ret[i]=entry.getKey();
            i++;
            if(i==20)break;
        }
        bufferedReader.close();
        return ret;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
            // write output file
            PrintWriter writer=new PrintWriter("output.txt","UTF-8");
            for (String item: topItems){
                writer.println(item);
            }
            writer.close();
        }
    }
}