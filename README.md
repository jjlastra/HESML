# HESML
HESML Java software library of ontology-based semantic similarity measures and information content models

This is the GitHub public repository for the HESML Java software library of ontology-based semantic similarity measures and information content models.

The initial master code of this repository matches the HESML V1R2 version available as Mendeley dataset at http://dx.doi.org/10.17632/t87s78dg78.2.

HESML V1R2 is the second release of the Half-Edge Semantic Measures Library (HESML) [1], which is a new, scalable and efficient Java software library of ontology-based semantic similarity measures and Information Content (IC) models based on WordNet.

HESML V1R2 implements most ontology-based semantic similarity measures and Information Content (IC) models based on WordNet reported in the literature. In addition, it provides a XML-based input file format in order to specify the execution of reproducible experiments on WordNet-based similarity, even with no software coding.

The V1R2 release significantly improves the performance of HESML V1R1. HESML is introduced and detailed in a companion reproducibility paper [1] of the methods and experiments introduced in [2,3,4].

The main features of HEMSL are as follows: (1) it is based on an efficient and linearly scalable representation for taxonomies called PosetHERep introduced in [1], (2) its performance exhibits a linear scalability as regards the size of the taxonomy, and (3) it does not use any caching strategy of vertex sets.

Licensing information

HESML V1R2 is freely distributed for any non-commercial purpose under a CC By-NC-SA-4.0 license, subject to the citing of the main HESML paper [1] as attribution requirement. On other hand, the commercial use of the similarity measures introduced in [2], as well as part of the intrinsic IC models introduced in [3] and [4], is protected by a patent application [5]. In addition, any user of HESML must fulfill other licensing terms described in [1] related to other resources distributed with the library, such as WordNet and a dataset of corpus-based IC models, among others.

References:

[1] Lastra-Díaz, J. J., & García-Serrano, A. (2017). HESML: a scalable ontology-based semantic similarity measures library with a set of reproducible experiments and a replication dataset. Information Systems.

[2] Lastra-Díaz, J. J., & García-Serrano, A. (2015). A novel family of IC-based similarity measures with a detailed experimental survey on WordNet. Engineering Applications of Artificial Intelligence Journal, 46, 140â€“153. http://dx.doi.org/10.1016/j.engappai.2015.09.006

[3] Lastra-Díaz, J. J., & García-Serrano, A. (2015). A new family of information content models with an experimental survey on WordNet. Knowledge-Based Systems, 89, 509â€“526. http://dx.doi.org/10.1016/j.knosys.2015.08.019

[4] Lastra-Díaz, J. J., & García-Serrano, A. (2016). A refinement of the well-founded Information Content models with a very detailed experimental survey on WordNet. Universidad Nacional de EducaciÃ³n a Distancia (UNED). http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement

[5] Lastra Díaz, J. J., & García Serrano, A. (2016). System and method for the indexing and retrieval of semantically annotated data using an ontology-based information retrieval model. United States Patent and Trademark Office (USPTO) Application, US2016/0179945 A1.

Steps to reproduce the library

HESML V1R2 is distributed as a Java class library (HESML-V1R2.jar) plus a test driver application (HESMLclient.jar), which have been developed using NetBeans 8.0.2 for Windows, although it has been also compiled and evaluated on Linux-based platforms using the corresponding NetBeans versions.

In order to compile HESML, you must follow the following steps:

(1) Download the full distribution of HESML V1R2..

(2) Install Java 8, Java SE Dev Kit 8 and NetBeans 8.0.2 or higher in your workstation.

(3) Launch NetBeans IDE and open the HESML and HESMLclient projects contained in the root folder. NetBeans automatically detects the presence of a nbproject subfolder with the project files.

(4) Select HESML and HESMLclient projects in the project treeview respectively. Then, invoke the "Clean and Build project (Shift + F11)" command in order to compile both projects.

In order to remain up to date on new HESML versions, as well as asking for technical support, we invite the readers to subscribe to the HESML forum by sending an email to the following address:

hesml+subscribe@googlegroups.com

For more information, we refer the reader to the paper below:

Lastra-Díaz, J. J., & García-Serrano, A. (2017). HESML: a scalable ontology-based semantic similarity measures library with a set of reproducible experiments and a replication dataset. Information Systems.
