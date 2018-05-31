import java.util.Scanner;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int a = in.nextInt();
        int b;
        if (a == 0) {
            b = in.nextInt();
        } else {
            b = in.nextInt();
        }
        System.out.println(a + b);
    }

  public static void main2(String[] args) {
      Object a = new Object();
      synchronized (a) {
          System.out.println("test");
      }
  }

  private static Object b = new Object();

  public static void main3(String[] args) {
      synchronized (b) {
          System.out.println("test");
      }
  }
}
