# PV168 - evidencia nehnuteľností
Táto aplikácia predstavuje jednoduchú simuláciu systému pre realitnú spoločnosť.
Umožňuje evidenciu majetku, klientov a predajných zmlúv s možnosťou vyhladávania,
pridávania, vymazávania a upravovania jednotlivých entít. Dáta sa ukladajú buď do embedded Derby databázy (používanej hlavne pri testovaní), alebo vo finálnej verzii projektu do standalone Derby databázy (špecifikovanej v dbconfig.properties). 

Projekt obsahuje desktopovú aj webovú verziu tejto aplikácie. 

## Súčasný progres
- [x] Finálny push class, use case diagramov; vyplnenie README.md
- [x] Vytvorenie testov pre aplikáciu
- [x] Implementácia backendu
    - [x] Implementácia Client Managera
    - [x] Implementácia Property Managera
    - [x] Implementácia Contract Managera
- [x] Vytvorenie webového rozhrania
- [x] Finálna verzia projektu
    - [x] Logovanie aplikačnej vrstvy
    - [X] Lokalizácia do troch jazykov
    - [x] Uloženie konfigurácie databázy do .properties súboru
    - [x] Grafické rozhranie
    - [X] Rozdelenie aplikácie do vlákien

## Možné vylepšenia
- Zredukovať duplikovaný kód starajúci sa o formuláciu SQL dotazov použitím Spring frameworku

## Autori
- Michal Stolárik
- Martin Balucha

## Use Case Diagram
![alt text](finalUseCaseDiagram.png "Use Case Diagram")

## Class Diagram
![alt text](finalClassDiagram.png "Class Diagram")


