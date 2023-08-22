package jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

public class TestJasypt {
	
	public static void main(String args[])
	{
		StandardPBEStringEncryptor standardPBEStringEncryptor = new StandardPBEStringEncryptor();
		standardPBEStringEncryptor.setPassword("demoPassword");
		standardPBEStringEncryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
		standardPBEStringEncryptor.setIvGenerator(new RandomIvGenerator());
	    String result = standardPBEStringEncryptor.encrypt("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");
        System.out.println(result);
        System.out.println(standardPBEStringEncryptor.decrypt(result));
        //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
	}

}
