

# Steps to prepare the BERT enviroment for HESML experiments.

First, install Python 3.6 if it's not installed and install 
the basic libraries.


```
$ python3 -V 
$ sudo apt-get update 
$ sudo apt-get install build-essential 
$ sudo apt-get install python3.6 
$ sudo apt-get install python3-pip 
$ sudo apt-get install mysql-server 
$ sudo apt-get install libmysqlclient-dev 
$ sudo apt-get install python3-dev 
$ sudo apt-get install git 
$ pip3 -V 
$ pip3 install virtualenv 
```

Then, go to the BERTExperiments directory (in HESML library) 
and create the virtual environme**nt.


```
$ cd BERTExperiments/
$ virtualenv -p python3 venv
$ source venv/bin/activate
$ (venv) which python
$ (venv) pip -V
```

Once the virtual environment is activated in the current terminal,
install the libraries.
    
```
$ (venv) pip install -r requirements.txt
```

