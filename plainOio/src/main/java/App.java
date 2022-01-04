public class App {

    public static void main(String[] args) throws Exception {
//        new PlainNioServer().serve(33569);
//        new PlainOioServer().serve(33569);
        new PlainNettyOioServer().serve(33569);
//        new PlainNettyNioServer().serve(33569);

    }
}
