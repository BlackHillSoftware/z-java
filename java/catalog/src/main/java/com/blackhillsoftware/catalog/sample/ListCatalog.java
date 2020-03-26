package com.blackhillsoftware.catalog.sample;

import java.io.IOException;

import com.ibm.jzos.CatalogSearch;

public class ListCatalog 
{
	public static void main(String[] args) throws IOException 
	{
		if (args.length != 1) 
		{
			System.out.println("Usage: CatalogList <pattern>");
			return;
		}
		
		// Write headings
		String formatString = "%-44s %-4s %-6s%n";			
		System.out.format(formatString, 
				"Entry Name", 
				"Type", 
				"Volume");
		
		// Set up CSI
		CatalogSearch catSearch = new CatalogSearch(args[0]);
		catSearch.addFieldName("ENTYPE");
		catSearch.addFieldName("ENTNAME");
		catSearch.addFieldName("NAME");
		catSearch.addFieldName("VOLSER");
		catSearch.search();
		
		// Iterate through results
		while (catSearch.hasNext()) 
		{
			CatalogSearch.Entry entry = (CatalogSearch.Entry) catSearch.next();
			if (entry.isDatasetEntry()) 
			{
				String entryName = entry.getField("ENTNAME").getFString();
				String entryType = entry.getField("ENTYPE").getFString();
				String[] relatedNames = entry.getField("NAME").getFStringArray(44);
				String[] volumes = entry.getField("VOLSER").getFStringArray(6);
						
				// Entry name, type and first volume entry if present
				System.out.format(formatString, 
						entryName, 
						entryType, 
						(volumes != null && volumes.length > 0) ? volumes[0] : "");
				
				// Additional volumes, if present
				if (volumes != null && volumes.length > 0)
				{
					for (int i = 1; i < volumes.length; i++)	
					{
						System.out.format(formatString, 
								"", 
								"", 
								volumes[i]);					
					}
				}
				
				// List any related names
				if (relatedNames != null && relatedNames.length > 0)
				{
					System.out.format("     Related Names%n");
					for (int i = 0; i < relatedNames.length; i++)	
					{
						System.out.format("     %-44s%n",	relatedNames[i]);					
					}				
				}
			}
		}
	}
	

}