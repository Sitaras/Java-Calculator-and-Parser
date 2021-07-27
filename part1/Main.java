import java.io.IOException;

public class Main{
  public static void main(String[] args){
   try {
       Calculator evaluator = new Calculator(System.in);
       System.out.println("Result: " + evaluator.eval());
   }
   catch ( IOException | ParseError err ) {
     System.err.println(err.getMessage());
   }

  }
}