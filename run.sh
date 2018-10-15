#!/bin/bash
echo  "*******************************************************************************************"
echo "Copyright (C) 2017 Jianxin Wang(jxwang@mail.csu.edu.cn), Xingyu Liao(liaoxingyu@csu.edu.cn)"
echo "School of Information Science and Engineering"
echo "Central South University"
echo "ChangSha"
echo "CHINA, 410083"
echo  "*******************************************************************************************"
#define
FilterTH=0.5
FilterTHLow=0.7
Trim_Size=50
WindowSize=11
KmerSize_Low=21
KmerSize_Normal=31
Scaff_Sd=0.25
#Get.
TEST=$(cat ./config.txt)
i=0;
for p in $TEST; do
	arra[$i]=$p
	i=$((i+1))
done
for ((i=0;i<${#arra[@]};i++))
    do
    if [ $i = 0 ] 
    then
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       home=$var
    fi
    if [ $i = 1 ] 
    then 
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       DataSetName=$var
    fi
    if [ $i = 2 ] 
    then
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       lib1_left_name=$var
    fi
    if [ $i = 3 ] 
    then 
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       lib1_right_name=$var
    fi
    if [ $i = 4 ] 
    then
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       Read_Length=$var
    fi
    if [ $i = 5 ] 
    then 
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       InsertLength1=$var
    fi
	if [ $i = 6 ] 
    then 
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       Sd_insert=$var
    fi
    if [ $i = 7 ] 
    then
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       ReferenceName=$var
    fi
    if [ $i = 8 ] 
    then 
       var=`echo "${arra[$i]}"|awk -F '=' '{print $2}' `
       dsk_bin=$var
    fi
done 
###########################################################
echo  "Step-0: System initialization"
if [ ! -d $home/lib/ ];then
   mkdir $home/lib/
fi
#if [ ! -d $home/Quast_brakpoints/ ];then
#   mkdir $home/Quast_brakpoints/
#fi
if [ ! -d $home/EPGA/ ];then
   mkdir $home/EPGA/
fi
if [ ! -d $home/spades/ ];then
   mkdir $home/spades/
fi
if [ ! -d $home/Final_Assemblies/ ];then
   mkdir $home/Final_Assemblies/
fi
if [ ! -d $home/Auxiliary_lib ];then
   mkdir $home/Auxiliary_lib
fi
if [ ! -d $home/dsk/ ];then
   mkdir $home/dsk/
fi
if [ ! -d $home/Log/ ];then
   mkdir $home/Log/
fi
if [ ! -d $home/MisAssembly/ ];then
   mkdir $home/MisAssembly/
fi
if [ ! -d $home/QSstatistics/ ];then
   mkdir $home/QSstatistics/
fi
if [ ! -d $home/Quast/ ];then
   mkdir $home/Quast/
fi
if [ ! -d $home/ConfigFile/ ];then
   mkdir $home/ConfigFile/
fi
if [ ! -d $home/MUM/ ];then
   mkdir $home/MUM/
fi
if [ ! -d $home/SSPACE-STANDARD-3.0_linux-x86_64/GapFill/ ];then
   mkdir $home/SSPACE-STANDARD-3.0_linux-x86_64/GapFill/
fi
###########################################################
if [ ! -d $home/lib/$DataSetName/ ];then
   mkdir $home/lib/$DataSetName/
fi
if [ ! -d $home/Final_Assemblies/$DataSetName/ ];then
   mkdir $home/Final_Assemblies/$DataSetName/
fi
if [ ! -d $home/dsk/$DataSetName/ ];then
   mkdir $home/dsk/$DataSetName/
fi
if [ ! -d $home/Log/$DataSetName/ ];then
   mkdir $home/Log/$DataSetName/
fi
if [ ! -d $home/MisAssembly/$DataSetName/ ];then
   mkdir $home/MisAssembly/$DataSetName/
fi
if [ ! -d $home/EPGA/$DataSetName/ ];then
   mkdir $home/EPGA/$DataSetName/
fi
#if [ ! -d $home/IDBA-UD/$DataSetName/ ];then
#   mkdir $home/IDBA-UD/$DataSetName/
#fi
if [ ! -d $home/spades/$DataSetName/ ];then
   mkdir $home/spades/$DataSetName/ 
fi
if [ ! -d $home/Auxiliary_lib/$DataSetName/ ];then
   mkdir $home/Auxiliary_lib/$DataSetName/
fi
if [ ! -d $home/QSstatistics/$DataSetName/ ];then
   mkdir $home/QSstatistics/$DataSetName/
fi
if [ ! -d $home/Quast/$DataSetName/ ];then
   mkdir $home/Quast/$DataSetName/
fi
if [ ! -d $home/MUM/$DataSetName/ ];then
   mkdir $home/MUM/$DataSetName/
fi
###########################################################
if [ ! -d $home/Log/$DataSetName/dsk/ ];then
   mkdir $home/Log/$DataSetName/dsk/
fi
if [ ! -d $home/Log/$DataSetName/ErrorCorrection/ ];then
   mkdir $home/Log/$DataSetName/ErrorCorrection/
fi
if [ ! -d $home/Log/$DataSetName/breakMisAssembly/ ];then
   mkdir $home/Log/$DataSetName/breakMisAssembly/
fi
if [ ! -d $home/Log/$DataSetName/running/ ];then
   mkdir $home/Log/$DataSetName/running/
fi
if [ ! -d $home/EPGA/$DataSetName/before/ ];then
   mkdir $home/EPGA/$DataSetName/before/
fi
if [ ! -d $home/EPGA/$DataSetName/low/ ];then
   mkdir $home/EPGA/$DataSetName/low/
fi
if [ ! -d $home/EPGA/$DataSetName/after/ ];then
   mkdir $home/EPGA/$DataSetName/after/
fi
if [ ! -d $home/EPGA/$DataSetName/afterfilter/ ];then
   mkdir $home/EPGA/$DataSetName/afterfilter/
fi
###########################################################
if [ ! -f $home/Log/$DataSetName/ErrorCorrection/$DataSet_Name.fa ]; then
  touch $home/Log/$DataSetName/ErrorCorrection/$DataSet_Name.fa
fi
if [ ! -f $home/Log/$DataSetName/running/out.log ]; then
  touch $home/Log/$DataSetName/running/out.log
fi
if [ ! -f $home/Log/$DataSetName/running/quast.log ]; then
  touch $home/Log/$DataSetName/running/quast.log
fi
if [ ! -f $home/Log/$DataSetName/dsk/out.log ]; then
  touch $home/Log/$DataSetName/dsk/out.log
fi
if [ ! -f $home/Log/$DataSetName/breakMisAssembly/out.log ]; then
  touch $home/Log/$DataSetName/breakMisAssembly/out.log
fi
###########################################################
echo  "Step-1: Compiling public class"
javac $home/Program/Pre_process.java
echo  "Step-2: Error correction"
rm -rf $home/Log/ErrorCorrection/$DataSet_Name.fa
cd $home/lib/$DataSetName/
karect -correct -threads=48 -matchtype=hamming -celltype=haploid -inputfile=$lib1_left_name.fastq -inputfile=$lib1_right_name.fastq > $home/Log/$DataSetName/ErrorCorrection/$DataSetName.fa
cp $home/lib/$DataSetName/karect_$lib1_left_name.fastq  $home/lib/$DataSetName/$lib1_left_name.corr.fastq
cp $home/lib/$DataSetName/karect_$lib1_right_name.fastq $home/lib/$DataSetName/$lib1_right_name.corr.fastq
cd $home
echo  "Step-3: Merge fastq files"
rm -rf $home/lib/$DataSetName/lib.fastq
perl $home/Program/shuffleSequences_fastq.pl  $home/lib/$DataSetName/$lib1_left_name.corr.fastq  $home/lib/$DataSetName/$lib1_right_name.corr.fastq  $home/lib/$DataSetName/lib.fastq
echo  "Step-4: Format covertion"
rm -rf $home/lib/$DataSetName/*.fasta
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/lib/$DataSetName/$lib1_left_name.corr.fastq $home/lib/$DataSetName/$lib1_right_name.corr.fastq $home/lib/$DataSetName/ Short1
echo  "Step-5: Generate Unique Kmer [dsk]"
cd $dsk_bin
./dsk  -file $home/lib/$DataSetName/Short1.fasta -kmer-size $WindowSize -abundance-min 1 > $home/Log/$DataSetName/dsk/out.log 2>&1
./dsk2ascii -file Short1.h5 -out Short1.txt > $home/Log/$DataSetName/dsk/out.log 2>&1
rm -rf $home/dsk/$DataSetName/*
cp Short1.txt $home/dsk/$DataSetName/KmerSet.fa
rm -rf Short1.txt
rm -rf Short1.h5
echo  "Step-6: K-mer classification"
rm -rf $home/dsk/$DataSetName/LowDepthKmer.fa
rm -rf $home/dsk/$DataSetName/LowFreThreshold.txt
java -classpath  $home/Program/ FilterKmerStaticsModify $home/dsk/$DataSetName/KmerSet.fa  $home/Log/$DataSetName/ErrorCorrection/$DataSetName.fa $FilterTH $home/dsk/$DataSetName/
echo  "Step-7: Divide the kmer hashtable into multiple sub-tables"
rm -rf $home/dsk/$DataSetName/KmerSet_A.fa
rm -rf $home/dsk/$DataSetName/KmerSet_T.fa
rm -rf $home/dsk/$DataSetName/KmerSet_G.fa
rm -rf $home/dsk/$DataSetName/KmerSet_C.fa
java -classpath  $home/Program/ GenerateBlockedKmerHashTable $home/dsk/$DataSetName/LowDepthKmer.fa  $home/dsk/$DataSetName/
echo  "Step-8: Read classification"
rm -rf $home/lib/$DataSetName/Short1.left.Marked.fasta
rm -rf $home/lib/$DataSetName/Short1.right.Marked.fasta
java -classpath  $home/Program/ peTrimer $home/dsk/$DataSetName/ $WindowSize $home/lib/$DataSetName/$lib1_left_name.corr.fastq  $home/lib/$DataSetName/Short1.left.Marked.fasta
java -classpath  $home/Program/ peTrimer $home/dsk/$DataSetName/ $WindowSize $home/lib/$DataSetName/$lib1_right_name.corr.fastq $home/lib/$DataSetName/Short1.right.Marked.fasta
echo  "Step-9: Mark reads in lib"
rm -rf $home/lib/$DataSetName/LowDepthFasta.fasta
rm -rf $home/lib/$DataSetName/NormalDepthFasta.fasta
rm -rf $home/lib/$DataSetName/LowDepthFastq.fastq
rm -rf $home/lib/$DataSetName/NormalDepthFastq.fastq
java -classpath  $home/Program/ FilterLowDepthReads2 $home/lib/$DataSetName/ $Trim_Size
echo  "Step-10: SPAdes assembly"
spades.py --pe1-1 $home/lib/$DataSetName/$lib1_left_name.corr.fastq --pe1-2 $home/lib/$DataSetName/$lib1_right_name.corr.fastq --sc --careful -o $home/spades/$DataSetName/ > $home/Log/$DataSetName/running/out.log
quast.py $home/spades/$DataSetName/contigs.fasta $home/spades/$DataSetName/scaffolds.fasta -R $home/Reference/$ReferenceName -o $home/spades/$DataSetName/quast > $home/Log/$DataSetName/running/out.log
echo  "Step-11: Generate Auxiliary libs"
rm -rf  $home/Auxiliary_lib/$DataSetName/InsertSize*
cd $home/art_bin_MountRainier
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 300  -s 30  -o $home/Auxiliary_lib/$DataSetName/InsertSize300_  > $home/Log/$DataSetName/running/out.log
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 500  -s 50  -o $home/Auxiliary_lib/$DataSetName/InsertSize500_  > $home/Log/$DataSetName/running/out.log
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 700  -s 70  -o $home/Auxiliary_lib/$DataSetName/InsertSize700_  > $home/Log/$DataSetName/running/out.log
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 900  -s 90  -o $home/Auxiliary_lib/$DataSetName/InsertSize900_  > $home/Log/$DataSetName/running/out.log
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 1500 -s 150  -o $home/Auxiliary_lib/$DataSetName/InsertSize1500_  > $home/Log/$DataSetName/running/out.log
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 2500 -s 250 -o $home/Auxiliary_lib/$DataSetName/InsertSize2500_    > $home/Log/$DataSetName/running/out.log
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 5000 -s 500 -o $home/Auxiliary_lib/$DataSetName/InsertSize5000_    > $home/Log/$DataSetName/running/out.log
./art_illumina -ss HS25 -sam -i $home/spades/$DataSetName/scaffolds.fasta -p -ef  -l 76 -f 100 -m 7500 -s 750 -o $home/Auxiliary_lib/$DataSetName/InsertSize7500_    > $home/Log/$DataSetName/running/out.log
cd $home/Auxiliary_lib/
rm -rf $home/lib/$DataSetName/Auxiliary*
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize300_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize300_2.fq $home/lib/$DataSetName/ Auxiliary300
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize500_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize500_2.fq $home/lib/$DataSetName/ Auxiliary500
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize700_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize700_2.fq $home/lib/$DataSetName/ Auxiliary700
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize900_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize900_2.fq $home/lib/$DataSetName/ Auxiliary900
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize1500_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize1500_2.fq $home/lib/$DataSetName/ Auxiliary1500
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize2500_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize2500_2.fq $home/lib/$DataSetName/ Auxiliary2500
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize5000_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize5000_2.fq $home/lib/$DataSetName/ Auxiliary5000
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/Auxiliary_lib/$DataSetName/InsertSize7500_1.fq $home/Auxiliary_lib/$DataSetName/InsertSize7500_2.fq $home/lib/$DataSetName/ Auxiliary7500
echo  "Step-12: Quality statistics"
rm -rf $home/QSstatistics/$DataSetName/*
java  -classpath  $home/Program/ QSstatistics $home/lib/$DataSetName/LowDepthFastq.fastq  $home/QSstatistics/$DataSetName/
rm -rf $home/lib/$DataSetName/LowDepthFastq_Filter.fastq
java  -classpath  $home/Program/ QualityControl  $home/QSstatistics/$DataSetName/QSFile.fa $home/lib/$DataSetName/LowDepthFastq.fastq  $home/lib/$DataSetName/LowDepthFastq_Filter.fastq $FilterTHLow
echo  "Step-13: Generate final low depth fasta file"
rm -rf $home/lib/$DataSetName/LowDepth_Trimed.fasta
java  -classpath  $home/Program/ GenerateFinalLowDepthFasta $home/lib/$DataSetName/LowDepthFastq_Filter.fastq $home/lib/$DataSetName/
#echo  "Step-14: Low depth read assembly"
#cd $home/EPGA/$DataSetName/low/
#rm -rf $home/EPGA/$DataSetName/low/*
#epga  $home/lib/$DataSetName/LowDepth_Trimed.fasta $InsertLength1 $Sd_insert $KmerSize_Low 24
echo  "Step-14: Normal depth read assembly [EPGA]"
cd $home/EPGA/$DataSetName/after/
rm -rf $home/EPGA/$DataSetName/after/*
epga  $home/lib/$DataSetName/NormalDepthFasta.fasta $InsertLength1 $Sd_insert $home/lib/$DataSetName/Auxiliary500.fasta 500 50 $home/lib/$DataSetName/Auxiliary700.fasta 700 70 $home/lib/$DataSetName/Auxiliary900.fasta 900 90 $home/lib/$DataSetName/Auxiliary1500.fasta 1500 150 $home/lib/$DataSetName/Auxiliary2500.fasta 2500 250 $home/lib/$DataSetName/Auxiliary5000.fasta 5000 500 $home/lib/$DataSetName/Auxiliary7500.fasta 7500 750 $KmerSize_Normal 72
echo  "Step-15: Change lines"
rm -rf $home/MUM/$DataSetName/*
rm -rf $home/spades/$DataSetName/contig.$DataSetName.changgeLines.fa
java -classpath  $home/Program/ ChangeLines $home/spades/$DataSetName/ $home/spades/$DataSetName/scaffolds.fasta $DataSetName
echo  "Step-16: MUMmer algnment [spades->epga]"
rm -rf  $home/EPGA/$DataSetName/afterfilter/*
nucmer -c 50 -p $home/MUM/$DataSetName/liaoxingyu_nucmer1 $home/EPGA/$DataSetName/after/scaffoldLong.fa $home/spades/$DataSetName/contig.$DataSetName.changgeLines.fa
delta-filter -i 70 -q -r $home/MUM/$DataSetName/liaoxingyu_nucmer1.delta > $home/MUM/$DataSetName/liaoxingyu_filter1
show-coords -dTlro $home/MUM/$DataSetName/liaoxingyu_filter1 > $home/MUM/$DataSetName/reference_nucmer_out_coords1.txt
echo  "Step-17: Error break [epga contigs]"
rm -rf $home/EPGA/$DataSetName/after/EPGA_BreakErrorPoints.fa
java -classpath  $home/Program/ breakErrorPoints $home/MUM/$DataSetName/reference_nucmer_out_coords1.txt $home/EPGA/$DataSetName/after/scaffoldLong.fa $home/spades/$DataSetName/contig.$DataSetName.changgeLines.fa  $home/EPGA/$DataSetName/after/  31
echo  "Step-18: MUMmer algnment [spades->epga_error_free]"
rm -rf  $home/EPGA/$DataSetName/afterfilter/*
nucmer -c 50 -p $home/MUM/$DataSetName/liaoxingyu_nucmer2 $home/spades/$DataSetName/contig.$DataSetName.changgeLines.fa $home/EPGA/$DataSetName/after/EPGA_BreakErrorPoints.fa
delta-filter -i 100 -q -r $home/MUM/$DataSetName/liaoxingyu_nucmer2.delta > $home/MUM/$DataSetName/liaoxingyu_filter2
show-coords -dTlro $home/MUM/$DataSetName/liaoxingyu_filter2 > $home/MUM/$DataSetName/reference_nucmer_out_coords2.txt
echo  "Step-19: Extension Short EPGA contigs by using long SPAdes contigs"
rm -rf $home/EPGA/$DataSetName/after/ReplaceEPGAcontig.*
rm -rf $home/EPGA/$DataSetName/after/FreeSPAdesContig.$DataSetName.fa
java -classpath  $home/Program/ ReplaceIncludedEPGAcontigs $home/MUM/$DataSetName/reference_nucmer_out_coords2.txt $home/EPGA/$DataSetName/after/EPGA_BreakErrorPoints.fa $home/spades/$DataSetName/contig.$DataSetName.changgeLines.fa $DataSetName $home/EPGA/$DataSetName/after/
echo  "Step-20: Merging Complementary contigs"
cd $home/EPGA/$DataSetName/after/
rm -rf $home/EPGA/$DataSetName/after/SpadesUnmapped2EPGAcontigs.fa
rm -rf $home/MUM/$DataSetName/Spades2EPGAerrorfree.sam
bwa index -a bwtsw $home/EPGA/$DataSetName/after/ReplaceEPGAcontig.$DataSetName.fa
bwa mem -t 48 -x intractg $home/EPGA/$DataSetName/after/ReplaceEPGAcontig.$DataSetName.fa $home/EPGA/$DataSetName/after/FreeSPAdesContig.$DataSetName.fa > $home/MUM/$DataSetName/Spades2EPGAerrorfree.sam
samtools fasta -f 4 -0 $home/EPGA/$DataSetName/after/SpadesUnmapped2EPGAcontigs.fa $home/MUM/$DataSetName/Spades2EPGAerrorfree.sam 
rm -rf $home/EPGA/$DataSetName/afterfilter/FinalContig_AfterMergeCompContigs.fa
java  -classpath  $home/Program/ MergeCompContigs $home/EPGA/$DataSetName/afterfilter/ $home/EPGA/$DataSetName/after/ReplaceEPGAcontig.$DataSetName.fa $home/EPGA/$DataSetName/after/SpadesUnmapped2EPGAcontigs.fa
echo  "Step-21: SSPACE Scaffolding"
cd $home/SSPACE-STANDARD-3.0_linux-x86_64
rm -rf *.fa
rm -rf $home/SSPACE-STANDARD-3.0_linux-x86_64/scaffolds_sspace/*
gunzip $home/spades/$DataSetName/corrected/$lib1_left_name.corr.00.0_0.cor.fastq.gz
gunzip $home/spades/$DataSetName/corrected/$lib1_right_name.corr.00.0_0.cor.fastq.gz
rm -rf $home/lib/$DataSetName/HammerShort1.*
rm -rf $home/SSPACE-STANDARD-3.0_linux-x86_64/$DataSetName.txt
java  -classpath  $home/Program/ GenerateScaffoldConfig $home $home/SSPACE-STANDARD-3.0_linux-x86_64/ $DataSetName $InsertLength1 $Scaff_Sd
java  -classpath  $home/Program/ GenerateFastaFromFastqFiles $home/spades/$DataSetName/corrected/$lib1_left_name.corr.00.0_0.cor.fastq $home/spades/$DataSetName/corrected/$lib1_right_name.corr.00.0_0.cor.fastq $home/lib/$DataSetName/ HammerShort1
cp $home/EPGA/$DataSetName/afterfilter/FinalContig_AfterMergeCompContigs.fa $home/SSPACE-STANDARD-3.0_linux-x86_64/EPGA_FinalContig.fasta
perl SSPACE_Standard_v3.0.pl -l ./$DataSetName.txt -s ./EPGA_FinalContig.fasta -b ./scaffolds_sspace  -T 64
cp $home/SSPACE-STANDARD-3.0_linux-x86_64/EPGA_FinalContig.fasta $home/EPGA/$DataSetName/afterfilter/EPGA_FinalContig2.fasta
cp $home/SSPACE-STANDARD-3.0_linux-x86_64/scaffolds_sspace/scaffolds_sspace.final.scaffolds.fasta $home/EPGA/$DataSetName/afterfilter/EPGA_FinalScaffold2.fasta
java -classpath  $home/Program/ ChangeLines $home/EPGA/$DataSetName/afterfilter/ $home/EPGA/$DataSetName/afterfilter/EPGA_FinalScaffold2.fasta $DataSetName
java  -classpath  $home/Program/ MergeScaffolds $home/EPGA/$DataSetName/afterfilter/contig.$DataSetName.changgeLines.fa $home/EPGA/$DataSetName/afterfilter/
echo  "Step-22: Quast evalution"
quast.py $home/spades/$DataSetName/contigs.fasta $home/spades/$DataSetName/scaffolds.fasta $home/EPGA/$DataSetName/after/scaffoldLong.fa $home/EPGA/$DataSetName/after/EPGA_BreakErrorPoints.fa $home/EPGA/$DataSetName/after/ReplaceEPGAcontig.$DataSetName.fa $home/EPGA/$DataSetName/afterfilter/FinalContig_AfterMergeCompContigs.fa $home/EPGA/$DataSetName/afterfilter/EPGA_FinalContig2.fasta $home/EPGA/$DataSetName/afterfilter/Final_EPGAScaffolds2.fa -m 500 -R $ReferenceName -o $home/Quast/$DataSetName/
cp $home/EPGA/$DataSetName/afterfilter/EPGA_FinalContig2.fasta $home/Final_Assemblies/$DataSetName/Contigs.fa
cp $home/EPGA/$DataSetName/afterfilter/Final_EPGAScaffolds2.fa $home/Final_Assemblies/$DataSetName/Scaffolds.fa
echo  "Step-23: EPGA-SC assembly completed successfully. Thank you!"