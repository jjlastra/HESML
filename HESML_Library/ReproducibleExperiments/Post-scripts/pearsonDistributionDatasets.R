# We clear all session variables

rm(list = ls())

# IMPORTANT:configuration of the input/output directories
# We define below the input directory for the input raw results
# in CSV file format, and the output directory for the
# final assembled tables in CSV file format.
# You must change these values in order to
# point to the proper directories in your hard drive.
# We also define below the name of the input raw CSV files
# containing the experimental results.

# The input and output directories below must end with '/' in
# Unix-like format to be compatible with Windows
# or Linux-based R distributions.

# First, we get the current file script directory

current_working_dir <- dirname(rstudioapi::getActiveDocumentContext()$path)

# and we set the working directory for the R project

setwd(current_working_dir)

# Define the root, input and output paths

rootDir = "../BioSentenceSimilarity_paper"
experimentSubdirectory = "BioSentenceSimFinalRawOutputFiles"
preprocessedExperimentsSubdirectory = "BioSentenceSimFinalProcessedOutputFiles"

inputDir =  paste(rootDir, "/", experimentSubdirectory, "/", sep = "")
outputDir =  paste(rootDir, "/", preprocessedExperimentsSubdirectory, "/", sep = "")

# We load the input raw results file for best combinations (FINAL RESULTS)

source(paste("bio_sentence_sim_scripts", "readBESTCOMBS.R", sep = "/"), local = knitr::knit_global())


# Size of the subset: m_dimSubset

calculate_pearson_spearman_harmonic_values_from_subsets <- function(m_dimSubset, rawDataHumanAndMeasure, total_combinations) 
{
  ########
  
  # We create all the necessary combinations, we obtain the indexes of the subsets we will create.
  
  # With the subindexes, we will create all the datasets for the subsequent calculation of h
  
  #######
  
  # Generation Cursor
  
  m_firstCombination = 1;
  
  # Number of elements in the subset
  
  m_nElements = length(rawDataHumanAndMeasure[,1])
  
  # We construct the vector of values and initialize it in increasing order
  
  m_currentSubset = c(1:m_dimSubset)
  
  m_currentSubset
  
  # Auxiliary variables using Mifsud's notation
  
  r <- m_dimSubset;
  n <- m_nElements;
  
  # We move the generator forward
  
  m_firstCombination = TRUE
  
  
  count_combination = 0
  
  # Create a list of subsets
  
  subsets <- list()
  
  # Iterate to create total_combinations subsets
  
  while (count_combination < total_combinations) 
  {
    # Increment the counter to create total_combinations subsets
    
    count_combination = count_combination + 1
  
    if (m_firstCombination)
    {
      # print("It is first combination")
      
      m_firstCombination <- FALSE;
      newValue <- TRUE;
      
    } 
    else if (m_currentSubset[r - 1] < n - 1) 
    {
      # print("It's not first combination")
      
      m_currentSubset[r - 1] <- m_currentSubset[r - 1] + 1;
      newValue <- TRUE;
      
    }
    else
    {
      # print("FOR loop")
      
      for(j in (r-1:0))
      {
        
        if (m_currentSubset[j - 1] < n - r + j - 1)
          {
          
          m_currentSubset[j - 1] <- m_currentSubset[j - 1] + 1;
          newValue = TRUE;
          
          for(s in (j:r))
          {
            m_currentSubset[s] = m_currentSubset[j - 1] + s - (j - 1);
            break;
          }
        }
      }
    }
    
    # Append the subset 
    
    # append(subsets,m_currentSubset)
    
    subsets[[length(subsets)+1]] <- m_currentSubset
  }
  
  # # Write results to a file
  # 
  # #define file name
  # sink('my_list.txt')
  # 
  # #print my_list to file
  # for(k in (1:total_combinations))
  # {
  #   print(subsets[k])
  # }
  # 
  # #close external connection to file 
  # sink()
  
  
  ########
  
  # For each subset, we get the human and liblock similarity values matrix
  
  #######
  
  # Test function - the first iteration should output the results for the best LiBlock combination in the MedSTS dataset : 0.769 0.710 0.739
  
  # k <- 1
  # subset_index <- subsets[k][[1]]
  # 
  # print(subset_index)
  # 
  # sim_values_matrix <- rawDataHumanAndLiBlock[c(subset_index),]
  # 
  # table_Pearson <- cor(sim_values_matrix[,1], sim_values_matrix[, 2], use="complete.obs", method = "pearson")
  # table_Spearman <- cor(sim_values_matrix[,1], sim_values_matrix[, 2], use="complete.obs", method = "spearman")
  # table_Harmonic <- 2 * table_Pearson * table_Spearman / (table_Pearson + table_Spearman)
  
  # We initialize the lists for Pearson, Spearman and Harmonic values
  
  pearson_values <- list()
  spearman_values <- list()
  harmonic_values <- list()
  
  # We iterate all the subsets
  
  for(k in (1:total_combinations))
  {
    # We get the indexes of the subset
    
    subset_index <- subsets[k][[1]]
    
    # We extract the similarity values
    
    sim_values_matrix <- rawDataHumanAndMeasure[c(subset_index),]
    
    # We calculate Pearson, Spearman and Harmonic values
  
    value_Pearson <- cor(sim_values_matrix[,1], sim_values_matrix[, 2], use="complete.obs", method = "pearson")
    value_Spearman <- cor(sim_values_matrix[,1], sim_values_matrix[, 2], use="complete.obs", method = "spearman")
    value_Harmonic <- 2 * value_Pearson * value_Spearman / (value_Pearson + value_Spearman)
  
    pearson_values <- append(pearson_values, value_Pearson)
    spearman_values <- append(spearman_values, value_Spearman)
    harmonic_values <- append(harmonic_values, value_Harmonic)
  }
  return(list(unlist(pearson_values),unlist(spearman_values),unlist(harmonic_values)))
}

# Create the histogram

create_histogram <- function(harmonic_values_dim_min, harmonic_values_dim_med, harmonic_values_dim_med2, harmonic_values_dim_max, rawDataHumanAndMeasure, xaxisTitle, methodName,filename)
{
  hgA <- hist(harmonic_values_dim_min, plot = FALSE) # Save first histogram data
  hgB <- hist(harmonic_values_dim_med, plot = FALSE) # Save 2nd histogram data
  hgC <- hist(harmonic_values_dim_med2, plot = FALSE) # Save 2nd histogram data
  hgD <- hist(harmonic_values_dim_max, plot = FALSE) # Save 2nd histogram data


  # pearson_chi_sq_harmonic_values_dim_min <- PearsonTest(harmonic_values_dim_min, adjust = TRUE)
  # pearson_chi_sq_harmonic_values_dim_min
  # print(pearson_chi_sq_harmonic_values_dim_min$p.value)

  range_breaks <- range(c(hgA$breaks, hgB$breaks, hgC$breaks, hgD$breaks)) # Get range for x-axis
  max_vals <- max(c(hgA$count, hgB$count, hgC$count, hgD$count)) # Get range for y-axis
  
  # 1. Open jpeg file
  # jpeg(file=filename)
  svg(filename)

  plot(hgA, col = 'lightblue', xlim = c(range_breaks[1], range_breaks[2]+0.02), ylim = c(0,max_vals), xlab=xaxisTitle, ylab="Frequency", main=paste(methodName," frequency distribution")) # Plot 1st histogram using a transparent color
  plot(hgB, col = 'lightcoral', add = TRUE) # Add 2nd histogram using different color
  plot(hgC, col = 'lightsalmon3', add = TRUE) # Add 2nd histogram using different color
  plot(hgD, col = 'lightgrey', add = TRUE) # Add 2nd histogram using different color
  
  legend(x = "topright",         # Posición
         legend = c("#100", "#300","#600","#900"), # Textos de la leyenda
         lty = c(1, 1,1,1),          # Tipo de líneas
         col = c('lightblue', 'lightcoral', 'lightsalmon3', 'lightgrey'),          # Colores de las líneas
         lwd = 2)                # Ancho de las líneas
  
  dev.off()
}

executeUsingMeasure <- function(columnIdentifier, methodName,filename)
{
  # We create the human and method similarity values matrix
  
  # defining data in the vector
  
  rawDataHumanAndLiBlock <- rawdata_MedSTS_BESTCOMBS[,c(1,columnIdentifier)] # usaremos más adelante esto como índice para extraer los datasets de cada subconjunto
  
  # Example of rawDataHumanAndLiBlock:
  #
  # head(rawDataHumanAndLiBlock)
  # 
  # Human   LiBlock
  # 1  3.50 0.8432335
  # 2  2.50 0.6477486
  # 3  3.45 0.6060388
  # 4  4.00 0.8427821
  # 5  3.00 0.2565587
  # 6  2.00 0.3600164
  
  ## Example 1 - Histogram with m_dimSubset_one and m_dimSubset_two samples
  
  values_dim_min <- calculate_pearson_spearman_harmonic_values_from_subsets(100,rawDataHumanAndLiBlock, 10000)
  values_dim_med1 <- calculate_pearson_spearman_harmonic_values_from_subsets(300,rawDataHumanAndLiBlock, 10000)
  values_dim_med2 <- calculate_pearson_spearman_harmonic_values_from_subsets(600,rawDataHumanAndLiBlock, 10000)
  values_dim_max <- calculate_pearson_spearman_harmonic_values_from_subsets(900,rawDataHumanAndLiBlock, 10000)
  
  # pearson
  
  # create_histogram(unlist(values_dim_min[1]), unlist(values_dim_med1[1]),unlist(values_dim_med2[1]),unlist(values_dim_max[1]),rawDataHumanAndLiBlock, "Pearson correlation score", methodName,paste(filename,"_pearson_10ksubsets.jpeg"))
  # 
  # # spearman
  # 
  # create_histogram(unlist(values_dim_min[2]), unlist(values_dim_med1[2]),unlist(values_dim_med2[2]),unlist(values_dim_max[2]),rawDataHumanAndLiBlock,"Spearman rank correlation score", methodName,paste(filename,"_spearman_10ksubsets.jpeg"))
  
  # harmonic 
  
  plot <- create_histogram(unlist(values_dim_min[3]), unlist(values_dim_med1[3]),unlist(values_dim_med2[3]),unlist(values_dim_max[3]),rawDataHumanAndLiBlock,"Harmonic score", methodName,paste(filename,"_harmonic_10ksubsets.svg"))

  return(plot)
}



# Experiment 1 - Histograms with LiBlock

columnIdentifier <- 7 # LiBlock
methodName <- "(A) [M4] LiBlock measure"
filename <- "liblock"

executeUsingMeasure(columnIdentifier, methodName,filename)
# 
# 
# ## Experiment 2 - Histogram with COM method
# 
# columnIdentifier <- 31 # COM method
# methodName <- "(B) [M17] COM method"
# filename <- "com"
# 
# executeUsingMeasure(columnIdentifier, methodName,filename)
# 
# 
# ## Experiment 3 - Histogram with BioWordVecint method
# 
# columnIdentifier <- 18 # BioWordVec_int method
# methodName <- "(C) [M26] BioWordVec_int method"
# filename <- "biowordvecint"
# 
# executeUsingMeasure(columnIdentifier, methodName,filename)
# 
# ## Experiment 4 - Histogram with BioWordVecint method
# 
# columnIdentifier <- 32 # OuBioBERT method
# methodName <- "(D) [M47] OuBioBERT method"
# filename <- "OuBioBERT"
# 
# executeUsingMeasure(columnIdentifier, methodName,filename)



library(DescTools)
library(nortest)

rawDataHumanAndLiBlock <- rawdata_MedSTS_BESTCOMBS[,c(1,18)]
values_dim_min <- calculate_pearson_spearman_harmonic_values_from_subsets(600,rawDataHumanAndLiBlock, 10000)

values_dim_min <- unlist(values_dim_min[3])

samples <- sample(values_dim_min, 100)

ad.test(samples)
shapiro.test(samples)

library("car")

pearson_chi_sq_harmonic_values_dim_min <- PearsonTest(samples, adjust = TRUE)
pearson_chi_sq_harmonic_values_dim_min
pvalue <- round(pearson_chi_sq_harmonic_values_dim_min$p.value, 3)


# Execute the Saphiro test

shap <- shapiro.test(samples)
print(shap)
pvalue_shap <- round(shap$p.value, 3)
pvalue_shap

# 1. Open jpeg file
# jpeg(file="qqplot.jpeg")
svg("qqplot.svg")

# plot <- qqPlot(samples, xlab="Norm quantiles", ylab="Harmonic values with 50 samples", title="Quantile-Quantile (QQ) plot ")
qqnorm(samples,main = "(B) Normal Q-Q Plot",
       xlab = "Theoretical Quantiles", ylab = "Sample Quantiles",
       plot.it = TRUE, datax = FALSE); 
qqPlot(samples,main = "(B) Normal Q-Q Plot",
       xlab = "Theoretical Quantiles", ylab = "Sample Quantiles");
qqline(samples, col = 2)

legend(
  x = "topleft",         # Posición
       legend = c(paste("Chi-square normality test. P-value : ", pvalue),paste("Shapiro-Wilk normality test. P-value : ", pvalue_shap)), # Textos de la leyenda
       lwd = 2)                # Ancho de las líneas

dev.off()








