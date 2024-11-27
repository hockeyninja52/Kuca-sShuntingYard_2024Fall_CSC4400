package driver;

import driver.Node;
import driver.TokenList;
import java.util.ArrayList;

public class ShuntngYard {
	public static String Operators = "+-*/^()";
	public static String Numbers = "1234567890";
	
	public static boolean IsNumber(String input) {
		for(char c: input.toCharArray()) {
			if (Numbers.indexOf(c) == -1) {
				return false;
			}
		}
		return true;
	}
	
	public static int getPrecedence(String input) {
		char op = input.charAt(0);
		switch (op) {
			case '(':
				return 1;
			case '+':
			case '-':
				return 2;
			case '*':
			case '/':
				return 3;
			case '^':
				return 4;
			case ')':
				return 5;
			default:
				return 0;
		}			
	}

	//parse a math expression into a linked list
    //input: the math expression as a string
    //parsed result will stored in Token list
	public static TokenList ParseFromExp(String exp) {
		TokenList lst = new TokenList(); //lst is a doublylinkedlist, support both queue and stack functions
		// if c is an operator, we create a node and append to the list
		//if c is a number, we need to check if the previous char is also a number
		//8, 1 = 18
		String curValue = "";
		for(char c: exp.toCharArray()) {
			if(Operators.indexOf(c) > -1) { //this is a valid operator
				if(!curValue.isEmpty()) {
					Node<String> num = new Node(curValue);
					lst.Append(num);
					curValue = "";
				}
				Node<String> node = new Node(String.format("%C", c)); //c + ""
				lst.Append(node);
			}else if(Numbers.indexOf(c) > -1) { //this is a number
				curValue += c;
			}
		}
		if(curValue.length() != 0) {
			Node<String> num = new Node(curValue);
			lst.Append(num);
		}
		return lst;
	}
	
	
	//take the tokens from Tokens queue, and stored the reversed polish expression in ReversePolish queue
	public static TokenList BuildFromTokens(TokenList tokenList) {
		TokenList outputQueue = new TokenList();
		TokenList opStack = new TokenList();
		Node<String> token = tokenList.Dequeue();
		if(token != null) {
			//we will do the algorithm on this token
			if(IsNumber(token.Payload)) {
				outputQueue.Enqueue(token);
			}else {
				int rank = getPrecedence(token.Payload);
				if(rank == 1) {
					opStack.Push(token);

				}else if(rank == 5) {
					Node<String> op = opStack.Peek();
					while(op != null && op.Payload != "(") {
						outputQueue.Enqueue(opStack.Pop());
					}
					opStack.Pop();
				}else {
					Node<String> op = opStack.Peek();
					int newRank = getPrecedence(op.Payload);
					while(newRank > rank) {
						outputQueue.Enqueue(op);
					}
					opStack.Push(token);
				}
			}
			//after everything is over
			token = tokenList.Dequeue();
		}
		
		
		/*
	     * 1.  While there are tokens to be read:
	     * 2.        Read a token
	     * 3.        If it's a number add it to queue
	     * 4.        If it's an operator
	     * 5.               While there's an operator on the top of the stack with greater precedence:
	     * 6.                       Pop operators from the stack onto the output queue
	     * 7.               Push the current operator onto the stack
	     * 8.        If it's a left bracket push it onto the stack
	     * 9.        If it's a right bracket 
	     * 10.            While there's not a left bracket at the top of the stack:
	     * 11.                     Pop operators from the stack onto the output queue.
	     * 12.             Pop the left bracket from the stack and discard it
	     * 13. While there are operators on the stack, pop them to the queue
	     */
		return outputQueue;
	}
	
	//process use the reverse polish format of expression to process the math result
	//output: the math result of the expression
	public static TokenList Process(TokenList lst) {
		TokenList queue = new TokenList();
		TokenList Stack = new TokenList();
		TokenList pList = new TokenList();
		TokenList OList = new TokenList();
		
		
		Node<String> r = lst.Head;
		Node<String> r1 = r.NextNode;
		Node<String> s  = Stack.Head;
		Node<String> n;
		int length = 11;
		
		
		
		while(r1 != null) {
			queue.Enqueue(r);
			r = r1;
			r1 = r1.NextNode;
		}
		if(r1 == null) {
			queue.Enqueue(r);
		}
		
		for(int i = 0; i < length; i++) {
			s = queue.Pop();
			if(IsNumber(s.Payload) == false) {
				int rank = getPrecedence(s.Payload);
				
				if(rank == 1 || rank == 5) {
					pList.Append(s);
				}else if(rank == 2 || rank == 3 || rank == 4) {
					OList.Append(s);
				}else {
					//return -1;
				}
			}else if(IsNumber(s.Payload) == true){
				Stack.Append(s);
			}
		}
		n = Stack.Pop();
		String input = n.Payload;
		double a = Integer.parseInt(input);
		
		n = Stack.Pop();
		input = n.Payload;
		double b = Integer.parseInt(input);
		
		n = OList.Pop();
		input = n.Payload;
		String c = input;
		
		a = Math(a,b,c, Stack);
		
		n = Stack.Pop();
		input = n.Payload;
		b = Integer.parseInt(input);
		
		n = OList.Pop();
		input = n.Payload;
		c = input;
		
		a = Math(a,b,c, Stack);
		
		double d = a;
		n = OList.Pop();
		input = n.Payload;
		String temp = input;
		
		n = Stack.Pop();
		input = n.Payload;
		a = Integer.parseInt(input);
		
		n = Stack.Pop();
		input = n.Payload;
		b = Integer.parseInt(input);
		
		n = OList.Pop();
		input = n.Payload;
		c = input;
		
		a = Math(a,b,c, Stack);
		c = temp;
		b = d;
		
		a = Math(a,b,c, Stack);
		
		IntoList(a, Stack);
		
		return(Stack);
	}
	
	
	
	public static double Math(double a, double b, String c, TokenList Stack) {
		double result = 0	;
		if("-".equals(c)) {
			result = b - a;
		}else if("+".equals(c)) {
			result = b + a;
		}else if("/".equals(c)) {
			result = b / a;
			
		}else if("^".equals(c)) {
			result = Math.pow(b, a);
		}
		return result;
		}
	public static void IntoList(double result, TokenList Stack) {
		String str = Double.toString(result);
		Node<String> answer = new Node(str);
		Stack.Append(answer);
	}
}


