import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {

    static double noise = 0.5;
    static int size1 = 100;
    static int size2 = 100;
    static int steps = 100;
    static String fifth = "off";


    public static void main(String[] args) throws URISyntaxException, InterruptedException {

        String result = "";

        LabClient first = new LabClient( new URI( "wss://sprs.herokuapp.com/first/stepan_tarasenkooooo" ));
        first.connect();
        while (!first.isOpen());
        first.getConnection().send("Let's start");
        synchronized (first){
                first.wait();
                result = first.responseFromServer();
        }

        first.getConnection().send( size1 + " " + size2 + " " + noise + " " + steps + " " + fifth);
        synchronized (first){
            first.wait();
            result = first.responseFromServer();
        }
        //Parsing the ethalones
        String ethalone[] = result.split("[^ ][0-9]\\n");
        for (int i = 0; i < ethalone.length; i++){
            ethalone[i] = ethalone[i].substring(1, ethalone[i].length());
        }

        String eth[][] = new String[ethalone.length][];
        for (int i = 0; i < ethalone.length; i++){
            eth[i] = ethalone[i].split("[ \\n]");
        }

        //converting ethalones to int array
        int [][] ethInt = new int[eth.length][eth[0].length];
        for (int i = 0; i < eth.length; i++){
            for (int j = 0; j < eth[i].length; j++){
                if (!eth[i][j].equals(""))
                    ethInt[i][j] = Integer.valueOf(eth[i][j]);
            }
        }
        //steps
        for (int k = 1; k <= steps; k++) {
            first.getConnection().send("Ready");
            synchronized (first) {
                first.wait();
                result = first.responseFromServer();
            }
            //parsing num array
            String numRes[] = result.split("[ \\n]");


            int num[] = new int[numRes.length];
            for (int i = 0; i < num.length; i++) {
                num[i] = Integer.valueOf(numRes[i]);
            }
            //xoring ethalones with recieved num
            for (int i = 0; i < ethInt.length; i++) {
                for (int j = 0; j < num.length; j++) {
                    ethInt[i][j] = ethInt[i][j] ^ num[j];
                }
            }

            ArrayList<Double> sumRes = new ArrayList<Double>();
            //trying to recognize
            for (int i = 0; i < ethInt.length; i++) {
                double sum = 0;
                for (int j = 0; j < ethInt[i].length; j++) {
                    sum = sum + ethInt[i][j] * noise + (1 - ethInt[i][j]) * (1-noise);
                }
                sumRes.add(sum);
            }
            //getting max element of array list and its index is our num
            System.out.println(getMaxElementIndex(sumRes.toArray()));
            first.getConnection().send(k + " " + getMaxElementIndex(sumRes.toArray()));
            synchronized (first) {
                first.wait();
                result = first.responseFromServer();
            }
            System.out.println(result);
        }
        first.getConnection().send("Bye");
        synchronized (first) {
            first.wait();
            result = first.responseFromServer();
        }
        System.out.println(result);
    }

    private static String getMaxElementIndex(Object[] arr) {
        Double max = (Double) arr[0];
        int indx = 0;
        for (int i = 0; i < arr.length; i ++){
            if (max < (Double) arr[i]) {
                max = (Double) arr[i];
                indx = i;
            }
        }
        return indx + "";
    }
}
