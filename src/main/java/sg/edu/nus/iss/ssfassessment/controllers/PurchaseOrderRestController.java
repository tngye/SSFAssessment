package sg.edu.nus.iss.ssfassessment.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.ssfassessment.model.Quotation;
import sg.edu.nus.iss.ssfassessment.services.QuotationService;

@RestController
@RequestMapping("/api")
public class PurchaseOrderRestController {

    @Autowired 
    public QuotationService qSvc;

    @PostMapping("/po")
    public ResponseEntity<String> getOrder(@RequestBody String payload) throws IOException{
        JsonObject body;
        try(InputStream is = new ByteArrayInputStream(payload.getBytes())){
            JsonReader reader = Json.createReader(is);
            body = reader.readObject();
        }catch(Exception ex){
            body = Json.createObjectBuilder().add("error", ex.getMessage()).build();
            return ResponseEntity.internalServerError().body(body.toString());
        }
        
        System.out.println(">>>>>> body:" + body);

        String name = body.getString("name");
        JsonArray itemsArr = body.getJsonArray("lineItems");
        System.out.println(">>>>>> itemsArr:" + itemsArr);

        List<String> itemsName = new ArrayList<String>();

        for (int i =0; i<itemsArr.size(); i++){
            JsonObject perItem = itemsArr.getJsonObject(i);
            itemsName.add(perItem.getString("item"));
        }
        
        Optional<Quotation> quotationOpt = qSvc.getQuotations(itemsName);
        Quotation quotation = quotationOpt.get();

        Float total = 0f;
        for (int i=0; i<itemsArr.size(); i++){
            JsonObject perItem = itemsArr.getJsonObject(i);
            int quantity = perItem.getInt("quantity");
            Float unit = quotation.getQuotation(perItem.getString("item"));
            total = total + (unit * quantity);
        }
       
        System.out.println(">>>>>> total:" + total);

        JsonObject results = Json.createObjectBuilder()
        .add("invoiceId", quotation.getQuoteId())
        .add("name", name)
        .add("total", total)
        .build();

        System.out.println(">>>>>> results:" + results);
        try{
            return ResponseEntity.ok(results.toString());
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.ok(null);
    }
    
}
