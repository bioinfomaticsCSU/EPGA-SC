//package epga_spades;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class CommonClass {
	//Set output format.
	public static String Changeformat(double value) {
		DecimalFormat df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(value);
	}
	//Get File Lines.
	public static int getFileLines(String ReadSetPath) throws IOException {
		int line = 0;
		String encoding = "utf-8";
		File file = new File(ReadSetPath);
		if (file.isFile() && file.exists()) {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
			BufferedReader bufferedReader = new BufferedReader(read);
			while ((bufferedReader.readLine()) != null) {
				line++;
			}
			bufferedReader.close();
		}
		return line;
	}
	//FASTA file to array.
	public static int FastaToArray(String ReadSetPath, String[] ReadSetArray) {
		int count = 0;
		try {
			String encoding = "utf-8";
			File file = new File(ReadSetPath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((bufferedReader.readLine()) != null) {
					ReadSetArray[count++] = bufferedReader.readLine();
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
		return count;
	}
	//Generate File Array.
	public static int FileToArray(String FilePath, String[] FileArray) throws IOException {
		int ReadCount = 0;
		String encoding = "utf-8";
		try {
			String readtemp = "";
			File file = new File(FilePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((readtemp = bufferedReader.readLine()) != null) {
					FileArray[ReadCount++] = readtemp;
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
		return ReadCount;
	}
	//FirstEstimate.
	public static double FirstEstimate(String LogPath) throws IOException {
		String encoding = "utf-8";
		String readtemp = "";
		double First_estimate = 0;
		File Coverage_Estimate = new File(LogPath);
		if (Coverage_Estimate.isFile() && Coverage_Estimate.exists()) {
			InputStreamReader read = new InputStreamReader(new FileInputStream(Coverage_Estimate), encoding);
			BufferedReader bufferedReader = new BufferedReader(read);
			while ((bufferedReader.readLine()) != null) {
				readtemp = bufferedReader.readLine();
				String[] Line_split = readtemp.split("=|\n");
				First_estimate = Double.valueOf(Line_split[1]);
				break;
			}
			bufferedReader.close();
		}
		return First_estimate;
	}
	//Generate FASTQ Array.
	public static int FastqToArray(String FastqPath, String[] FastqArray) throws IOException {
		int ReadCount = 0;
		String encoding = "utf-8";
		try {
			File file = new File(FastqPath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((bufferedReader.readLine()) != null) {
					String Str1 = bufferedReader.readLine();
					bufferedReader.readLine();
					String Str2 = bufferedReader.readLine();
					FastqArray[ReadCount++] = Str1 + "\t" + Str2;
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
		return ReadCount;
	}
	//Reverse
	public static String reverse(String s) {
		int length = s.length();
		String reverse = "";
		for (int i = 0; i < length; i++) {
			if (s.charAt(i) == 'A') {
				reverse = "T" + reverse;
			} else if (s.charAt(i) == 'T') {
				reverse = "A" + reverse;
			} else if (s.charAt(i) == 'G') {
				reverse = "C" + reverse;
			} else if (s.charAt(i) == 'C') {
				reverse = "G" + reverse;
			} else {
				reverse = "N" + reverse;
			}
		}
		return reverse;
	}
	//Hash Function.
	public static int RSHash(String str) {
		int hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}
		return (hash & 0x7FFFFFFF);
	}
	//KMER File To HashTable.
	public static int KmerFileToHash(String KmerFilePath, String[][] KmerHashTable, int SizeOfHash) throws IOException {
		int countDSK = 0;
		String encoding = "utf-8";
		try {
			String readtemp = "";
			File file = new File(KmerFilePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((readtemp = bufferedReader.readLine()) != null) {
					if (readtemp.length() > 0) {
						int hashCode = RSHash(readtemp) % SizeOfHash;
						if (KmerHashTable[hashCode][0] == null) {
							KmerHashTable[hashCode][0] = readtemp;
						} else {
							int i = 1;
							while (KmerHashTable[(hashCode + i) % SizeOfHash][0] != null) {
								i++;
							}
							if (KmerHashTable[(hashCode + i) % SizeOfHash][0] == null) {
								KmerHashTable[(hashCode + i) % SizeOfHash][0] = readtemp;
							}
						}
						countDSK++;
						if (countDSK % 10000000 == 0) {
							System.out.println(
									"P-rate:" + Changeformat((double) countDSK / (SizeOfHash - 100000) * 100) + "%");
						}
					}
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
		return countDSK;
	}
	//Get element From HashTable.
	public static int getHashUnit(String KmerStr, String HashTable[][], int SizeOfDSK) {
		int i = 1;
		int hashcode = RSHash(KmerStr) % SizeOfDSK;
		if (HashTable[hashcode][0] != null) {
			if (HashTable[hashcode][0].equals(KmerStr)) {
				return hashcode;
			} else {
				while (HashTable[(hashcode + i) % SizeOfDSK][0] != null) {
					if (HashTable[(hashcode + i) % SizeOfDSK][0].equals(KmerStr)) {
						return (hashcode + i) % SizeOfDSK;
					} else {
						i++;
					}
				}
			}
		}
		return -1;
	}
	//Calculate k-mer frequency.
	public static int CalculateAverageKmerScore(String KmerString, String KmerHashTable_A[][],
			int SizeOfKmerHashTable_A, String KmerHashTable_T[][], int SizeOfKmerHashTable_T,
			String KmerHashTable_G[][], int SizeOfKmerHashTable_G, String KmerHashTable_C[][],
			int SizeOfKmerHashTable_C) throws IOException {
		String FR_KmerStr = KmerString;
		String RE_KmerStr = reverse(KmerString);
		int HasHAddress_RE = -1;
		int HasHAddress_FR = -1;
		if (RE_KmerStr.charAt(0) == 'A') {
			HasHAddress_RE = getHashUnit(reverse(KmerString), KmerHashTable_A, SizeOfKmerHashTable_A);
			if (HasHAddress_RE != -1) {
				return 1;
			}
		}
		if (RE_KmerStr.charAt(0) == 'T') {
			HasHAddress_RE = getHashUnit(reverse(KmerString), KmerHashTable_T, SizeOfKmerHashTable_T);
			if (HasHAddress_RE != -1) {
				return 1;
			}
		}
		if (RE_KmerStr.charAt(0) == 'G') {
			HasHAddress_RE = getHashUnit(reverse(KmerString), KmerHashTable_G, SizeOfKmerHashTable_G);
			if (HasHAddress_RE != -1) {
				return 1;
			}
		}
		if (RE_KmerStr.charAt(0) == 'C') {
			HasHAddress_RE = getHashUnit(reverse(KmerString), KmerHashTable_C, SizeOfKmerHashTable_C);
			if (HasHAddress_RE != -1) {
				return 1;
			}
		}
		if (FR_KmerStr.charAt(0) == 'A') {
			HasHAddress_FR = getHashUnit(KmerString, KmerHashTable_A, SizeOfKmerHashTable_A);
			if (HasHAddress_FR != -1) {
				return 1;
			}
		}
		if (FR_KmerStr.charAt(0) == 'T') {
			HasHAddress_FR = getHashUnit(KmerString, KmerHashTable_T, SizeOfKmerHashTable_T);
			if (HasHAddress_FR != -1) {
				return 1;
			}
		}
		if (FR_KmerStr.charAt(0) == 'G') {
			HasHAddress_FR = getHashUnit(KmerString, KmerHashTable_G, SizeOfKmerHashTable_G);
			if (HasHAddress_FR != -1) {
				return 1;
			}
		}
		if (FR_KmerStr.charAt(0) == 'C') {
			HasHAddress_FR = getHashUnit(KmerString, KmerHashTable_C, SizeOfKmerHashTable_C);
			if (HasHAddress_FR != -1) {
				return 1;
			}
		}
		return -1;
	}
	//Get low depth reads
	public static void GetLowDepthReads(String ReadSetPath, int Trime_Size) {
		try {
			int LowNum_Fasta = 0;
			int NormalNum_Fasta = 0;
			int LowNum_Fastq = 0;
			int NormalNum_Fastq = 0;
			String readtemp1 = "";
			String readtemp2 = "";
			String encoding = "utf-8";
			File file1 = new File(ReadSetPath + "Short1.left.Marked.fasta");
			File file2 = new File(ReadSetPath + "Short1.right.Marked.fasta");
			if (file1.isFile() && file1.exists() && file2.isFile() && file2.exists()) {
				InputStreamReader read1 = new InputStreamReader(new FileInputStream(file1), encoding);
				BufferedReader bufferedReader1 = new BufferedReader(read1);
				InputStreamReader read2 = new InputStreamReader(new FileInputStream(file2), encoding);
				BufferedReader bufferedReader2 = new BufferedReader(read2);
				while ((readtemp1 = bufferedReader1.readLine()) != null
						&& (readtemp2 = bufferedReader2.readLine()) != null) {
					if (readtemp1.charAt(0) != '>' && readtemp2.charAt(0) != '>') {
						if (readtemp1.charAt(0) == '#') {
							if (readtemp2.charAt(0) == '#') {
								String[] SplitLine1 = readtemp1.split("\t|\\s+");
								String[] SplitLine2 = readtemp2.split("\t|\\s+");
								String FastqStr_Left = "@" + (LowNum_Fastq++) + "\n"
										+ SplitLine1[0].substring(1, SplitLine1[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine1[1].substring(0, SplitLine1[1].length() - Trime_Size) + "\n";
								String FastqStr_Right = "@" + (LowNum_Fastq++) + "\n"
										+ SplitLine2[0].substring(1, SplitLine2[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine2[1].substring(0, SplitLine2[1].length() - Trime_Size) + "\n";
								//Low FASTQ.
								FileWriter writer1 = new FileWriter(ReadSetPath + "LowDepthFastq.fastq", true);
								writer1.write(FastqStr_Left + FastqStr_Right);
								writer1.close();
								//Low FASTA.
								FileWriter writer2 = new FileWriter(ReadSetPath + "LowDepthFasta.fasta", true);
								writer2.write(">" + (LowNum_Fasta++) + "\n"
										+ SplitLine1[0].substring(1, SplitLine1[0].length() - Trime_Size) + "\n" + ">"
										+ (LowNum_Fasta++) + "\n"
										+ SplitLine2[0].substring(1, SplitLine2[0].length() - Trime_Size) + "\n");
								writer2.close();
							} else if (readtemp2.charAt(0) != '#') {
								//Low FASTQ.
								String[] SplitLine1 = readtemp1.split("\t|\\s+");
								String[] SplitLine2 = readtemp2.split("\t|\\s+");
								String FastqStr_Left = "@" + (LowNum_Fastq++) + "\n"
										+ SplitLine1[0].substring(1, SplitLine1[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine1[1].substring(0, SplitLine1[1].length() - Trime_Size) + "\n";
								String FastqStr_Right = "@" + (LowNum_Fastq++) + "\n"
										+ SplitLine2[0].substring(0, SplitLine2[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine2[1].substring(0, SplitLine2[1].length() - Trime_Size) + "\n";
								//Mark.
								String RepStr = "N";
								for (int h = 1; h < SplitLine2[0].length() - Trime_Size; h++) {
									RepStr += "N";
								}
								//Low FASTQ.
								FileWriter writer1 = new FileWriter(ReadSetPath + "LowDepthFastq.fastq", true);
								writer1.write(FastqStr_Left + FastqStr_Right);
								writer1.close();
								//Low FASTA.
								FileWriter writer2 = new FileWriter(ReadSetPath + "LowDepthFasta.fasta", true);
								writer2.write(">" + (LowNum_Fasta++) + "\n"
										+ SplitLine1[0].substring(1, SplitLine1[0].length() - Trime_Size) + "\n" + ">"
										+ (LowNum_Fasta++) + "\n"
										+ SplitLine2[0].substring(0, SplitLine2[0].length() - Trime_Size) + "\n");
								writer2.close();
								//Normal.
								FileWriter writer3 = new FileWriter(ReadSetPath + "NormalDepthFasta.fasta", true);
								writer3.write(">" + (NormalNum_Fasta++) + "\n" + RepStr + "\n" + ">"
										+ (NormalNum_Fasta++) + "\n"
										+ SplitLine2[0].substring(0, SplitLine2[0].length() - Trime_Size) + "\n");
								writer3.close();
							}
						} else if (readtemp1.charAt(0) != '#') {
							if (readtemp2.charAt(0) == '#') {
								//Low FASTQ.
								String[] SplitLine1 = readtemp1.split("\t|\\s+");
								String[] SplitLine2 = readtemp2.split("\t|\\s+");
								String FastqStr_Left = "@" + (LowNum_Fastq++) + "\n"
										+ SplitLine1[0].substring(0, SplitLine1[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine1[1].substring(0, SplitLine1[1].length() - Trime_Size) + "\n";
								String FastqStr_Right = "@" + (LowNum_Fastq++) + "\n"
										+ SplitLine2[0].substring(1, SplitLine2[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine2[1].substring(0, SplitLine2[1].length() - Trime_Size) + "\n";
								//Mark.
								String RepStr = "N";
								for (int h = 1; h < SplitLine1[0].length() - Trime_Size; h++) {
									RepStr += "N";
								}
								//Low FASTQ.
								FileWriter writer1 = new FileWriter(ReadSetPath + "LowDepthFastq.fastq", true);
								writer1.write(FastqStr_Left + FastqStr_Right);
								writer1.close();
								//Low FASTA.
								FileWriter writer2 = new FileWriter(ReadSetPath + "LowDepthFasta.fasta", true);
								writer2.write(">" + (LowNum_Fasta++) + "\n"
										+ SplitLine1[0].substring(0, SplitLine1[0].length() - Trime_Size) + "\n" + ">"
										+ (LowNum_Fasta++) + "\n"
										+ SplitLine2[0].substring(1, SplitLine2[0].length() - Trime_Size) + "\n");
								writer2.close();
								//Normal.
								FileWriter writer3 = new FileWriter(ReadSetPath + "NormalDepthFasta.fasta", true);
								writer3.write(">" + (NormalNum_Fasta++) + "\n"
										+ SplitLine1[0].substring(0, SplitLine1[0].length() - Trime_Size) + "\n" + ">"
										+ (NormalNum_Fasta++) + "\n" + RepStr + "\n");
								writer3.close();
							} else if (readtemp2.charAt(0) != '#') {
								//Low FASTQ.
								String[] SplitLine1 = readtemp1.split("\t|\\s+");
								String[] SplitLine2 = readtemp2.split("\t|\\s+");
								String FastqStr_Left = "@" + (NormalNum_Fastq++) + "\n"
										+ SplitLine1[0].substring(0, SplitLine1[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine1[1].substring(0, SplitLine1[1].length() - Trime_Size) + "\n";
								String FastqStr_Right = "@" + (NormalNum_Fastq++) + "\n"
										+ SplitLine2[0].substring(0, SplitLine2[0].length() - Trime_Size) + "\n" + "+"
										+ "\n" + SplitLine2[1].substring(0, SplitLine2[1].length() - Trime_Size) + "\n";
								//Low FASTQ.
								FileWriter writer1 = new FileWriter(ReadSetPath + "NormalDepthFastq.fastq", true);
								writer1.write(FastqStr_Left + FastqStr_Right);
								writer1.close();
								//Low FASTA.
								FileWriter writer2 = new FileWriter(ReadSetPath + "NormalDepthFasta.fasta", true);
								writer2.write(">" + (NormalNum_Fasta++) + "\n"
										+ SplitLine1[0].substring(0, SplitLine1[0].length() - Trime_Size) + "\n" + ">"
										+ (NormalNum_Fasta++) + "\n"
										+ SplitLine2[0].substring(0, SplitLine2[0].length() - Trime_Size) + "\n");
								writer2.close();
							}
						}
					}
				}
				bufferedReader1.close();
				bufferedReader2.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
	}
	//Get the average quality score of read.
	public static String GetAverageQualityScoreOfRead(String QualityStringofRead) {
		int Sum = 0;
		double dd = 0;
		for (int k = 0; k < QualityStringofRead.length(); k++) {
			Sum += (int) (QualityStringofRead.charAt(k));
		}
		dd = (double) Sum / QualityStringofRead.length();
		return Changeformat(dd);
	}
	//Get the average quality score of read.
	public static double Double_GetAverageQualityScoreOfRead(String QualityStringofRead) {
		int Sum = 0;
		for (int k = 0; k < QualityStringofRead.length(); k++) {
			Sum += (int) (QualityStringofRead.charAt(k));
		}
		return (double) Sum / QualityStringofRead.length();
	}
	//Generate FastqFile array.
	public static void FastqFile_Array(String FastqFilePath, String FinalWritePath, double QSth) {
		int Index = 0;
		try {
			String encoding = "utf-8";
			File file = new File(FastqFilePath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((bufferedReader.readLine()) != null) {
					String Line1 = bufferedReader.readLine();
					bufferedReader.readLine();
					String Line2 = bufferedReader.readLine();
					if (Double_GetAverageQualityScoreOfRead(Line2) > QSth) {
						FileWriter writer1 = new FileWriter(FinalWritePath, true);
						writer1.write("@" + (Index++) + "\n" + Line1 + "\n" + "+" + "\n" + Line2 + "\n");
						writer1.close();
					} else {
						String ReplaceStr = "N";
						String ReplaceQS = "$";
						for (int w = 0; w < Line1.length() - 1; w++) {
							ReplaceStr += "N";
							ReplaceQS += "$";
						}
						FileWriter writer1 = new FileWriter(FinalWritePath, true);
						writer1.write("@" + (Index++) + "\n" + ReplaceStr + "\n" + "+" + "\n" + ReplaceQS + "\n");
						writer1.close();
					}
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
	}
	//Load FASTQ to array.
	public static int LoadFastqToArray(String ReadSetPath, String[] ReadSetArray) {
		int count = 0;
		try {
			String encoding = "utf-8";
			File file = new File(ReadSetPath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((bufferedReader.readLine()) != null) {
					ReadSetArray[count++] = bufferedReader.readLine();
					bufferedReader.readLine();
					bufferedReader.readLine();
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
		return count;
	}
	//Change lines of CONTIGS file.
	public static String readContigFile(String ContigPath) throws IOException {
		int LineCount = 0;
		int LineNum = 0;
		String encoding = "utf-8";
		String readtemp = "";
		String ReadTemp = "";
		try {
			File file = new File(ContigPath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding); 
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((readtemp = bufferedReader.readLine()) != null) {
					if (readtemp.charAt(0) == '>' && LineCount == 0) {
						ReadTemp = ">NODE_" + (LineNum++) + "_Length" + "\n";
						LineCount++;
					} else {
						if (readtemp.charAt(0) == '>') {
							ReadTemp += "\n" + ">NODE_" + (LineNum++) + "_Length" + "\n";
						} else {
							ReadTemp += readtemp;
						}
					}
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
		return ReadTemp;
	}
	//Get FASTA Head.
	public static int GetFastaHead(String ReadSetPath, int[] ReadSetArray) {
		int count = 0;
		String readtemp = "";
		try {
			String encoding = "utf-8";
			File file = new File(ReadSetPath);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((readtemp = bufferedReader.readLine()) != null) {
					if(readtemp.charAt(0) == '>')
					{
						String[] SplitLine1 = readtemp.split("--");
					    ReadSetArray[count++] = Integer.parseInt(SplitLine1[0].substring(1, SplitLine1[0].length()));
					}
				}
				bufferedReader.close();
			} else {
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
		return count;
	}
	//Get reverse string.
	public static String reverseString(String str){ 
		StringBuffer stringBuffer = new StringBuffer (str);
		StringBuffer Str=stringBuffer.reverse();
		return  Str.toString();
    }
	//Add common functions.
}
//Generate FASTA From FASTQ.
class GenerateFastaFromFastqFiles {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String short_1_A_Path = args[0];
		String short_1_B_Path = args[1];
		String OutPutPath = args[2];
		String DataSetName = args[3];
		try {
			int Num=0;
			String readtemp1="";
			String readtemp2="";
			int Short1_NumLeft=0;
			int Short1_NumRight=0;
			String encoding = "utf-8";
			File file1 = new File(short_1_A_Path);
			File file2 = new File(short_1_B_Path);
			if (file1.exists()&&file2.exists()) {
				InputStreamReader read1 = new InputStreamReader(new FileInputStream(file1), encoding);
				InputStreamReader read2 = new InputStreamReader(new FileInputStream(file2), encoding);
				BufferedReader bufferedReader1 = new BufferedReader(read1);
				BufferedReader bufferedReader2 = new BufferedReader(read2);
				while ((bufferedReader1.readLine())!=null && (bufferedReader2.readLine())!= null)
				{
					//The second line.
                    readtemp1=bufferedReader1.readLine();
                    readtemp2=bufferedReader2.readLine();						
				    //Write left.
					FileWriter writer1 = new FileWriter(OutPutPath + DataSetName+".left.fasta",true);
					writer1.write(">"+(Short1_NumLeft++)+"\n"+readtemp1+"\n");
					writer1.close();
					//Write right.	
					FileWriter writer2 = new FileWriter(OutPutPath + DataSetName+".right.fasta",true);
					writer2.write(">"+(Short1_NumRight++)+"\n"+readtemp2+"\n");
					writer2.close();
					//Write all.	
					FileWriter writer3 = new FileWriter(OutPutPath + DataSetName+".fasta", true);
					writer3.write(">"+(Num)+"\n"+readtemp1 +"\n"+">"+(Num)+"\n"+readtemp2+"\n");
					writer3.close();
					//The third line.
					bufferedReader1.readLine();
					bufferedReader2.readLine();
				    //The fourth line.
					bufferedReader1.readLine();
					bufferedReader2.readLine();
				}
				bufferedReader1.close();
				bufferedReader2.close();
			} 
			else 
			{
				System.out.println("File is not exist!");
			}
		} catch (Exception e) {
			System.out.println("Error liaoxingyu");
			e.printStackTrace();
		}
	}
}
class FilterKmerStaticsModify {
	public static void main(String[] args) throws IOException {
		//Get Depth Start.
		String DSKPath = args[0];
		String LogPath = args[1];
		String ThreLowDepth = args[2];
		String LowFreKmerWrite = args[3];
		double LowDepthThreshold = Double.parseDouble(ThreLowDepth);
		double First_estimate = 0;
		//Loading.
		int SizeOfkmerStatisticFile = CommonClass.getFileLines(DSKPath);
		String KmerStatisticArray[] = new String[SizeOfkmerStatisticFile];
		int RealSizeOfKmerStatisticArray = CommonClass.FileToArray(DSKPath, KmerStatisticArray);
		System.out.print("K-mer size:" + RealSizeOfKmerStatisticArray + "\t");
		First_estimate = CommonClass.FirstEstimate(LogPath);
		System.out.println("Low Depth Threshold:" + LowDepthThreshold * First_estimate);
		//Write AverageCoverage.
		String WriteStr = String.valueOf(LowDepthThreshold * First_estimate);
		FileWriter writer1 = new FileWriter(LowFreKmerWrite + "LowFreThreshold.txt", true);
		writer1.write(WriteStr);
		writer1.close();
		//over.
		int Lines = 0;
		for (int t = 0; t < RealSizeOfKmerStatisticArray; t++) {
			String[] linesplit = KmerStatisticArray[t].split("\t|\\s+");
			double CurrentRate = Double.parseDouble(linesplit[1]);
			if (CurrentRate <= LowDepthThreshold * First_estimate) {
				FileWriter writer2 = new FileWriter(LowFreKmerWrite + "LowDepthKmer.fa", true);
				writer2.write(">" + (Lines++) + "\n" + linesplit[0] + "\n");
				writer2.close();
			}
		}
		//Free
		KmerStatisticArray = null;
	}
}
//Generate blocked KMER HashTable.
class GenerateBlockedKmerHashTable {
	public static void main(String[] args) throws IOException {
		String UniqueKmerPath = args[0];
		String BlockedKmerSetWritePath = args[1];
		//KmerToHashTable.
		int KmerLines = CommonClass.getFileLines(UniqueKmerPath);
		String KmerArray[] = new String[KmerLines];
		int RealKmerLines = CommonClass.FileToArray(UniqueKmerPath, KmerArray);
		for (int g = 0; g < RealKmerLines; g++) {
			if (KmerArray[g].charAt(0) == 'A') {
				FileWriter writer = new FileWriter(BlockedKmerSetWritePath + "KmerSet_A.fa", true);
				writer.write(KmerArray[g] + "\n");
				writer.close();
			} else if (KmerArray[g].charAt(0) == 'T') {
				FileWriter writer = new FileWriter(BlockedKmerSetWritePath + "KmerSet_T.fa", true);
				writer.write(KmerArray[g] + "\n");
				writer.close();
			} else if (KmerArray[g].charAt(0) == 'G') {
				FileWriter writer = new FileWriter(BlockedKmerSetWritePath + "KmerSet_G.fa", true);
				writer.write(KmerArray[g] + "\n");
				writer.close();
			} else if (KmerArray[g].charAt(0) == 'C') {
				FileWriter writer = new FileWriter(BlockedKmerSetWritePath + "KmerSet_C.fa", true);
				writer.write(KmerArray[g] + "\n");
				writer.close();
			}
		}
		//Free.
		KmerArray = null;
	}
}
//Paired-end read trimming.
class peTrimer {
	public static void main(String[] args) throws IOException, Exception, ExecutionException {
		//TODO Auto-generated method stub
		String UniqueKmerPath = args[0];
		String WindowSize = args[1];
		String FastqFilePath = args[2];
		String ReadWritePath = args[3];
		//Loading file.
		int SizeOfFastqFile = CommonClass.getFileLines(FastqFilePath) / 4;
		String FastqArray[] = new String[SizeOfFastqFile];
		int RealSizeFastqArray = CommonClass.FastqToArray(FastqFilePath, FastqArray);
		System.out.println("RealSizeFastaArray:" + RealSizeFastqArray);
		//Array.
		int SizeOfHash_A = CommonClass.getFileLines(UniqueKmerPath + "KmerSet_A.fa") + 100000;
		String KmerHashTable_A[][] = new String[SizeOfHash_A][2];
		int SizeOfHash_T = CommonClass.getFileLines(UniqueKmerPath + "KmerSet_T.fa") + 100000;
		String KmerHashTable_T[][] = new String[SizeOfHash_T][2];
		int SizeOfHash_G = CommonClass.getFileLines(UniqueKmerPath + "KmerSet_G.fa") + 100000;
		String KmerHashTable_G[][] = new String[SizeOfHash_G][2];
		int SizeOfHash_C = CommonClass.getFileLines(UniqueKmerPath + "KmerSet_C.fa") + 100000;
		String KmerHashTable_C[][] = new String[SizeOfHash_C][2];
		//initialization
		for (int t = 0; t < SizeOfHash_A; t++) {
			KmerHashTable_A[t][0] = null;
			KmerHashTable_A[t][1] = null;
		}
		for (int t = 0; t < SizeOfHash_T; t++) {
			KmerHashTable_T[t][0] = null;
			KmerHashTable_T[t][1] = null;
		}
		for (int t = 0; t < SizeOfHash_G; t++) {
			KmerHashTable_G[t][0] = null;
			KmerHashTable_G[t][1] = null;
		}
		for (int t = 0; t < SizeOfHash_C; t++) {
			KmerHashTable_C[t][0] = null;
			KmerHashTable_C[t][1] = null;
		}
		CommonClass.KmerFileToHash(UniqueKmerPath + "KmerSet_A.fa", KmerHashTable_A, SizeOfHash_A);
		CommonClass.KmerFileToHash(UniqueKmerPath + "KmerSet_T.fa", KmerHashTable_T, SizeOfHash_T);
		CommonClass.KmerFileToHash(UniqueKmerPath + "KmerSet_G.fa", KmerHashTable_G, SizeOfHash_G);
		CommonClass.KmerFileToHash(UniqueKmerPath + "KmerSet_C.fa", KmerHashTable_C, SizeOfHash_C);
		//Multiple Threads.
		ExecutorService pool_1 = Executors.newFixedThreadPool(48);
		int scount = RealSizeFastqArray;
		int SplitSize = RealSizeFastqArray / 48;
		int windowSize = Integer.parseInt(WindowSize);
		@SuppressWarnings("rawtypes")
		Callable c1_1 = new RemoveTechSeqByMultipleThreads(0, windowSize, scount, FastqArray, SplitSize, 0,
				SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_2 = new RemoveTechSeqByMultipleThreads(1, windowSize, scount, FastqArray, SplitSize, SplitSize,
				2 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_3 = new RemoveTechSeqByMultipleThreads(2, windowSize, scount, FastqArray, SplitSize, 2 * SplitSize,
				3 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_4 = new RemoveTechSeqByMultipleThreads(3, windowSize, scount, FastqArray, SplitSize, 3 * SplitSize,
				4 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_5 = new RemoveTechSeqByMultipleThreads(4, windowSize, scount, FastqArray, SplitSize, 4 * SplitSize,
				5 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_6 = new RemoveTechSeqByMultipleThreads(5, windowSize, scount, FastqArray, SplitSize, 5 * SplitSize,
				6 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_7 = new RemoveTechSeqByMultipleThreads(6, windowSize, scount, FastqArray, SplitSize, 6 * SplitSize,
				7 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_8 = new RemoveTechSeqByMultipleThreads(7, windowSize, scount, FastqArray, SplitSize, 7 * SplitSize,
				8 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_9 = new RemoveTechSeqByMultipleThreads(8, windowSize, scount, FastqArray, SplitSize, 8 * SplitSize,
				9 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_10 = new RemoveTechSeqByMultipleThreads(9, windowSize, scount, FastqArray, SplitSize, 9 * SplitSize,
				10 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T, KmerHashTable_G,
				SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_11 = new RemoveTechSeqByMultipleThreads(10, windowSize, scount, FastqArray, SplitSize,
				10 * SplitSize, 11 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_12 = new RemoveTechSeqByMultipleThreads(11, windowSize, scount, FastqArray, SplitSize,
				11 * SplitSize, 12 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_13 = new RemoveTechSeqByMultipleThreads(12, windowSize, scount, FastqArray, SplitSize,
				12 * SplitSize, 13 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_14 = new RemoveTechSeqByMultipleThreads(13, windowSize, scount, FastqArray, SplitSize,
				13 * SplitSize, 14 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_15 = new RemoveTechSeqByMultipleThreads(14, windowSize, scount, FastqArray, SplitSize,
				14 * SplitSize, 15 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_16 = new RemoveTechSeqByMultipleThreads(15, windowSize, scount, FastqArray, SplitSize,
				15 * SplitSize, 16 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_17 = new RemoveTechSeqByMultipleThreads(16, windowSize, scount, FastqArray, SplitSize,
				16 * SplitSize, 17 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_18 = new RemoveTechSeqByMultipleThreads(17, windowSize, scount, FastqArray, SplitSize,
				17 * SplitSize, 18 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_19 = new RemoveTechSeqByMultipleThreads(18, windowSize, scount, FastqArray, SplitSize,
				18 * SplitSize, 19 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_20 = new RemoveTechSeqByMultipleThreads(19, windowSize, scount, FastqArray, SplitSize,
				19 * SplitSize, 20 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_21 = new RemoveTechSeqByMultipleThreads(20, windowSize, scount, FastqArray, SplitSize,
				20 * SplitSize, 21 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_22 = new RemoveTechSeqByMultipleThreads(21, windowSize, scount, FastqArray, SplitSize,
				21 * SplitSize, 22 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_23 = new RemoveTechSeqByMultipleThreads(22, windowSize, scount, FastqArray, SplitSize,
				22 * SplitSize, 23 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_24 = new RemoveTechSeqByMultipleThreads(23, windowSize, scount, FastqArray, SplitSize,
				23 * SplitSize, 24 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_25 = new RemoveTechSeqByMultipleThreads(24, windowSize, scount, FastqArray, SplitSize,
				24 * SplitSize, 25 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_26 = new RemoveTechSeqByMultipleThreads(25, windowSize, scount, FastqArray, SplitSize,
				25 * SplitSize, 26 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_27 = new RemoveTechSeqByMultipleThreads(26, windowSize, scount, FastqArray, SplitSize,
				26 * SplitSize, 27 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_28 = new RemoveTechSeqByMultipleThreads(27, windowSize, scount, FastqArray, SplitSize,
				27 * SplitSize, 28 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_29 = new RemoveTechSeqByMultipleThreads(28, windowSize, scount, FastqArray, SplitSize,
				28 * SplitSize, 29 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_30 = new RemoveTechSeqByMultipleThreads(29, windowSize, scount, FastqArray, SplitSize,
				29 * SplitSize, 30 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_31 = new RemoveTechSeqByMultipleThreads(30, windowSize, scount, FastqArray, SplitSize,
				30 * SplitSize, 31 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_32 = new RemoveTechSeqByMultipleThreads(31, windowSize, scount, FastqArray, SplitSize,
				31 * SplitSize, 32 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_33 = new RemoveTechSeqByMultipleThreads(32, windowSize, scount, FastqArray, SplitSize,
				32 * SplitSize, 33 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_34 = new RemoveTechSeqByMultipleThreads(33, windowSize, scount, FastqArray, SplitSize,
				33 * SplitSize, 34 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_35 = new RemoveTechSeqByMultipleThreads(34, windowSize, scount, FastqArray, SplitSize,
				34 * SplitSize, 35 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_36 = new RemoveTechSeqByMultipleThreads(35, windowSize, scount, FastqArray, SplitSize,
				35 * SplitSize, 36 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_37 = new RemoveTechSeqByMultipleThreads(36, windowSize, scount, FastqArray, SplitSize,
				36 * SplitSize, 37 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_38 = new RemoveTechSeqByMultipleThreads(37, windowSize, scount, FastqArray, SplitSize,
				37 * SplitSize, 38 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_39 = new RemoveTechSeqByMultipleThreads(38, windowSize, scount, FastqArray, SplitSize,
				38 * SplitSize, 39 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_40 = new RemoveTechSeqByMultipleThreads(39, windowSize, scount, FastqArray, SplitSize,
				39 * SplitSize, 40 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_41 = new RemoveTechSeqByMultipleThreads(40, windowSize, scount, FastqArray, SplitSize,
				40 * SplitSize, 41 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_42 = new RemoveTechSeqByMultipleThreads(41, windowSize, scount, FastqArray, SplitSize,
				41 * SplitSize, 42 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_43 = new RemoveTechSeqByMultipleThreads(42, windowSize, scount, FastqArray, SplitSize,
				42 * SplitSize, 43 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_44 = new RemoveTechSeqByMultipleThreads(43, windowSize, scount, FastqArray, SplitSize,
				43 * SplitSize, 44 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_45 = new RemoveTechSeqByMultipleThreads(44, windowSize, scount, FastqArray, SplitSize,
				44 * SplitSize, 45 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_46 = new RemoveTechSeqByMultipleThreads(45, windowSize, scount, FastqArray, SplitSize,
				45 * SplitSize, 46 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_47 = new RemoveTechSeqByMultipleThreads(46, windowSize, scount, FastqArray, SplitSize,
				46 * SplitSize, 47 * SplitSize - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings("rawtypes")
		Callable c1_48 = new RemoveTechSeqByMultipleThreads(47, windowSize, scount, FastqArray, SplitSize,
				47 * SplitSize, scount - 1, KmerHashTable_A, SizeOfHash_A, KmerHashTable_T, SizeOfHash_T,
				KmerHashTable_G, SizeOfHash_G, KmerHashTable_C, SizeOfHash_C);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_1 = pool_1.submit(c1_1);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_2 = pool_1.submit(c1_2);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_3 = pool_1.submit(c1_3);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_4 = pool_1.submit(c1_4);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_5 = pool_1.submit(c1_5);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_6 = pool_1.submit(c1_6);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_7 = pool_1.submit(c1_7);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_8 = pool_1.submit(c1_8);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_9 = pool_1.submit(c1_9);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_10 = pool_1.submit(c1_10);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_11 = pool_1.submit(c1_11);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_12 = pool_1.submit(c1_12);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_13 = pool_1.submit(c1_13);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_14 = pool_1.submit(c1_14);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_15 = pool_1.submit(c1_15);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_16 = pool_1.submit(c1_16);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_17 = pool_1.submit(c1_17);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_18 = pool_1.submit(c1_18);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_19 = pool_1.submit(c1_19);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_20 = pool_1.submit(c1_20);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_21 = pool_1.submit(c1_21);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_22 = pool_1.submit(c1_22);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_23 = pool_1.submit(c1_23);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_24 = pool_1.submit(c1_24);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_25 = pool_1.submit(c1_25);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_26 = pool_1.submit(c1_26);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_27 = pool_1.submit(c1_27);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_28 = pool_1.submit(c1_28);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_29 = pool_1.submit(c1_29);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_30 = pool_1.submit(c1_30);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_31 = pool_1.submit(c1_31);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_32 = pool_1.submit(c1_32);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_33 = pool_1.submit(c1_33);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_34 = pool_1.submit(c1_34);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_35 = pool_1.submit(c1_35);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_36 = pool_1.submit(c1_36);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_37 = pool_1.submit(c1_37);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_38 = pool_1.submit(c1_38);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_39 = pool_1.submit(c1_39);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_40 = pool_1.submit(c1_40);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_41 = pool_1.submit(c1_41);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_42 = pool_1.submit(c1_42);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_43 = pool_1.submit(c1_43);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_44 = pool_1.submit(c1_44);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_45 = pool_1.submit(c1_45);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_46 = pool_1.submit(c1_46);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_47 = pool_1.submit(c1_47);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Future f1_48 = pool_1.submit(c1_48);
		//Future
		if (f1_1.get().toString() != null) {
			System.out.println(f1_1.get().toString());
		}
		if (f1_2.get().toString() != null) {
			System.out.println(f1_2.get().toString());
		}
		if (f1_3.get().toString() != null) {
			System.out.println(f1_3.get().toString());
		}
		if (f1_4.get().toString() != null) {
			System.out.println(f1_4.get().toString());
		}
		if (f1_5.get().toString() != null) {
			System.out.println(f1_5.get().toString());
		}
		if (f1_6.get().toString() != null) {
			System.out.println(f1_6.get().toString());
		}
		if (f1_7.get().toString() != null) {
			System.out.println(f1_7.get().toString());
		}
		if (f1_8.get().toString() != null) {
			System.out.println(f1_8.get().toString());
		}
		if (f1_9.get().toString() != null) {
			System.out.println(f1_9.get().toString());
		}
		if (f1_10.get().toString() != null) {
			System.out.println(f1_10.get().toString());
		}
		if (f1_11.get().toString() != null) {
			System.out.println(f1_11.get().toString());
		}
		if (f1_12.get().toString() != null) {
			System.out.println(f1_12.get().toString());
		}
		if (f1_13.get().toString() != null) {
			System.out.println(f1_13.get().toString());
		}
		if (f1_14.get().toString() != null) {
			System.out.println(f1_14.get().toString());
		}
		if (f1_15.get().toString() != null) {
			System.out.println(f1_15.get().toString());
		}
		if (f1_16.get().toString() != null) {
			System.out.println(f1_16.get().toString());
		}
		if (f1_17.get().toString() != null) {
			System.out.println(f1_17.get().toString());
		}
		if (f1_18.get().toString() != null) {
			System.out.println(f1_18.get().toString());
		}
		if (f1_19.get().toString() != null) {
			System.out.println(f1_19.get().toString());
		}
		if (f1_20.get().toString() != null) {
			System.out.println(f1_20.get().toString());
		}
		if (f1_21.get().toString() != null) {
			System.out.println(f1_21.get().toString());
		}
		if (f1_22.get().toString() != null) {
			System.out.println(f1_22.get().toString());
		}
		if (f1_23.get().toString() != null) {
			System.out.println(f1_23.get().toString());
		}
		if (f1_24.get().toString() != null) {
			System.out.println(f1_24.get().toString());
		}
		if (f1_25.get().toString() != null) {
			System.out.println(f1_25.get().toString());
		}
		if (f1_26.get().toString() != null) {
			System.out.println(f1_26.get().toString());
		}
		if (f1_27.get().toString() != null) {
			System.out.println(f1_27.get().toString());
		}
		if (f1_28.get().toString() != null) {
			System.out.println(f1_28.get().toString());
		}
		if (f1_29.get().toString() != null) {
			System.out.println(f1_29.get().toString());
		}
		if (f1_30.get().toString() != null) {
			System.out.println(f1_30.get().toString());
		}
		if (f1_31.get().toString() != null) {
			System.out.println(f1_31.get().toString());
		}
		if (f1_32.get().toString() != null) {
			System.out.println(f1_32.get().toString());
		}
		if (f1_33.get().toString() != null) {
			System.out.println(f1_33.get().toString());
		}
		if (f1_34.get().toString() != null) {
			System.out.println(f1_34.get().toString());
		}
		if (f1_35.get().toString() != null) {
			System.out.println(f1_35.get().toString());
		}
		if (f1_36.get().toString() != null) {
			System.out.println(f1_36.get().toString());
		}
		if (f1_37.get().toString() != null) {
			System.out.println(f1_37.get().toString());
		}
		if (f1_38.get().toString() != null) {
			System.out.println(f1_38.get().toString());
		}
		if (f1_39.get().toString() != null) {
			System.out.println(f1_39.get().toString());
		}
		if (f1_40.get().toString() != null) {
			System.out.println(f1_40.get().toString());
		}
		if (f1_41.get().toString() != null) {
			System.out.println(f1_41.get().toString());
		}
		if (f1_42.get().toString() != null) {
			System.out.println(f1_42.get().toString());
		}
		if (f1_43.get().toString() != null) {
			System.out.println(f1_43.get().toString());
		}
		if (f1_44.get().toString() != null) {
			System.out.println(f1_44.get().toString());
		}
		if (f1_45.get().toString() != null) {
			System.out.println(f1_45.get().toString());
		}
		if (f1_46.get().toString() != null) {
			System.out.println(f1_46.get().toString());
		}
		if (f1_47.get().toString() != null) {
			System.out.println(f1_47.get().toString());
		}
		if (f1_48.get().toString() != null) {
			System.out.println(f1_48.get().toString());
		}
		System.out.println("Classification completed!");
		//Write.
		int Lines = 0;
		for (int v = 0; v < RealSizeFastqArray; v++) {
			FileWriter writer = new FileWriter(ReadWritePath, true);
			writer.write(">" + (Lines++) + "\n" + FastqArray[v] + "\n");
			writer.close();
		}
		//shutdown pool.
		FastqArray = null;
		KmerHashTable_A = null;
		KmerHashTable_T = null;
		KmerHashTable_G = null;
		KmerHashTable_C = null;
		pool_1.shutdown();
	}
}
//MarkReads.
class RemoveTechSeqByMultipleThreads implements Callable<Object> {

	int Index = 0;
	int Start;
	int end;
	int RemoveRate = 0;
	int ArraySize = 0;
	int SizeOfHashTable_A = 0;
	int SizeOfHashTable_T = 0;
	int SizeOfHashTable_G = 0;
	int SizeOfHashTable_C = 0;
	int windowsize = 0;
	int SplitSize = 0;
	String ReadSetArray[];
	String KmerFreHashTable_A[][];
	String KmerFreHashTable_T[][];
	String KmerFreHashTable_G[][];
	String KmerFreHashTable_C[][];
	String encoding = "utf-8";
	String ReplaceStr = "";
	String CheckStr = "";
	//Construction.
	public RemoveTechSeqByMultipleThreads(int index, int Windowsize, int scount, String readSetArray[], int splitSize,
			int StartPosition, int Endposition, String kmerFreHashTable_A[][], int sizeofHashTable_A,
			String kmerFreHashTable_T[][], int sizeofHashTable_T, String kmerFreHashTable_G[][], int sizeofHashTable_G,
			String kmerFreHashTable_C[][], int sizeofHashTable_C) {
		Start = StartPosition;
		end = Endposition;
		ReadSetArray = readSetArray;
		KmerFreHashTable_A = kmerFreHashTable_A;
		SizeOfHashTable_A = sizeofHashTable_A;
		KmerFreHashTable_T = kmerFreHashTable_T;
		SizeOfHashTable_T = sizeofHashTable_T;
		KmerFreHashTable_G = kmerFreHashTable_G;
		SizeOfHashTable_G = sizeofHashTable_G;
		KmerFreHashTable_C = kmerFreHashTable_C;
		SizeOfHashTable_C = sizeofHashTable_C;
		ArraySize = scount;
		windowsize = Windowsize;
		Index = index;
		SplitSize = splitSize;
	}
	//Run.
	public String call() throws IOException {
		//Mark Reads.
		for (int i = Start; i <= end; i++) {
			int CountOfLow = 0;
			String[] SplitLine = ReadSetArray[i].split("\t|\\s+");
			for (int k = 0; k <= SplitLine[0].length() - windowsize; k++) {
				if (CommonClass.CalculateAverageKmerScore(SplitLine[0].substring(k, k + windowsize), KmerFreHashTable_A,
						SizeOfHashTable_A, KmerFreHashTable_T, SizeOfHashTable_T, KmerFreHashTable_G, SizeOfHashTable_G,
						KmerFreHashTable_C, SizeOfHashTable_C) != -1) {
					CountOfLow++;
				}
				if (CountOfLow >= (SplitLine[0].length() - windowsize + 1) / 20) {
					ReadSetArray[i] = "#" + ReadSetArray[i];
					break;
				}
			}
			/*
			//Output Process rate.
			if (i % 10000 == 0) {
				System.out.println("Remove-rate:" + Thread.currentThread().getName() + "->"
						+ CommonClass.Changeformat(100 * ((double) RemoveRate / SplitSize)) + "%");
			}
			RemoveRate++;
			*/
		}
		return "The thread:" + Index + "->running completed!";
	}
}
//Filter Low Depth Reads.
class FilterLowDepthReads2 {
	public static void main(String[] args) throws IOException {
		String ReadSetPath = args[0];
		String TrimeSize = args[1];
		int Trime_Size = Integer.parseInt(TrimeSize);
		CommonClass.GetLowDepthReads(ReadSetPath, Trime_Size);
	}
}
//Read quality statistics.
class QSstatistics {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String FastqFilePath = args[0];
		String QSWritePath = args[1];
		String encoding = "utf-8";
		int SumCount = CommonClass.getFileLines(FastqFilePath) / 4;
		double AverageQS[][] = new double[SumCount][2];
		int LineCount = 0;
		File file1 = new File(FastqFilePath);
		if (file1.isFile() && file1.exists()) {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file1), encoding);
			BufferedReader bufferedReader1 = new BufferedReader(read);
			while ((bufferedReader1.readLine()) != null) {
				bufferedReader1.readLine();
				bufferedReader1.readLine();
				String CurrentQS = CommonClass.GetAverageQualityScoreOfRead(bufferedReader1.readLine());
				AverageQS[LineCount++][0] = Double.parseDouble(CurrentQS);
			}
			bufferedReader1.close();
		} else {
			System.out.println("File is not exist!");
		}
		//Statistics.
		int MarKCount = 0;
		double MarkQS[][] = new double[SumCount][2];
		for (int w = 0; w < LineCount; w++) {
			if (AverageQS[w][0] != 0) {
				int IndexCount = 1;
				for (int r = w + 1; r < LineCount; r++) {
					if (AverageQS[r][0] != 0) {
						if (AverageQS[w][0] == AverageQS[r][0]) {
							IndexCount++;
							AverageQS[r][0] = 0;
						}
					}
				}
				MarkQS[MarKCount][0] = AverageQS[w][0];
				MarkQS[MarKCount][1] = IndexCount;
				MarKCount++;
			}
		}
		//Get Statistics.
		double exch1 = 0;
		double exch2 = 0;
		for (int k = 0; k < MarKCount; k++) {
			for (int h = k + 1; h < MarKCount; h++) {
				if (MarkQS[k][0] > MarkQS[h][0]) {
					exch1 = MarkQS[k][0];
					exch2 = MarkQS[k][1];
					MarkQS[k][0] = MarkQS[h][0];
					MarkQS[k][1] = MarkQS[h][1];
					MarkQS[h][0] = exch1;
					MarkQS[h][1] = exch2;
				}
			}
		}
		//Write.
		for (int g = 0; g < MarKCount; g++) {
			FileWriter writer1 = new FileWriter(QSWritePath + "QSFile.fa", true);
			writer1.write(MarkQS[g][0] + "\t" + MarkQS[g][1] + "\n");
			writer1.close();
		}
		//Free.
		AverageQS = null;
	}
}
//read quality control.
class QualityControl {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String QSfilePath = args[0];
		String FQfilePath = args[1];
		String FinalWritePath = args[2];
		String FilterHT = args[3];
		int LineQSfile = CommonClass.getFileLines(QSfilePath);
		String QSarray[] = new String[LineQSfile];
		int RealQSfile = CommonClass.FileToArray(QSfilePath, QSarray);
		System.out.println("The real size of QS file:" + RealQSfile);
		String[] SplitLine1 = QSarray[0].split("\t|\\s+");
		String[] SplitLine2 = QSarray[RealQSfile - 1].split("\t|\\s+");
		double QSLow = Double.parseDouble(SplitLine1[0]);
		double QSHig = Double.parseDouble(SplitLine2[0]);
		double QSHt = Double.parseDouble(FilterHT);
		double Filter = QSHt * (QSHig - QSLow) + QSLow;
		System.out.println("High:"+QSHig+"\t"+"Low:"+QSLow);
		System.out.println("The filter threshold is:" + Filter);
		//Filtering.
		CommonClass.FastqFile_Array(FQfilePath, FinalWritePath, Filter);
		//Free.
		QSarray = null;
	}
}
//Generate Final Low Depth FASTA.
class GenerateFinalLowDepthFasta {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String LowDepthFastqPath = args[0];
		String FinalLowDepthWritePath = args[1];
		int LinesLowDepthFq = CommonClass.getFileLines(LowDepthFastqPath) / 4;
		String LowDepthReadArray[] = new String[LinesLowDepthFq];
		int realLowDepthReads = CommonClass.LoadFastqToArray(LowDepthFastqPath, LowDepthReadArray);
		int Count_LowDepth = 0;
		for (int w = 0; w < realLowDepthReads; w++) {
			FileWriter writer1 = new FileWriter(FinalLowDepthWritePath + "LowDepth_Trimed.fasta", true);
			writer1.write(">" + (Count_LowDepth++) + "\n" + LowDepthReadArray[w] + "\n");
			writer1.close();
		}
		//Free.
		LowDepthReadArray=null;
	}
}
//Change CONTIG lines.
class ChangeLines {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String FinalContigWritePath = args[0];
		String ContigAfterPath = args[1];
		String DataName = args[2];
		String RealString = CommonClass.readContigFile(ContigAfterPath);
		//Write.
		FileWriter writer1 = new FileWriter(FinalContigWritePath + "contig." + DataName + ".changgeLines.fa", true);
		writer1.write(RealString);
		writer1.close();
		System.out.println("File write process end!");
	}
}
//Get MIS-assembly CONTIGS.
class GetMisassemblyContigs {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String EPGAscaffoldPath = args[0];
		String MisAssemblyContigPath = args[1];
		String ContigWritePath = args[2];
		//Load1.
		int LinesEPGAscaffold = CommonClass.getFileLines(EPGAscaffoldPath) / 2;
		String EPGAscaffoldArray[] = new String[LinesEPGAscaffold];
		int Realsize_EPGAscaff = CommonClass.FastaToArray(EPGAscaffoldPath, EPGAscaffoldArray);
		//Load2.
		int LinesMisassembly = CommonClass.getFileLines(MisAssemblyContigPath) / 2;
		int MisassemblyArray[] = new int[LinesEPGAscaffold+LinesMisassembly];
		int Realsize_Misassembly = CommonClass.GetFastaHead(MisAssemblyContigPath, MisassemblyArray);
		//Mark.
		for (int g = 0; g < Realsize_EPGAscaff; g++) {
			for (int f = 0; f < Realsize_Misassembly; f++) {
				if (g == MisassemblyArray[f]) {
					EPGAscaffoldArray[g] = "#" + EPGAscaffoldArray[g];
					break;
				}
			}
		}
		//Write.
		for (int w = 0; w < Realsize_EPGAscaff; w++) {
			if (EPGAscaffoldArray[w].charAt(0) == '#') {
				FileWriter writer = new FileWriter(ContigWritePath + "/Misassembly_EPGAcontigs.fa", true);
				writer.write(">" + (w) + "\n"
						+ EPGAscaffoldArray[w].substring(1, EPGAscaffoldArray[w].length()) + "\n");
				writer.close();
			} else {
				FileWriter writer = new FileWriter(ContigWritePath + "/ErrorFree_EPGAcontigs.fa", true);
				writer.write(">" + (w) + "\n" + EPGAscaffoldArray[w] + "\n");
				writer.close();
			}
		}
		//Free.
		EPGAscaffoldArray=null;
		MisassemblyArray=null;
	}
}
class ReplaceIncludedEPGAcontigs {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String MUMmerFile = args[0];
		String ErrorFreeEPGAcontigFile = args[1];
		String SPAdescontigFile = args[2];
		String DataName = args[3];
		String FinalEPGAcontigPath = args[4];
		//Alignment.
		int SizeOfMUMmerFile = CommonClass.getFileLines(MUMmerFile);
		String MUMerArray[] = new String[SizeOfMUMmerFile];
		int RealSizeMUMmer = CommonClass.FileToArray(MUMmerFile, MUMerArray);
		System.out.println("The real size of MUMmer is:" + RealSizeMUMmer);
		//Load Error Free EPGA.
		int SizeOfErrorFreeEPGAFile = CommonClass.getFileLines(ErrorFreeEPGAcontigFile);
		String ErrorFreeEPGAcontigArray[] = new String[SizeOfErrorFreeEPGAFile];
		int RealSizeErrorFreeEPGAcontig = CommonClass.FastaToArray(ErrorFreeEPGAcontigFile, ErrorFreeEPGAcontigArray);
		System.out.println("The real size of Error Free EPGA assembly is:" + RealSizeErrorFreeEPGAcontig);
		//Load SPAdes.
		int SizeOfSPAdesFile = CommonClass.getFileLines(SPAdescontigFile);
		String SPAdescontigArray[] = new String[SizeOfSPAdesFile];
		int RealSizeSPAdescontig = CommonClass.FastaToArray(SPAdescontigFile, SPAdescontigArray);
		System.out.println("The real size of SPAdes assembly is:" + RealSizeSPAdescontig);
		//Process.
		//Set<Integer> hashSet = new HashSet<Integer>();
		List<Integer> hashSet = new ArrayList<Integer>();
		for (int w = 4; w < RealSizeMUMmer; w++) 
		{
			String[] SplitLine1 = MUMerArray[w].split("\t|\\s+");
			//if(SplitLine1.length==14 && (SplitLine1[13].equals("[CONTAINS]") || SplitLine1[13].equals("[BEGIN]") || SplitLine1[13].equals("[END]")))
			if(SplitLine1.length==14 && (SplitLine1[13].equals("[CONTAINS]")))
			{
				String[] SplitLine2 = SplitLine1[11].split("_");
				int SPAdes_id = Integer.parseInt(SplitLine2[1]);
				hashSet.add(SPAdes_id);
				int EPGA_id = Integer.parseInt(SplitLine1[12]);
				System.out.println("SplitLine1[13]:"+SplitLine1[13]+"\t"+"Spades:"+SPAdes_id+"\t"+"epga:"+EPGA_id);
				ErrorFreeEPGAcontigArray[EPGA_id]="#"+ErrorFreeEPGAcontigArray[EPGA_id];	
			}
		}
		//Write free spades contigs.
		int FreeSPadesContig=0;
		for(int j=0;j<RealSizeSPAdescontig;j++)
		{
			if(!hashSet.contains(j))
			{
				FileWriter writer = new FileWriter(FinalEPGAcontigPath + "/FreeSPAdesContig." + DataName + ".fa", true);
				writer.write(">Free:" + (FreeSPadesContig++) + "\n" + SPAdescontigArray[j] + "\n");
				writer.close();
			}
		}
		//Write.
		for (int w = 0; w < RealSizeErrorFreeEPGAcontig; w++) 
		{
			if(ErrorFreeEPGAcontigArray[w].charAt(0)!='#')
			{
				FileWriter writer = new FileWriter(FinalEPGAcontigPath + "/ReplaceEPGAcontig." + DataName + ".fa", true);
				writer.write(">" + (w) + "\n" + ErrorFreeEPGAcontigArray[w] + "\n");
				writer.close();
			}
		}
		int MergeNew=0;
		Iterator<Integer> it = hashSet.iterator();
		while (it.hasNext()) {
			int SPAdesIndex = it.next();
			FileWriter writer = new FileWriter(FinalEPGAcontigPath + "/ReplaceEPGAcontig." + DataName + ".fa", true);
			writer.write(">Add:" + (MergeNew++) + "\n" + SPAdescontigArray[SPAdesIndex] + "\n");
			writer.close();
		}
		//Free.
		MUMerArray=null;
		ErrorFreeEPGAcontigArray=null;
		SPAdescontigArray=null;
	}
}
//Replace Error CONTIGS.
class GetOverlap3 {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
		String MUMmerFile = args[0];
		String ErrorEPGAcontigFile = args[1];
		String ErrorFreeEPGAcontigFile = args[2];
		String SPAdescontigFile = args[3];
		String DataName = args[4];
		String FinalEPGAcontigPath = args[5];
		//Alignment.
		int SizeOfMUMmerFile = CommonClass.getFileLines(MUMmerFile);
		String MUMerArray[] = new String[SizeOfMUMmerFile];
		int RealSizeMUMmer = CommonClass.FileToArray(MUMmerFile, MUMerArray);
		System.out.println("The real size of MUMmer is:" + RealSizeMUMmer);
		//Load EPGA.
		int SizeOfErrorEPGAFile = CommonClass.getFileLines(ErrorEPGAcontigFile);
		String ErrorEPGAcontigArray[] = new String[SizeOfErrorEPGAFile];
		int RealSizeErrorEPGAcontig = CommonClass.FastaToArray(ErrorEPGAcontigFile, ErrorEPGAcontigArray);
		System.out.println("The real size of Error EPGA assembly is:" + RealSizeErrorEPGAcontig);
		//Load EPGA.
		int SizeOfErrorFreeEPGAFile = CommonClass.getFileLines(ErrorFreeEPGAcontigFile);
		String ErrorFreeEPGAcontigArray[] = new String[SizeOfErrorFreeEPGAFile];
		int RealSizeErrorFreeEPGAcontig = CommonClass.FastaToArray(ErrorFreeEPGAcontigFile, ErrorFreeEPGAcontigArray);
		System.out.println("The real size of Error Free EPGA assembly is:" + RealSizeErrorFreeEPGAcontig);
		//Load SPAdes.
		int SizeOfSPAdesFile = CommonClass.getFileLines(SPAdescontigFile);
		String SPAdescontigArray[] = new String[SizeOfSPAdesFile];
		int RealSizeSPAdescontig = CommonClass.FastaToArray(SPAdescontigFile, SPAdescontigArray);
		System.out.println("The real size of SPAdes assembly is:" + RealSizeSPAdescontig);
		//Process.
		Set<Integer> hashSet = new HashSet<Integer>();
		for (int w = 4; w < RealSizeMUMmer; w++) {
			if (MUMerArray[w].charAt(0) != '#') {
				int CountSave = 0;
				String SaveTempArray[] = new String[RealSizeMUMmer];
				String[] SplitLine1 = MUMerArray[w].split("\t|\\s+");
				SaveTempArray[CountSave++] = MUMerArray[w];
				MUMerArray[w] = "#" + MUMerArray[w];
				for (int e = w + 1; e < RealSizeMUMmer; e++) {
					if (MUMerArray[e].charAt(0) != '#') {
						String[] SplitLine2 = MUMerArray[e].split("\t|\\s+");
						if (SplitLine1[11].equals(SplitLine2[11])) {
							SaveTempArray[CountSave++] = MUMerArray[e];
							MUMerArray[e] = "#" + MUMerArray[e];
						}
					}
				}
				// Mark read.
				for (int r = 0; r < CountSave; r++) {
					String[] SplitLine31 = SaveTempArray[r].split("\t|\\s+");
					String[] SplitLine41 = SplitLine31[12].split("_");
					int SPAdes_id = Integer.parseInt(SplitLine41[1]);
					hashSet.add(SPAdes_id);
				}
			}
		}
		//Write.
		int CountCorrEPGA=0;
		String CorrEPGAContigArray[]=new String[2*(RealSizeErrorFreeEPGAcontig+RealSizeSPAdescontig)];
		int EPId = 0;
		for (int w = 0; w < RealSizeErrorFreeEPGAcontig; w++) {
            CorrEPGAContigArray[CountCorrEPGA++]=ErrorFreeEPGAcontigArray[w];
		}
		Iterator<Integer> it = hashSet.iterator();
		while (it.hasNext()) {
			int EPGAIndex = it.next();
            CorrEPGAContigArray[CountCorrEPGA++]=SPAdescontigArray[EPGAIndex];
		}
		//Sort process.
		String exch="";
		for(int w=0;w<CountCorrEPGA;w++)
		{
			for(int h=w+1;h<CountCorrEPGA;h++)
			{
				if(CorrEPGAContigArray[w].length()<CorrEPGAContigArray[h].length())
				{
					exch=CorrEPGAContigArray[w];
					CorrEPGAContigArray[w]=CorrEPGAContigArray[h];
					CorrEPGAContigArray[h]=exch;
				}
			}
		}
		//Delete duplicate records.
		for(int w=0;w<CountCorrEPGA;w++)
		{
			for(int h=w+1;h<CountCorrEPGA;h++)
			{
		       	if(CorrEPGAContigArray[w].equals(CorrEPGAContigArray[h])||CorrEPGAContigArray[w].equals(CommonClass.reverse(CorrEPGAContigArray[h])))
				{
					CorrEPGAContigArray[h]="%"+CorrEPGAContigArray[h];
				}
			}
		}
		for(int j=0;j<CountCorrEPGA;j++)
		{
			if(CorrEPGAContigArray[j].charAt(0)!='%')
			{
				 FileWriter writer = new FileWriter(FinalEPGAcontigPath + "/CorrEPGAcontig." + DataName + ".fa", true);
			     writer.write(">" + (EPId++) + "\n" + CorrEPGAContigArray[j] + "\n");
			     writer.close();
			}
		}
		//Free.
		MUMerArray=null;
		ErrorEPGAcontigArray=null;
		ErrorFreeEPGAcontigArray=null;
		SPAdescontigArray=null;
		CorrEPGAContigArray=null;
	}
}
class MergeCompContigs {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	    String FinalContigWritePath=args[0];
		String ContigAfterPath=args[1];
		String FreeContigSPAdesPath=args[2];
		//Loading.
		int SizeOfContigAfter=CommonClass.getFileLines(ContigAfterPath)/2;
	    String ContigSetAfterArray[]=new String[SizeOfContigAfter];
	    int RealSizeOfContigSetAfter=CommonClass.FastaToArray(ContigAfterPath,ContigSetAfterArray); 
	    System.out.println("The real size of ContigSetAfter is:"+RealSizeOfContigSetAfter);
		//low.
		int SizeOfContigSPAdes=CommonClass.getFileLines(FreeContigSPAdesPath);
	    String ContigSetSPAdesArray[]=new String[SizeOfContigSPAdes+1];
	    int RealSizeOfContigSetSPAdes=CommonClass.FastaToArray(FreeContigSPAdesPath,ContigSetSPAdesArray); 
	    System.out.println("The real size of ContigSetSPAdes is:"+RealSizeOfContigSetSPAdes);
		//Merge.
		int LoadingContigAfterCount=0;
		String LoadingContigAfterArray[]=new String[RealSizeOfContigSetAfter+RealSizeOfContigSetSPAdes]; 
		for(int r=0;r<RealSizeOfContigSetAfter;r++)
		{
			 if(ContigSetAfterArray[r].length()>=64)
			 {
				  LoadingContigAfterArray[LoadingContigAfterCount++]=ContigSetAfterArray[r];
			 }
		}
		for(int g=0;g<RealSizeOfContigSetSPAdes;g++)
		{
		     if(ContigSetSPAdesArray[g].charAt(0)!='#')
		     {
				 String RevStr=CommonClass.reverse(ContigSetSPAdesArray[g]);
		    	 for(int r=0;r<RealSizeOfContigSetAfter;r++)
		    	 {
		    		 if((ContigSetAfterArray[r].length()>=64)&&(ContigSetAfterArray[r].contains(ContigSetSPAdesArray[g])||ContigSetAfterArray[r].contains(RevStr)))
		    		 {
		    			 ContigSetSPAdesArray[g]="#"+ContigSetSPAdesArray[g];
		    			 break;
		    		 }
		    	 }
		     }
		}
		for(int w=0;w<RealSizeOfContigSetSPAdes;w++)
		{
			 if(ContigSetSPAdesArray[w].charAt(0)!='#')
			 {
				 LoadingContigAfterArray[LoadingContigAfterCount++]=ContigSetSPAdesArray[w];
			 }
		}
	    //Write.
		int LineNum1=0;
	    for(int x=0;x<LoadingContigAfterCount;x++)
	    {
			FileWriter writer1= new FileWriter(FinalContigWritePath+"FinalContig_AfterMergeCompContigs.fa",true);
	        writer1.write(">"+(LineNum1++)+":"+LoadingContigAfterArray[x].length()+"\n"+LoadingContigAfterArray[x]+"\n");
	        writer1.close();
	    }
    }
}
class breakErrorPoints {
	public static void main(String[] args) throws IOException {
		//TODO Auto-generated method stub
        String MUMmerFile=args[0];
        String EPGAcontigFile=args[1];
        String SPAdescontigFile=args[2];
        String FinalEPGAcontigPath=args[3];
        String FilterThreshold=args[4];
        //Alignment.
        int filterThreshold=Integer.parseInt(FilterThreshold);
		int SizeOfMUMmerFile=CommonClass.getFileLines(MUMmerFile);
        String MUMerArray[]=new String[SizeOfMUMmerFile];
        int RealSizeMUMmer=CommonClass.FileToArray(MUMmerFile,MUMerArray);
        System.out.println("The real size of MUMmer is:"+RealSizeMUMmer);
        //Load EPGA.
		int SizeOfEPGAFile=CommonClass.getFileLines(EPGAcontigFile);
        String EPGAcontigArray[]=new String[SizeOfEPGAFile];
        int RealSizeEPGAcontig=CommonClass.FastaToArray(EPGAcontigFile,EPGAcontigArray);
        System.out.println("The real size of EPGA assembly is:"+RealSizeEPGAcontig);
        //Load Velvet.
		int SizeOfSPAdesFile=CommonClass.getFileLines(SPAdescontigFile);
        String SPAdescontigArray[]=new String[SizeOfSPAdesFile];
        int RealSizeSPAdescontig=CommonClass.FastaToArray(SPAdescontigFile,SPAdescontigArray);
        System.out.println("The real size of SPAdes assembly is:"+RealSizeSPAdescontig);
        //Process.
        int Lines=0;
        String SaveTempArray[]=new String[RealSizeMUMmer];
        for(int w=4;w<RealSizeMUMmer;w++)
        {
        	if(MUMerArray[w].charAt(0)!='#')
        	{
	        	int CountSave=0;
	        	String [] SplitLine1 = MUMerArray[w].split("\t|\\s+");
	        	SaveTempArray[CountSave++]=MUMerArray[w];
	        	MUMerArray[w]="#"+MUMerArray[w];
	            for(int e=w+1;e<RealSizeMUMmer;e++)
	            {
	            	if(MUMerArray[e].charAt(0)!='#')
	            	{
		            	String [] SplitLine2 = MUMerArray[e].split("\t|\\s+");
		            	if(SplitLine1[11].equals(SplitLine2[11]))
		            	{
		            		SaveTempArray[CountSave++]=MUMerArray[e];
		            		MUMerArray[e]="#"+MUMerArray[e];
		            	}
	            	}
	            }  
	            //Mark read.
				String [] SplitLine3 = SaveTempArray[0].split("\t|\\s+");
	            String [] SplitLine4 = SplitLine3[11].split(":|--");
	            int Ref_id=Integer.parseInt(SplitLine4[0]);
	            int Read_Len=EPGAcontigArray[Ref_id].length();
	            int CountReadArray=0;
	            char ReadArray[]=new char[Read_Len];
	            for(int g=0;g<Read_Len;g++)
	            {
	            	char td=EPGAcontigArray[Ref_id].charAt(g);
	            	ReadArray[CountReadArray++]=td;
	            }
	            //Check.
	            double MarkReadArray[][]=new double[Read_Len][2];
	            for(int k=0;k<Read_Len;k++)
	            {
	            	MarkReadArray[k][0]=0;
	            	MarkReadArray[k][1]=1;
	            }
	            for(int h=0;h<CountSave;h++)
	            {
	            	String [] SplitLine5 = SaveTempArray[h].split("\t|\\s+");
	        		int Ref_Start=Integer.parseInt(SplitLine5[0]);
	            	int Ref_End=Integer.parseInt(SplitLine5[1]);
	            	double MateRate=Double.parseDouble(SplitLine5[6]);
	            	for(int n=Ref_Start-1;n<Ref_End;n++)
	            	{
	            		MarkReadArray[n][0]+=1;
	            		MarkReadArray[n][1]=MarkReadArray[n][1]*(MateRate/100);
	            	}
	            }
	            for(int c=0;c<Read_Len;c++)
	            {
	            	if(MarkReadArray[c][0]==0)
	            	{
	            		ReadArray[c]='%';
	            	}
	            }
	            //Splits.
	            int IndexCount=0;
	            String Output_Str="";
	            String StrText = new String(ReadArray);
	            if(StrText.contains("%"))
	            {
		            String [] SplitLine = StrText.split("%");
		            for(int f=0;f<SplitLine.length;f++)
		            {
		            	if(SplitLine[f].length()>filterThreshold)
		            	{
		            		if((IndexCount==0))
		            		{
		            			Output_Str=">"+(Lines++)+"\n"+SplitLine[f]+"\n";
		            		}
		            	    else
		            	    {
		            		    Output_Str+=">"+(Lines++)+"\n"+SplitLine[f]+"\n";
		            	    }
		            	}
						IndexCount++;
		            }
	            }
	            else
	            {
	            	Output_Str+=">"+(Lines++)+"\n"+EPGAcontigArray[Ref_id]+"\n";
	            }
			    FileWriter writer= new FileWriter(FinalEPGAcontigPath+"/EPGA_BreakErrorPoints.fa",true);
                writer.write(Output_Str);
                writer.close();
        	}
        }
	}
}
class MergeScaffolds {
	public static void main(String[] args) throws IOException {
		String ScaffoldPath=args[0];
		String FinalEPGAScaffoldPath=args[1];
		//Loading Scaffolds.
		int SizeOfScaffoldFile=CommonClass.getFileLines(ScaffoldPath)/2;
        String ScaffoldArray[]=new String[SizeOfScaffoldFile];
        int RealSizeScaffoldArray=CommonClass.FastaToArray(ScaffoldPath,ScaffoldArray);
        System.out.println("The real size of Scaffolds is:"+RealSizeScaffoldArray);
		//Sort process.
		String exch="";
		for(int w=0;w<RealSizeScaffoldArray;w++)
		{
			for(int h=w+1;h<RealSizeScaffoldArray-1;h++)
			{
				if(ScaffoldArray[w].length()<ScaffoldArray[h].length())
				{
					exch=ScaffoldArray[w];
					ScaffoldArray[w]=ScaffoldArray[h];
					ScaffoldArray[h]=exch;
				}
			}
		}
		//Delete duplicate records.
		for(int w=0;w<RealSizeScaffoldArray;w++)
		{
			for(int h=w+1;h<RealSizeScaffoldArray;h++)
			{
		       	if(ScaffoldArray[w].equals(ScaffoldArray[h])||ScaffoldArray[w].equals(CommonClass.reverse(ScaffoldArray[h])))
				{
					ScaffoldArray[h]="%"+ScaffoldArray[h];
				}
			}
		}
		int EPId=0;
		for(int j=0;j<RealSizeScaffoldArray;j++)
		{
			if(ScaffoldArray[j].charAt(0)!='%')
			{
				 FileWriter writer = new FileWriter(FinalEPGAScaffoldPath + "/Final_EPGAScaffolds2.fa", true);
			     writer.write(">" + (EPId++) + "\n" + ScaffoldArray[j] + "\n");
			     writer.close();
			}
		}
	}
}
//Pre_process.
class GenerateScaffoldConfig{
	public static void main(String[] args) throws IOException{
		String HamePath=args[0];
		String SSPACE_Path=args[1];
        String DataSetName=args[2];
		String InsertSize=args[3];
		String St_insertsize=args[4];
		String ScaffoldConfigWrite="lib1 bwa "+HamePath+"/lib/"+DataSetName+"/HammerShort1.left.fasta   "+HamePath+"/lib/"+DataSetName+"/HammerShort1.right.fasta "+InsertSize+" "+St_insertsize+" "+"FR";
		FileWriter writer= new FileWriter(SSPACE_Path+DataSetName+".txt",true);
        writer.write(ScaffoldConfigWrite);
        writer.close();
	}
}
public class Pre_process {
	public static void main(String[] args) throws IOException{
		
	}
}