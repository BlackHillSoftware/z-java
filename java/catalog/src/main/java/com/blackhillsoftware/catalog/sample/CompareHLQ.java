package com.blackhillsoftware.catalog.sample;

import java.io.IOException;
import java.util.*;

import com.ibm.jzos.CatalogSearch;

public class CompareHLQ 
{
	public static void main(String[] args) throws IOException 
	{
		if (args.length != 2) 
		{
			System.out.println("Usage: CompareHLQ hlq1 hlq2");
			return;
		}
		
		String hlq1 = args[0];
		String hlq2 = args[1];
		
		// Get entries - the HLQ is stripped leaving the suffix to be compared  
		Set<String> hlq1Entries = getHlqEntries(hlq1);
		Set<String> hlq2Entries = getHlqEntries(hlq2);
				
		// Create a Set with suffixes that appear under both prefixes
		Set<String> intersection = new HashSet<>(hlq1Entries);
		intersection.retainAll(hlq2Entries);
		
		// Remove names that appear in both, result: each has names unique to that prefix 
		hlq1Entries.removeAll(intersection);
		hlq2Entries.removeAll(intersection);
		
		System.out.format("%nEntries in %s Only:%n", hlq1);
		hlq1Entries.stream()
			.sorted()
			.forEachOrdered(entryName -> 
				{
					System.out.format("%s%s%n", hlq1, entryName);
				});

		System.out.format("%nEntries in %s Only:%n", hlq2);
		hlq2Entries.stream()
			.sorted()
			.forEachOrdered(entryName -> 
				{
					System.out.format("%s%s%n", hlq2, entryName);
				});
	}

	public static Set<String> getHlqEntries(String hlq) 
	{
		Set<String> suffixes = new HashSet<>();
		
		CatalogSearch catSearch = new CatalogSearch(hlq + ".**");
		catSearch.addFieldName("ENTNAME");
		catSearch.search();
		
		while (catSearch.hasNext()) 
		{
			CatalogSearch.Entry entry = (CatalogSearch.Entry) catSearch.next();
			if (entry.isDatasetEntry()) 
			{
				String entryName = entry.getField("ENTNAME").getFString();
				suffixes.add(entryName.substring(hlq.length()));
			}
		}
		return suffixes;
	}
}