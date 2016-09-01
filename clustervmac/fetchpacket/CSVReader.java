package clustervmac.fetchpacket;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
	
	public List<String[]> readCSV(String filePath){
		List<String[]> content = new ArrayList<String[]>();
	    String line = "";
	    String cvsSplitBy = ",";
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	        while ((line = br.readLine()) != null) {
	            content.add(line.split(cvsSplitBy));
	        }
	    } catch (IOException e) {
	    	System.out.println("Unable to read csv packet file");
	        e.printStackTrace();
	    }
	    return content;
	}

}
