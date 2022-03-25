package sg.edu.nus.iss.ssfassessment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import sg.edu.nus.iss.ssfassessment.model.Quotation;
import sg.edu.nus.iss.ssfassessment.services.QuotationService;

@SpringBootTest
class SsfassessmentApplicationTests {
	@Autowired 
	public QuotationService qSvc;

	@Test
	void contextLoads() {
	}


	@Test
	void testQuotationSvcGetQuotations() throws IOException{
		

		String message= "";
		try{
		List<String> testList = new ArrayList<String>();
		testList.add("durian");
		testList.add("plum");
		testList.add("pear");
		Optional<Quotation> opt = qSvc.getQuotations(testList);
		}catch(HttpClientErrorException e){
			message = e.getMessage();
		}
		String actualmessage ="400 Bad Request";
		
		Assertions.assertTrue(message.contains(actualmessage));
	}
	

}
