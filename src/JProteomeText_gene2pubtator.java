
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;

//2017-08-07
//@Author Michael T.-L. Lee @Tainan, Taiwan
//email: michaelee0407@gmail.com
public class JProteomeText_gene2pubtator {


	private ArrayList<Integer> matched_Pair_GeneIDList_TOP100 = null;
	private ArrayList<Integer> matched_Pair_PMIDList_TOP100 = null;
	private int count_Big_F = 0;
	private int count_Big_T = 0;
	private int count_Big_P = 0;
	private int count_T_intersect_P = 0;
	
	
	private File total_citation_Count_per_query_topic = null;
	private BufferedWriter out_total_citation_Count_per_query_topic = null;

	

	public JProteomeText_gene2pubtator(String QueryTerm, String species){
        
		try{
			
			/*******************************************
    		Human (Homo sapiens)	9606
    		Mouse (Mus musculus)	10090
    		Fly (Drosophila melanogaster)	7227
    		Worm (Caenorhabditis elegans)	6239
    		Yeast (Saccharomyces cerevisiae)	4932 / 559292
    		Rat (Rattus norvegicus)		10116
    		******************************************/
			
			species = species.toLowerCase();
			String[] species_array = {"human","mouse","rat","fly","worm","yeast"};
			int num_of_speices = 6;
			
			if(species.equals("all")){
				num_of_speices = 6;
			}
			else{
				num_of_speices = 1;
				species_array[0] = species;
			}

			for(int sp = 0; sp < num_of_speices; sp++){
			
				boolean computePPS = true;
				
				QueryTerm = QueryTerm.toLowerCase();
				String root_directory = "./PPS_Results/" + species_array[sp] + "/";
				
				File statistic_directory =  new File("./PPS_Results/" + species_array[sp] + "/statistic/");
				if(!statistic_directory.exists()){
					statistic_directory.mkdirs();
				}
				
				total_citation_Count_per_query_topic = new File("./PPS_Results/" + species_array[sp] + "/statistic/"+"Total_Citation_Count_"+QueryTerm);
				out_total_citation_Count_per_query_topic = new BufferedWriter(
						new FileWriter(total_citation_Count_per_query_topic));
				
				
				String fileDirAndName_total_unique_PMIDs_CitationYear = "";
				String fileDirAndName_gene2pubtator_tax_id_gene2Count = "";
				String fileDirAndName_gene2pubtator_tax_id_PMID_Sort = "";
				String fileDirAndName_UniProt_TaxID_idmapping_GeneID = "";
				
				String entrez_esearch_db = "pubmed";
		    	String entrez_esearch_retmax = "5000000";
		    	int  entrez_esearch_retstart = 0;
				
				URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?"
						+"db="+entrez_esearch_db
						+"&term="+species_array[sp]
						+"&retmax="+entrez_esearch_retmax
						+"&retstart="+ entrez_esearch_retstart
						);
				
				URLConnection connect = url.openConnection();
				BufferedReader in_F = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
				
				String temp = null;
				boolean done_parse_PMID_Count = false;
				int PMID_count = 0;
				
				while ((temp = in_F.readLine()) != null && !done_parse_PMID_Count) {
					if(!done_parse_PMID_Count){
						if(temp.indexOf("<Count>") != -1){
							PMID_count = Integer.parseInt(temp.substring(temp.indexOf(">")+1, temp.indexOf("</Count>")));
							
							done_parse_PMID_Count = true;
						}
					}
				}
				
				if(in_F!= null){
					in_F.close();
				}
				
				count_Big_F = PMID_count;
				System.out.println("species="+species_array[sp] + "-> count_Big_F=" + count_Big_F);
				out_total_citation_Count_per_query_topic.write("F=" + count_Big_F + "\n");
				
				if(species_array[sp].equals("human")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_9606_CitationYear";
					fileDirAndName_gene2pubtator_tax_id_gene2Count = "./data/myPubtator/gene2pubtator_9606_gene2Count";
					fileDirAndName_gene2pubtator_tax_id_PMID_Sort = "./data/myPubtator/gene2pubtator_9606";
					fileDirAndName_UniProt_TaxID_idmapping_GeneID = "./data/myProteinUniProt/HUMAN_9606_idmapping_GeneID";
				}
				else if(species_array[sp].equals("mouse")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_10090_CitationYear";
					fileDirAndName_gene2pubtator_tax_id_gene2Count = "./data/myPubtator/gene2pubtator_10090_gene2Count";
					fileDirAndName_gene2pubtator_tax_id_PMID_Sort = "./data/myPubtator/gene2pubtator_10090";
					fileDirAndName_UniProt_TaxID_idmapping_GeneID = "./data/myProteinUniProt/MOUSE_10090_idmapping_GeneID";
				}
				else if(species_array[sp].equals("fly")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_7227_CitationYear";
					fileDirAndName_gene2pubtator_tax_id_gene2Count = "./data/myPubtator/gene2pubtator_7227_gene2Count";
					fileDirAndName_gene2pubtator_tax_id_PMID_Sort = "./data/myPubtator/gene2pubtator_7227";
					fileDirAndName_UniProt_TaxID_idmapping_GeneID = "./data/myProteinUniProt/DROME_7227_idmapping_GeneID";			
				}
				else if(species_array[sp].equals("worm")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_6239_CitationYear";
					fileDirAndName_gene2pubtator_tax_id_gene2Count = "./data/myPubtator/gene2pubtator_6239_gene2Count";
					fileDirAndName_gene2pubtator_tax_id_PMID_Sort = "./data/myPubtator/gene2pubtator_6239";
					fileDirAndName_UniProt_TaxID_idmapping_GeneID = "./data/myProteinUniProt/CAEEL_6239_idmapping_GeneID";
				}
				else if(species_array[sp].equals("yeast")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_4932_CitationYear";
					fileDirAndName_gene2pubtator_tax_id_gene2Count = "./data/myPubtator/gene2pubtator_4932_gene2Count";
					fileDirAndName_gene2pubtator_tax_id_PMID_Sort = "./data/myPubtator/gene2pubtator_4932";
					fileDirAndName_UniProt_TaxID_idmapping_GeneID = "./data/myProteinUniProt/YEAST_559292_idmapping_GeneID";
				}
				else if(species_array[sp].equals("rat")){
					fileDirAndName_total_unique_PMIDs_CitationYear = "./data/myPubtator/total_unique_PMIDs_10116_CitationYear";
					fileDirAndName_gene2pubtator_tax_id_gene2Count = "./data/myPubtator/gene2pubtator_10116_gene2Count";
					fileDirAndName_gene2pubtator_tax_id_PMID_Sort = "./data/myPubtator/gene2pubtator_10116";
					fileDirAndName_UniProt_TaxID_idmapping_GeneID = "./data/myProteinUniProt/RAT_10116_idmapping_GeneID";
				}
				else{
					computePPS = false;
					System.out.println("Sorry, current platform is not handling selected species");
				}
				
				
				
				if(computePPS){
	
					if(QueryTerm.equals("all") || QueryTerm.equals("hpp22") || QueryTerm.equals("organ")){
							
						if(QueryTerm.equals("hpp22") || QueryTerm.equals("all")){
					
String[] hpp22_topics = {
		"brain",
		"cancer", 
		"cardiovascular", 
		"diabetes",
		"epigenetic",
		"chromatin", 
		"eye",
		"glycoproteins", 
		"immune",
		"kidney",
		"liver",
		"mitochondria",
		"musculoskeletal",
		"plasma",
		"respiratory",
		"stem+cells"
};


							for(int i = 0; i < hpp22_topics.length; i++){
								
								
								out_total_citation_Count_per_query_topic.write(hpp22_topics[i] + "\t");
								
								
								ComputePSS_from_extractAbstractUsingEntrez_Esearch(
										hpp22_topics[i]+"+"+species_array[sp], 
					        			root_directory, 
					        			fileDirAndName_total_unique_PMIDs_CitationYear,
					        			fileDirAndName_gene2pubtator_tax_id_gene2Count,
					        			fileDirAndName_gene2pubtator_tax_id_PMID_Sort,
					        			fileDirAndName_UniProt_TaxID_idmapping_GeneID,
					        			count_Big_F);
							}
						}
						
						if(QueryTerm.equals("organ") || QueryTerm.equals("all")){
					
String[] organ_systems_topics = {
		
		"cardiovascular",
		"digestive",
		"endocrine",
		"integumentary system",
		"lymphatic system or immune system",
		"muscular",
		"nervous",
		"renal or urinary",
		"reproductive",
		"respiratory or respiratory system",
		"skeletal"
};
							
		
							String query_string = "";
							String[] query_array = null;
							for(int i = 0; i < organ_systems_topics.length; i++){
								
								query_array = organ_systems_topics[i].split(" ");
		
								for(int j = 0; j < query_array.length; j++){
									if(j==0){
										query_string = query_array[j];
									}
									else{
										if(query_array[j].toLowerCase().equals("or")){
											query_string = query_string + "+" + species_array[sp] + "+" + query_array[j];
										}
										else{
											query_string = query_string + "+" + query_array[j];
										}
									}
								}
								
								
								out_total_citation_Count_per_query_topic.write(query_string + "\t");
								
								ComputePSS_from_extractAbstractUsingEntrez_Esearch(
										query_string+"+"+species_array[sp], 
					        			root_directory, 
					        			fileDirAndName_total_unique_PMIDs_CitationYear,
					        			fileDirAndName_gene2pubtator_tax_id_gene2Count,
					        			fileDirAndName_gene2pubtator_tax_id_PMID_Sort,
					        			fileDirAndName_UniProt_TaxID_idmapping_GeneID,
					        			count_Big_F);
							}
						}
					}
					else{
						
			        	ComputePSS_from_extractAbstractUsingEntrez_Esearch(
			        			QueryTerm+"+"+species_array[sp], 
			        			root_directory, 
			        			fileDirAndName_total_unique_PMIDs_CitationYear,
			        			fileDirAndName_gene2pubtator_tax_id_gene2Count,
			        			fileDirAndName_gene2pubtator_tax_id_PMID_Sort,
			        			fileDirAndName_UniProt_TaxID_idmapping_GeneID,
			        			count_Big_F);
					}//else
						
				}
				
				
				if(out_total_citation_Count_per_query_topic!=null){
					out_total_citation_Count_per_query_topic.close();
				}
			
        		
			}//for
			
			
			
			
			
			
			
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	
	

	public void ComputePSS_from_extractAbstractUsingEntrez_Esearch(
			String QueryTerm, 
			String root_directory, 
			String fileDirAndName_total_unique_PMIDs_CitationYear,
			String fileDirAndName_gene2pubtator_tax_id_gene2Count,
			String fileDirAndName_gene2pubtator_tax_id_PMID_Sort,
			String fileDirAndName_UniProt_TaxID_idmapping_GeneID,
			int count_Big_F
	){
	
		
		long startTime = System.currentTimeMillis();
		
		matched_Pair_GeneIDList_TOP100 = new ArrayList<Integer>();
		matched_Pair_PMIDList_TOP100 = new ArrayList<Integer>();
		
		
		File input_directory = new File(root_directory);
		if(!input_directory.exists()){       
			 input_directory.mkdirs();
	    }

		System.out.print("Calculating PPS [query="+QueryTerm+ "]");

		ArrayList<Integer> unique_queried_topic_pmidList = new ArrayList<Integer>();

		ArrayList<Integer> matched_PMIDList = new ArrayList<Integer>();
		ArrayList<Integer> matched_PMID_CitationList = new ArrayList<Integer>();
		ArrayList<Integer> matched_PMID_PubDateList = new ArrayList<Integer>();
		ArrayList<PPS_data> PPS_result_List = new ArrayList<PPS_data>();
	
		String temp = null;
		
		//String query_url 
		//= "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=cancer+AND+human&retmax=1000000&retstart=2486433";
		
		String entrez_esearch_db = "pubmed";
    	String entrez_esearch_retmax = "5000000";
    	int  entrez_esearch_retstart = 0;

		//System.out.println("URL = " + "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?"
		//		+"\ndb="+entrez_esearch_db
		//		+"\n&term="+QueryTerm
		//		+"\n&retmax="+entrez_esearch_retmax
		//		+"\n&retstart="+ entrez_esearch_retstart);

		try{
			
			System.out.print(".");
			
			URL url = new URL("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?"
					+"db="+entrez_esearch_db
					+"&term="+QueryTerm
					+"&retmax="+entrez_esearch_retmax
					+"&retstart="+ entrez_esearch_retstart
					);
			
			URLConnection connect = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
			

			String pmid = null;
			boolean IdList_parsing_begin = false;
			boolean done_parse_PMID_Count = false;
			int IdList_index = -1;
			int PMID_count = 0;
			int current_PMID = 0;
			
			
			while ((temp = in.readLine()) != null) {

				if(!done_parse_PMID_Count){
					if(temp.indexOf("<Count>") != -1){
						PMID_count = Integer.parseInt(temp.substring(temp.indexOf(">")+1, temp.indexOf("</Count>")));
						//System.out.println("PMID_count =" + PMID_count);
						done_parse_PMID_Count = true;
					}
				}
				else if(!IdList_parsing_begin){
					IdList_index =  temp.indexOf("<IdList>");
					if(IdList_index != -1){
						IdList_parsing_begin = true;
					}
				}
				//To save total unique PMID list results into ArrayList
				else if(current_PMID < PMID_count){
					pmid = temp.substring(temp.indexOf(">")+1, temp.indexOf("</Id>"));	
					unique_queried_topic_pmidList.add(Integer.parseInt(pmid));
					current_PMID++;
				}
			}//while
			
	
			if(in!=null){	
				in.close();
			}
				
			//esearch_resultList.clear();
			count_Big_T = unique_queried_topic_pmidList.size();
			System.out.print(".count_Big_T="+count_Big_T);
			out_total_citation_Count_per_query_topic.write("T=" + count_Big_T + "\t");
			
			//System.out.println("Query result (unique_queried_topic_pmidList.size() = " + unique_queried_topic_pmidList.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	

		//=====================================================================================================================
		
		// This section is to compute the intersected PMID list between gene2pubtator PMID list and queried topic returned PMID lists
		// The trick to increase the computation time is take advantage of BIG array which was set to the size of the LARGEST PMID number.
		// The size need to be adjusted if LARGEST PMID is greater than the current setting 28753741(Date: 2017-08-19). 
				
		//=====================================================================================================================
		
		temp= null;
		String[] token_string = null;
		matched_PMIDList = new ArrayList<Integer>();
		matched_PMID_CitationList = new ArrayList<Integer>();
		matched_PMID_PubDateList = new ArrayList<Integer>();
		
		ArrayList<String> gene2pubtator_topic_gene_count_geneIDList = new ArrayList<String>();
		ArrayList<Integer> gene2pubtator_topic_gene_count_CountList = new ArrayList<Integer>();
		
		
final int TOTAL_PMID_ARRAY_SIZE = 30000000;
		
		int[] total_unique_PMID_Array = new int[TOTAL_PMID_ARRAY_SIZE]; //need to change accordingly
		int[] total_unique_PMID_Citation_Array = new int[TOTAL_PMID_ARRAY_SIZE]; //need to change accordingly
		int[] total_unique_PMID_PubDate_Array = new int[TOTAL_PMID_ARRAY_SIZE]; //need to change accordingly
		
		
		//File gene2pubtator_inputFile 
		//= new File(fileDirAndName_gene2pubtator);
		
		File gene2pubtator_total_unique_PMIDs_CitationYear_inputFile 
		= new File(fileDirAndName_total_unique_PMIDs_CitationYear);
		
		//System.out.println(gene2pubtator_total_unique_PMIDs_CitationYear_inputFile.getName()
		//	+" is exists()? = " + gene2pubtator_total_unique_PMIDs_CitationYear_inputFile.exists());

		if (gene2pubtator_total_unique_PMIDs_CitationYear_inputFile.exists()){
			try {
				BufferedReader in_gene2pubtator = new BufferedReader(
						new FileReader(gene2pubtator_total_unique_PMIDs_CitationYear_inputFile));
				//temp = in_gene2pubtator.readLine();//skip the first line
				String[] token = null;
				while ((temp = in_gene2pubtator.readLine()) != null) {
					token = temp.split("\t");
					total_unique_PMID_Array[Integer.parseInt(token[0])] = 1; //save a lot of Execution time this way
					total_unique_PMID_Citation_Array[Integer.parseInt(token[0])] = Integer.parseInt(token[1]); //save a lot of Execution time this way
					total_unique_PMID_PubDate_Array[Integer.parseInt(token[0])] = Integer.parseInt(token[2]); //save a lot of Execution time this way
				}
				
				if(in_gene2pubtator!=null){
					in_gene2pubtator.close();
				}

			
				//System.out.println("--- ("+QueryTerm+") ===> "
				//+"================== start matching process ==============================");
				
			
				for(int m = 0; m < unique_queried_topic_pmidList.size(); m++){
					
					if(unique_queried_topic_pmidList.get(m) <= TOTAL_PMID_ARRAY_SIZE){
						if(total_unique_PMID_Array[unique_queried_topic_pmidList.get(m)] == 1){
							matched_PMIDList.add( unique_queried_topic_pmidList.get(m));
							matched_PMID_CitationList.add(total_unique_PMID_Citation_Array[unique_queried_topic_pmidList.get(m)]);
							matched_PMID_PubDateList.add(total_unique_PMID_PubDate_Array[unique_queried_topic_pmidList.get(m)]);
							//num_of_matched++;
							
							//if(num_of_matched%1000==0){ //print out the progress of matching process
							//	System.out.println(" ("+QueryTerm+")===> "
							//			+" num_of_matched = "+ num_of_matched);
							//}
						}
					}
				}
				
				
				Collections.sort(matched_PMIDList, new Comparator<Integer>() {
			        @Override
			        public int compare(Integer data1, Integer data2)
			        {
			        	if( data1.intValue() < data2.intValue()){
			        		return  -1;
			        	}
			        	if( data1.intValue() > data2.intValue()){
			        		return 1;
			        	}
			        	return 0;
			        }
			    });
				
				//System.out.println("matched_PMIDList.size() = "+ matched_PMIDList.size());
				System.out.print(".");
				
			} catch ( IOException e) {
				e.printStackTrace();
			}
						
		}//if (gene2pubtator_inputFile.exists()){
		
	
	
		//=================================================================================
		
		File gene2pubtator_topic_gene_Count_inputFile 
		= new File(fileDirAndName_gene2pubtator_tax_id_gene2Count);
		
		if(gene2pubtator_topic_gene_Count_inputFile.exists()){
			
			try{
			
				BufferedReader in_gene_Count = new BufferedReader(
						new FileReader(gene2pubtator_topic_gene_Count_inputFile));
				
				while ((temp = in_gene_Count.readLine()) != null) {
					token_string = temp.split("\t");
					if(token_string.length == 2){	
						gene2pubtator_topic_gene_count_geneIDList.add(token_string[0]);
						gene2pubtator_topic_gene_count_CountList.add(Integer.parseInt(token_string[1]));
					}
				}
				
				if(in_gene_Count!=null){
					in_gene_Count.close();
				}
				
				System.out.print(".");
				//System.out.println(
				//		"[Read from File:] gene2pubtator_topic_gene_count_geneIDList.size() = "+ gene2pubtator_topic_gene_count_geneIDList.size()
				//		+"\n"+"gene2pubtator_topic_gene_count_CountList.size() = "+ gene2pubtator_topic_gene_count_CountList.size());
			} catch ( IOException e) {
				e.printStackTrace();
			}
		}
		
		
		//=====================================================================================================================
		
		// This section is to compute GeneID to PMID pair of T+P, we need this data to further compute citation count for each GeneID
		// Use the pre-computed gene2pubtator_tax_id_PMID_Sort which is sorted version of gene2pubtator(downloaded from pubtator)
		// The format of the gene2pubtator_tax_id_PMID_Sort file is a tab-delimited text file while 
		// first term is PMID and second term is GeneID, since the PMID is sorted so that matching process does not need to search back
		// save a lot of execution time.
				
		//=====================================================================================================================
				
		
		
		File gene2pubtator_tax_id_PMID_Sort_inputFile = 
  				new File(fileDirAndName_gene2pubtator_tax_id_PMID_Sort);
  		
  		//System.out.println(gene2pubtator_tax_id_PMID_Sort_inputFile.getName()
  		//		+" is exists()? = " + gene2pubtator_tax_id_PMID_Sort_inputFile.exists());
  		//
		
		ArrayList<GeneID_to_PMID_data> matched_Pair_gene_to_PMID_dataList = new ArrayList<GeneID_to_PMID_data>();
		
		ArrayList<Integer> matched_Pair_GeneIDList = new ArrayList<Integer>();
		ArrayList<Integer> matched_Pair_PMIDList = new ArrayList<Integer>();
		

  		//String fileDirAndName_gene2pubtator_matched_Pair 
		//= organ_system_directory_string+QueryTerm+"_gene2pubtator_matched_Pair";
		
		//File file_gene2pubtator_matched_Pair = new File(
		//		fileDirAndName_gene2pubtator_matched_Pair);

		//System.out.println(file_gene2pubtator_matched_Pair.getName()
  		//		+" is exists()? = " + file_gene2pubtator_matched_Pair.exists());
  		
		
		//if(!file_gene2pubtator_matched_Pair.exists()){
  		
		if (gene2pubtator_tax_id_PMID_Sort_inputFile.exists()){
		
			try{
				
				BufferedReader in = new BufferedReader(
						new FileReader(gene2pubtator_tax_id_PMID_Sort_inputFile));

				int current_index = 0;
				int current_PMID = matched_PMIDList.get(current_index);
				
				while ((temp = in.readLine()) != null) {
					
					token_string = temp.split("\t");
					
					if(token_string.length == 2){
						
						//System.out.println(Integer.parseInt(token_string[0]) + "\t" + current_PMID);
						
						if(current_PMID  < Integer.parseInt(token_string[0])){
							if(current_index < matched_PMIDList.size()-1){
								current_index++;
								current_PMID = matched_PMIDList.get(current_index);
							}
						}
						
						if(Integer.parseInt(token_string[0]) == current_PMID){

							matched_Pair_gene_to_PMID_dataList.add(
									new GeneID_to_PMID_data(
									Integer.parseInt(token_string[1]), 
									Integer.parseInt(token_string[0])));
							
							if(current_index < matched_PMIDList.size()-1){
								current_index++;
								current_PMID = matched_PMIDList.get(current_index);
							}
						}	
					}
		
				}//while
				
				
				//System.out.println("matched_Pair_gene_to_PMID_dataList.size() = " + matched_Pair_gene_to_PMID_dataList.size());

				Collections.sort(matched_Pair_gene_to_PMID_dataList, new Comparator<GeneID_to_PMID_data>() {
			        @Override
			        public int compare(GeneID_to_PMID_data data1, GeneID_to_PMID_data data2)
			        {
			        	if( data1.getGeneID() < data2.getGeneID()){
			        		return  -1;
			        	}
			        	if( data1.getGeneID() > data2.getGeneID()){
			        		return 1;
			        	}
			        	return 0;
			        }
			    });

				for(int a = 0 ; a < matched_Pair_gene_to_PMID_dataList.size(); a++){
					matched_Pair_GeneIDList.add(matched_Pair_gene_to_PMID_dataList.get(a).getGeneID());
					matched_Pair_PMIDList.add(matched_Pair_gene_to_PMID_dataList.get(a).getPMID());
				}
				System.out.print(".");

  			} catch ( IOException e) {
  				e.printStackTrace();
  			}
			
  		}//if (gene2pubtator_inputFile.exists()){
			
		
  		
		//=====================================================================================================================
		
		//For each matched GeneID to PubMedID pair ----> calculate the sum of citation/yr of each GeneID(protein)
		
		//=====================================================================================================================
		
		ArrayList<String> gene2pubtator_matched_Pair_Citation_geneIDList = null;
		ArrayList<Integer> gene2pubtator_matched_Pair_Citation_PMIDCountList  = null;
		ArrayList<Integer> gene2pubtator_matched_Pair_Citation_CitationCountList  = null;
		
		// reading and filtering uniprot_speciesName_taxid_idmapping_dat_gz
		ArrayList<String> uniprotFile_UniProt_ID_dataList = new ArrayList<String>();
		ArrayList<String> uniprotFile_UniProtKB_ID_dataList = new ArrayList<String>();
		ArrayList<String> uniprotFile_Gene_Name_dataList = new ArrayList<String>();
		ArrayList<String> uniprotFile_GeneID_dataList = new ArrayList<String>();

		int total_citation_count_per_query_topic = 0;
		
		try{
		
			gene2pubtator_matched_Pair_Citation_geneIDList = new ArrayList<String>();
			gene2pubtator_matched_Pair_Citation_PMIDCountList  = new ArrayList<Integer>();
			gene2pubtator_matched_Pair_Citation_CitationCountList  = new ArrayList<Integer>();
			
			String current_geneID = null;
			int PMID_count = 0;
			int total_citation_count_per_year = 0;
			int pub_year = 0;
			
			
			current_geneID = matched_Pair_GeneIDList.get(0)+"";
			PMID_count = 1;
			pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(0)];
			if(pub_year >= 2018){
				pub_year = 2017;
			}
			total_citation_count_per_year = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(0)]/(2018-pub_year);
			total_citation_count_per_query_topic = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(0)];
		
			for(int a = 1; a < matched_Pair_GeneIDList.size(); a++){
				
				if(current_geneID.equals(matched_Pair_GeneIDList.get(a)+"")){
					PMID_count++;
					pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(a)];
					if(pub_year >= 2018){
						pub_year = 2017;
					}
					total_citation_count_per_year = total_citation_count_per_year + total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)]/(2018-pub_year);
					total_citation_count_per_query_topic = total_citation_count_per_query_topic + total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)];
					
					//System.out.println(total_citation_count_per_year);
				}
				else{
					//save current data
					gene2pubtator_matched_Pair_Citation_geneIDList.add(current_geneID);
					gene2pubtator_matched_Pair_Citation_PMIDCountList.add(PMID_count);
					gene2pubtator_matched_Pair_Citation_CitationCountList.add(total_citation_count_per_year);
					current_geneID = matched_Pair_GeneIDList.get(a)+"";
					PMID_count = 1;	
					pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(a)];
					if(pub_year >= 2018){
						pub_year = 2017;
					}
					total_citation_count_per_year = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)]/(2018-pub_year);
					total_citation_count_per_query_topic = total_citation_count_per_query_topic + total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(a)];
					
				}

			}//
			

			System.out.print(".[total citaion]="+total_citation_count_per_query_topic+"..");
			out_total_citation_Count_per_query_topic.write("total_citaion=" + total_citation_count_per_query_topic);
			out_total_citation_Count_per_query_topic.newLine();
			
			//prepare file for Reader of File: species_TaxID_idmapping_GeneID 	 			
	  		File UniProt_TaxID_idmapping_GeneID_inputFile = new File(fileDirAndName_UniProt_TaxID_idmapping_GeneID);
	  		
	  		//System.out.println(UniProt_TaxID_idmapping_GeneID_inputFile.getName()+" is exists()? = " 
	  		//+ UniProt_TaxID_idmapping_GeneID_inputFile.exists());;
	  		
	  		temp = null;
	  		token_string = null;
	  		if (UniProt_TaxID_idmapping_GeneID_inputFile.exists()){
				
				BufferedReader in = new BufferedReader(new FileReader(UniProt_TaxID_idmapping_GeneID_inputFile));

				while ((temp = in.readLine()) != null) {
					token_string = temp.split("\t");
					if(token_string.length == 4){	
						uniprotFile_UniProt_ID_dataList.add(token_string[0]);
						uniprotFile_UniProtKB_ID_dataList.add(token_string[1]);
						uniprotFile_Gene_Name_dataList.add(token_string[2]);
						uniprotFile_GeneID_dataList.add(token_string[3]);
					}
				}

	  		}
	  		
	  		
	  		//System.out.println("[Read from File]: " + 
			//		"uniprotFile_UniProtKB_ID_dataList.size() = "+ uniprotFile_UniProt_ID_dataList.size()
			//		+"\n"+
			//		"uniprotFile_Gene_Name_dataList.size() = "+ uniprotFile_Gene_Name_dataList.size()
			//		+"\n"+
			//		"uniprotFile_GeneID_dataList.size() = "+ uniprotFile_GeneID_dataList.size());

				
		
		} catch ( IOException e) {
			e.printStackTrace();
		}
		
		
		int index_geneID = -1;
		temp= null;
		
		//P is the set of publications linked to a particular
		//protein in all studies
		
		//F is the set of all publications linked to any
		//proteins in any topics, containing all PMIDs in the
		//Gene2PubMed file within a particular taxonomy 
		
		//T+P is the set of publications linked to a
		//particular protein within a queried topic.
		
		//calculate Big T, where T is the set of publications that are linked to any protein
		//within a particular taxonomy and that are retrieved from a
		//queried topic
		
		
		double PartI = 0.00;
		double PartII = 0.00;
		double PartIII = 0.00;
		
		
		
		int index_unipro = -1;
		String UniProtKB_ID = "-";
		String GeneID = "-";
		String Gene_Name = "-";
		

		for(int i =0; i < gene2pubtator_matched_Pair_Citation_geneIDList.size(); i++){

			// calculate Big P 
			index_unipro = uniprotFile_GeneID_dataList.indexOf(gene2pubtator_matched_Pair_Citation_geneIDList.get(i));
			if(index_unipro != -1){
				UniProtKB_ID = uniprotFile_UniProtKB_ID_dataList.get(index_unipro);
				GeneID = uniprotFile_GeneID_dataList.get(index_unipro);
				Gene_Name = uniprotFile_Gene_Name_dataList.get(index_unipro);
			}
			else{
				UniProtKB_ID = "-";
				GeneID = "-";
				Gene_Name = "-";
			}
			
			
			//PPS = (1+log10(TP)+log10((Cit/yr+1)/10)) * (1+log10(F/T)+log10(F/P))
			
			index_geneID = gene2pubtator_topic_gene_count_geneIDList.indexOf(gene2pubtator_matched_Pair_Citation_geneIDList.get(i));
			if(index_geneID != -1){
				count_Big_P = gene2pubtator_topic_gene_count_CountList.get(index_geneID);		
			}
			else{
				System.out.println(gene2pubtator_matched_Pair_Citation_geneIDList.get(i));
			}
			count_T_intersect_P = gene2pubtator_matched_Pair_Citation_PMIDCountList.get(i);
			
			
			//PartI = 1+log10(TP)
			if(count_T_intersect_P == 1){
				PartI = 1;
			}
			else{
				PartI = 1+Math.log10(count_T_intersect_P);
			}
			
			//PartII = log10((Cit/yr+1)/10)
			PartII = (gene2pubtator_matched_Pair_Citation_CitationCountList.get(i)+1)*0.1;
			if(PartII <= 1.0){
				PartII = 0.0;
			}
			else{
				PartII = Math.log10(PartII);
			}

			//PartIII = (1+log10(F/T)+log10(F/P))
			if(count_Big_F > count_Big_P && count_Big_F > count_Big_T){
				PartIII = 1 + Math.log10(count_Big_F/count_Big_T)+Math.log10(count_Big_F/count_Big_P);
			}
			else{
				PartIII = 1;
			}
			
		
			
			double PPS = (PartI+PartII)*PartIII;	
				

			String PPS_s = new DecimalFormat("#0.000").format(PPS);
			String PartI_s = new DecimalFormat("#0.000").format(PartI);
			String PartII_s = new DecimalFormat("#0.000").format(PartII);
			String PartIII_s = new DecimalFormat("#0.000").format(PartIII);
			

			PPS_result_List.add(
					new PPS_data( 
							QueryTerm,
							UniProtKB_ID,
							GeneID,
							Gene_Name,
							Double.parseDouble(PPS_s),
							count_Big_P,
							count_T_intersect_P,
							gene2pubtator_matched_Pair_Citation_CitationCountList.get(i),
							Double.parseDouble(PartI_s),
							Double.parseDouble(PartII_s),
							Double.parseDouble(PartIII_s))
			);
		

		}//for loop
		
		
		System.out.print(".");
		
		Collections.sort(PPS_result_List, new Comparator<PPS_data>() {
	        @Override
	        public int compare(PPS_data PS1, PPS_data PS2)
	        {
	        	if( PS1.getPS_value() > PS2.getPS_value()){
	        		return  -1;
	        	}
	        	if( PS1.getPS_value() < PS2.getPS_value()){
	        		return 1;
	        	}
	        	return 0;
	        }
	    });
		
		
		Vector<String> input_UniProtKB_ID_vector = new Vector();
		Vector<String> input_geneID_vector = new Vector();
		Vector<String> input_Gene_Name_vector = new Vector();
		Vector<Double> input_geneID_PS_vector = new Vector();
		Vector<Integer> input_geneID_PS_bigT_vector = new Vector();
		Vector<Integer> input_geneID_PS_bigP_vector = new Vector();
		Vector<Integer> input_geneID_PS_bigF_vector = new Vector();
		Vector<Integer> input_geneID_PS_intsT_P_vector = new Vector();
		Vector<Integer> input_geneID_Citation_vector = new Vector();  
		Vector<Double> input_geneID_LOG_intsT_P_vector = new Vector();
		Vector<Double> input_geneID_LOG_T_div_P_vector = new Vector();
		Vector<Double> input_geneID_LOG_Citation_vector = new Vector();  
		
		
		Vector<Integer> PPS_TOP100_geneID_vector = new Vector();
		Vector<String> PPS_TOP100_total_unique_PMIDs_vector = new Vector();
		Vector<Integer> PPS_TOP100_geneID_Sorted_vector = new Vector();
		
		
		try{
		
			//write to File
			String file_Output_DirAndName 
			= root_directory+QueryTerm+"_PPS";
			
			File outputFile = new File(file_Output_DirAndName);
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			
			
			out.write("Big_T" + "\t" + count_Big_T + "\n");
			out.write("Big_F" + "\t" + count_Big_F + "\n");
			
			out.write(
					"UniProtKB_ID" + "\t" +
					"GeneID"  + "\t" +
					"Gene_Name" + "\t" +
					"PPS" + "\t" +
					"Big_P" + "\t" +
					"T+P" + "\t" +
					"Citation" + "\t" +
					"1+Log(T+P)" + "\t" +
					"log10((Cit/yr+1)/10)" + "\t" +
					"1+log10(F/T)+log10(F/P)"
					);
			out.newLine();
			
			int count100 = 0;
			index_geneID = -1;
			for(int j = 0; j < PPS_result_List.size(); j++){
	
				input_UniProtKB_ID_vector.add(PPS_result_List.get(j).getUniProtKB_ID());
				input_geneID_vector.add(PPS_result_List.get(j).getGeneID());
				input_Gene_Name_vector.add(PPS_result_List.get(j).getGene_Name());
				input_geneID_PS_vector.add(PPS_result_List.get(j).getPS_value());
				input_geneID_PS_bigP_vector.add(PPS_result_List.get(j).getCount_Big_P());
				input_geneID_PS_intsT_P_vector.add(PPS_result_List.get(j).getCount_T_intersect_P() );
				input_geneID_Citation_vector.add(PPS_result_List.get(j).getTotal_citation_count());
				input_geneID_LOG_intsT_P_vector.add(PPS_result_List.get(j).getPartI_value());
				input_geneID_LOG_T_div_P_vector.add(PPS_result_List.get(j).getPartII_value()); 
				input_geneID_LOG_Citation_vector.add(PPS_result_List.get(j).getPartIII_value()); 

				
				if(!PPS_result_List.get(j).getGeneID().equals("-")){
					
					
					if(count100 < 100){
						count100++;
						PPS_TOP100_geneID_vector.add(Integer.parseInt(PPS_result_List.get(j).getGeneID()));
						PPS_TOP100_geneID_Sorted_vector.add(Integer.parseInt(PPS_result_List.get(j).getGeneID()));
					}
					
					out.write(
							PPS_result_List.get(j).getUniProtKB_ID()
							+ "\t" + PPS_result_List.get(j).getGeneID()
							+ "\t" + PPS_result_List.get(j).getGene_Name()
							+ "\t" + PPS_result_List.get(j).getPS_value() 
							+ "\t" + PPS_result_List.get(j).getCount_Big_P() 
							+ "\t" + PPS_result_List.get(j).getCount_T_intersect_P()
							+ "\t" + PPS_result_List.get(j).getTotal_citation_count()
							+ "\t" + PPS_result_List.get(j).getPartI_value()
							+ "\t" + PPS_result_List.get(j).getPartII_value()
							+ "\t" + PPS_result_List.get(j).getPartIII_value()
							);
					out.newLine();
				}
			}
			
			if(out != null){
				out.close();
			}
			
			
			
			
			long endTime = System.currentTimeMillis();
			long duration_in_ms = (endTime -  startTime);  
			
			long sec =  duration_in_ms/1000;
			
			String exection_time_string = "Finished!---> Execucution:"
					+ sec + " seconds"; // and " + duration_in_ms%1000 + " milliseconds.";
			System.out.println(exection_time_string);
			

			// Sort TOP100 proteins in PPS score
			
			Collections.sort(PPS_TOP100_geneID_Sorted_vector, new Comparator<Integer>() {
		        @Override
		        public int compare(Integer geneID1, Integer geneID2)
		        {
		        	if( geneID1 < geneID2){
		        		return  -1;
		        	}
		        	if( geneID1 > geneID2){
		        		return 1;
		        	}
		        	return 0;
		        }
		    });
			
			
			//write to File
			double total_citation_count_per_year_Filter_Value = 5.0;
			
			//System.out.println("PPS_TOP100_geneID_Sorted_vector.size()  = " + PPS_TOP100_geneID_Sorted_vector.size()); 
	
			int current_index = 0;
			int curent_geneID = 0;
			if(current_index < PPS_TOP100_geneID_Sorted_vector.size()){
				PPS_TOP100_geneID_Sorted_vector.get(current_index);
			}
			int pub_year = 0;
			double total_citation_count_per_year = 0;
		
			for(int g = 0; g < matched_Pair_GeneIDList.size(); g++){	
				if(curent_geneID == matched_Pair_GeneIDList.get(g) ){
					// filter Citation/Yr here
					pub_year = total_unique_PMID_PubDate_Array[matched_Pair_PMIDList.get(g)];
					if(pub_year >= 2018){
						pub_year = 2017;
					}
					total_citation_count_per_year = total_unique_PMID_Citation_Array[matched_Pair_PMIDList.get(g)]/(2018-pub_year);
					
					if(total_citation_count_per_year >= total_citation_count_per_year_Filter_Value){
						matched_Pair_GeneIDList_TOP100.add(matched_Pair_GeneIDList.get(g));
						matched_Pair_PMIDList_TOP100.add(matched_Pair_PMIDList.get(g));
					}
				}
				else if( (matched_Pair_GeneIDList.get(g) > curent_geneID) && current_index < 99){
					current_index++;//pick the next geneID of TOP100
					if(current_index < PPS_TOP100_geneID_Sorted_vector.size()){
						curent_geneID = PPS_TOP100_geneID_Sorted_vector.get(current_index);
					}
					//System.out.println("current_index #" + current_index + "\t" + curent_geneID);
				}
				
			}//for
			
			//System.out.println("***** Run Extract TOP100 PMID for view publication *********");

			long startTime_pmid = System.currentTimeMillis();
			
			ExtractTOP100PMIDCitation(root_directory, QueryTerm);
			
			long endTime_pmid = System.currentTimeMillis();
			long duration_in_ms_pmid = (endTime_pmid -  startTime_pmid);  
			
			long sec_pmid =  duration_in_ms_pmid/1000;
			
			String exection_time_string_ExtractTOP100PMIDCitation = "Finished!---> Execucution:" 
					+ sec_pmid + " seconds"; //+ duration_in_ms_pmid%1000 + " milliseconds.";
			System.out.println(exection_time_string_ExtractTOP100PMIDCitation);
			
			//free memory
			total_unique_PMID_Array = new int[0];//free memory of Array
			total_unique_PMID_Citation_Array = new int[0];//free memory of Array
			total_unique_PMID_PubDate_Array = new int[0];//free memory of Array
			
			

		} catch ( IOException e) {
			e.printStackTrace();
		}	
		

  }
	
	
	

	
	public void ExtractTOP100PMIDCitation(String root_directory, String QueryTerm){
		
		ArrayList<Integer> loop_unique_PMIDsList = new ArrayList<Integer>();
		ArrayList<Integer> ESumm_citationList = new ArrayList<Integer>();
		ArrayList<Integer> ESumm_PubDateList = new ArrayList<Integer>();
		ArrayList<String> ESumm_AuthorList = new ArrayList<String>();
		ArrayList<String> ESumm_TitleList = new ArrayList<String>();
		ArrayList<String> ESumm_FullJournalNameList = new ArrayList<String>();
		

		try {
			
			
			
			String uid_query_string = "";
			
			int total_PMID = matched_Pair_PMIDList_TOP100.size();
			System.out.print("Extracting PMIDs [query="+QueryTerm+ "]..PMID#="+total_PMID);
			
			//System.out.println("total_PMID = " + total_PMID);
			
			int loop_count = 0;
			int num_query_size = 800; 
			//System.out.println("num_query_size = " + num_query_size);
			
			if(total_PMID%num_query_size == 0){
				loop_count = total_PMID/num_query_size;
			}
			else{
				loop_count = (total_PMID/num_query_size)+1;
			}
					
			//System.out.println("loop_count = " + loop_count);
			
			
			int end_number = -1;
			URL obj  = null;
			HttpURLConnection con = null;
			int start_i = 1;
			BufferedReader in = null;
			
			for(int i = start_i; i <= loop_count; i++){
				
				uid_query_string = "";
				
				if(i*num_query_size > total_PMID){
					end_number = total_PMID;
				}
				else{
					end_number = i*num_query_size;
				}
				
				System.out.print(".");
				//System.out.println("(#"+i+") --- number_PMIDs = " + end_number);
				
				for(int j = (i-1)*num_query_size; j < end_number ; j++){

					//loop_unique_PMIDsList.add(total_unique_PMIDsList.get(j));
					if(uid_query_string.equals("")){
						uid_query_string = matched_Pair_PMIDList_TOP100.get(j)+"";
					}
					else{
						uid_query_string = uid_query_string + "," + matched_Pair_PMIDList_TOP100.get(j)+"";
					}
				}

				//uid_query_string = "20467650";
				
				//if(uid_query_string.length() > 0 ){
				if(uid_query_string.length() > 0 ){
					
					String query_url 
							= "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?"+
									"db=pubmed"+ "&id=" + uid_query_string;
			
					obj = new URL(query_url);
					con = (HttpURLConnection) obj.openConnection();
			
					// optional default is GET
					con.setRequestMethod("POST");
			
					//add request header
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
			
					int responseCode = con.getResponseCode();
					//System.out.println("\nSending 'POST' request to URL : " + query_url);
					//System.out.println("Response Code : " + responseCode);
			
					
					in = new BufferedReader(
					        new InputStreamReader(con.getInputStream()));
					String inputLine;
					
					String temp_PMID = null;
					String temp_year = null;
					String temp_author = null;
					String temp_title = null;
					StringBuffer response = new StringBuffer();
			
										
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
						//System.out.println(inputLine);
						
						if(inputLine.indexOf("<Id>")!=-1){
							temp_PMID = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Id>"));
							loop_unique_PMIDsList.add(Integer.parseInt(temp_PMID));
							temp_author= null;
							//System.out.println(temp_PMID);
						}
						
						else if(inputLine.indexOf("<Item Name=\"PubDate\"")!=-1){
							temp_year = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf(">")+5);
							//System.out.println(temp_year);
							try{
							
								ESumm_PubDateList.add(Integer.parseInt(temp_year));
								
							} catch ( NumberFormatException e) {
								//e.printStackTrace();
								inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
								temp_year = inputLine.substring(inputLine.indexOf(" ")+1, inputLine.indexOf(" ")+5);
								//System.out.println(temp_year);
								ESumm_PubDateList.add(Integer.parseInt(temp_year));
							}
							
						}
						else if(inputLine.indexOf("<Item Name=\"Author\"") != -1 ){
							if(temp_author == null){
								temp_author = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
								ESumm_AuthorList.add(temp_author);
							}
						}
						//else if(inputLine.indexOf("<Item Name=\"LastAuthor\"") != -1 ){
						//	if(temp_author== null){
						//		temp_author = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
						//		ESumm_AuthorList.add(temp_author);
						//		temp_author= null;
						//	}
						//}
						else if(inputLine.indexOf("<Item Name=\"Title\"") != -1 ){
							
							if(temp_author == null){
								ESumm_AuthorList.add("-");
							}
							
							if(inputLine.indexOf("</Item>") != -1 ){
								inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
							}
							else{
								inputLine = in.readLine();
							}
							inputLine = inputLine.replaceAll("\\[", "");
							inputLine = inputLine.replaceAll("\\]", "");
							ESumm_TitleList.add(inputLine);
						}
						else if(inputLine.indexOf("<Item Name=\"FullJournalName\"") != -1 ){
							if(inputLine.indexOf("</Item>") != -1 ){
								inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
							}
							else{
								inputLine = in.readLine();
							}
							
							if(inputLine.indexOf(".") != -1 ){
								inputLine = inputLine.substring(0, inputLine.indexOf("."));
							}
							ESumm_FullJournalNameList.add(inputLine);

						}
						else if(inputLine.indexOf("<Item Name=\"PmcRefCount\"")!=-1){
							inputLine = inputLine.substring(inputLine.indexOf(">")+1, inputLine.indexOf("</Item>"));
							ESumm_citationList.add(Integer.parseInt(inputLine));
							//System.out.println(inputLine);
						}

					}
					
					in.close();

				}					
			}//for loop
			//======================================================
			
			
			//writer
			File file_TOP100_view_publication 
			= new File(root_directory+QueryTerm+"_TOP100");
			BufferedWriter out_TOP100_view_publication = new BufferedWriter(new FileWriter(file_TOP100_view_publication));
			
			int index_PMID = -1;
			
			for(int m = 0; m < matched_Pair_GeneIDList_TOP100.size(); m++){
					
				index_PMID = loop_unique_PMIDsList.indexOf(matched_Pair_PMIDList_TOP100.get(m));
				if(index_PMID!=-1){
					//System.out.println(token_string[1]);
					out_TOP100_view_publication.write(
						matched_Pair_PMIDList_TOP100.get(m) + "\t" +
						matched_Pair_GeneIDList_TOP100.get(m) + "\t" +
						ESumm_AuthorList.get(index_PMID) + "\t" +
						ESumm_TitleList.get(index_PMID) + "\t" +
						ESumm_FullJournalNameList.get(index_PMID) + "\t" +
						ESumm_PubDateList.get(index_PMID) + "\t" +
						ESumm_citationList.get(index_PMID) 
						+ "\n");
				}

			}
			
			if(out_TOP100_view_publication!= null){
				out_TOP100_view_publication.close();
			}
			

		} catch ( IOException e) {
			e.printStackTrace();
			
		}
		

	}
	

    
    


public static void main(String[] args) throws IOException  {
		
	String query_string = "";
	String species = "";
	if(args.length > 0){
		
		species = args[args.length-1];
		
		for(int i = 0; i < args.length-1; i++){
			if(i==0){
				query_string = args[i];
			}
			else{
				if(args[i].toLowerCase().equals("or")){
					query_string = query_string + "+" + species + "+" + args[i];
				}
				else{
					query_string = query_string + "+" + args[i];
				}
			}
		}
		
		
		if(query_string.equals("") || query_string == null || query_string.isEmpty()){
			System.out.println("Sorry, the query term is empty!");
		}
		else{
			JProteomeText_gene2pubtator obj = new JProteomeText_gene2pubtator(query_string, species);
		}
		
	}
	else{
		Scanner in = new Scanner(System.in);
		System.out.print("Query Term + space + species(ex. brain human/mouse/rat/fly/worm/yeast) [-1 to exit]:");
		query_string = in.nextLine();
		
		String[] query_array = null;
		String species_s = "";
		
		while(!query_string.equals("-1")){
			if(query_string.equals("") || query_string == null || query_string.isEmpty()){
				System.out.println("Sorry, the query term is empty!");
			}
			else{
				query_array = query_string.split(" ");
				species_s = query_array[query_array.length-1];
				
				for(int i = 0; i < query_array.length-1; i++){
					if(i==0){
						query_string = query_array[i];
					}
					else{
						if(query_array[i].toLowerCase().equals("or")){
							query_string = query_string + "+" + species_s + "+" + query_array[i];
						}
						else{
							query_string = query_string + "+" + query_array[i];
						}
						
					}
				}
				JProteomeText_gene2pubtator obj = new JProteomeText_gene2pubtator(query_string, species_s);
			}
			
			System.out.print("Query Term + space + species(ex. brain human/mouse/rat/fly/worm/yeast) [-1 to exit]:");
			query_string = in.nextLine();
		}
	}
	
	

}//end of main method



}//end of class









	










class GeneID_to_PMID_data{
	
	private int GeneID;
	private int PMID;
	
	public GeneID_to_PMID_data(int GeneID, int PMID){
		this.GeneID = GeneID;
		this.PMID = PMID;
	}
	
	public int getGeneID() {
		return GeneID;
	}

	public void setGeneID(int geneID) {
		GeneID = geneID;
	}

	public int getPMID() {
		return PMID;
	}

	public void setPMID(int pMID) {
		PMID = pMID;
	}
	
}


class PMID_to_GeneID_data{
	
	private int PMID;
	private int GeneID;
	
	public PMID_to_GeneID_data(int PMID, int GeneID){
		this.PMID = PMID;
		this.GeneID = GeneID;
	}
	
	public int getGeneID() {
		return GeneID;
	}

	public void setGeneID(int geneID) {
		GeneID = geneID;
	}

	public int getPMID() {
		return PMID;
	}

	public void setPMID(int pMID) {
		PMID = pMID;
	}
	
}


class PPS_data{
	
	private String disease_topic;
	private String UniProtKB_ID;
	private String  GeneID;
	private String  Gene_Name;
	private double PS_value;
	private int count_Big_P;
	private int count_T_intersect_P;
	private int total_citation_count;
	private double PartI_value;
	private double PartII_value;
	private double PartIII_value;

	public PPS_data(
			String disease_topic,
			String UniProtKB_ID,
			String  GeneID,
			String  Gene_Name, 
			double PS_value, 
			int count_Big_P,  
			int count_T_intersect_P, 
			int total_citation_count, 
			double PartI_value, 
			double PartII_value, 
			double PartIII_value){
		this.disease_topic= disease_topic;
		this.UniProtKB_ID = UniProtKB_ID;
		this.GeneID = GeneID;
		this.Gene_Name = Gene_Name;
		this.PS_value = PS_value;
		this.count_Big_P = count_Big_P;
		this.count_T_intersect_P = count_T_intersect_P;
		this.total_citation_count = total_citation_count;
		this.PartI_value = PartI_value;
		this.PartII_value = PartII_value;
		this.PartIII_value = PartIII_value;
	}

	
	public String getDisease_topic() {
		return disease_topic;
	}

	public void setDisease_topic(String disease_topic) {
		this.disease_topic = disease_topic;
	}
	
	public String getUniProtKB_ID() {
		return UniProtKB_ID;
	}

	public void setUniProtKB_ID(String uniProtKB_ID) {
		UniProtKB_ID = uniProtKB_ID;
	}

	public String getGene_Name() {
		return Gene_Name;
	}

	public void setGene_Name(String gene_Name) {
		Gene_Name = gene_Name;
	}
	
	public String getGeneID() {
		return GeneID;
	}

	public void setGeneID(String GeneID) {
		this.GeneID = GeneID;
	}

	public double getPS_value() {
		return PS_value;
	}

	public void setPS_value(double PS_value) {
		PS_value = PS_value;
	}
	
	public int getCount_Big_P() {
		return count_Big_P;
	}

	public void setCount_Big_P(int count_Big_P) {
		this.count_Big_P = count_Big_P;
	}
	
	public int getCount_T_intersect_P() {
		return count_T_intersect_P;
	}

	public void setCount_T_intersect_P(int count_T_intersect_P) {
		this.count_T_intersect_P = count_T_intersect_P;
	}

	public int getTotal_citation_count() {
		return total_citation_count;
	}

	public void setTotal_citation_count(int total_citation_count) {
		this.total_citation_count = total_citation_count;
	}
	
	public double getPartI_value() {
		return PartI_value;
	}

	public void setPartI_value(double partI_value) {
		PartI_value = partI_value;
	}

	public double getPartII_value() {
		return PartII_value;
	}

	public void setPartII_value(double partII_value) {
		PartII_value = partII_value;
	}

	public double getPartIII_value() {
		return PartIII_value;
	}

	public void setPartIII_value(double partIII_value) {
		PartIII_value = partIII_value;
	}

	
}
