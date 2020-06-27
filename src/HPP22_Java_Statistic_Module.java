import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;

public class HPP22_Java_Statistic_Module {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String species = "";
		if(args.length > 0){
			species = args[0];
		}
		else{
			Scanner in = new Scanner(System.in);
			System.out.print("please enter species(ex. human/mouse/rat/fly/worm/yeast/all):");
			species = in.nextLine();
		}
		
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
			
			String fileDirectory = "./PPS_Results/" + species_array[sp] + "/";
			String statistic_directory_s = "./PPS_Results/" + species_array[sp] + "/statistic/";
			File statistic_directory =  new File(statistic_directory_s);
			if(!statistic_directory.exists()){
				statistic_directory.mkdirs();
			}
			System.out.println("=======================   "+species_array[sp]+"   =====================");
			retrieve_top_proteins_from_PPS_results_of_each_species(fileDirectory, statistic_directory_s, species);
		}
	}

	
	
	public static void retrieve_top_proteins_from_PPS_results_of_each_species(String fileDirectory, String statistic_directory_s, String species){
	
		
		try{
			
			
			ArrayList<PPS_data> PPS_result_List = new ArrayList<PPS_data>();
			
			
			File fileDirectory_of_PPS_results_of_each_species
			= new File(fileDirectory);
			
			String query_topic = null;
			
			String[] file_Name_Array = fileDirectory_of_PPS_results_of_each_species.list();
			System.out.println(fileDirectory_of_PPS_results_of_each_species.getName() 
					+ " has number of files = " + file_Name_Array.length);
			
			File file_PPS_results_of_each_species = null;
			
			for(int i = 0; i < file_Name_Array.length; i++){

				if(file_Name_Array[i].indexOf("PPS")!=-1){
				
					query_topic = file_Name_Array[i].substring(0, file_Name_Array[i].indexOf("_"));
					
					file_PPS_results_of_each_species = new File(
							fileDirectory+file_Name_Array[i]);
					//check if the citation file is exist?
					System.out.println(file_PPS_results_of_each_species.getName()
							+" is exists()? = " + file_PPS_results_of_each_species.exists());
					
					if(file_PPS_results_of_each_species.exists()){
		
						BufferedReader in_PPS_results_of_each_species = new BufferedReader(
								new FileReader(file_PPS_results_of_each_species));
			
						String inputLine = null;
						String[] token_string = null;
						
						inputLine = in_PPS_results_of_each_species.readLine();//print T
						inputLine = in_PPS_results_of_each_species.readLine();//print F
						inputLine = in_PPS_results_of_each_species.readLine();//print Title
						
						while ((inputLine = in_PPS_results_of_each_species.readLine()) != null) {
							
							token_string = inputLine.split("\t");
							
							
							/*
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
							 */
							
							if(token_string.length == 10){
								PPS_result_List.add(
										new PPS_data( 
											query_topic,
											token_string[0],
											token_string[1],
											token_string[2],
											Double.parseDouble(token_string[3]),
											Integer.parseInt(token_string[4]),
											Integer.parseInt(token_string[5]),
											Integer.parseInt(token_string[6]),
											Double.parseDouble(token_string[7]),
											Double.parseDouble(token_string[8]),
											Double.parseDouble(token_string[9]))
								);
							}
							
							
							
						}
		
					}
				}//if(file_Name_Array[i].indexOf("PPS")!=-1){
			}//for(int i = 0; i < file_Name_Array.length; i++){
			
			
			boolean sort_by_PPS = true;
			boolean sort_by_P = true;
			int top_count = 100;
			
			//writer
			File file_top_PPS_result_of_each_species_Title = null;
			
			
			
			//To sort PPS_result_List by PPS score
			if(sort_by_PPS){
				
				file_top_PPS_result_of_each_species_Title 
				= new File(statistic_directory_s+"TOP_N_PS_results_in_"+species);
				
				Collections.sort(PPS_result_List, new Comparator<PPS_data>() {
			        @Override
			        public int compare(PPS_data PPS1, PPS_data PPS2)
			        {
			        	if( PPS1.getPS_value() > PPS2.getPS_value()){
			        		return  -1;
			        	}
			        	if( PPS1.getPS_value() < PPS2.getPS_value()){
			        		return 1;
			        	}
			        	return 0;
			        }
			    });
			}
			
			BufferedWriter out_top_PPS_result_of_each_species_Title = 
					new BufferedWriter(new FileWriter(file_top_PPS_result_of_each_species_Title));
			
			
			String top_PPS_result_of_each_species_Title =
			"Topic"
			+ "\t" +"UniProtKB_ID"
			+ "\t" +"GeneID"
			+"\t"+"Gene_Name"
			+"\t"+"PPS"
			+"\t"+"Big_P"
			+"\t"+"T+P"
			+"\t"+"Citation";
			//System.out.println(top_PPS_result_of_each_species_Title);
			out_top_PPS_result_of_each_species_Title.write(top_PPS_result_of_each_species_Title);
			out_top_PPS_result_of_each_species_Title.newLine();
			
			String top_PPS_result_of_each_species = ""; 
			for(int j = 0; j < PPS_result_List.size(); j++){
				
				top_PPS_result_of_each_species = 
					PPS_result_List.get(j).getDisease_topic() + "\t" + 
					PPS_result_List.get(j).getUniProtKB_ID() + "\t" + 
					PPS_result_List.get(j).getGeneID() + "\t" +
					PPS_result_List.get(j).getGene_Name() + "\t" +
					PPS_result_List.get(j).getPS_value() + "\t" +
					PPS_result_List.get(j).getCount_Big_P() + "\t" +
					PPS_result_List.get(j).getCount_T_intersect_P() + "\t" +
					PPS_result_List.get(j).getTotal_citation_count();
				
				out_top_PPS_result_of_each_species_Title.write(top_PPS_result_of_each_species);
				out_top_PPS_result_of_each_species_Title.newLine();
				
				//System.out.println(top_PPS_result_of_each_species);
			}
				
			
			
			if(out_top_PPS_result_of_each_species_Title!=null){
				out_top_PPS_result_of_each_species_Title.close();
			}
			
			
			
			if(sort_by_P){
				
				file_top_PPS_result_of_each_species_Title 
				= new File(statistic_directory_s+"TOP_N_num_of_P_results_in_"+species);

				Collections.sort(PPS_result_List, new Comparator<PPS_data>() {
			        @Override
			        public int compare(PPS_data PPS1, PPS_data PPS2)
			        {
			        	if( PPS1.getCount_Big_P() > PPS2.getCount_Big_P()){
			        		return  -1;
			        	}
			        	if( PPS1.getCount_Big_P() < PPS2.getCount_Big_P()){
			        		return 1;
			        	}
			        	return 0;
			        }
			    });
			}
			
			
			out_top_PPS_result_of_each_species_Title = 
					new BufferedWriter(new FileWriter(file_top_PPS_result_of_each_species_Title));
			
			
			top_PPS_result_of_each_species_Title = 
			"Topic"
			+ "\t" +"UniProtKB_ID"
			+ "\t" +"GeneID"
			+"\t"+"Gene_Name"
			+"\t"+"PPS"
			+"\t"+"Big_P"
			+"\t"+"T+P"
			+"\t"+"Citation";
			//System.out.println(top_PPS_result_of_each_species_Title);
			out_top_PPS_result_of_each_species_Title.write(top_PPS_result_of_each_species_Title);
			out_top_PPS_result_of_each_species_Title.newLine();
			
			ArrayList<String> total_UniProtKB_ID_List = new ArrayList<String>();
			
			top_PPS_result_of_each_species = ""; 
			for(int j = 0; j < PPS_result_List.size(); j++){
				
				if(!total_UniProtKB_ID_List.contains(PPS_result_List.get(j).getUniProtKB_ID())){
				
					total_UniProtKB_ID_List.add(PPS_result_List.get(j).getUniProtKB_ID());
					
					top_PPS_result_of_each_species = 
						PPS_result_List.get(j).getDisease_topic() + "\t" + 
						PPS_result_List.get(j).getUniProtKB_ID() + "\t" + 
						PPS_result_List.get(j).getGeneID() + "\t" +
						PPS_result_List.get(j).getGene_Name() + "\t" +
						PPS_result_List.get(j).getPS_value() + "\t" +
						PPS_result_List.get(j).getCount_Big_P() + "\t" +
						PPS_result_List.get(j).getCount_T_intersect_P() + "\t" +
						PPS_result_List.get(j).getTotal_citation_count();
					
					out_top_PPS_result_of_each_species_Title.write(top_PPS_result_of_each_species);
					out_top_PPS_result_of_each_species_Title.newLine();
					
					//System.out.println(top_PPS_result_of_each_species);
				}
			}
				
			
			
			if(out_top_PPS_result_of_each_species_Title!=null){
				out_top_PPS_result_of_each_species_Title.close();
			}
			
			
			
			
		} catch ( IOException e) {
			e.printStackTrace();
			
		}
			
	
	}//end of submethod: retrieve_top_proteins_from_PPS_results_of_each_species(String fileDirectory)
	
	
	
}//End of class
