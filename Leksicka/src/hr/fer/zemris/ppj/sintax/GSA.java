package hr.fer.zemris.ppj.sintax;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 
 * @author ajuric
 *
 */
public class GSA {
	
	public static void main(String[] args) throws FileNotFoundException {
		//InputStream input = new FileInputStream(new File("NEŠTO!!"));
        InputStream input = System.in;
        GSA generator = new GSA(input);
        generator.generateSA();
	}

	private InputStream input;

	public GSA(InputStream input) {
		this.input = input;
	}
	
	private void generateSA() {
		// TODO Auto-generated method stub
		
	}

}
