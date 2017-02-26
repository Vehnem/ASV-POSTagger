# ASV-POSTagger

based on https://github.com/datquocnguyen/RDRPOSTagger

## Basic usage
First use

    $ asv -postagger  -help
    
Generate empty properties file

    $ asv -postagger  -genprops
    
Example

    #Tagger  Properties:
    #Fri Feb 24  12:02:28  CET  2017
    input   = <path/to/goldCorpus/file > #Falls  leer  benutze  Datenbank
    output = <path/to/result/folder/>    #Ausgabeordner
    delimiter= \\|                       #Trennzeichen  im Gold  Corpus
    limit=  -1                           #Wie  viele  Zeilen  aus der Datenbank , falls  -1 dann  alles
    sentence_column=                     #Tabellenspalte  mit den getaggten  Saetzen
    dbAdress=                            #Datenbankadresse
    testPercentage= 10                   #Angabe  der zu  verwenden Daten  zum  Test in  Prozent
    dbUser=                              #Name  des  Datenbankbenutzer
    table=                               #Tabelle  mit den  getaggten Saetzen
    dbPassword=                          #Datenbankpasswort
    
Train

    $ asv -postagger  tagger.properties
