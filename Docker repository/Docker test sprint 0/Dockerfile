# Compile: sudo docker build --no-cache -t hesml_test .
# Execute: sudo docker create -it --name "hesml_test" hesml_test:latest
# 		or sudo docker run -it --name "hesml_test" hesml_test:latest

# sudo chmod 777 /var/run/docker.sock


# Start from an Ubuntu image 

FROM ubuntu:latest

# Remove interactive console inputs

ARG DEBIAN_FRONTEND=noninteractive

# Install the dependencies

RUN  apt-get update 

RUN  apt-get install -y wget unzip build-essential checkinstall openjdk-8-jdk python3 libreadline-gplv2-dev libncursesw5-dev libssl-dev libsqlite3-dev tk-dev libgdbm-dev libc6-dev libbz2-dev git r-base libxml2-dev libcurl4-openssl-dev

RUN  apt-get install -y libreadline-gplv2-dev libncursesw5-dev libssl-dev libsqlite3-dev tk-dev libgdbm-dev libc6-dev libbz2-dev 
RUN  wget https://www.python.org/ftp/python/3.6.0/Python-3.6.0.tar.xz 
RUN  tar xvf Python-3.6.0.tar.xz && cd Python-3.6.0 && ./configure && make altinstall 

RUN  apt-get install -y python3-pip virtualenv 
RUN  pip3 install virtualenv 
RUN  pip3.6 install virtualenv 
RUN  pip3 install --upgrade pip 
RUN  pip3.6 install --upgrade pip 

RUN  rm -rf /var/lib/apt/lists/*

# Install R Packages: 'collections', 'kableExtra', 'knitr', 'readr', 'stringr'

RUN R -e "install.packages('collections', repos='http://cran.rstudio.com/')"
RUN R -e "install.packages('kableExtra', repos='http://cran.rstudio.com/')"
RUN R -e "install.packages('knitr', repos='http://cran.rstudio.com/')"
RUN R -e "install.packages('readr', repos='http://cran.rstudio.com/')"
RUN R -e "install.packages('stringr', repos='http://cran.rstudio.com/')"
RUN R -e "install.packages('xtable', repos='http://cran.rstudio.com/')"

# Create a non-root user 

RUN groupadd -g 1000 user
RUN useradd -d /home/user -s /bin/bash -m user -u 1000 -g 1000


# Establish environment variables

ENV HOME /home/user
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/jre


# Create working directories

RUN mkdir -m 700 $HOME/.netbeans && \
 mkdir -m 700 $HOME/HESML && \
    chown -R user:user $HOME/.netbeans $HOME/HESML


# Clone from HESML repo

WORKDIR $HOME/HESML

RUN git clone -b HESML-STS_dev_sprint_0 https://github.com/jjlastra/HESML.git

# Execute the experiment

RUN cd $HOME/HESML/HESML/HESML_Library/HESMLSTSclient && java -jar -Xms4096m dist/HESMLSTSclient.jar ../ReproducibleExperiments/BioSentenceSimilarity_paper/string_benchmark.stsexp

RUN cd $HOME/HESML/HESML/HESML_Library/ReproducibleExperiments/Post-scripts/ && Rscript bio_sentence_sim_allExperiments_table.R