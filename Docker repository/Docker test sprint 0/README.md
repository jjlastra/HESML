# HESML-STS --- sprint 0

## Features implemented in HESML-STS --- sprint 0
	- String-based sentence similarity measures
	- Preprocessing methods: Char filtering, stop words removal, tokenizers and lowe casing.
	- Benchmark datasets: BIOSSES, MedSTS, CTR
	- Expriment configuration file: string_benchmark.stsexp
	- R script for post-processing results. Output in .csv and .latex formats.


> Requisites: **Docker installed in an operating system**
## How to run the experiments

1. Download the Dockerfile from this directory
2. Save the file in an empty directory, open a terminal and compile the image (up to 10 minutes):
```sh
[sudo] docker build --no-cache -t hesml_test .
```
3. Create the container. It is not necessary to start the container!
```sh
[sudo] docker create -it --name "hesml_test" hesml_test:latest
```
4. Copy the raw and processed output files into the directory

```sh
[sudo] docker cp hesml_test:/home/user/HESML/HESML/HESML_Library/ReproducibleExperiments/BioSentenceSimilarity_paper/BioSentenceSimFinalProcessedOutputFiles .
```
```sh
[sudo] docker cp hesml_test:/home/user/HESML/HESML/HESML_Library/ReproducibleExperiments/BioSentenceSimilarity_paper/BioSentenceSimFinalRawOutputFiles .
```

## Pending features 

- Measures:
    - Word/Sentence embedding methods (SWEM, USE, ...) - sprint 2
    - Ontology-based methods (UBSM, WBSM) - sprint 3
    - Language models methods (BERT) - sprint 5
- Preprocessing:
    - NERs: Metamap, CTakes. - sprint 4



 
## License

> Copyright (C) 2016-2020 Universidad Nacional de Educación a Distancia (UNED)
>
> This program is free software for non-commercial use:
> you can redistribute it and/or modify it under the terms of the
> Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
> (CC BY-NC-SA 4.0) as published by the Creative Commons Corporation,
> either version 4 of the License, or (at your option) any later version.
>
> This program is distributed in the hope that it will be useful,
> but WITHOUT ANY WARRANTY; without even the implied warranty of
> MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
> section 5 of the CC BY-NC-SA 4.0 License for more details.
>
> You should have received a copy of the Creative Commons
> Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0) 
> license along with this program. If not,
> see <http://creativecommons.org/licenses/by-nc-sa/4.0/>.

