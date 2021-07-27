import java.io.IOException;
import java.io.InputStream;


/*

Grammar:

#1 expr  -> term expr2
#2 expr2 ->   +  term expr2
#3 	       |  -  term expr2
#4	       |  epsilon
#5 term -> factor term2
#6 term2 -> ** factor term2
#7         | epsilon
#8 factor -> number
#9         | ( expr )
#10 number -> 1...9 number_tail
#11         | 0
#12 number_tail -> 0...9
#13              | epsilon

*/

public class Calculator{
  
  private InputStream in; // used to reading data from a stream
  private int lookahead; // store the token that will be parsed
  

  public Calculator(InputStream in) throws IOException { // constructor
    this.in = in; // initialize the reading stream
    lookahead = in.read(); // read the first token
  }
  
  
  private void consume(int symbol) throws ParseError, IOException {
    if (lookahead != symbol)
      throw new ParseError();
    lookahead = in.read(); // read the next token
  }


  private static int pow(int base, int exponent) {
    if (exponent < 0)
      return 0;
    if (exponent == 0)
      return 1;
    if (exponent == 1)
      return base;
  
    if (exponent % 2 == 0) // even exp -> b ^ exp = (b^2)^(exp/2)
      return pow(base * base, exponent / 2);
    else                  // odd exp -> b ^ exp = b * (b^2)^(exp/2)
      return base * pow(base * base, exponent / 2);
  }
  
  
  private int evalDigit (int digit) {
    return digit - '0';
  }
  
  
  private int consumeNumber() throws ParseError, IOException {
    int next;
    String number=""; // to form whole number (number is a sequence of digits)
    
      if (lookahead == '0' ) {
        // if the lookahead is the zero digit then the final number is the zero
        next = evalDigit(lookahead); // consume the digit
        number = number + next;

        consume(lookahead);

        if(lookahead >= '0' && lookahead <= '9'){ // if zero followed from other digits (0,1,2,...,9) then throw parse error
            throw new ParseError();
         }        
      } 
        
      while (lookahead >= '0' && lookahead <= '9') {
        // as while the lookahead is digit (0,1,2,...,9) keep repeating to form whole number

        next = evalDigit(lookahead); // consume the digit
        number = number + next;

        consume(lookahead);
      }

        return Integer.parseInt(number); // return the number

    }


    public int eval() throws ParseError, IOException {
      int finalResult = expr();
      if (lookahead != '\n' && lookahead != -1) { // if lookahead isn't the newline character or EOF, throw parse error
          throw new ParseError();
      }
      return finalResult;
    }


    private int expr() throws ParseError, IOException {
      // expr  -> term expr2
      int result = term();
      result = expr2(result);

      return result;
      }


    private int term() throws ParseError, IOException {
      // term -> factor term2
      int number = factor();
      return term2(number);
      }


    private int factor() throws ParseError, IOException{
      // factor ->  number
      //          | ( expr )

      // factor should start with number (sequence of digits) or with left parenthesis

      if ( lookahead >= '0' && lookahead <= '9' ) { // if lookahead is digit then consume whole number that follows
        int number = consumeNumber();
        return number;
      }
      else if (lookahead == '(') { // if lookahead is left parenthesis consume it and then call recursively expr()
        consume(lookahead);

        int result = expr();

        // expression estimated and now the parentheses should "close", right parenthesis needed (whatever else it follows is parse error)
        if (lookahead == ')') { // if lookahead is right parenthesis then consume it
          consume(lookahead);
        }
        else{ // throw parse error
          throw new ParseError();
        }

        return result; // finally return the result
      }
      else{ // throw parse error
        throw new ParseError();
      }
    }


    private int expr2(int prevNumber) throws ParseError, IOException {
        // expr2 ->   +  term expr2
        //          | -  term expr2
        //          | epsilon

        // expr2 either is empty either it starts with "+" operator or "-" operator 
        if (lookahead == '+') { // if lookahead is "+" operator, consume it 
          consume(lookahead);

          int result = prevNumber+term(); // + term
          return expr2(result); // expr2
        }
        else if (lookahead == '-') { // if lookahead is "-" operator, consume it
          consume(lookahead);

          int result = prevNumber-term(); // - term
          return expr2(result); // expr2

        }
        else{ // lookahead isn't "+" operator or "-" operator, expr2 is empty, just return
          // epsilon
          return prevNumber;
        }
      }


    private int term2(int base) throws ParseError, IOException {
        // term2 ->  ** factor term2
        //         | epsilon

        // term2 either is empty either it starts with "**" operator
        // consume the "**" operator
        if (lookahead == '*') {
          consume(lookahead);
          if (lookahead == '*') {
            consume(lookahead);

            int power=factor(); // estimate the power

            // "**" operator has right precedence
            if (lookahead == '*') { // if the power operator ("**") follows, call the same function recursively
              power = term2(power);
            }

            int result = pow(base,power); // estimate the final result and return it
            return result;

          }
          else{ // "*" should be followed from "*", throw parse error
            throw new ParseError();
          }
        }
        else{ // term2 is empty, just return
          // epsilon
          return base;
        }
      }


}
