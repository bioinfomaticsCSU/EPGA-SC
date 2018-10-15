Latest Version
==============
Please see the latest version of EPGA-SC:https://github.com/bioinfomaticsCSU/EPGA-SC


License
=======

Copyright (C) 2017 Jianxin Wang(jxwang@mail.csu.edu.cn), Xingyu Liao(liaoxingyu@csu.edu.cn)

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, see <http://www.gnu.org/licenses/>.

Jianxin Wang(jxwang@mail.csu.edu.cn), Xingyu Liao(liaoxingyu@csu.edu.cn)
School of Information Science and Engineering
Central South University
ChangSha
CHINA, 410083


Installation and operation of EPGA-SC 
==================================

### Dependencies

When running EPGA-SC from GitHub source the following tools are
required:
* [jdk.1.8.0](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [dsk.2.1.0](http://minia.genouest.org/dsk/)
* [karect.1.0](http://aminallam.github.io/karect/)
* [SPAdes.3.12.0](http://cab.spbu.ru/software/spades/)
* [EPGA](https://github.com/bioinfomaticsCSU/EPGA)
* [mummer-4.0](https://mummer4.github.io/index.html)
* [quast.4.3](https://sourceforge.net/projects/quast/files/)

### Add system environment variables

The user can modify the system environment variables with the following commands:

vim /etc/profile

export JAVA_HOME="/usr/local/jdk1.8.0_20/bin"

export DSK_HOME="/home/.../dsk-2.1.0-Linux/bin"

export karet_HOME=/home/.../karect

export SPAdes_HOME=/home/.../SPAdes-3.12.0-Linux/bin

export EPGA_HOME=/home/.../EPGA-master

export MUMmer_HOME=/home/.../mummer-4.0/bin

export Quast_HOME=/home/.../quast-4.3

export PATH="$JAVA_HOME:$DSK_HOME:$karet_HOME:$SPAdes_HOME:$EPGA_HOME:$MUMmer_HOME:$Quast_HOME:$PATH"

source /etc/profile
 
### Install EPGA-SC

EPGA-SC automatically compiles all its sub-parts when needed (on the first use). 
Thus, installation is not required.

### Run EPGA-SC.

    (1)Loading library files to the working directory of EPGA-SC. Before running EPGA-SC, we need to load the library files into lib folder under the working directory of EPGA-SC(/home/.../EPGA-SC/lib/).
	
	For example:
	
	cp /home/.../ecoli_mda_lane8.1.fastq  /home/.../EPGA-SC/lib/  ('ecoli_mda_lane8.1.fastq' is the left mate reads of the library)
	cp /home/.../ecoli_mda_lane8.2.fastq  /home/.../EPGA-SC/lib/  ('ecoli_mda_lane8.2.fastq' is the right mate reads of the library)
	
	(2)Note that the naming prefix of the paired-end reads files is consistent with the name of the dataset (The value of variable 'DataSetName').
	
	For example:
	
	The library is composed of paired-end reads('ecoli_mda_lane8.1.fastq' and 'ecoli_mda_lane8.2.fastq'), and the value of DataSetName is 'ecoli_mda_lane8'.
	
	DataSetName=ecoli_mda_lane8
    lib1_left_name=ecoli_mda_lane8.1
    lib1_right_name=ecoli_mda_lane8.2
	
### Edit the configuration:
    
	Before running EPGA-SC, we need to configure the config.txt file(/home/.../EPGA-SC/config.txt).
    
	For example:
    
    home=/homed/liaoxingyu/EPGA-SC
    DataSetName=ecoli_mda_lane8
    lib1_left_name=ecoli_mda_lane8.1
    lib1_right_name=ecoli_mda_lane8.2
    Read_Length=100
    InsertLength1=266
    Sd_insert=25
    ReferenceName=/homee/liaoxingyu/EPGA-SC/Reference/EcoliCompleteReference.fasta
    dsk_bin=/home/liaoxingyu/ClusterTool/dsk-2.1.0-Linux/bin
	
	* 'home': The working directory of EPGA-SC.  eg: If EPGA-SC is stored in the directory of "/home/.../tool/EPGA-SC", home=/home/tool/.../EPGA-SC.
	* 'DataSetName': The name of the dataset. eg: DataSetName=ecoli_mda_lane8.
	* 'lib_left_name': The name of the left fastq file of the library. eg: If the left fastq file of the library is "ecoli_mda_lane8.1.fastq" , then lib1_left_name=ecoli_mda_lane8.1
	* 'lib_right_name': The name of the right fastq file of the library. eg: If the right fastq file of the library is "ecoli_mda_lane8.2.fastq" , then lib1_right_name=ecoli_mda_lane8.2
	* 'Read_length': The average length of reads.
	* 'InsertLength1':  The average insertsizes of paired-end reads. 
	* 'Sd_insert': The standard deviation of insertsizes.
	* 'ReferenceName': The storage path of the reference file(only used in quast evluation). eg: If the reference file(reference.fa) is stored in the directory of "/home/tool/reference/", then ReferenceName=/home/tool/reference/reference.fa. 
	* 'dsk_bin': The path of the bin directory of dsk. eg: If dsk is installed in the directory of "/home/tool/dsk", then dsk_bin=/home/tool/dsk/bin.
    
### Run the following command to start the EPGA-SC.
     
	cd /home/.../EPGA-SC
	./run.sh
    
	If the system prompts "operation not permitted" ,we need to run the following commands to modify the permissions of EPGA-SC folder at this time.
    
	cd ..
	chmod -R 777  EPGA-SC
	cd EPGA-SC
	./run.sh

### Output.
    
	(1)The final assemblies.
    
        /home/.../EPGA-SC/Final_Assemblies/$DataSetName/Contigs.fa
		
		/home/.../EPGA-SC/Final_Assemblies/$DataSetName/Scaffolds.fa

	(2)The quast evalutions of the final assemblies.

	    /homee/.../EPGA-SC/Quast/$DataSetName/
