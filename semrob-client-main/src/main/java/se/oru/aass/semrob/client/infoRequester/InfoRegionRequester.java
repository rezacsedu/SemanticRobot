package se.oru.aass.semrob.client.infoRequester;

public class InfoRegionRequester extends InfoRequester{
	
	public static final String RELATION_MESSAGE = "Within";
	
	private String generateBlockXMLResponse (String[] regionInfo, boolean withRelation) {
		String xmlResponse = "";
		if (regionInfo.length == 7) {
			String regionID = regionInfo[0];
			String regionType = regionInfo[1];
			String boundary = regionInfo[2];
			String count = regionInfo[3];
			String centroidX = regionInfo[4];
			String centroidY = regionInfo[5];
			double precision = Double.parseDouble(regionInfo[6]);
			
			
			xmlResponse += "<object id=\"" + regionID + "\" centerx=\"" + centroidX + "\" centery=\"" + centroidY + "\">\n";
			if (withRelation)
				xmlResponse += "<relation>" + RELATION_MESSAGE + "</relation>\n";
			xmlResponse += "<meta name=\"RegionType\" value=\"" + regionType + "\" />\n";
			xmlResponse += "<meta name=\"Precision\" value=\"" + precision + "\" />\n";
			if (!withRelation) {
				//xmlResponse += "<area count=\"" + count + "\">" + boundary + "</area>\n";
			}
			if (withRelation)
				xmlResponse += "</object>\n";
		}
		return xmlResponse;
	}
	
	
	@Override
	public void printInfo(String response) {
		super.printInfo(response);
		
		String xmlResponse = "";
		
		if (response != null && response.trim().length() > 0) {
			
			String[] regionsInfo = response.split(SPLITTER_SERVER_RESPONSE_INSTANCE);
			if (regionsInfo.length > 0) {
				xmlResponse += XML_RESPONSE_HEADING;
				
				int responseIndex = 0;
				for (String info :  regionsInfo) {
					if (info.length() > 0) {
						String[] regionInfo = info.split(SPLITTER_SERVER_RESPONSE_INSTANCE_INFO);
						
						if (responseIndex == 0) {
							xmlResponse += generateBlockXMLResponse(regionInfo, false);
							if (regionsInfo.length > 1)
								xmlResponse += "<related>\n";
						}
						else
							xmlResponse += generateBlockXMLResponse(regionInfo, true);
						responseIndex ++;
					}
				}
				
				if (regionsInfo.length > 1) 
					xmlResponse += "</related>\n";
				
				xmlResponse += "</object>\n";	
			}
		}
		else {
			xmlResponse += XML_RESPONSE_HEADING;
			xmlResponse += "<object >\n";
			xmlResponse += "</object>\n";	
		}
		System.out.println(xmlResponse);
	}
}
