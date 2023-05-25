# LaTeXExamenShuffler

## Pré-requis

- java 20
- javac 20
- maven 3.8.1
- pdfLaTeX
- en option LuaLaTeX et XeLaTeX

## Compiler le projet

Lancer depuis la racine du projet la commande `mvn install`


## Syntaxe Acceptée

### Structure du document

LES accepte comme entrée des fichiers au format .tex qui respectent les spécifications suivantes : 

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


### Syntaxe propre à l'outil

 - Toutes les annotations doivent être précédées de `%%`.

### Fixer les exercices

Par défaut lorsque la sous commande generate est appelée, l'ordre des exercices est mélangé. Si l'on souhaite qu'un exercise conserve son emplacement dans le document inital, on peut fixer sa position en ajoutant l'annotation `%%fixed` sur la même ligne que le `\begin{exo}` qui déclare l'exercice que l'on souhaite fixer - et sans espace.

Par exemple :

```
\begin{exo}%%fixed
contenu de l'exercice
\end{exo}
```


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

Comme étant un bloc de questions dont on souhaite changer l'ordre en ajoutant `%%shuffle` à la ligne précédant le début du bloc et `%endshuffle` à la ligne suivant la fin du bloc. L'ordre des items à l'interieur du bloc sera alors mélangé. Il est également possible de fixer la position d'un item en ajoute `%%fixed` concaténé à la fin de la ligne ou l'item est déclaré


### Renommage des variables


Il est possible de déclarer des scopes ou l'on renomme des variables. Seules les occurences de ces variables situées dans des blocs `$contenu$` au sein du scope seront remplacées. 

La syntaxe de déclaration d'un tel scope est comme suit :

```
%%var
%% x : y;z
%%enddec
contenu ou le renommage s'applique
%%endvar
```

`%%var` et `%%endvar` délimitent la zone d'application du scope. Chaque variable que l'on souhaite voir renommée au sein de ce scope doit être déclarée en dessous de `%%var`. Pour chaque variable on doit dédier une ligne de déclaration donc si l'on veut renommer `x` et `y` on aura.


```
%%var
%%x : a
%%y : b
%%enddec
contenu ou le renommage s'applique
%%endvar
```

`%%enddec` doit toujours suivre la dernière ligne de déclaration de variable. Sur une ligne de déclaration de variable on déclare d'abord le nom de la variable que l'on souhaite voir renommée. Seul les noms de 1 caractère sont pris en charge. Ensuite on doit ajouter `:` puis une liste des valeurs par lequelles x peut être renommées séparées par des `;` . Si l'on ne déclare qu'une seule valeur possible on ne met pas de `;`. Au moins une valeur doit être déclarée. 

Si plusieurs valeurs sont déclarée pour une variable la valeur est choisie aléatoirement.

Il est possible de déclarer un scope au sein d'un autre scope. Le renommage des variables déclarées au sein d'un scope s'applique aussi sur un scope contenu dans celui-ci.

Si un scope déclare une variable étant déjà déclarée dans un scope qui l'englobe, la dernière déclaration sera prise en compte.

Exemple :

```
%%var
%% x : a
%% y : b
%%enddec

ici x = a et y = b

%%var
%% x : p
%%enddec

ici x = p et y = b


%%endvar

ici x = a et y = b

%%endvar


```


### Subset

Il est possible de déclarer des blocs qui constitue des sous-ensembles d'exercices. De tels blocs sont signalés comme suit :

```
%%subset i 
des exercices
%%endsubset

```

les blocs subset ne peuvent pas se chevaucher. Un bloc subset ne permet pas commencer ou se terminer à l'intérieur d'un bloc exercice. 

Les variations générées contiendront `i` exercices choisis aléatoirement parmi ceux contenus dans le bloc subset en lieu du subset. 

## Lancement du programme

La commande generate doit être lancée comme suit 

`./latexExamenShuffler.sh sousCommande cheminInput nbrVariations`

- `sousCommande` spécifie quelle version de `LES` sera exécuter (`generate` ou `subset`, voir plus bas)

- `nbrVariations` variations du fichier .tex de chemin `cheminInput` seront alors générées. Elles seront nommées "generatedI.tex" , I allant de 1 au nombre de variations demandé. 
Par défaut les variations se situeront dans le dossier `output/`. 


### Options

- Vous pouvez préciser avec l'option `--output-dir outputDirPath` le répertoire dans lequel vous souhaitez que les variations soient générées.

- Vous pouvez également préciser avec l'option `--output-filename outputFilename` le nom des fichiers générés.

- Il est possible de choisir le compilateur LaTeX que l'on souhaite utiliser avec l'option `--compiler nom-du-compilateur`. Par défaut le compilateur utilisé est pdfLaTeX. 
Les compilateurs supportés sont : pdfLaTeX, LuaLaTeX ou XeLaTeX

### generate

Lorsque la sous commande generate est appelée, les annotations subset ne seront pas prises en compte. L'ordre des exercices sera mélangé, sauf ceux étant fixés ( vois syntaxe de l'outil ).

### subset 

Lorsque la sous-commande subset est appelée, les annotations subset seront prises en compte comme décrite dans la syntaxe de l'outil. L'ordre des exercises ne sera pas mélangé. 

### Exemple d'utilisation

Execution avec permutation d'exercices, de questions et de variables.
```shell
./latexExamenShuffler.sh generate examples/cc1.tex 5
```

Execution sur un fichier contenant du code lua via lualatex.
```shell
./latexExamenShuffler.sh generate --compiler lualatex examples/cc2.tex 5
```

Execution avec domaine.
```shell
./latexExamenShuffler.sh subset examples/enonce-base.tex 5
```

## Auteurs

- Kévin Dang
- Alexandre Sabri
- Robin Arnoux
