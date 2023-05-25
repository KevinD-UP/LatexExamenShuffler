# LaTeXExamenShuffler

## Pré-requis

- java 20
- maven 3.8.1
- pdfLaTeX ou LuaLaTeX ou XeLaTeX

## Compiler le projet

Lancer depuis la racine du projet la commande `mvn package`


## Syntaxe Acceptée

LES accepte comme entrée des fichiers au format .tex qui respectent les spécifications suivante : 

- Le document doit définir les environnements suivants :

```latex
\newtheorem{exi}{}
\newenvironment{exo}{\begin{exi}\em}{\end{exi}}
```

- Le document doit avoir la structure suivante : 

```
Ce que l'on souhaite sauf des exercices - début du document

Suite de blocs exo

Ce que l'on souhaite sauf des exercices - fin du document
```

- Un exercice doit être déclaré comme un bloc  `\begin{exo}` et  `\end{exo}`

- Tout ce qui se trouve entre une ligne `\end{exo}` et une ligne `\begin{exo}` ne sera pas reporté sur les variations générées.



## Lancer le projet

Lancer depuis le répertoire du projet la commande 
```
./latexExamenShuffler.sh cheminInput nbrVariations
```

nbrVariations variations du fichier .tex de chemin inputPath seront alors générées. Elles seront nommées "generatedI.tex" , I allant de 1 au nombre de variation demandé. 
Par défaut les variations se situeront dans le dossier `output/`. 

Vous pouvez préciser avec l'option `--output-dir outputDirPath` le répertoire dans lequel vous souhaitez que les variations soient générées.

Vous pouvez également préciser avec l'option `--output-filename outputFilename` le nom des fichiers générés.

## Fonctionnalités

### Exercices 

- Pour être accepté en entrée, un document .tex doit définir à minima 

```latex
\newtheorem{exi}{}
\newenvironment{exo}{\begin{exi}\em}{\end{exi}}
```
avant le premier exercice.

- Un exercice doit être compris entre les lignes `\begin{exo}` et  `\end{exo}`

- Tout ce qui se trouve entre une ligne `\end{exo}` et une ligne `\begin{exo}` ne sera pas reporté sur les variations générées.

- Il est possible d'indiquer qu'un exercice doit toujours être à la même position en écrivant le commentaire `%fixed` directement collé à `\begin{exo}`.

### Question

- Il est possible de définir un bloc 
```
\begin{itemize}
\item....
	....
\item ....
\end{itemize}
```

ou un bloc 

```
\begin{enumerate}
\item....
	....
\item ....
\end{enumerate}
```

Comme étant un bloc de questions dont on souhaite changer l'ordre en ajoutant `%qb` à la ligne précédant le début du bloc et `%endqb` à la ligne suivant la fin du bloc. L'ordre des items à l'interieur du bloc sera alors mélangé. 


### Variables
Pour déclarer des noms de variables qui peuvent être modifiés aléatoirement par l'utilitaire, il faut
tout d'abord signaler un espace de nommage avec

%var
%endvar

Ces espaces de nommage peuvent être imbriqués.

Ensuite une variable et ses noms de remplacements se signalent,
au début d'un espace de nommage, de la manière suivante:

nomdansleLatex : nomderemplacment1 ; nomderemplacement2 ; ...

plusieurs variables se signalent une ligne à la fois :

nomdansleLatex : nomderemplacment1 ; nomderemplacement2 ; ...
autrenomdansleLatex : autrenomderemplacement1 ; autrenomderemplacement2 ; ...



### Compilation

- Il est possible de choisir le compilateur LaTeX que l'on souhaite utiliser avec l'option `--compiler nom-du-compilateur`. Par défaut le compilateur utilisé est pdfLaTeX. 
Les compilateurs supportés sont : pdfLaTeX, LuaLaTeX ou XeLaTeX

## Auteurs

- Kévin Dang
- Alexandre Sabri
- Robin Arnoux
