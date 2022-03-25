package sg.edu.nus.iss.ssfassessment.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.ssfassessment.model.Quotation;

@Service
public class QuotationService {

    public Optional<Quotation> getQuotations(List<String> Items) throws IOException {
        JsonArrayBuilder itemsArrBuilder = Json.createArrayBuilder();
        Items.stream().forEach(v -> itemsArrBuilder.add(v));

        JsonArray itemsArr = itemsArrBuilder.build();

        RequestEntity<String> req = RequestEntity
                .post("https://quotation.chuklee.com/quotation")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept", "application/json")
                .body(itemsArr.toString(), String.class);

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);

        JsonObject data = null;
        try (InputStream is = new ByteArrayInputStream(resp.getBody().getBytes())) {
            JsonReader reader = Json.createReader(is);
            data = reader.readObject();
            }
            

        Quotation quotation = new Quotation();
        quotation.setQuoteId(data.getString("quoteId"));

        JsonArray quotationArr = data.getJsonArray("quotations");
        System.out.println(">>>> quotationArr: " + quotationArr);
        
        Map<String, Float> quotationMap = new HashMap<String, Float>();
        for(int i=0; i<quotationArr.size(); i++){
            JsonObject quotationObj = quotationArr.getJsonObject(i);

            JsonValue unit = quotationObj.get("unitPrice");
            Float unitPrice = Float.parseFloat(unit.toString());

            quotationMap.put(quotationObj.getString("item"), unitPrice);
        }

        quotation.setQuotations(quotationMap);
      
        try { 
            return Optional.of(quotation);
        // pokemon exception 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

}
