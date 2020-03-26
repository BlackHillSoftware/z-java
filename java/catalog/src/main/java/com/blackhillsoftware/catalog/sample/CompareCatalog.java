package com.blackhillsoftware.catalog.sample;

import java.io.IOException;
import java.util.*;

import com.ibm.jzos.CatalogSearch;

public class CompareCatalog 
{
	public static void main(String[] args) throws IOException 
	{
		if (args.length != 2) 
		{
			System.out.println("Usage: Compare catalog1 catalog2");
			return;
		}
		
		Map<String, CatalogEntry> catalog1Entries = getCatalogEntries(args[0]);
		Map<String, CatalogEntry> catalog2Entries = getCatalogEntries(args[1]);
		
		// Create sets with names from each catalog
		Set<String> catalog1Names = new HashSet<>(catalog1Entries.keySet());	
		Set<String> catalog2Names = new HashSet<>(catalog2Entries.keySet());
		
		// Create a Set with names that appear in both catalogs
		Set<String> intersection = new HashSet<>(catalog1Names);
		intersection.retainAll(catalog2Names);
		
		// Remove names that appear in both, result: each has names unique to that catalog 
		catalog1Names.removeAll(intersection);
		catalog2Names.removeAll(intersection);
		
		System.out.println("Entries with differences");
		intersection.stream()
			.sorted()
			.forEachOrdered(entryName -> 
				{
					CatalogEntry catalog1Entry = catalog1Entries.get(entryName);
					CatalogEntry catalog2Entry = catalog2Entries.get(entryName);
					
					if (!catalog1Entry.equals(catalog2Entry))
					{
						System.out.format("Catalog 1:%n%s%n", catalog1Entry.toString());
						System.out.format("Catalog 2:%n%s%n", catalog2Entry.toString());
					}
				});

		System.out.println();
		System.out.println("Entries in Catalog 1 Only:");
		catalog1Names.stream()
			.sorted()
			.forEachOrdered(entryName -> 
				{
					System.out.format("%s%n", catalog1Entries.get(entryName).toString());
				});

		System.out.println();
		System.out.println("Entries in Catalog 2 Only:");
		catalog2Names.stream()
			.sorted()
			.forEachOrdered(entryName -> 
				{
					System.out.format("%s%n", catalog2Entries.get(entryName).toString());
				});
	}

	public static Map<String, CatalogEntry> getCatalogEntries(String catalogname) 
	{
		Map<String, CatalogEntry>  entries = new HashMap<>();
		
		CatalogSearch catSearch = new CatalogSearch("**");
		catSearch.setCatalogName(catalogname);
		catSearch.setSingleCatalog(true);
		catSearch.addFieldName("ENTYPE");
		catSearch.addFieldName("ENTNAME");
		catSearch.addFieldName("NAME");
		catSearch.addFieldName("DEVTYP");
		catSearch.addFieldName("VOLSER");
		catSearch.search();
		
		while (catSearch.hasNext()) 
		{
			CatalogSearch.Entry entry = (CatalogSearch.Entry) catSearch.next();
			if (entry.isDatasetEntry()) 
			{
				CatalogEntry catalogEntry = CatalogEntry.fromSearchEntry(entry);
				entries.put(catalogEntry.entryName, catalogEntry);
			}
		}
		return entries;
	}
	
	private static class CatalogEntry 
	{	
		public static CatalogEntry fromSearchEntry(CatalogSearch.Entry entry)
		{
			CatalogEntry result = new CatalogEntry();
			
			result.entryName = entry.getField("ENTNAME").getFString();
			result.entryType = entry.getField("ENTYPE").getFString();	
			result.relatedNames = entry.getField("NAME").getFStringArray(44);
			if (result.relatedNames == null)
				result.relatedNames = new String[0];
			result.volumes = entry.getField("VOLSER").getFStringArray(6);	
			if (result.volumes == null)
				result.volumes = new String[0];
			result.deviceTypes = entry.getField("DEVTYP").getIntArray(4);
			if (result.deviceTypes == null)
				result.deviceTypes = new int[0];
			
			return result;
		}

		@Override
		public String toString() 
		{
			String formatString = "%-44s %1s %-6s %8s%n";
			
			// Entry name, type and first volume and device type entries if present
			String result = String.format(formatString, 
					entryName, 
					entryType, 
					volumes.length > 0 ? volumes[0] : "",
					deviceTypes.length > 0 ? String.format("%8X", deviceTypes[0]) : "");
			
			// List additional volumes/devices
			// Volumes and device type arrays probably have the same number of entries,
			// but handle it here in case they do not.
			int volumeDeviceCount = Math.max(volumes.length, deviceTypes.length);
			for (int i=1; i < volumeDeviceCount; i++)
			{
				result += String.format(formatString, 
						"", 
						"", 
						volumes.length > i ? volumes[i] : "",
						deviceTypes.length > i ? String.format("%8X", deviceTypes[i]) : "");
			}
			
			// List any related names
			for (int i = 0; i < relatedNames.length; i++)	
			{
				result += String.format("%18s %-44s%n",	
						i == 0 ? "Related Names:" : "", // Insert heading into first line
						relatedNames[i]);					
			}				
			return result;
		}
		
		// Eclipse generated hashCode and equals
		@Override
		public int hashCode() 
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(deviceTypes);
			result = prime * result + Arrays.hashCode(relatedNames);
			result = prime * result + Arrays.hashCode(volumes);
			result = prime * result + Objects.hash(entryName, entryType);
			return result;
		}

		@Override
		public boolean equals(Object obj) 
		{
			if (this == obj)
				return true;
			if (!(obj instanceof CatalogEntry))
				return false;
			CatalogEntry other = (CatalogEntry) obj;
			return Arrays.equals(deviceTypes, other.deviceTypes) 
					&& Objects.equals(entryName, other.entryName)
					&& Objects.equals(entryType, other.entryType) 
					&& Arrays.equals(relatedNames, other.relatedNames)
					&& Arrays.equals(volumes, other.volumes);
		}

		public String entryName;
		public String entryType;
		public String[] relatedNames;
		public String[] volumes;
		public int[] deviceTypes;
	}
}