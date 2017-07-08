# HESML V1R3

HESML Java software library of ontology-based semantic similarity measures and information content models

This is the GitHub public repository for the HESML Java software library of ontology-based semantic similarity measures and information content models.

The initial master code of this repository matches the HESML V1R2 version available as Mendeley dataset at http://dx.doi.org/10.17632/t87s78dg78.2.

HESML V1R3 is the third release of the Half-Edge Semantic Measures Library (HESML) [1], which is a new, scalable and efficient Java software library of ontology-based semantic similarity measures and Information Content (IC) models based on WordNet.

HESML V1R3 implements most ontology-based semantic similarity measures and Information Content (IC) models based on WordNet reported in the literature. In addition, it provides a XML-based input file format in order to specify the execution of reproducible experiments on WordNet-based similarity, even with no software coding.

HESML is introduced and detailed in a companion reproducibility paper [1] of the methods and experiments introduced in [2,3,4].

The main features of HESML are as follows: (1) it is based on an efficient and linearly scalable representation for taxonomies called PosetHERep introduced in [1], (2) its performance exhibits a linear scalability as regards the size of the taxonomy, and (3) it does not use any caching strategy of vertex sets.

Main novelties in HESML V1R3

The V1R2 release significantly improves the performance of HESML V1R1, whilst HESML V1R3 provides two minor changes on HESML V1R2. First, the vertex ID has been updated from Integer to Long type in order to support a larger number of vertexes. Second, we have added five new similarity measures detailed in the papers below:

(1) Hao, D., Zuo, W., Peng, T., & He, F. (2011). An Approach for Calculating Semantic Similarity between Words Using WordNet. In Proc. of the Second International Conference on Digital Manufacturing Automation (pp. 177–180). IEEE.

(2) Liu, X. Y., Zhou, Y. M., & Zheng, R. S. (2007). Measuring Semantic Similarity in Wordnet. In Proc. of the 2007 International Conference on Machine Learning and Cybernetics (Vol. 6, pp. 3431–3435). IEEE.

(3) Pekar, V., & Staab, S. (2002). Taxonomy Learning: Factoring the Structure of a Taxonomy into a Semantic Classification Decision. In Proceedings of the 19th International Conference on Computational Linguistics (Vol. 1, pp. 1–7). Stroudsburg, PA, USA: Association for Computational Linguistics.

(4) Stojanovic, N., Maedche, A., Staab, S., Studer, R., & Sure, Y. (2001). SEAL: A Framework for Developing SEmantic PortALs. In Proceedings of the 1st International Conference on Knowledge Capture (pp. 155–162). New York, NY, USA: ACM.

Licensing information

HESML library is freely distributed for any non-commercial purpose under a CC By-NC-SA-4.0 license, subject to the citing of the main HESML paper [1] as attribution requirement. On other hand, the commercial use of the similarity measures introduced in [2], as well as part of the intrinsic IC models introduced in [3] and [4], is protected by a patent application [5]. In addition, any user of HESML must fulfill other licensing terms described in [1] related to other resources distributed with the library, such as WordNet and a dataset of corpus-based IC models, among others.

References:

[1] Lastra-Díaz, J. J., García-Serrano, A., Batet, M., Fernández, M. and Chirigati, F. (2017). HESML: a scalable ontology-based semantic similarity measures library with a set of reproducible experiments and a replication dataset. Information Systems. http://dx.doi.org/10.1016/j.is.2017.02.002

[2] Lastra-Díaz, J. J., & García-Serrano, A. (2015). A novel family of IC-based similarity measures with a detailed experimental survey on WordNet. Engineering Applications of Artificial Intelligence Journal, 46, 140-153. http://dx.doi.org/10.1016/j.engappai.2015.09.006

[3] Lastra-Díaz, J. J., & García-Serrano, A. (2015). A new family of information content models with an experimental survey on WordNet. Knowledge-Based Systems, 89, 509-526. http://dx.doi.org/10.1016/j.knosys.2015.08.019

[4] Lastra-Díaz, J. J., & García-Serrano, A. (2016). A refinement of the well-founded Information Content models with a very detailed experimental survey on WordNet. Universidad Nacional de EducaciÃƒÂ³n a Distancia (UNED). http://e-spacio.uned.es/fez/view/bibliuned:DptoLSI-ETSI-Informes-Jlastra-refinement

[5] Lastra Díaz, J. J., & García Serrano, A. (2016). System and method for the indexing and retrieval of semantically annotated data using an ontology-based information retrieval model. United States Patent and Trademark Office (USPTO) Application, US2016/0179945 A1.

Steps to reproduce the library

HESML V1R3 is distributed as a Java class library (HESML-V1R3.jar) plus a test driver application (HESMLclient.jar), which have been developed using NetBeans 8.0.2 for Windows, although it has been also compiled and evaluated on Linux-based platforms using the corresponding NetBeans versions.

In order to compile HESML, you must follow the following steps:

(1) Download the full distribution of HESML V1R3.

(2) Install Java 8, Java SE Dev Kit 8 and NetBeans 8.0.2 or higher in your workstation.

(3) Launch NetBeans IDE and open the HESML and HESMLclient projects contained in the root folder. NetBeans automatically detects the presence of a nbproject subfolder with the project files.

(4) Select HESML and HESMLclient projects in the project treeview respectively. Then, invoke the "Clean and Build project (Shift + F11)" command in order to compile both projects.

In order to remain up to date on new HESML versions, as well as asking for technical support, we invite the readers to subscribe to the HESML forum by sending an email to the following address:

hesml+subscribe@googlegroups.com

Steps to use the library

You can use the HESMLclient program to run reproducible experiments or create your own client programs using the HESMLclient code as example. For more information, including a detailed description of how to run the reproducible experiments and extending the library, we refer the reader to the paper [1] above.
