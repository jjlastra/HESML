# HESML V2R1 (2.1.0.0)

HESML V2R1 Java software library of ontology-based semantic similarity measures and information content (IC) models. The main novelty of HESML V2R1 is the implementation of most sentence similarity methods for the biomedical domain.

This is the GitHub public repository for the HESML Java software library of ontology-based semantic similarity measures and Information Content (IC) models.

The initial master code of this repository matches the HESML V1R2 version available as Mendeley dataset at http://dx.doi.org/10.17632/t87s78dg78.2.

HESML V2R1 is the sixth release of the Half-Edge Semantic Measures Library (HESML) [1], which is a new, scalable and efficient Java software library of ontology-based semantic similarity measures and Information Content (IC) models, which also implements the evaluation of pre-trained word embedding models and sentence similarity methods for the biomedical domain.

HESML V2R1 implements most ontology-based semantic similarity measures and Information Content (IC) models reported in the literature which are based on WordNet, SNOMED-CT, MeSH, GO and OBO-based ontologies. In addition, it provides a XML-based input file format in order to specify the execution of reproducible experiments on word/concept similarity based on WordNet, SNOMED-CT, MeSH, and GO ontologies, even with no software coding.

HESML is introduced and detailed in a companion reproducibility paper [1, 7] of the methods and experiments introduced in [2,3,4, 6].

The main features of HESML are as follows: (1) it is based on an efficient and linearly scalable representation for taxonomies called PosetHERep introduced in [1], (2) its performance exhibits a linear scalability as regards the size of the taxonomy, and (3) it does not use any caching strategy of vertex sets.

# Main novelties in HESML V2R1

Main novelties provided by HESML V2R1 are as follows:

(1) The main novelties introduced by HESML V2R1 are as follows: (1) the software implementation of a new package for the evaluation of sentence similarity methods; 

(2) the software implementation of most of the sentence similarity methods in the biomedical domain; 

(3) the implementation of a new package for sentence pre-processing together with a set of sentence pre-processing configurations; 

(4) the integration of the three main biomedical NER tools, Metamap , MetamapLite and cTAKES; 

(5) the software implementation of a parser based on the averaging Simple Word EMbeddings (SWEM) models introduced by Shen et al. for efficiently loading and evaluating FastText-based  and other word embedding models; 

(6) the integration of Python wrappers for the evaluation of BERT Universal Sentence Encoder (USE) and Flair models; and finally, 

(7) the software implementation of a new string-based sentence similarity method based on the aggregation of the Li et al. similarity and Block distance measures, called LiBlock, as well as eight new variants of the ontology-based methods proposed by Sogancioglu et al., and a new pre-trained word embedding model based on FastText and trained on the full-text of the articles in the PMC-BioC corpus.

# Licensing information

HESML library is freely distributed for any non-commercial purpose under a CC By-NC-SA-4.0 license, subject to the citing of the main HESML papers [1, 7] as attribution requirement. On other hand, the commercial use of the similarity measures introduced in [2], as well as part of the intrinsic IC models introduced in [3] and [4], is protected by a patent application [5]. In addition, any user of HESML must fulfill other licensing terms described in [1] related to other resources distributed with the library, such as WordNet and a dataset of corpus-based IC models, among others.

# References:

[1] Lastra-Díaz, J. J., García-Serrano, A., Batet, M., Fernández, M. and Chirigati, F. (2017). HESML: a scalable ontology-based semantic similarity measures library with a set of reproducible experiments and a replication dataset. Information Systems 66, 97-118. http://dx.doi.org/10.1016/j.is.2017.02.002

[2] Lastra-Díaz, J. J., & García-Serrano, A. (2015). A novel family of IC-based similarity measures with a detailed experimental survey on WordNet. Engineering Applications of Artificial Intelligence Journal, 46, 140-153. http://dx.doi.org/10.1016/j.engappai.2015.09.006

[3] Lastra-Díaz, J. J., & García-Serrano, A. (2015). A new family of information content models with an experimental survey on WordNet. Knowledge-Based Systems, 89, 509-526. http://dx.doi.org/10.1016/j.knosys.2015.08.019

[4] Lastra-Díaz, J. J., & García-Serrano, A. (2016). A refinement of the well-founded Information Content models with a very detailed experimental survey on WordNet. Universidad Nacional de Educación a Distancia (UNED). http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement

[5] Lastra Díaz, J. J., & García Serrano, A. (2016). System and method for the indexing and retrieval of semantically annotated data using an ontology-based information retrieval model. United States Patent and Trademark Office (USPTO) Application, US2016/0179945 A1.

[6] J.J. Lastra-Díaz, J. Goikoetxea, M. Hadj Taieb, A. García-Serrano, M. Ben Aouicha, E. Agirre, A reproducible survey on word embeddings and ontology-based methods for word similarity: linear combinations outperform the state of the art, Engineering Applications of Artificial Intelligence. 85 (2019) 645–665. https://doi.org/10.1016/j.engappai.2019.07.010

[7] J.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano, HESML: a real-time semantic measures library for the biomedical domain with a reproducible survey, Submitted for Publication. (2020).

[8] R. Gentleman, Visualizing and distances using GO, URL Http://www. Bioconductor. Org/docs/vignettes. Html. 38 (2005). http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.592.2206&rep=rep1&type=pdf.

[9] C. Pesquita, D. Faria, H. Bastos, A. Falcão, F. Couto, Evaluating GO-based semantic similarity measures, in: Proc. 10th Annual Bio-Ontologies Meeting, 2007: p. 38.

[10] F. Azuaje, H. Wang, O. Bodenreider, Ontology-driven similarity approaches to supporting gene functional assessment, in: Proceedings of the ISMB’2005 SIG Meeting on Bio-Ontologies, academia.edu, 2005: pp. 9–10.

[11] J.L. Sevilla, V. Segura, A. Podhorski, E. Guruceaga, J.M. Mato, L.A. Martínez-Cruz, F.J. Corrales, A. Rubio, Correlation between gene expression and GO semantic similarity, IEEE/ACM Trans. Comput. Biol. Bioinform. 2 (2005) 330–338.

[12] J.J. Lastra-Díaz, A. Lara-Clares, A. García-Serrano, Reproducibility dataset for a benchmark of biomedical semantic measures libraries, (2020). https://doi.org/10.21950/OTDA4Z.

[13] Lara-Clares, Alicia; Lastra-Díaz, Juan J.; Garcia-Serrano, Ana, Reproducible experiments on word and sentence similarity measures for the biomedical domain (2021), https://doi.org/10.21950/EPNXTR

Steps to reproduce the library

HESML V2R1 is distributed as a Java class library (HESML-V2R1.0.1.jar) plus three test driver applications (HESMLclient.jar, HESML_GOclien, HESML_UMLScliet), which have been developed using NetBeans 8.2 for Windows, although it has been also compiled and evaluated on Linux-based platforms using the corresponding NetBeans versions.

The HESML-V2R1.0.1.jar file is already included in the HESML_Library\HESML\dist folder of the HESML_Release_V2R1.zip distribution file. In order to compile HESML from its source files, you must follow the following steps:

(1) Download the full distribution of HESML V2R1.

(2) Install Java 8, Java SE Dev Kit 8 and NetBeans 8.0.2 or higher in your workstation.

(3) Launch NetBeans IDE and open the HESML and client projects contained in the root folder. NetBeans automatically detects the presence of a nbproject subfolder with the project files.

(4) Select HESML, HESMLSTS and client projects in the project treeview respectively. Then, invoke the "Clean and Build project (Shift + F11)" command in order to compile both projects.

In order to remain up to date on new HESML versions, as well as asking for technical support, we invite the readers to visit http://hesml.lsi.uned.es and subscribe to the HESML forum by sending an email to the following address:

hesml+subscribe@googlegroups.com

Steps to use the library

You can use the HESML client programs to run reproducible experiments or create your own client programs using the client source code as example. For more information, including a detailed description of how to run the reproducible experiments and extending the library, we refer the reader to the introductory paper [1] cited above.
